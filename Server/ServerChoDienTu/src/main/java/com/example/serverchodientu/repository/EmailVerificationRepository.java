package com.example.serverchodientu.repository;

import com.example.serverchodientu.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {
    Optional<EmailVerification> findByEmailAndOtpCode(String email, String otpCode);
    Optional<EmailVerification> findByEmail(String email);
}
