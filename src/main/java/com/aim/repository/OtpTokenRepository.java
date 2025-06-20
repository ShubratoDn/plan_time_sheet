package com.aim.repository;

import com.aim.entity.OtpToken;
import com.aim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserAndUsedOrderByCreatedAtDesc(User user, boolean used);
    Optional<OtpToken> findTopByUserOrderByCreatedAtDesc(User user);
    Optional<OtpToken> findByUserAndOtpAndUsed(User user, String otp, boolean used);
} 