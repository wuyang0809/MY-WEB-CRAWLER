package com.yj.crawler.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author wuyang
 * @date 2018/8/2 18:32
 */
@Configuration
public class RabbitMQConnectionConfig {

    /**
     * 引入配置
     * @return
     */
    @Bean(name = "rabbitMQConfig")
    @ConfigurationProperties(ignoreUnknownFields = false, prefix = "rabbitmq")
    public RabbitMQConfig getRabbitMQConfig() {
        RabbitMQConfig config = new RabbitMQConfig();
        return config;
    }

    /**
     * 初始化连接池
     * @param rabbitMQConfig
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    @Bean
    public RabbitMQConnectionPool getRabbitMQConnectionPool(@Qualifier(value ="rabbitMQConfig") RabbitMQConfig rabbitMQConfig) throws IOException, TimeoutException {
        RabbitMQConnectionPool rabbitMQConnectionPool = new RabbitMQConnectionPool();
        rabbitMQConnectionPool.init(rabbitMQConfig);
        return rabbitMQConnectionPool;
    }

}
