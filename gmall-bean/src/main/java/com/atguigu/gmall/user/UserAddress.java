package com.atguigu.gmall.user;

import com.atguigu.gmall.SuperBean;
import lombok.Data;

/**
 * 用户收货地址的VO
 */
@Data
public class UserAddress extends SuperBean{
    //id user_address user_id consignee phone_num is_default
    private String userAddress;
    private Integer userId;
    private String consignee; //收货人
    private String phoneNum; //收货人电话
    private String isDefault;
}
