package com.example.serverchodientu.service;

import com.example.serverchodientu.entity.EmailVerification;
import com.example.serverchodientu.repository.EmailVerificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepo;

    public MailService(JavaMailSender mailSender, EmailVerificationRepository emailVerificationRepo) {
        this.mailSender = mailSender;
        this.emailVerificationRepo = emailVerificationRepo;
    }

    private String generateOtp() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }
    public void sendOtpToVerifyEmail(String email) {
        String otp = generateOtp();
        LocalDateTime now = LocalDateTime.now();

        EmailVerification verification = emailVerificationRepo.findByEmail(email)
                .orElse(new EmailVerification());

        if (verification.getId() != null) {
            LocalDateTime lastSend = verification.getOtpLastSend();

            if (lastSend != null && lastSend.toLocalDate().isEqual(now.toLocalDate())) {
                if (verification.getOtpCount() >= 5) {
                    throw new RuntimeException("Bạn đã gửi quá 5 mã OTP trong hôm nay. Vui lòng quay lại vào ngày mai!");
                }
                verification.setOtpCount(verification.getOtpCount() + 1);
            } else {
                verification.setOtpCount(1);
            }
        } else {
            verification.setEmail(email);
            verification.setOtpCount(1);
        }

        verification.setOtpCode(otp);
        verification.setOtpExpiry(now.plusMinutes(5));
        verification.setOtpLastSend(now);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mã OTP Xác Thực Email Của App Chợ Điện Tử");
        message.setText("Mã OTP xác thực của bạn là " + otp + ". Mã có hiệu lực 5 trong vòng 5 phút.");
        mailSender.send(message);
        emailVerificationRepo.save(verification);

    }
}
