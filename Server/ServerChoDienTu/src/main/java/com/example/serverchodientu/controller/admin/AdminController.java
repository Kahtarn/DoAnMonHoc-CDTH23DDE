package com.example.serverchodientu.controller.admin;

import com.example.serverchodientu.dto.ApiResponse;
import com.example.serverchodientu.entity.Categories;
import com.example.serverchodientu.service.admin.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final CategoryService categoryService;

    public AdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Categories>> addCategory(
            @RequestParam("name") String name,
            @RequestParam("iconUrl") MultipartFile icon) {

        Categories newCate = categoryService.createCategory(name, icon);
        return ResponseEntity.ok(ApiResponse.ok(newCate));
    }
}
