package com.springboot.example.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders", schema = "order_management")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key column
//     @Column(name = "user_id", nullable = false)
//     private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(
            name = "order_amount",
            precision = 10,
            scale = 2,
            nullable = false
    )
    private BigDecimal orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private OrderStatus status;

    @Column(
            name = "created_at",
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Transient
    private String allOrderInfo; // This field will not be persisted in the database

    // Constructors
    public Orders() {}

    public Orders(Users user, BigDecimal orderAmount, OrderStatus status) {
        this.user = user;
        this.orderAmount = orderAmount;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAllOrderInfo() {
        return "Orders{" +
                "id=" + id +
                ", userId=" + user.getId()+
                ", orderAmount=" + orderAmount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", allOrderInfo='" + allOrderInfo + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", userId=" + user.getId() +
                ", orderAmount=" + orderAmount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", allOrderInfo='" + allOrderInfo + '\'' +
                '}';
    }
}

