package com.example.serverchodientu.dto.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class EditPostRequest {
    private String title;
    private BigDecimal price;
    private String description;
    private Integer categoryId;
    private MultipartFile thumbnailUrl;
    private List<MultipartFile> imageUrl;
    private List<String> existingImages;

    public EditPostRequest() {
    }

    public EditPostRequest(String title, BigDecimal price, String description, Integer categoryId, MultipartFile thumbnailUrl, List<MultipartFile> imageUrl, List<String> existingImages) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
        this.existingImages = existingImages;
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

    public List<String> getExistingImages() {
        return existingImages;
    }

    public void setExistingImages(List<String> existingImages) {
        this.existingImages = existingImages;
    }
}
