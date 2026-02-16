package com.springboot.example.service;

import org.springframework.stereotype.Service;

@Service
public class CashPaymentServiceImpl implements PaymentService {
    public void payMoney() {
        System.out.println("Payment has been taken in Cash");
    }
}
