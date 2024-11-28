package com.example.demo.Service.RabbitMQ;

import com.example.demo.Domain.Order;
import com.example.demo.Domain.request.OrderRequest;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.OrderService;
import com.example.demo.config.RabbitMQ.JobQueue;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.StatusOrder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderConsumer {
    private EmailService emailService;
    private SecurityUtil securityUtil;
    private RabbitTemplate rabbitTemplate;
    private final OrderService oderService;
    public OrderConsumer(EmailService emailService, SecurityUtil securityUtil, OrderService oderService) {
        this.emailService = emailService;
        this.securityUtil = securityUtil;
        this.oderService = oderService;
    }
    @RabbitListener(queues = JobQueue.QUEUE_DEV)
    public void handleRegister(OrderMessage<Long> msg) {
    Order order=this.oderService.findById(msg.getData());
        if (order == null || Objects.equals(order.getStatus(), StatusOrder.SUCCESS)) {
            return;
        }
        if (msg.hasDelay()) {
            Long nextDelay = msg.removeNextDelay();
            rabbitTemplate.convertAndSend(
                    JobQueue.QUEUE_DEV,
                    msg,
                    message -> {
                        message.getMessageProperties().setDelayLong(nextDelay);
                        return message;
                    });
        } else {
            order.setStatus(StatusOrder.FAILED);
            this.oderService.save(order);
        }
    }

}
