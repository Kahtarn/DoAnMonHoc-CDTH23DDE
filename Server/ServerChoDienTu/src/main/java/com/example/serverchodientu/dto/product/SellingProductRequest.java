package com.example.serverchodientu.dto.product;


public class SellingProductRequest {
    private Integer userId;
    public SellingProductRequest(Integer user_id) {
        this.userId = user_id;

    }

    public SellingProductRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer user_id) {
        this.userId = user_id;
    }

}
