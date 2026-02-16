package com.springboot.example.service;

import com.springboot.example.dto.Order;
import com.springboot.example.entity.Orders;
import com.springboot.example.mapper.OrderMapper;
import com.springboot.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    // private PaymentService paymentService;

    // This is constructor based dependency injection.
    // We can also use setter based dependency injection.
    // here we dont need to use @Autowired annotation as there is only one constructor. If there are multiple constructors then we need to use
    // @Autowired annotation to specify which constructor to use for dependency injection.
    public OrderServiceImpl(PaymentService ps) {
        this.paymentService = ps;
    }

    // Because of @Autowired annotation, Spring will automatically inject the
    // CashPaymentServiceImpl bean into the OrderServiceImpl bean
    // when it is created. This is called dependency injection.
//    @Autowired
//    public OrderServiceImpl(CashPaymentServiceImpl cashPaymentServiceImpl) {
//        this.paymentService = cashPaymentServiceImpl;
//    }

    // Alternate to above constructor injection
    // here I can do it with @Qualifier annotation
    // to specify which implementation of PaymentService to inject.
    @Autowired
    @Qualifier("cashPaymentServiceImpl")
    private PaymentService paymentService;
    // @Qualifier always wins over @Primary.

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    public void placeOrder(Order order) {
        System.out.println("Order placement started...");
        Orders OrderToSave = orderMapper.toEntity(order);
        Orders savedOrder = orderRepository.save(OrderToSave);
        System.out.println("Order is created successfully "+savedOrder.getAllOrderInfo());
        paymentService.payMoney();
        System.out.println("Payment done Order placed");
    }
}
