package com.example.serverchodientu.controller.product;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.dto.product.EditPostRequest;
import com.example.serverchodientu.dto.product.PostProductRequest;
import com.example.serverchodientu.dto.product.ProductDetailsResponse;
import com.example.serverchodientu.dto.product.SetFavoriteRespond;
import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/product/")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getAll")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Product savedProduct = productService.createPostProduct(request, currentEmail);

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
    public ResponseEntity<ApiResponse<String>> update(
            @PathVariable Integer id,
            @RequestBody EditPostRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        productService.updateProduct(id, currentEmail, request);

        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công!"));
    }


    @PatchMapping("/{id}/mark-as-sold")
    public ResponseEntity<ApiResponse<String>> markAsSold(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        productService.markAsSold(id, currentEmail);

        return ResponseEntity.ok(ApiResponse.ok("Đã đánh dấu sản phẩm là đã bán!"));
    }

    @PostMapping("/set-favorite/{productId}")
    public ResponseEntity<ApiResponse<SetFavoriteRespond>> setFavorite(@PathVariable Integer productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String currentEmail = authentication.getName();
        try {
            SetFavoriteRespond isFavorite = productService.setFavorite(productId, currentEmail);
            if (isFavorite.getIsFavorite()) {
                return ResponseEntity.ok(ApiResponse.ok(isFavorite));
            }
            return ResponseEntity.ok(ApiResponse.ok(isFavorite));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/get-favorite")
    public ResponseEntity<ApiResponse<List<Product>>> getFavorite() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String currentEmail = authentication.getName();
        try {
            List<Product> listProduct = productService.getFavorite(currentEmail);

            return ResponseEntity.ok(ApiResponse.ok(listProduct));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/favorite-status/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(@PathVariable Integer productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String currentEmail = authentication.getName();
        try {
            Boolean isfavorite = productService.isFavorite(productId, currentEmail);
            return ResponseEntity.ok(ApiResponse.ok(isfavorite));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

}