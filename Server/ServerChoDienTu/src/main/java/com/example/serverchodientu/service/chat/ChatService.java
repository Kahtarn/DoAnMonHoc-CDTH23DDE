package com.example.serverchodientu.service.chat;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.chat.*;
import com.example.serverchodientu.entity.Chat;
import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.entity.Room;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.repository.ChatRepository;
import com.example.serverchodientu.repository.ProductRepository;
import com.example.serverchodientu.repository.RoomRepository;
import com.example.serverchodientu.repository.UserRepository;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository usersRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ProductRepository productRepository;

    public ChatService(ChatRepository chatRepository, UserRepository usersRepository, RoomRepository roomRepository, ProductRepository productRepository) {
        this.chatRepository = chatRepository;
        this.usersRepository = usersRepository;
        this.roomRepository = roomRepository;
        this.productRepository = productRepository;
    }

    public ApiResponse<String> sendMessage(SendChatRequest message) {
        // 1. Create Room Name (Sort IDs to match Android logic)
        List<String> ids = Arrays.asList(String.valueOf(message.getSenderId()), String.valueOf(message.getReceiverId()));
        Collections.sort(ids);
        String roomName = "chat_user_" + ids.get(0) + "_user_" + ids.get(1);

        // 2. Prepare Data for Firestore
        String messageId = message.getId();

        Map<String, Object> firestoreData = new HashMap<>();
        firestoreData.put("senderId", message.getSenderId());
        firestoreData.put("receiverId", message.getReceiverId());
//        firestoreData.put("productId", message.getProductId());
        firestoreData.put("content", message.getContent());
        firestoreData.put("isRevoke", false);
        firestoreData.put("createAt", new Date());

        Firestore db = FirestoreClient.getFirestore();

        try {
            User receiver = usersRepository.findById(message.getReceiverId()).orElse(null);
            User sender = usersRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            if (receiver != null && receiver.getFcmToken() != null) {
                // 3. Write to Firestore
                db.collection("chat_rooms")
                        .document(roomName)
                        .collection("messages")
                        .document(messageId)
                        .set(firestoreData);
                // 4. Save to MySQL
                saveToSqlDatabase(message, roomName);

                updateFirestoreInbox(sender, receiver, message.getContent(), roomName);

                // 5. Send FCM Notification
                // IMPORTANT: Use data payload instead of notification payload
                // This allows Android to handle the notification styling
                Message fcmMessage = Message.builder()
                        .setToken(receiver.getFcmToken())
                        .putData("title", sender.getFullName())           // Sender name as title
                        .putData("body", message.getContent())            // Message content
                        .putData("roomId", roomName)                      // Room ID for grouping
                        .putData("senderId", sender.getId().toString())   // Sender ID
                        .build();

                try {
                    String response = FirebaseMessaging.getInstance().send(fcmMessage);
                    System.out.println("Successfully sent message: " + response);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    return ApiResponse.error("Error sending FCM: " + e.getMessage());
                }
            }

            return ApiResponse.success("Sent to room: " + roomName);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("Error sending message: " + e.getMessage());
        }
    }

    private void updateFirestoreInbox(User sender, User receiver, String content, String roomName) {
        Firestore db = FirestoreClient.getFirestore();
        Date now = new Date();

        // Data cho người nhận (Partner là người gửi)
        Map<String, Object> receiverInbox = createInboxMap(sender, content, roomName, now);
        db.collection("inboxes").document(String.valueOf(receiver.getId()))
                .collection("conversations").document(String.valueOf(roomName))
                .set(receiverInbox, SetOptions.merge());

        // Data cho người gửi (Partner là người nhận)
        Map<String, Object> senderInbox = createInboxMap(receiver, content, roomName, now);
        db.collection("inboxes").document(String.valueOf(sender.getId()))
                .collection("conversations").document(String.valueOf(roomName))
                .set(senderInbox, SetOptions.merge());
    }

    private Map<String, Object> createInboxMap(User partner, String content, String roomName, Date time) {
        Map<String, Object> map = new HashMap<>();
        map.put("partnerId", partner.getId());
        map.put("partnerName", partner.getFullName());
        map.put("avatarUrl", partner.getAvatarUrl());
        map.put("lastMessage", content);
        map.put("time", time); // Trường này cực kỳ quan trọng để orderBy trên Android
        map.put("roomName", roomName);
        return map;
    }

    // Extracted method to handle SQL logic cleanly
    private void saveToSqlDatabase(SendChatRequest message, String roomName) {
        // Find existing room in both directions
        var roomOpt = roomRepository.findRoomBySenderId_IdAndReceiverId_Id(message.getSenderId(), message.getReceiverId());
        if (roomOpt.isEmpty()) {
            roomOpt = roomRepository.findRoomBySenderId_IdAndReceiverId_Id(message.getReceiverId(), message.getSenderId());
        }
        User senderUser = usersRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiverUser = usersRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Room room;
        if (roomOpt.isEmpty()) {
            room = new Room();
            room.setName(roomName);
            room.setSenderId(senderUser);
            room.setReceiverId(receiverUser);
            roomRepository.save(room);
        } else {
            room = roomOpt.get();
        }

        // Save Chat Message to SQL
//        Product p = productRepository.findById(message.getProductId()).orElse(null);

        Chat c = new Chat();
        c.setRoomId(room);
        c.setContent(message.getContent());
//        c.setProductId(p);
        c.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        c.setSender(senderUser);

        chatRepository.save(c);
    }


    public ApiResponse<String> saveFCMToken(FCMTokenRequest request) {
        try {
            User user = request.getUser();
            String fcm_token = request.getToken();
            user.setFcmToken(fcm_token);
            usersRepository.save(user);
            return ApiResponse.success("Luu token moi thanh cong");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public String createFirebaseToken(Integer userId) {
        // Tạo Custom Token dựa trên ID người dùng từ MySQL
        try {
            String customToken = FirebaseAuth.getInstance().createCustomToken(String.valueOf(userId));
            return customToken;
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<String> revokeMessage(ChatRevokeRequest request) {
        // Update trường isRevoke = true tại đúng ID đó
        try {
            FirestoreClient.getFirestore()
                    .collection("chat_rooms")
                    .document(request.getRoomName())
                    .collection("messages")
                    .document(request.getMessageId()) // <--- ID vẫn y nguyên
                    .update("isRevoke", true);
        } catch (Exception e) {
            return ApiResponse.error("Thu hoi tin nhan that bai, " + e.getMessage());
        }
        return ApiResponse.success("Thu hoi tin nhan thanh cong");
    }

}
