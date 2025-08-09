package com.newsletter;

import com.newsletter.model.Subscription;
import com.newsletter.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository repository;

    @Test
    void shouldCreateSubscription() throws Exception {
        String json = """
                {
                  "firstName": "Elton",
                  "lastName": "Saraci",
                  "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Elton"))
                .andExpect(jsonPath("$.lastName").value("Saraci"));

        assertThat(repository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        String json = """
                {
                  "firstName": "Elton",
                  "lastName": "Saraci",
                  "email": "invalid-email"
                }
                """;

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        repository.save(new Subscription("dup@example.com", "Elton", "Saraci"));

        String json = """
                {
                  "firstName": "Elton",
                  "lastName": "Saraci",
                  "email": "dup@example.com"
                }
                """;

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email already subscribed: dup@example.com"));
    }

    @Test
    void shouldRejectEmptyFirstName() throws Exception {
        String json = """
            {
              "firstName": "",
              "lastName": "Saraci",
              "email": "test@example.com"
            }
            """;

        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.fieldErrors.firstName").value("First name must not be blank"));
    }

    @Test
    void shouldFetchAllSubscriptions() throws Exception {
        repository.deleteAll();
        repository.save(new Subscription("john@example.com", "John", "Doe"));
        repository.save(new Subscription("jane@example.com", "Jane", "Smith"));
        mockMvc.perform( org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/newsletter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
    }

    @Test
    void shouldFetchSubscriptionByEmail() throws Exception {
        repository.deleteAll();
        repository.save(new Subscription("john@example.com", "John", "Doe"));
        repository.save(new Subscription("jane@example.com", "Jane", "Smith"));
        mockMvc.perform( org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/newsletter/jane@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void shouldDeleteSubscriptionByEmail() throws Exception {
        repository.deleteAll();
        repository.save(new Subscription("john@example.com", "John", "Doe"));
        repository.save(new Subscription("jane@example.com", "Jane", "Smith"));
        mockMvc.perform(delete("/api/newsletter/john@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(repository.findByEmail("john@example.com")).isNotPresent();
    }


}
