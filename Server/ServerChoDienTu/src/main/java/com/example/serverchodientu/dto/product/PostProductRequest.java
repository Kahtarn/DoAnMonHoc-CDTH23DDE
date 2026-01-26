package com.example.serverchodientu.dto.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class PostProductRequest {
    private Integer sellerId;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private MultipartFile thumbnailUrl;
    private List<MultipartFile> imageUrl;

    public PostProductRequest(Integer sellerId, Integer categoryId, String title, String description, BigDecimal price, MultipartFile thumbnailUrl, List<MultipartFile> imageUrl) {
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

    public MultipartFile getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(MultipartFile thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<MultipartFile> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<MultipartFile> imageUrl) {
        this.imageUrl = imageUrl;
    }
}
