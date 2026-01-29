package com.example.serverchodientu.dto.user.privateUser;

public class EditProfileRequest {
    private String fullName;
    private String provinceName;
    private String wardName;
    private Boolean gender;

    public EditProfileRequest(String fullName, String provinceName, String wardName, Boolean gender) {
        this.fullName = fullName;
        this.provinceName = provinceName;
        this.wardName = wardName;
        this.gender = gender;
    }

    public EditProfileRequest() {
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

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }
}
