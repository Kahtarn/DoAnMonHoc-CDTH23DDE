package com.example.serverchodientu.repository;

import com.example.serverchodientu.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByStatusOrderByCreateAtDesc(Integer status);
    List<Product> findByTitleContainingIgnoreCaseAndStatus(String title, Integer status, Sort sort);
    List<Product> findBySellerIdAndStatusOrderByCreateAtDesc(Integer sellerId, Integer status);
    List<Product> findByCategoryId(Integer categoryId);
}
