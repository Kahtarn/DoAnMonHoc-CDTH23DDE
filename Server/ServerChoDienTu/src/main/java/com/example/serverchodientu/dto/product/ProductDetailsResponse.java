package com.example.serverchodientu.dto.product;

import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.entity.ProductImage;

import java.util.List;

public class ProductDetailsResponse {
    private Product product;
    private List<String> images;

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public ProductDetailsResponse(Product product, List<ProductImage> productImages) {
        this.product = product;
        this.images = productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(java.util.stream.Collectors.toList());
    }
}
