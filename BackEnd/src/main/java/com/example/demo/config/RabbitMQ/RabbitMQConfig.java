package com.example.demo.config.RabbitMQ;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String rabbitVirtualHost;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Primary
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(this.rabbitHost, this.rabbitPort);
        connectionFactory.setUsername(this.rabbitUsername);
        connectionFactory.setPassword(this.rabbitPassword);
        connectionFactory.setVirtualHost(this.rabbitVirtualHost);
        return connectionFactory;
    }

    @Primary
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Primary
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setPrefetchCount(10);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    @DependsOn("amqpAdmin")
    public List<Queue> defineQueues(AmqpAdmin amqpAdmin) {
        List<Queue> queues = new ArrayList<>();
        for (String queueName : JobQueue.queueNameList) {
            try {
                Queue queue = QueueBuilder.durable(queueName)
                        .build();
                amqpAdmin.declareQueue(queue);
                queues.add(queue);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi khai báo queue " + queueName + ": " + e.getMessage(), e);
            }
        }
        return queues;
    }


}