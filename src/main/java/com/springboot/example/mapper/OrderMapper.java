package com.springboot.example.mapper;

import com.springboot.example.dto.Order;
import com.springboot.example.entity.OrderStatus;
import com.springboot.example.entity.Orders;
import com.springboot.example.entity.Users;
import com.springboot.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    @Autowired
    private UserRepository userRepository;

    public Orders toEntity(Order order) {
        if (order == null) {
            return null;
        }
        Orders orders = new Orders();
        orders.setUser(userRepository.findById(order.getUserId()).orElseThrow());
        orders.setOrderAmount(order.getOrderAmount());
        orders.setStatus(OrderStatus.valueOf(order.getStatus()));
        return orders;
    }

//    public static Order toDto(Orders orders) {
//        if (orders == null) {
//            return null;
//        }
//        Order order = new Order();
//        order.setId(orders.getId());
//        order.setProductName(orders.getProductName());
//        order.setQuantity(orders.getQuantity());
//        order.setPrice(orders.getPrice());
//        return order;
//    }
}
