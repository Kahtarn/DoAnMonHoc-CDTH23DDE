package com.example.serverchodientu.dto.chat;


import java.sql.Timestamp;

public class InboxResponse {
    private Integer partnerId;
    private String partnerName;
    private String avatarUrl;
    private String lastMessage;
    private Timestamp time;
    private Integer roomId;

    public InboxResponse(Integer partnerId, String partnerName, String avatarUrl, String lastMessage, Timestamp time, Integer roomId) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.avatarUrl = avatarUrl;
        this.lastMessage = lastMessage;
        this.time = time;
        this.roomId = roomId;
    }

    public InboxResponse() {

    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}