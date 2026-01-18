package com.example.serverchodientu.service.product;

import com.example.serverchodientu.dto.product.PostProductRequest;
import com.example.serverchodientu.dto.product.ProductDetailsResponse;
import com.example.serverchodientu.entity.Categories;
import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.entity.ProductImage;
import com.example.serverchodientu.entity.User;
import com.example.serverchodientu.repository.CategoriesRepository;
import com.example.serverchodientu.repository.ProductImageRepository;
import com.example.serverchodientu.repository.ProductRepository;
import com.example.serverchodientu.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final ProductImageRepository productImageRepo;
    private final CategoriesRepository categoriesRepo;

    public ProductService(ProductRepository productRepo, ProductImageRepository productImageRepo, UserRepository userRepo, CategoriesRepository categoriesRepo) {
        this.productRepo = productRepo;
        this.productImageRepo = productImageRepo;
        this.userRepo = userRepo;
        this.categoriesRepo = categoriesRepo;
    }

    //Lay danh sach san pham
    public List<Product> getAll() {
        return productRepo.findAllByOrderByCreateAtDesc(0);
    }

    //loc tim kiem theo gia, thoi gian
    public List<Product> searchProducts(String name, String sortType) {
        Sort sort;
        if ("price_asc".equalsIgnoreCase(sortType)) {
            sort = Sort.by("price").ascending();
        } else if ("price_desc".equalsIgnoreCase(sortType)) {
            sort = Sort.by("price").descending();
        } else {
            sort = Sort.by("createAt").descending();
        }

        return productRepo.findByTitleContainingIgnoreCaseAndStatus(name, 0, sort);
    }

    public ProductDetailsResponse getProductDetail(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        List<ProductImage> images = productImageRepo.findByProductId_Id(id);
        return new ProductDetailsResponse(product, images);
    }

    @Transactional
    public Product createProduct(PostProductRequest request) {
        User seller = userRepo.findById(request.getSellerId()).orElseThrow();
        Categories cate = categoriesRepo.findById(request.getCategoryId()).orElseThrow();
        // 1. Lưu thông tin sản phẩm vào bảng product
        Product product = new Product();
        product.setSeller(seller); // Nhớ map đúng object User nếu cần
        product.setCategory(cate);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setStatus(0);
        product.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));

        Product savedProduct = productRepo.save(product);

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            List<ProductImage> images = request.getImageUrl().stream().map(url -> {
                ProductImage img = new ProductImage();
                img.setProductId(savedProduct);
                img.setImageUrl(url);
                return img;
            }).collect(Collectors.toList());

            productImageRepo.saveAll(images);
        }

        return savedProduct;
    }

    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
        productImageRepo.deleteByProductId(id);
        productRepo.deleteById(id);
    }

}
