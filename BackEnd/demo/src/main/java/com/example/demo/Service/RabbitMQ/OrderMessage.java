package com.example.demo.Service.RabbitMQ;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderMessage<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    private T data;
    private List<Long> delayMillis;

    public OrderMessage(T data, List<Long> delayMillis) {
        this.data = data;
        this.delayMillis = delayMillis;
    }

    public static <T> OrderMessage<T> of(T data, Long... delayMillis) {
        return new OrderMessage<>(data, List.of(delayMillis));
    }

    public Long removeNextDelay() {
        return delayMillis.remove(0);
    }

    public boolean hasDelay() {
        return !delayMillis.isEmpty();
    }
}
