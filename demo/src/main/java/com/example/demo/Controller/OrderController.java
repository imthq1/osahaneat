package com.example.demo.Controller;

import com.example.demo.Domain.Order;
import com.example.demo.Domain.request.OrderRequest;
import com.example.demo.Service.OrderService;
import com.example.demo.Service.RabbitMQ.OrderMessage;
import com.example.demo.config.RabbitMQ.JobQueue;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;
    public OrderController(OrderService orderService, RabbitTemplate rabbitTemplate) {
        this.orderService = orderService;
        this.rabbitTemplate = rabbitTemplate;
    }
    @PostMapping("/orders")
    @ApiMessage("Create a new Oder")
    public ResponseEntity<String> createOder(@RequestBody OrderRequest orderRequest) throws IdInvalidException {
        if(orderRequest.getFoods()==null)
        {
            throw new IdInvalidException("Exists is empty");
        }
        Order order=this.orderService.createOrder(orderRequest);

        OrderMessage<Long> orderMessage =new OrderMessage<>();
        orderMessage.setDelayMillis(
                Stream.generate(() -> 60000L).limit(15).collect(Collectors.toList()));
        orderMessage.setData(order.getId());
        orderMessage.setData(order.getId());

        rabbitTemplate.convertAndSend(JobQueue.QUEUE_DEV,orderMessage,message -> {
            message.getMessageProperties().setDelayLong(60000L);
            return message;
        });
        return ResponseEntity.ok().body("Order created successfully");
    }
}
