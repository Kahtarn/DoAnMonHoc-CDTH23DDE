package com.example.serverchodientu.service.product;


import com.example.serverchodientu.dto.product.PostProductRequest;
import com.example.serverchodientu.dto.product.ProductDetailsResponse;
import com.example.serverchodientu.entity.*;
import com.example.serverchodientu.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final ProductImageRepository productImageRepo;
    private final CategoriesRepository categoriesRepo;
    private final FavoriteRepository favoriteRepo;

    public ProductService(ProductRepository productRepo, ProductImageRepository productImageRepo, UserRepository userRepo, CategoriesRepository categoriesRepo, FavoriteRepository favoriteRepo) {
        this.productRepo = productRepo;
        this.productImageRepo = productImageRepo;
        this.userRepo = userRepo;
        this.categoriesRepo = categoriesRepo;
        this.favoriteRepo = favoriteRepo;
    }

    public List<Product> getAll() {
        return productRepo.findAllByOrderByCreateAtDesc(0);
    }

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

    @Transactional
    public Product updateProduct(Integer productId, String currentEmail, PostProductRequest request) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (!product.getSeller().getEmail().equals(currentEmail)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa sản phẩm này!");
        }

        if (request.getCategoryId() != null) {
            Categories category = categoriesRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            product.setCategory(category);
        }

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return productRepo.save(product);
    }

    public List<Product> getByCategoryId(Integer categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    public List<Product> getMySellingProducts(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + email));

        return productRepo.findBySellerIdAndStatusOrderByCreateAtDesc(user.getId(), 0);
    }

    public List<Product> getMySoldProducts(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + email));

        return productRepo.findBySellerIdAndStatusOrderByCreateAtDesc(user.getId(), 1);
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
        Product product = new Product();
        product.setSeller(seller);
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

    @Transactional
    public void markAsSold(Integer productId, String currentEmail) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (!product.getSeller().getEmail().equals(currentEmail)) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này!");
        }

        product.setStatus(1);
    }

    @Transactional
    public void setFavorite(Integer productId, String email) {
        Product p = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Khong tim thay product"));
        User u = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Khong tim thay san pham"));

        Optional<Favorite> favoriteOpt = favoriteRepo.findFavoriteByUserAndProduct(u, p);

        if (favoriteOpt.isPresent()) {
            favoriteRepo.delete(favoriteOpt.get());
        } else {
            Favorite favorite = new Favorite();
            favorite.setProduct(p);
            favorite.setUser(u);
            favorite.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));
            favoriteRepo.save(favorite);
        }
    }

    public List<Product> getFavorite(String email) {
        User u = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Khong tim thay san pham"));

        List<Product> listProduct = favoriteRepo.findFavoriteProductsByUserId(u.getId());

        if (listProduct.isEmpty()) {
            return Collections.emptyList();
        } else {
            return listProduct;
        }
    }

    @Transactional
    public boolean isFavorite(Integer productId, String email) {
        // ... tìm p và u ...
        Product p = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Khong tim thay product"));
        User u = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Khong tim thay san pham"));
        Optional<Favorite> fav = favoriteRepo.findFavoriteByUserAndProduct(u, p);
        if (fav.isPresent()) {
            favoriteRepo.delete(fav.get());
            return false; // Trạng thái hiện tại: Đã bỏ thích
        } else {
            Favorite newFav = new Favorite();
            // ... set p, u, createAt ...
            favoriteRepo.save(newFav);
            return true; // Trạng thái hiện tại: Đã thích
        }
    }

}
