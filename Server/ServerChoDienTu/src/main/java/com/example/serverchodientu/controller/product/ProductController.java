package com.example.serverchodientu.controller.product;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.product.PostProductRequest;
import com.example.serverchodientu.dto.product.ProductDetailsResponse;
import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<ProductDetailsResponse>> getProductDetail(@PathVariable Integer id) {
        ProductDetailsResponse data = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<String>> postProduct(@RequestBody PostProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng bài bán thành công!"));
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

}
