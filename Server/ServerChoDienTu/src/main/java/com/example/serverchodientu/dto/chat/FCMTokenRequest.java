package com.example.serverchodientu.dto.chat;

import com.example.serverchodientu.entity.User;

public class FCMTokenRequest {
    User user;
    String token;

    public FCMTokenRequest(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public FCMTokenRequest() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
