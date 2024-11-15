package com.example.demo.config.RabbitMQ;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableRabbit
@Configuration
public class RabitMQConfig {

    private final AmqpAdmin amqpAdmin;
    @Autowired
    public RabitMQConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${spring.rabbitmq.virtual-host}")
    private String rabbitVirtualHost;

    private CachingConnectionFactory getCachingConnectionFactoryCommon() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(this.rabbitHost, this.rabbitPort);
        connectionFactory.setUsername(this.rabbitUsername);
        connectionFactory.setPassword(this.rabbitPassword);
        return connectionFactory;
    }

    @Primary
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = this.getCachingConnectionFactoryCommon();
        connectionFactory.setVirtualHost(this.rabbitVirtualHost);
        return connectionFactory;
    }

    @Primary
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setChannelTransacted(true); // Enable transactions
        return template;
    }

    @Primary
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @PostConstruct
    public void init() {
        try {
            defineQueues();
        } catch (Exception e) {
            throw new RuntimeException("Không thể khởi tạo RabbitMQ queues: " + e.getMessage(), e);
        }
    }

    private void defineQueues() {
        for (String queueName : JobQueue.queueNameList) {
            try {
                QueueInformation queueInfo = amqpAdmin.getQueueInfo(queueName);
                if (queueInfo == null) {
                    Queue queue = QueueBuilder.durable(queueName)
                            .build();
                    amqpAdmin.declareQueue(queue);
                }
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi khai báo queue " + queueName + ": " + e.getMessage(), e);
            }
        }
    }
//    @Bean(name = "amqpAdminJob")
//    public AmqpAdmin amqpAdminJob() {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(this.rabbitConnectionJob());
//        return rabbitAdmin;
//    }
//
//    @Bean("rabbitConnectionJob")
//    public ConnectionFactory rabbitConnectionJob() {
//        CachingConnectionFactory connectionFactory = this.getCachingConnectionFactoryCommon();
//        connectionFactory.setVirtualHost(this.rabitVirtualHostJob);
//        return connectionFactory;
//    }
//
//    @Bean("rabbitTemplateJob")
//    public RabbitTemplate rabbitTemplateJob(@Qualifier("rabbitConnectionJob") ConnectionFactory connectionFactory) {
//        return new RabbitTemplate(connectionFactory);
//    }
//
//    @Bean("containerFactoryJob")
//    public SimpleRabbitListenerContainerFactory containerFactoryJob(@Qualifier("rabbitConnectionJob") ConnectionFactory connectionFactory) {
//        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setDefaultRequeueRejected(false);
//        factory.setConnectionFactory(connectionFactory);
//        factory.setDefaultRequeueRejected(false);
//        return factory;
//    }




}
