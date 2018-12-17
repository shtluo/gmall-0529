package com.atguigu.gmall.order;

import com.atguigu.gmall.user.UserAddress;

import java.util.List;

public interface OrderService {
    /**
     * 创建放重复提交的令牌
     */
    String createTradeToken();

    /**
     * 验证令牌是否有效
     * @param tradeToken
     * @return
     */
    boolean verfyToken(String tradeToken);

    /**
     * 验证库存不足
     * @param userId
     * @return
     */
    List<String> verfyStock(Integer userId);

    /**
     * 创建订单
     * @param userId
     */
    void createOrder(Integer userId,OrderInfoTo infoTo);

    /**
     * 获取用户地址
     * @param userAddressId
     * @return
     */
    UserAddress getUserAddressById(Integer userAddressId);
}
