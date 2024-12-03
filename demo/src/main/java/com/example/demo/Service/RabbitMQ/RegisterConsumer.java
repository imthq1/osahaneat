package com.example.demo.Service.RabbitMQ;

import com.example.demo.Service.EmailService;

import com.example.demo.config.RabbitMQ.JobQueue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RegisterConsumer {

    private final EmailService emailService;
    public RegisterConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = JobQueue.QUEUE_DEV_REGISTER)
    public void process(String email) {
        this.emailService.sendLinkVerify(email);
    }

}
