package com.example.serverchodientu.dto.favorite;

import com.example.serverchodientu.entity.Product;

public class FavoriteRespond {
    Product product;

    public FavoriteRespond(Product product) {
        this.product = product;
    }

    public FavoriteRespond() {

    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
