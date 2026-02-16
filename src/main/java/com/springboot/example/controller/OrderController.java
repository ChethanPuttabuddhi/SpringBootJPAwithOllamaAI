package com.springboot.example.controller;

import com.springboot.example.dto.Order;
import com.springboot.example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeorder")
    @Operation( summary = "To place the order", description = "To place the order")
    public void placeOrder(@RequestBody Order order) {
        System.out.println("Input received is \n "+ order.toString());
        orderService.placeOrder(order);

    }
}
