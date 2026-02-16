package com.springboot.example.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class UPIPaymentServiceImpl implements PaymentService
{
    public void payMoney() {
        System.out.println("Payment has been taken in UPI");
    }
}
