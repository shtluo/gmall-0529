package com.atguigu.gmall.user;

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
}
