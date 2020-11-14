package com.atguigu.gmall.pms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


import javax.annotation.PostConstruct;
@Slf4j
@Configuration
public class RabbitConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    public  void init(){
        rabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey)->{
            log.error("消息发送失败交换机（），路由键（），消息类型（）",exchange,routingKey,new String(message.getBody()));
        });
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause)->{
            if (!ack){
                log.error("消息没有到达交换机"+cause);
            }
    });
    }
}
