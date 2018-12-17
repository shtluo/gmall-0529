package com.atguigu.rabbitmq.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderInfo implements Serializable{
    private String orderId;
    private Integer orderStatus;
    private BigDecimal totalAmout;
}
