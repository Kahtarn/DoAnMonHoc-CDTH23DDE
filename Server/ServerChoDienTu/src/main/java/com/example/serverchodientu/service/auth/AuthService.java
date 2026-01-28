package com.example.serverchodientu.service.auth;

import com.example.serverchodientu.dto.auth.LoginRequest;
import com.example.serverchodientu.dto.auth.RegisterRequest;
import com.example.serverchodientu.dto.auth.ResetPasswordRequest;
import com.example.serverchodientu.entity.EmailVerification;
import com.example.serverchodientu.entity.RefreshToken;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.repository.EmailVerificationRepository;
import com.example.serverchodientu.repository.RefreshTokenRepository;
import com.example.serverchodientu.repository.UserRepository;
import com.example.serverchodientu.service.JwtService;
import com.example.serverchodientu.service.MailService;
import com.example.serverchodientu.service.chat.ChatService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {
    @Value("${jwt.refreshExperation}")
    private long refreshExpirationMs;

    private final UserRepository userRepo;
    private final EmailVerificationRepository emailVerificationRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtService jwtService;
    private final MailService mailService;
    private final ChatService chatService;

    public AuthService(UserRepository userRepo,
                       EmailVerificationRepository emailVerificationRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepo,
                       MailService mailService, ChatService chatService) {
        this.userRepo = userRepo;
        this.emailVerificationRepo = emailVerificationRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.mailService = mailService;
        this.chatService = chatService;
    }

    @Transactional
    public String register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        EmailVerification ev = emailVerificationRepo.findByEmailAndOtpCode(request.getEmail(), request.getOtpCode())
                .orElseThrow(() -> new RuntimeException("Mã OTP không chính xác!"));

        if (ev.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }

        User u = new User();
        u.setEmail(request.getEmail());
        u.setUsername(request.getUsername());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setFullName(request.getFullName());
        u.setPhone(request.getPhone());
        u.setProvinceName(request.getProvinceName());
        u.setWardName(request.getWardName());
        u.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));
        u.setGender(request.isGender());
        userRepo.save(u);
        emailVerificationRepo.delete(ev);

        return "Đăng ký tài khoản thành công!";
    }

    public boolean isEmailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    @Transactional
    public Map<String, Object> login(LoginRequest request) {
        User u = userRepo.findByEmailOrUsername(request.getEmailOrUsername(), request.getEmailOrUsername())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc email không tồn tại!"));

        if (!passwordEncoder.matches(request.getPassword(), u.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        String accessToken = jwtService.generateAccessToken(u);
        String refreshTokenStr = jwtService.generateRefreshToken();
        String fcmToken = chatService.createFirebaseToken(u.getId());
        saveRefreshToken(u, refreshTokenStr);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshTokenStr,
                "firebaseToken", fcmToken,
                "userId", u.getId(),
                "username", u.getUsername(),
                "fullName", u.getFullName()
        );
    }

    private void saveRefreshToken(User user, String tokenStr) {
        refreshTokenRepo.deleteByUserId(user);

        RefreshToken rt = new RefreshToken();
        rt.setUserId(user);
        rt.setToken(tokenStr);
        rt.setExpiryDate(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000));

        refreshTokenRepo.save(rt);
    }

    @Transactional
    public Map<String, Object> refreshToken(String requestRefreshToken) {
        RefreshToken rt = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token không tồn tại trong hệ thống!"));
        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(rt);
            throw new RuntimeException("Refresh Token đã hết hạn. Vui lòng đăng nhập lại!");
        }
        User user = rt.getUserId();
        String newAccessToken = jwtService.generateAccessToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", requestRefreshToken
        );
    }

    @Transactional
    public void sendForgotPasswordOtp(String email) {
        if (!userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email này chưa được đăng ký tài khoản!");
        }
        mailService.sendOtpToVerifyEmail(email);
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        EmailVerification ev = emailVerificationRepo.findByEmailAndOtpCode(request.getEmail(), request.getOtpCode())
                .orElseThrow(() -> new RuntimeException("Mã OTP không chính xác!"));

        if (ev.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }

        User user = userRepo.findByEmailOrUsername(request.getEmail(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        emailVerificationRepo.delete(ev);

        return "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.";
    }

    @Transactional
    public String logout() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmailOrUsername(email, email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        refreshTokenRepo.deleteByUserId(user);

        return "Đăng xuất thành công!";
    }

}