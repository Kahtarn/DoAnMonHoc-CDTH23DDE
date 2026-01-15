package com.example.serverchodientu.entity;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByOrderByCreateAtDesc(Integer status);
    List<Product> findByTitleContainingIgnoreCaseAndStatus(String title, Integer status, Sort sort);
    List<Product> findBySellerIdAndStatus(Integer sellerId, Integer status);
}
