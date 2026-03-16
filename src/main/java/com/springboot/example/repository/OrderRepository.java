package com.springboot.example.repository;

import com.springboot.example.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    // Find orders by status
    // Custom query to find orders by total amount greater than
    @Query("SELECT o FROM Orders o WHERE o.orderAmount > :amount")
    List<Orders> findOrdersByAmountGreaterThan(@Param("amount") Double amount);

    // Delete orders by status
    void deleteByStatus(String status);

    @Query("SELECT o FROM Orders o WHERE o.user.id = :userId")
    List<Orders> findByUserId(@Param("userId") Long userId);
}

