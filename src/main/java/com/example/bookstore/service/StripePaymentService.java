package com.example.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.dto.StripeCheckoutResponse;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderStatus;
import com.example.bookstore.entity.Payment;
import com.example.bookstore.entity.PaymentStatus;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessRuleException;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;

    public StripePaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository,
            CurrentUserService currentUserService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.currentUserService = currentUserService;
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional 
    public StripeCheckoutResponse createCheckoutSession(Long orderId) throws  StripeException {
        User user = currentUserService.getCurrentUser();
        Order order = orderRepository.findByIdAndUser(orderId, user).orElseThrow(() -> 
    new ResourceNotFoundException("Order not found with ID: " + orderId));

    if(order.getStatus() == OrderStatus.CANCELLED) {
        throw new BusinessRuleException("Cancelled order cannot be paid");
    }

    if(order.getStatus() != OrderStatus.PENDING) {
        throw new BusinessRuleException("This order cannot be paid");
    }

    Payment payment = paymentRepository.findByOrder(order).orElseGet(() -> {
        Payment newPayment = new Payment();
        newPayment.setOrder(order);

        newPayment.setAmount(order.getTotalAmount());
        newPayment.setProvider("STRIPE");
        newPayment.setStatus(PaymentStatus.PAID);
        newPayment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(newPayment);
    });

    if(payment.getStatus() == PaymentStatus.PAID) {
        throw new BusinessRuleException("Order is alreday paid");
    }

    // 95000 MNT -> 95000.00
    long stripeAmount = order.getTotalAmount().multiply(new BigDecimal("100")).longValueExact();

    SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/payment/success?session_id{CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/customer/order" + order.getId() + "?payment_cancelled")
            .setCustomerEmail(user.getEmail())
            .putMetadata("order_id", order.getId().toString())
            .putMetadata("payment_id", payment.getId().toString())
            .addLineItem(
                    SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("mnt")
                                    .setUnitAmount(stripeAmount)
                                    .setProductData(
                                            SessionCreateParams
                                                .LineItem
                                                .PriceData
                                                .ProductData
                                                .builder()
                                            .setName("Bookstore Order #" + order.getId())       
                                    .build())
                                .build())
                    .build())
            .build();

    Session session = Session.create(params);

    payment.setProviderPaymentId(session.getId());
    paymentRepository.save(payment);
    return new StripeCheckoutResponse(session.getUrl());
    }
    
}

