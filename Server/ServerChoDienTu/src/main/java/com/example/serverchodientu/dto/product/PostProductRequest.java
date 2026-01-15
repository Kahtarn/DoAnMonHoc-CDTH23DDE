package com.example.serverchodientu.dto.product;

import java.math.BigDecimal;
import java.util.List;

public class PostProductRequest {
    private Integer sellerId;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private String thumbnailUrl;
    private List<String> imageUrl;

    public PostProductRequest(Integer sellerId, Integer categoryId, String title, String description, BigDecimal price, String thumbnailUrl, List<String> imageUrl) {
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
}
