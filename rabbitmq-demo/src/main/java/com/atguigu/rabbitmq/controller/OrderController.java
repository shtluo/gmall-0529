package com.atguigu.rabbitmq.controller;

import com.atguigu.rabbitmq.bean.OrderInfo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
public class OrderController {
    //使用spring家的template发送消息
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/createOrder")
   public OrderInfo createOrder(){
       OrderInfo orderInfo = new OrderInfo();
       String substring = UUID.randomUUID().toString().substring(0, 5);
       orderInfo.setOrderId(substring);
       double v = Math.random() * 100;
       long round = Math.round(v);
       int status = Integer.parseInt(String.valueOf(round));
       orderInfo.setOrderStatus(status);
       orderInfo.setTotalAmout(new BigDecimal("99.8"));

       rabbitTemplate.convertAndSend("user_order_delay_exchange",
               "order_delay",orderInfo,(Message message)->{
                   message.getMessageProperties().setExpiration(String.valueOf(1000*60*1));
                   return message;
               });
       return orderInfo;
   }
}
