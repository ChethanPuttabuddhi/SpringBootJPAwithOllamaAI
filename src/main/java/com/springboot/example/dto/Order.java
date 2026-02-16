package com.springboot.example.dto;

import org.springframework.lang.NonNull;

import java.math.BigDecimal;

// @component // I should not use this tag here because
// It is not needed for Spring to maintain this bean
// It will be singleton if Spring manages this bean

public class Order {

    @NonNull
    private Long userId;

    private BigDecimal OrderAmount;

    @NonNull
    private String Status;

    public Order(@NonNull Long userId, BigDecimal orderAmount, @NonNull String status) {
        this.userId = userId;
        OrderAmount = orderAmount;
        Status = status;
    }

    @NonNull
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NonNull Long userId) {
        this.userId = userId;
    }

    public BigDecimal getOrderAmount() {
        return OrderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        OrderAmount = orderAmount;
    }

    @NonNull
    public String getStatus() {
        return Status;
    }

    public void setStatus(@NonNull String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId=" + userId +
                ", OrderAmount=" + OrderAmount +
                ", Status='" + Status + '\'' +
                '}';
    }
}
