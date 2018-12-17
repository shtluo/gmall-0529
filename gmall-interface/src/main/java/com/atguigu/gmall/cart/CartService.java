package com.atguigu.gmall.cart;

import java.util.List;

public interface CartService {

    /**
     *添加商品到购物车
     * @param skuId 商品id
     * @param cartKey 未登录下的购物车id
     * @param num 商品数量
     * @return
     */
    String addToCartUnLogin(Integer skuId, String cartKey, Integer num);

    /**
     *
     * @param skuId 商品id
     * @param userId 用户id，用来拼接cart-key
     * @param num
     */
    void addToCartLogin(Integer skuId, Integer userId, Integer num);

    /**
     * 获取购物车数据
     * @param cartKey 购物车在redis中保存用的key
     * @return
     */
    CartVo getYourCart(String cartKey);

    void mergeCart(String cartKey, Integer userId);

    /**
     *查询购物车所有数据
     * @param cartKey
     * @param login
     * @return
     */
    List<CartItem> getCartInfoList(String cartKey, boolean login);

    /**
     * 查询购物车中的某个数据
     * @param cartKey
     * @param skuId
     * @return
     */
    CartItem getCartItemInfo(String cartKey, Integer skuId);

    /**
     * 返回用户选中的商品
     * @param id 用户id
     * @return
     */
    List<CartItem> getCartInfoCheckdList(int id);

    /**
     * 购物车是否勾选
     * @param skuId
     * @param checkFlag
     * @param tempCartKey
     * @param userId
     * @param loginFlag
     */
    void checkItem(Integer skuId, Boolean checkFlag, String tempCartKey, int userId, boolean loginFlag);
}
