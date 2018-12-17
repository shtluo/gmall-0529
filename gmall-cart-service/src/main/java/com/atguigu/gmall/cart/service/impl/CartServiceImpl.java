package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.CartItem;
import com.atguigu.gmall.cart.CartService;
import com.atguigu.gmall.cart.CartVo;
import com.atguigu.gmall.cart.SkuItem;
import com.atguigu.gmall.constant.CartConstant;
import com.atguigu.gmall.manager.SkuService;
import com.atguigu.gmall.manager.sku.SkuInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    JedisPool jedisPool;

    @Reference
    SkuService skuService;


    /**
     * 用户未登录加入购物车
     * @param skuId 商品id
     * @param cartKey 未登录下的购物车id
     * @param num 商品数量
     * @return
     */
    @Override
    public String addToCartUnLogin(Integer skuId, String cartKey, Integer num) {
        Jedis jedis = jedisPool.getResource();
        if(!StringUtils.isEmpty(cartKey)){
            //之前创建过购物车
            //1、根据这个cartKey判断一下在Reids中是否存在这个key(防止传来的cartkey是否非法)
            Boolean exists = jedis.exists(cartKey);
            if(exists == false){
                //传来的这个件不存在
                String newCartKey = createCart(num,skuId,false,null);
                return newCartKey;
            }else{

                String skuInfoJson = jedis.hget(cartKey, skuId + "");
                if(!StringUtils.isEmpty(skuInfoJson)){
                    addNum(skuId, cartKey, num, jedis, skuInfoJson);
//--------写到这
                }else{
                    //2.购物车中无此商品
                    try {
                        CartItem cartItem = new CartItem();
                        SkuInfo skuInfo = skuService.getSkuInfoBySkuId(skuId);
                        SkuItem skuItem = new SkuItem();
                        BeanUtils.copyProperties(skuInfo,skuItem);
                        cartItem.setNum(num);
                        cartItem.setSkuItem(skuItem);
                        cartItem.setTotalPrice(cartItem.getTotalPrice());
                        String jsonString = JSON.toJSONString(cartItem);
                        jedis.hset(cartKey,skuItem.getId()+"",jsonString);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            //无购物车就新建
           return createCart(num,skuId,false,null);
        }
        jedis.close();
        return cartKey;
    }

    private void addNum(Integer skuId, String cartKey, Integer num, Jedis jedis, String skuInfoJson) {
        //1.购物车中此商品,叠加数量
        CartItem cartItem = JSON.parseObject(skuInfoJson, CartItem.class);
        cartItem.setNum(cartItem.getNum()+num);
        cartItem.setTotalPrice(cartItem.getTotalPrice());
        String jsonString = JSON.toJSONString(cartItem);
        jedis.hset(cartKey, skuId+"",jsonString);
    }

    /**
     * 用户登录了加入购物车
     * @param skuId 商品id
     * @param userId 用户id，用来拼接cart-key
     * @param num
     */
    @Override
    public void addToCartLogin(Integer skuId, Integer userId, Integer num) {
        //登录后加入购物车
        Jedis jedis = jedisPool.getResource();
        Boolean exists = jedis.exists(CartConstant.USER_CART_PREFIX+userId);
        if(exists){
            //已经有购物车
            String cartKey = CartConstant.USER_CART_PREFIX+skuId;
            String hget = jedis.hget(cartKey, skuId + "");
            if(!StringUtils.isEmpty(hget)){
                //有这个商品,叠加数量
                addNum(skuId,cartKey,num,jedis,hget);
                /*CartItem cartItem = JSON.parseObject(hget, CartItem.class);
                cartItem.setNum(cartItem.getNum()+num);
                cartItem.setTotalPrice(cartItem.getTotalPrice());
                String s = JSON.toJSONString(cartItem);
                jedis.hset(cartKey,skuId+"",s);*/
            }else{
                try {
                    //没有这个商品
                    CartItem cartItem = new CartItem();
                    //查出这个商品
                    SkuInfo skuInfoBySkuId = skuService.getSkuInfoBySkuId(skuId);
                    SkuItem skuItem = new SkuItem();
                    BeanUtils.copyProperties(skuInfoBySkuId,skuItem);
                    cartItem.setSkuItem(skuItem);
                    cartItem.setNum(num);
                    cartItem.setTotalPrice(cartItem.getTotalPrice());
                    String s = JSON.toJSONString(cartItem);
                    jedis.hset(cartKey,skuId+"",s);

                    //拿出之前的顺序，把顺序更新以下
                    String fieldOrder = jedis.hget(cartKey, "fieldOrder");
                    List list = JSON.parseObject(fieldOrder, List.class);
                    //把新的商品放进List
                    list.add(skuId);
                    String string = JSON.toJSONString(list);
                    jedis.hset(cartKey,"fieldOrder",string);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            //第一次添加，还没有购物车
            String newCartKey = createCart(num, skuId, true, userId);

        }
    }

    @Override
    public CartVo getYourCart(String cartKey) {
        return null;
    }

    @Override
    public void mergeCart(String cartKey, Integer userId) {
        //合并购物车
        //1、查出临时购物车的所有数据
        List<CartItem> cartItems = getCartInfoList(cartKey, false);

        if(cartItems != null && cartItems.size() > 0){
            //挨个购物车添加到用户的购物车中
            for (CartItem tempCartItem : cartItems) {
                addToCartLogin(tempCartItem.getSkuItem().getId(),userId,tempCartItem.getNum());
            }
            //合并完成:删除redis中的临时购物车
            Jedis jedis = jedisPool.getResource();
            jedis.del(cartKey);
            //删除临时购物车中的cookie

            //2、把这些用户数据填到用户购物车中
        }
    }

    @Override
    public List<CartItem> getCartInfoList(String cartKey, boolean login) {
        String queryKey = cartKey;
        if(login){
            queryKey = CartConstant.USER_CART_PREFIX + cartKey;
        }


        //查redis中这个key对应的购物车数据
        Jedis jedis = jedisPool.getResource();
        //map的key是什么，值是什么？
        List<CartItem> cartItemList = new ArrayList<>();
        //Map<String, String> hgetAll = jedis.hgetAll(queryKey);
        String fieldOrder = jedis.hget(queryKey, "fieldOrder");
        List list = JSON.parseObject(fieldOrder, List.class);
        for (Object o : list) {
            int idSort = Integer.parseInt(o.toString());
            String hget = jedis.hget(queryKey, idSort + "");
            CartItem cartItem = JSON.parseObject(hget, CartItem.class);
            cartItemList.add(cartItem);
        }
        return cartItemList;
    }

    @Override
    public CartItem getCartItemInfo(String cartKey, Integer skuId) {
        Jedis jedis = jedisPool.getResource();
        String json = jedis.hget(cartKey, skuId + "");
        CartItem cartItem = JSON.parseObject(json, CartItem.class);
        return cartItem;
    }

    @Override
    public List<CartItem> getCartInfoCheckdList(int id) {
        Jedis jedis = jedisPool.getResource();
        String cartKey = CartConstant.USER_CART_PREFIX + id;
        //1、获取购物车所有商品
        Map<String, String> stringMap = jedis.hgetAll(cartKey);
        //2、保存所有被选中的商品
        List<CartItem> checkedItem = new ArrayList<>();
        List<CartItem> cartItemList = getCartInfoList(id + "", true);
        if(cartItemList == null) {
            return  null;
        }

        for (CartItem cartItem : cartItemList) {
            if(cartItem.isCheck()){
                checkedItem.add(cartItem);
            }
        }
        if(checkedItem.size() == 0) {
            return null;
        }

        jedis.close();
        return checkedItem;
    }

    @Override
    public void checkItem(Integer skuId, Boolean checkFlag, String tempCartKey, int userId, boolean loginFlag) {
        //购物车勾选
        String caryKey = loginFlag?CartConstant.USER_CART_PREFIX + userId:tempCartKey;
        CartItem cartItem = getCartItemInfo(caryKey, skuId);
        //设置勾选状态
        cartItem.setCheck(checkFlag);

        //修改购物车数据
        String string = JSON.toJSONString(cartItem);
        Jedis jedis = jedisPool.getResource();
        jedis.hset(caryKey,skuId+"",string);
        jedis.close();
    }

    /**
     *
     * @param num 同一个商品的数量
     * @param skuId 商品id
     * @param login 是否登陆
     * @param userId 用户id
     * @return
     */
    private String createCart(Integer num,Integer skuId,boolean login,Integer userId){
        Jedis jedis = jedisPool.getResource();
        String newCartKey = null;
        if(login){

            //已登陆用的key
            newCartKey = CartConstant.USER_CART_PREFIX+userId;

        }else{
            //新建购物车
            //未登录用的key
            newCartKey = CartConstant.TEMP_CART_PREFIX+UUID
                    .randomUUID().toString().substring(0,10).replaceAll("-","");
        }


        //保存购物车数据
        try {
            SkuInfo skuInfo = skuService.getSkuInfoBySkuId(skuId);

            CartItem cartItem = new CartItem();
            SkuItem skuItem = new SkuItem();
            //拷贝商品的详细信息进来，准备保存到redis
            BeanUtils.copyProperties(skuInfo,skuItem);
            cartItem.setSkuItem(skuItem);
            cartItem.setNum(num);
            cartItem.setTotalPrice(cartItem.getTotalPrice());
            String jsonString = JSON.toJSONString(cartItem);
            List<Integer> ids = new ArrayList<>();
            ids.add(cartItem.getSkuItem().getId());
            jedis.hset(newCartKey, skuItem.getId() + "", jsonString);
            String fieldOrder = JSON.toJSONString(ids);
            jedis.hset(newCartKey,"fieldOrder",fieldOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        jedis.close();
        return newCartKey;
    }
}
