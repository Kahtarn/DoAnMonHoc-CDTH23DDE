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
    public void markAsSold(Integer productId, Integer currentUserId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (!product.getSeller().getId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này!");
        }

        product.setStatus(1);
        productRepo.save(product);
    }

}
