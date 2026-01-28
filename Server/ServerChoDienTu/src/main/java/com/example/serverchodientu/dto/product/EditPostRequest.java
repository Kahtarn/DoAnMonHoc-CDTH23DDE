package com.example.serverchodientu.dto.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class EditPostRequest {
    private String title;
    private BigDecimal price;
    private String description;
    private Integer categoryId;

    public EditPostRequest() {
    }

    public EditPostRequest(String title, BigDecimal price, String description, Integer categoryId) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
