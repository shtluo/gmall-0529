package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.manager.mapper.UserAddressMapper;
import com.atguigu.gmall.user.User;
import com.atguigu.gmall.user.UserAddress;
import com.atguigu.gmall.user.UserSerivce;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserSerivce {
    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public User getUser(String id) {
        return null;
    }

    @Override
    public void buyMovie(String uId, String mid) {

    }

    /**
     * 获取用户地址列表详情
     * @param id 用户id
     * @return
     */
    @Override
    public List<UserAddress> getUserAddressesByUserId(int id) {
        List<UserAddress> UserAddress = userAddressMapper
                .selectList(new QueryWrapper<UserAddress>().eq("user_id", id));
        return UserAddress;
    }
}
