package com.example.serverchodientu.dto.user.privateUser;

import java.sql.Timestamp;

public class ProfileUserResponse {
    private String fullName;
    private String email;
    private String phone;
    private Boolean gender;
    private String provinceName;
    private String wardName;
    private Timestamp createAt;
    private String avatarUrl;

    public ProfileUserResponse(String fullName, String email, String phone, Boolean gender, String provinceName, String wardName, Timestamp createAt, String avatarUrl) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.provinceName = provinceName;
        this.wardName = wardName;
        this.createAt = createAt;
        this.avatarUrl = avatarUrl;
    }

    public ProfileUserResponse() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
