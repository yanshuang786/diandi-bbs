package com.yan.sms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanshuang
 * @date 2022/3/22 9:57 上午
 */
@Configuration
public class RabbitMqConfig {

    public static final String DD_BLOG = "dd.blog";
    public static final String DD_EMAIL = "dd.email";
    public static final String EXCHANGE_DIRECT = "exchange.direct";
    public static final String ROUTING_KEY_BLOG = "dd.blog";
    public static final String ROUTING_KEY_EMAIL = "dd.email";

    /**
     * 声明交换机
     */
    @Bean(EXCHANGE_DIRECT)
    public Exchange exchangeDirect() {
        // 声明路由交换机，durable:在rabbitmq重启后，交换机还在
        // 交换机名称，交换机是否持久化
        return ExchangeBuilder.directExchange(EXCHANGE_DIRECT).durable(true).build();
    }


    /**
     * 获得博客队列
     * @return 队列
     */
    @Bean(DD_BLOG)
    public Queue ddBlog() {
        /**
         * 1、队列名字
         * 2、是否持久化
         * 3、排他性
         * 4、是否自动删除（如果没有消费者时）
         */
        return new Queue(DD_BLOG, true, false,false);
    }

    /**
     * 获得消息队列
     * @return 队列
     */
    @Bean(DD_EMAIL)
    public Queue ddEmail() {
        /**
         * 1、队列名字
         * 2、是否持久化
         * 3、排他性
         * 4、是否自动删除（如果没有消费者时）
         */
        return new Queue(DD_EMAIL, true, false,false);
    }


    /**
     * mogu.blog 队列绑定交换机，指定routingKey
     *
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定关系
     */
    @Bean
    public Binding bingingQueueInFromBlog(@Qualifier(DD_BLOG) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_BLOG).noargs();
    }

    /**
     * mogu.mail 队列绑定交换机，指定routingKey
     *
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定关系
     */
    @Bean
    public Binding bingingQueueInFromEmail(@Qualifier(DD_EMAIL) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_EMAIL).noargs();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
