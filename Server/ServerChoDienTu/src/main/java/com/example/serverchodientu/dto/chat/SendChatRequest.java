package com.example.serverchodientu.dto.chat;

import java.sql.Timestamp;


public class SendChatRequest {
    private String id;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Integer productId;
    private Timestamp createdAt;


    public SendChatRequest(String id, Integer senderId, Integer receiverId, String content, Integer productId, Timestamp createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public SendChatRequest() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
