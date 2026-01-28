package com.example.serverchodientu.controller.product;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.entity.Categories;
import com.example.serverchodientu.service.product.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoriesService categoriesService;

    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/getCategories")
    public ResponseEntity<ApiResponse<List<Categories>>> getCategories() {
        List<Categories> data = categoriesService.getCategories();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}