package com.example.demo.Controller;

import com.example.demo.Domain.Order;
import com.example.demo.Domain.response.PaymentDTO;
import com.example.demo.Domain.response.ResponseObject;
import com.example.demo.Service.OrderService;
import com.example.demo.Service.payment.PaymentService;
import com.example.demo.util.constant.StatusOrder;
import com.example.demo.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;
    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request)throws IdInvalidException {
        long OrderID = Long.parseLong(request.getParameter("OrderID"));
        if(this.orderService.findById(OrderID) == null) {
            throw new IdInvalidException("Order not exist");
        }
        return new ResponseObject(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request,OrderID));
    }
    @GetMapping("/vn-pay-callback")
    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        long orderId = Long.parseLong(request.getParameter("vnp_TxnRef"));

        Order order = this.orderService.findById(orderId);
        if ("00".equals(status)) {
            order.setStatus(StatusOrder.SUCCESS);
            this.orderService.save(order);
            PaymentDTO.VNPayResponse response = PaymentDTO.VNPayResponse.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl("")
                    .build();

            return new ResponseObject<>(HttpStatus.OK, "Success", response);
        } else {
            order.setStatus(StatusOrder.FAILED);
            this.orderService.save(order);
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
        }
    }

}
