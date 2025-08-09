package com.newsletter.kafka;

import com.newsletter.model.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SubscriptionEventProducer {

    private static final String TOPIC = "newsletter-subscriptions";

    private final KafkaTemplate<String, Subscription> kafkaTemplate;

    public SubscriptionEventProducer(KafkaTemplate<String, Subscription> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendSubscriptionEvent(Subscription subscription) {
        CompletableFuture.runAsync(() -> {
            try {
                kafkaTemplate.send(TOPIC, subscription.getEmail(), subscription);
            } catch (Exception ex) {
                log.error("Error occurred while sending Kafka event: {}", ex.getMessage(), ex);
            }
        });
    }


}
