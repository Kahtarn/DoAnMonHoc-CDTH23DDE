package com.example.serverchodientu.service.product;

import com.example.serverchodientu.entity.Categories;
import com.example.serverchodientu.repository.CategoriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepository categoriesRepo;

    public CategoriesService(CategoriesRepository categoriesRepo) {
        this.categoriesRepo = categoriesRepo;
    }

    public List<Categories> getCategories() {
        return categoriesRepo.findAll();
    }
}
