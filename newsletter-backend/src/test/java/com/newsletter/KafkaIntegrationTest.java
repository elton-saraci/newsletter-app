package com.newsletter;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.newsletter.kafka.SubscriptionEventProducer;
import com.newsletter.model.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = { "newsletter-subscriptions" })
@EnableKafka
public class KafkaIntegrationTest {

    private static final String TOPIC = "newsletter-subscriptions";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private SubscriptionEventProducer producer;

    private KafkaMessageListenerContainer<String, String> container;
    private BlockingQueue<ConsumerRecord<String, String>> records;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "testGroup", "true", embeddedKafkaBroker);
        consumerProps.put("auto.offset.reset", "earliest");
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        ContainerProperties containerProperties = new ContainerProperties(TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        records = new LinkedBlockingQueue<>();

        container.setupMessageListener((MessageListener<String, String>) record -> {
            records.add(record);
        });

        container.start();
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.stop();
        }
    }

    @Test
    void shouldSendFullSubscriptionAsKafkaMessage() throws Exception {
        Subscription subscription = new Subscription("test@example.com", "Elton", "Saraci");

        producer.sendSubscriptionEvent(subscription);

        ConsumerRecord<String, String> received = records.take();

        assertThat(received).isNotNull();
        assertThat(received.topic()).isEqualTo(TOPIC);
        assertThat(received.key()).isEqualTo(subscription.getEmail());

        // Deserialize JSON string to Subscription object
        Subscription receivedSubscription = objectMapper.readValue(received.value(), Subscription.class);

        assertThat(receivedSubscription.getEmail()).isEqualTo(subscription.getEmail());
        assertThat(receivedSubscription.getFirstName()).isEqualTo(subscription.getFirstName());
        assertThat(receivedSubscription.getLastName()).isEqualTo(subscription.getLastName());
        assertThat(receivedSubscription.getCreatedAt()).isNotNull();
    }
}
