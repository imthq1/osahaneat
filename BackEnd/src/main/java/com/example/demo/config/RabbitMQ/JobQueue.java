package com.example.demo.config.RabbitMQ;

import java.util.Arrays;
import java.util.List;

public class JobQueue {
    public static final String QUEUE_PROCESS = "rabbit-queue";
    public static final String QUEUE_DEV_REGISTER = "rabbit-queue-register";
    public static final String QUEUE_DEV_ORDER = "rabbit-queue-order";

    public static final List<String> queueNameList = Arrays.asList(QUEUE_PROCESS, QUEUE_DEV_REGISTER,QUEUE_DEV_ORDER);
}

