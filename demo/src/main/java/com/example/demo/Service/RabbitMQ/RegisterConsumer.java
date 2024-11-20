package com.example.demo.Service.RabbitMQ;

import com.example.demo.Service.EmailService;
import com.example.demo.config.RabbitMQ.JobQueue;
import com.example.demo.util.SecurityUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RegisterConsumer {
    private EmailService emailService;
    private SecurityUtil securityUtil;
    public RegisterConsumer(EmailService emailService, SecurityUtil securityUtil) {
        this.emailService = emailService;
        this.securityUtil = securityUtil;
    }
    @RabbitListener(queues = JobQueue.QUEUE_DEV_REGISTER)
    public void handleRegister(String email) {
        try {
            this.emailService.sendLinkVerify(email.toString().replace("\"", ""));
        } catch (Exception e) {
            System.err.println("Error handling message from RabbitMQ: " + e);
            e.printStackTrace();
        }
    }

}
