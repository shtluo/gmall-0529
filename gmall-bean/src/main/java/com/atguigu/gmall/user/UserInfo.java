package com.atguigu.gmall.user;

import com.atguigu.gmall.SuperBean;
import lombok.Data;

@Data
public class UserInfo extends SuperBean{
    //    id  login_name  nick_name      passwd    name
    // phone_num  email        head_img  user_level
    private String loginName;
    private String nickName;
    private String passwd;
    private String name;
    private String phoneNum;
    private String email;
    private String headImg;
    private String userLevel;
}
