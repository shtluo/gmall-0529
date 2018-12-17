package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.cart.CartItem;
import com.atguigu.gmall.cart.CartService;
import com.atguigu.gmall.cart.CartVo;
import com.atguigu.gmall.cart.SkuItem;
import com.atguigu.gmall.constant.CartConstant;
import com.atguigu.gmall.order.*;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.mapper.UserAddressMapper;
import com.atguigu.gmall.user.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    JedisPool jedisPool;
    @Reference
    CartService cartService;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    UserAddressMapper userAddressMapper;


    @Override
    public String createTradeToken() {
        //创建一个令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        Jedis jedis = jedisPool.getResource();
        //最好设置一个过期时间，来到提交订单页，3分钟不提交就token自动失效
        jedis.setex(token,60*3,"66666");
        return token;
    }

    @Override
    public boolean verfyToken(String tradeToken) {
        Jedis jedis = jedisPool.getResource();
        Long del = jedis.del(tradeToken);
        return del==1L?true:false;
    }

    /**
     * 验证库存是否不足
     * @param userId
     * @return
     */
    @Override
    public List<String> verfyStock(Integer userId) {
        //验证用户商品购物车里勾选的每一个商品的库存是否足够
        //Java代码模仿发请求的工具 RestTmplate,HttpClient
        List<CartItem> cartInfoCheckdList = cartService.getCartInfoCheckdList(userId);
        List<String> result = new ArrayList<>();
        for (CartItem cartItem : cartInfoCheckdList) {
            Integer skuId = cartItem.getSkuItem().getId();
            Integer num = cartItem.getNum();
            try {
                boolean b = stockCheck(skuId, num);
                if(!b){
                    //验证库存失败
                    result.add(cartItem.getSkuItem().getSkuName());
                }
            } catch (IOException e) {
                log.info("验证库存失败{}",e);
            }
        }

        return result;
    }


    @Override
    public void createOrder(Integer userId,OrderInfoTo infoTo) {
        //1、获取购物车里的商品信息
        List<CartItem> checkdList = cartService.getCartInfoCheckdList(userId);
        CartVo cartVo = new CartVo();
        cartVo.setCartItems(checkdList);
        BigDecimal totalPrice = cartVo.getTotalPrice(); //计算总额

        //这些是刚开始就默认设置的
        OrderInfo orderInfo = new OrderInfo(); //总订单
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.setPaymentWay(PaymentWay.ONLINE);
        orderInfo.setCreateTime(new Date());
        //30分钟没有支付自动过期
       long expireTime =  System.currentTimeMillis()+1000*60*30;
        orderInfo.setExpireTime(new Date(expireTime));

        //把用户id传过来获取到购物车选中商品
        orderInfo.setUserId(userId);
        //把页面的详情传过来（OrderInfoTo）
        orderInfo.setDeliveryAddress(infoTo.getDeliveryAddress());
        orderInfo.setOrderComment(infoTo.getOrderComment());
        orderInfo.setConsignee(infoTo.getConsignee());
        orderInfo.setConsigneeTel(infoTo.getConsigneeTel());
        //订单对外业务号
        orderInfo.setOutTradeNo("ATGUIGU_"+System.currentTimeMillis()+"_"+userId);
        //订单总金额
        orderInfo.setTotalAmount(totalPrice);

        //所有属性设置完毕，进行保存操作（OrderInfoMapper）,保存总订单，然后保存每一项，分两张表保存（order_info，order_detail）
        //2.1)
        orderInfoMapper.insert(orderInfo);


        for (CartItem cartItem : checkdList) {
            SkuItem skuItem = cartItem.getSkuItem(); //订单项
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImgUrl(skuItem.getSkuDefaultImg());
            orderDetail.setOrderId(orderInfo.getId());
            orderDetail.setSkuId(skuItem.getId());
            orderDetail.setOrderPrice(skuItem.getPrice());
            orderDetail.setSkuName(skuItem.getSkuName());
            orderDetail.setSkuNum(cartItem.getNum());
            //插入订单项到数据库
            //2.2) 到此购物车中的数据全部保存到订单表中
            orderDetailMapper.insert(orderDetail);
        }
        //订单创建完成就删除购物车选中的数据
        Jedis jedis = jedisPool.getResource();
        String[] delStrIds = new String[checkdList.size()];
        for(int i = 0; i < checkdList.size(); i++){
            delStrIds[i] = checkdList.get(i).getSkuItem().getId()+"";
        }
        jedis.hdel(CartConstant.USER_CART_PREFIX+userId,delStrIds);
        jedis.close();
    }

    /**
     * 获取用户地址
     * @param userAddressId
     * @return
     */
    @Override
    public UserAddress getUserAddressById(Integer userAddressId) {
        return userAddressMapper.selectById(userAddressId);
    }

    /**
     * 第三方的所有功能怎么掉？
     *  发短信？
     *
     * @param skuId
     * @param num
     * @return
     * @throws IOException
     */
    private boolean stockCheck(Integer skuId,Integer num) throws IOException {
        //1、验证用户购物车里面勾选的商品的每一个库存是否足够
        //1）、HttpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //2）、
        HttpGet httpGet = new HttpGet("http://www.gware.com/hasStock?skuId="+skuId+"&num="+num);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            // 404 NOTFOUD
            //获取响应体
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent(); //0或者1
            String data = EntityUtils.toString(entity);
            return  "0".equals(data)?false:true;

        }  finally {
            //关响应
            response.close();
        }
    }
}
