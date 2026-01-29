package com.example.serverchodientu.dto.chat;


public class ChatRevokeRequest {
    String roomName;
    String messageId;
    Boolean isRevoke = true;

    public ChatRevokeRequest(String roomName, String messageId, Boolean isRevoke) {
        this.roomName = roomName;
        this.messageId = messageId;
        this.isRevoke = isRevoke;
    }

    public ChatRevokeRequest() {

    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Boolean getRevoke() {
        return isRevoke;
    }

    public void setRevoke(Boolean revoke) {
        isRevoke = revoke;
    }
}


