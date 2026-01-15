package com.example.serverchodientu.dto.product;

import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.entity.ProductImage;

import java.util.List;

public class ProductDetailsResponse {
    private Product product;
    private List<String> imageUrl;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<String> getImages() {
        return imageUrl;
    }

    public void setImages(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ProductDetailsResponse(Product product, List<ProductImage> productImages) {
        this.product = product;
        this.imageUrl = productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(java.util.stream.Collectors.toList());
    }
}
