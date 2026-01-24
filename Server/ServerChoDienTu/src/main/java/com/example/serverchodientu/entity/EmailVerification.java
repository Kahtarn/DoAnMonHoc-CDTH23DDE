package com.example.serverchodientu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @JsonIgnoreProperties
    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Column(name = "otp_expiry", nullable = false)
    private LocalDateTime otpExpiry;


    @Column(name = "otp_count", nullable = false)
    private Integer otpCount;

    @Column(name = "otp_last_send", nullable = false)
    private LocalDateTime otpLastSend;

    public EmailVerification(Integer id, String email, String otpCode, LocalDateTime otpExpiry, Integer otpCount, LocalDateTime otpLastSend) {
        this.id = id;
        this.email = email;
        this.otpCode = otpCode;
        this.otpExpiry = otpExpiry;
        this.otpCount = otpCount;
        this.otpLastSend = otpLastSend;
    }

    public EmailVerification() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public Integer getOtpCount() {
        return otpCount;
    }

    public void setOtpCount(Integer otpCount) {
        this.otpCount = otpCount;
    }

    public LocalDateTime getOtpLastSend() {
        return otpLastSend;
    }

    public void setOtpLastSend(LocalDateTime otpLastSend) {
        this.otpLastSend = otpLastSend;
    }
}
