package com.newsletter.service;

import com.newsletter.dto.SubscriptionRequest;
import com.newsletter.exception.InvalidEmailException;
import com.newsletter.kafka.SubscriptionEventProducer;
import com.newsletter.model.Subscription;
import com.newsletter.repository.SubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionEventProducer eventProducer;

    public Subscription subscribe(SubscriptionRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new InvalidEmailException("Email already subscribed: " + request.getEmail());
        });
        Subscription subscription = new Subscription(request.getEmail(), request.getFirstName(), request.getLastName());
        Subscription saved = repository.save(subscription);
        eventProducer.sendSubscriptionEvent(saved);
        return saved;
    }

    public List<Subscription> getAllSubscriptions() {
        return repository.findAll();
    }

    public Subscription getSubscriptionByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public boolean deleteSubscriptionByEmail(String email) {
        return repository.deleteByEmail(email) == 1;
    }

}
