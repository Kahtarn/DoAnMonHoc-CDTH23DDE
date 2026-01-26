package com.example.serverchodientu.controller.product;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.product.PostProductRequest;
import com.example.serverchodientu.dto.product.ProductDetailsResponse;
import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getAll")
    //Lay danh sach bai ban
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {
        List<Product> data = productService.getAll();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Product>>> search(@RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "sort", defaultValue = "newest") String sort) {
        List<Product> data = productService.searchProducts(name, sort);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<ProductDetailsResponse>> getProductDetail(@PathVariable Integer id) {
        ProductDetailsResponse detail = productService.getProductDetail(id);

        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(@RequestParam("categoryId") Integer categoryId) {
        List<Product> data = productService.getByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Product>> postProduct(@ModelAttribute PostProductRequest request) {
        if (request.getThumbnailUrl() == null || request.getThumbnailUrl().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ảnh đại diện sản phẩm là bắt buộc!"));
        }

        Product savedProduct = productService.createPostProduct(request);

        return ResponseEntity.ok(ApiResponse.ok(savedProduct));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.ok("Đã xóa bài viết thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/my-selling")
    public ResponseEntity<ApiResponse<List<Product>>> getMySellingProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        List<Product> myProduct = productService.getMySellingProducts(currentEmail);
        return ResponseEntity.ok(ApiResponse.ok(myProduct));
    }

    @GetMapping("/my-sold")
    public ResponseEntity<ApiResponse<List<Product>>> getMySoldProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        List<Product> myProduct = productService.getMySoldProducts((currentEmail));
        return ResponseEntity.ok(ApiResponse.ok(myProduct));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Integer id,
            @RequestBody PostProductRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();

            Product updatedProduct = productService.updateProduct(id, currentEmail, request);

            return ResponseEntity.ok(ApiResponse.ok(updatedProduct));
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("quyền") ? HttpStatus.FORBIDDEN : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/mark-as-sold")
    public ResponseEntity<ApiResponse<String>> markAsSold(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        productService.markAsSold(id, currentEmail);

        return ResponseEntity.ok(ApiResponse.ok("Đã đánh dấu sản phẩm là đã bán!"));
    }
}
