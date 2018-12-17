package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.order.OrderInfoTo;
import com.atguigu.gmall.order.OrderService;
import com.atguigu.gmall.order.OrderSubmitVo;
import com.atguigu.gmall.user.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class OrderController {
    @Reference
    OrderService orderService;
    /**
     *
     * @param submitVo
     * @return
     */
    @RequestMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, HttpServletRequest request){
        //1、只收页面的两个数据

        //4、都验证通过可以生成订单
        log.info("从页面传来的数据：{}",submitVo);
        //2、防止重复提交
        String tradeToken = submitVo.getTradeToken();
        boolean token = orderService.verfyToken(tradeToken);
        if(!token){
            //令牌失效，到下单失败页面（tradeFail.html）
            request.setAttribute("errorMsg","下单失败，请去购物车页重新刷新提交！");
            return "tradeFail";
        }

        //3、验证库存；库存失败就来失败页
        Map<String,Object> userInfo = (Map<String, Object>) request.getAttribute("userInfo");
        Integer userId = Integer.parseInt(userInfo.get("id")+"");
        //库存不足返回不足的商品名称
        List<String> stockNotGou = orderService.verfyStock(userId);
        if(stockNotGou != null && stockNotGou.size()>0){
            //库存不足还是来到失败页面（tradeFail.html）
            String jsonString = JSON.toJSONString(stockNotGou);
            request.setAttribute("errorMsg","库存不足"+jsonString);
            return "tradeFail";
        }

        //验证令牌和库存都没有问题，来到创建订单业务

        //方法要求传一个OrderInfoTo，
        OrderInfoTo infoTo = new OrderInfoTo();
        infoTo.setOrderComment(submitVo.getOrderComment());
        //拿到用户地址id
        Integer userAddressId = submitVo.getUserAddressId();
        UserAddress userAddress = orderService.getUserAddressById(userAddressId);
        infoTo.setConsignee(userAddress.getConsignee());
        infoTo.setConsigneeTel(userAddress.getPhoneNum());
        infoTo.setDeliveryAddress(userAddress.getUserAddress());

        try{
            orderService.createOrder(userId,infoTo);
        }catch (Exception e){
            request.setAttribute("errroMsg","网络异常。。。");
            return "tradeFail";
        }


        //下单成功，来到支付页面
        return "list";
    }


}
