package com.example.demo.Domain;

import com.example.demo.util.constant.StatusOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResult {
    @Id
    private long orderId;
    private StatusOrder statusOrder;
    private String message;
}
