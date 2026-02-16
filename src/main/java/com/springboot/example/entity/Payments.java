package com.springboot.example.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", schema = "payment_management")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK to order_management.orders(id)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", length = 30, nullable = false)
    private String paymentStatus;

    @Column(
            name = "amount",
            precision = 10,
            scale = 2,
            nullable = false
    )
    private BigDecimal amount;

    @Column(
            name = "payment_time",
            insertable = false,
            updatable = false
    )
    private LocalDateTime paymentTime;

    // Constructors
    public Payments() {}

    public Payments(
            Long orderId,
            String paymentMethod,
            String paymentStatus,
            BigDecimal amount
    ) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
}
