package com.atguigu.gmall.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderSubmitVo implements Serializable {
    private Integer userAddressId; //用户地址的id
    private String orderComment; //用户备注
    private String tradeToken; //防止重复提交令牌
}
