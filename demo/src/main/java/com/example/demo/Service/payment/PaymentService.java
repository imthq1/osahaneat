package com.example.demo.Service.payment;

import com.example.demo.Domain.Order;
import com.example.demo.Domain.response.PaymentDTO;
import com.example.demo.Service.OrderService;
import com.example.demo.config.vnpay.VnpayConfig;
import com.example.demo.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class PaymentService {
    private final VnpayConfig vnPayConfig;
    private final OrderService orderService;
    public PaymentService(VnpayConfig vnPayConfig, OrderService orderService) {
        this.vnPayConfig = vnPayConfig;
        this.orderService = orderService;
    }
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request,Long OrderID) {
        Order order=this.orderService.findById(OrderID);
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(Math.round(order.getTotalPrice() * 100L)));


        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        vnpParamsMap.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VnpayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VnpayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VnpayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
}
