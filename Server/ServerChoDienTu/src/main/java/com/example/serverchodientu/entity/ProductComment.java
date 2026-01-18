package com.example.serverchodientu.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class ProductComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ProductComment parent;

    @Column(name = "create_at", insertable = false, updatable = false)
    private Timestamp createAt;

    public ProductComment(Integer id, Product product, User user, String content, ProductComment parent, Timestamp createAt) {
        this.id = id;
        this.product = product;
        this.user = user;
        this.content = content;
        this.parent = parent;
        this.createAt = createAt;
    }

    public ProductComment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ProductComment getParent() {
        return parent;
    }

    public void setParent(ProductComment parent) {
        this.parent = parent;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }
}
