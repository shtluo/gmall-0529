package com.atguigu.rabbitmq.Service;

import com.atguigu.rabbitmq.bean.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {


    //监听消息队列，接收消息
    @RabbitListener(queues = {"user_order_queue"})
        public void getMsg(OrderInfo orderInfo) {

        log.info("收到了过期消息：{}",orderInfo);
        if (orderInfo.getOrderStatus() % 2 == 0) {
            log.info("此订单支付成功！");
        } else {
            log.info("此订单支付失败！");
        }

    }
}
