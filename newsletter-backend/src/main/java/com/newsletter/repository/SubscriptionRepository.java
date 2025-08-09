package com.newsletter.repository;


import com.newsletter.model.Subscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM Subscription s WHERE s.email = :email")
    @Transactional
    int deleteByEmail(@Param("email") String email);

}

