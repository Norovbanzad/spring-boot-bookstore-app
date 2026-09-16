package com.example.bookstore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController 
@RequestMapping("/api/payments/stripe")
public class StripeWebhookController {
    private final StripeWebhookService stripeWebhookService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader ("Stripe-Signature") String signature) {
        final Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
            
        } catch (SignatureVerificationException se) {
            return ResponseEntity.badRequest().body("Invalid Stripe Signature");
        }

        stripeWebhookService.handleEvent(event);

        return ResponseEntity.ok("Webhook processed");
    }
}
