package com.atguigu.gmall.user;

import java.util.List;

public interface UserSerivce {
    /**
     * 获取用户id
     * @param id
     * @return
     */
    public User getUser(String id);

    /**
     * 购买电影票
     * @param uId
     * @param mid
     */
    public void buyMovie(String uId, String mid);

    /**
     * 获取用户的收货地址列表
     * @param id 用户id
     * @return
     */
    List<UserAddress> getUserAddressesByUserId(int id);
}
