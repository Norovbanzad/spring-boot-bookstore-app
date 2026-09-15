package com.example.bookstore.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.StripeCheckoutResponse;
import com.example.bookstore.service.StripePaymentService;
import com.stripe.exception.StripeException;

@RestController 
@RequestMapping ("api/customer/orders")
public class CustomerPaymentController {
    
    private final StripePaymentService stripePaymentService;

    public CustomerPaymentController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }
    
    @PostMapping("/{orderId}/payment") 
    public StripeCheckoutResponse createPayment(@PathVariable  Long orderId) throws  StripeException {
        return stripePaymentService.createCheckoutSession(orderId);
    }
}
