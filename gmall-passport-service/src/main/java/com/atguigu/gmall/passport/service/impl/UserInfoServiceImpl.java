package com.atguigu.gmall.passport.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.passport.mapper.UserInfoMapper;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserInfoServiceImpl implements UserInfoService{

    @Autowired
    UserInfoMapper userInfoMapper;
    /**
     * 用户登录
     * @return
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        String md5Hex = DigestUtils.md5Hex(userInfo.getPasswd());
        userInfo.setPasswd(md5Hex);

        return userInfoMapper.selectOne(new QueryWrapper<UserInfo>()
                .eq("login_name",userInfo.getLoginName())
                .eq("passwd",userInfo.getPasswd()));
    }
}
