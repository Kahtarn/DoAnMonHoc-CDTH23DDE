package com.example.serverchodientu.service.product;

import com.example.serverchodientu.dto.product.EditPostRequest;
import com.example.serverchodientu.dto.product.PostProductRequest;
import com.example.serverchodientu.dto.product.ProductDetailsResponse;
import com.example.serverchodientu.dto.product.SetFavoriteRespond;
import com.example.serverchodientu.entity.*;
import com.example.serverchodientu.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        return productRepo.findAllByStatusOrderByCreateAtDesc(0);
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
    public Product updateProduct(Integer productId, String currentEmail, EditPostRequest request) {
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
        return productRepo.findByCategoryIdAndStatus(categoryId, 0);
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
    public Product createPostProduct(PostProductRequest request, String email) {
        User seller = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Categories cate = categoriesRepo.findById(request.getCategoryId()).orElseThrow();
        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(cate);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(0);
        product.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));


        if (request.getThumbnailUrl() != null) {
            String fileName = saveToDisk(request.getThumbnailUrl());
            product.setThumbnailUrl("/uploads/" + fileName);
        }
        Product savedProduct = productRepo.save(product);

        if (request.getImageUrl() != null) {
            List<ProductImage> images = request.getImageUrl().stream().map(file -> {
                String fileName = saveToDisk(file);
                ProductImage img = new ProductImage();
                img.setProductId(savedProduct);
                img.setImageUrl("/uploads/" + fileName);
                return img;
            }).collect(Collectors.toList());

            productImageRepo.saveAll(images);
        }

        return savedProduct;
    }

    private String saveToDisk(MultipartFile file) {
        try {
            Path root = Paths.get("uploads");
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = root.resolve(fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file: " + e.getMessage());
        }
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
    public SetFavoriteRespond setFavorite(Integer productId, String email) {
        Product p = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy product"));
        User u = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy product"));

        Optional<Favorite> favoriteOpt = favoriteRepo.findFavoriteByUserAndProduct(u, p);
        SetFavoriteRespond setFavoriteRespond = new SetFavoriteRespond();
        if (favoriteOpt.isPresent()) {
            favoriteRepo.delete(favoriteOpt.get());
            setFavoriteRespond.setIsFavorite(false);
            setFavoriteRespond.setMessage("Xoá yêu thích thành công!");
            return setFavoriteRespond;
        } else {
            Favorite favorite = new Favorite();
            favorite.setProduct(p);
            favorite.setUser(u);
            favorite.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));
            favoriteRepo.save(favorite);

            setFavoriteRespond.setIsFavorite(true);
            setFavoriteRespond.setMessage("Thêm yêu thích thành công!");
            return setFavoriteRespond;

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
        Product p = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Khong tim thay product"));
        User u = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Khong tim thay san pham"));
        Optional<Favorite> fav = favoriteRepo.findFavoriteByUserAndProduct(u, p);
        if (fav.isEmpty()) {
            return false; // Trạng thái hiện tại: Đã bỏ thích
        } else {
            return true; // Trạng thái hiện tại: Đã thích
        }
    }

    public List<Product> getPublicSellingProducts(Integer userId) {
        return productRepo.findBySellerIdAndStatusOrderByCreateAtDesc(userId, 0);
    }

    public List<Product> getPublicSoldProducts(Integer userId) {
        return productRepo.findBySellerIdAndStatusOrderByCreateAtDesc(userId, 1);
    }

}
