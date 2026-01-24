package com.example.serverchodientu.dto.product;

public class SoldProductRequest {
    private Integer userId;

    public SoldProductRequest(Integer user_id) {
        this.userId = user_id;

    }
    public SoldProductRequest() {

    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer user_id) {
        this.userId = user_id;
    }

}