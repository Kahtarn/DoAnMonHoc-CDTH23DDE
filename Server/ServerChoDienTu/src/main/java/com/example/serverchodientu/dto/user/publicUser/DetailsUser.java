package com.example.serverchodientu.dto.user.publicUser;

import java.sql.Timestamp;

public class DetailsUser {
    private String fullName;
    private String provinceName;
    private String wardName;
    private String phone;
    private String email;
    private boolean gender;
    private Timestamp createAt;

    public DetailsUser(String fullName, String provinceName, String wardName, String phone, String email, boolean gender, Timestamp createAt) {
        this.fullName = fullName;
        this.provinceName = provinceName;
        this.wardName = wardName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.createAt = createAt;
    }

    public DetailsUser() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }
}
