package com.newsletter.controller;

import com.newsletter.dto.SubscriptionRequest;
import com.newsletter.model.Subscription;
import com.newsletter.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/newsletter")
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/subscribe")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Subscription> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Received request: " + request);
        Subscription saved = service.subscribe(request);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Get all subscriptions")
    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        List<Subscription> subscriptions = service.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Get subscription by email")
    @GetMapping("/{email}")
    public ResponseEntity<Subscription> getSubscriptionsByEmail(@PathVariable String email) {
        Subscription subscription = service.getSubscriptionByEmail(email);
        return ResponseEntity.ok(subscription);
    }

    @Operation(summary = "Delete subscription by email")
    @DeleteMapping("/{email}")
    public ResponseEntity<Boolean> deleteSubscriptionsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.deleteSubscriptionByEmail(email));
    }

}

