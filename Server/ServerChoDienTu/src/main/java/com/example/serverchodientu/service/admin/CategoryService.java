package com.example.serverchodientu.service.admin;

import com.example.serverchodientu.entity.Categories;
import com.example.serverchodientu.repository.CategoriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class CategoryService {
    private final CategoriesRepository categoriesRepo;

    public CategoryService(CategoriesRepository categoriesRepo) {
        this.categoriesRepo = categoriesRepo;
    }

    public Categories createCategory(String name, MultipartFile iconFile) {
        Categories category = new Categories();
        category.setName(name);

        if (iconFile != null && !iconFile.isEmpty()) {
            String filePath = saveToDisk(iconFile, "categories");
            category.setIconUrl("/uploads/" + filePath);
        }

        return categoriesRepo.save(category);
    }

    private String saveToDisk(MultipartFile file, String subFolder) {
        try {
            Path root = Paths.get("uploads", subFolder);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = root.resolve(fileName);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return subFolder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file icon: " + e.getMessage());
        }
    }
}
