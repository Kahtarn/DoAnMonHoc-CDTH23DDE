package com.example.serverchodientu.dto.user;

import org.springframework.web.multipart.MultipartFile;

public class UpdateAvatarRequest {
    private MultipartFile avatarUrl;

    public UpdateAvatarRequest(MultipartFile avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UpdateAvatarRequest() {
    }

    public MultipartFile getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(MultipartFile avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}