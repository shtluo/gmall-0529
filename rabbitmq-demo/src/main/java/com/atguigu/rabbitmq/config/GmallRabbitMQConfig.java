package com.atguigu.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GmallRabbitMQConfig {

    /**
     * 创建一个延时Exchange
     * @return
     */
    @Bean
    Exchange userOrderDelayExchange(){
        DirectExchange exchange = new DirectExchange("user_order_delay_exchange",
                true, false, null);
        return exchange;
    }

    /**
     * 建立延时交换机和延时队列的绑定关系
     * @return
     */
    @Bean
    Binding userOrderDelayExchangeQueueBinding(){
        Binding order_delay = BindingBuilder.bind(userOrderDelayQueue())
                .to(userOrderDelayExchange())
                .with("order_delay").noargs();
        return order_delay;
    }


    /**
     * 创建一个延时Queue
     */
    @Bean
    Queue userOrderDelayQueue(){
        //给队列指定延时规则
        Map<String,Object> args = new HashMap<>();
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
        args.put("x-dead-letter-exchange", "user_order_exchange");
        // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。
        args.put("x-dead-letter-routing-key", "order");

        Queue queue = new Queue("user_order_delay_queue", true,
                false, false, args);
        return queue;
    }
    //=======================以上创建了一个延时交换机和延时队列的绑定关系=====================================

    //=======================创建接受死信的Exchange(DLX)======================================================
    @Bean
    Exchange userOrderExchange(){
        DirectExchange user_order_exchange = new DirectExchange("user_order_exchange");
        return user_order_exchange;
    }

    //创建一个存死信的队列
    @Bean
    Queue userOrderQueue(){
        Queue queue = new Queue("user_order_queue");
        return queue;
    }
    //建立绑定关系
    @Bean
    Binding userOrderExchangeQueueBinding(){
        Binding order = BindingBuilder.bind(userOrderQueue())
                .to(userOrderExchange())
                .with("order")
                .noargs();
        return order;
    }







    //为了数据的跨平台性，将数据转为json
    @Bean
    public MessageConverter jsonMassageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
