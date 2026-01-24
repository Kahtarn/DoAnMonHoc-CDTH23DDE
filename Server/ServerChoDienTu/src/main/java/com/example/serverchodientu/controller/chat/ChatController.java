package com.example.serverchodientu.controller.chat;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.chat.*;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.repository.UserRepository;
import com.example.serverchodientu.service.chat.ChatService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;
//    request mau
//    {
//        "senderId" : 2,
//            "receiverId" : 1,
//            "productId" : 2,
//            "content" : "hang nay con khong?",

    /// /   "createAt" : "2000-2025-12-30 17:06:41-01"
//    }
    @Transactional
    @PostMapping("/send-private")
    public ApiResponse<String> sendFirestoreMessage(@RequestBody SendChatRequest message) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();
            User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException(("Khong tim thay user.")));
            message.setSenderId(user.getId());

            return chatService.sendMessage(message);
        } catch (Exception e) {
            return ApiResponse.error("Loi khi gui tin nhan: " + e.getMessage());
        }
    }

    // goi cai nay o home
    @PostMapping("/set-fcm-token")
    public ApiResponse<String> sendFCMToken(@RequestBody FCMTokenRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();
            User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException(("Khong tim thay user.")));
            request.setUser(user);
            return chatService.saveFCMToken(request);
        } catch (Exception e) {
            return ApiResponse.error("Loi khi nhan token thong bao");
        }
    }


    @PostMapping("/revoke-message")
    public ApiResponse<String> unsendMessage(@RequestBody ChatRevokeRequest request) {
        try {
            return chatService.revokeMessage(request);
        } catch (Exception e) {
            return ApiResponse.error("Loi thu hoi tin nhan");
        }
    }

}
