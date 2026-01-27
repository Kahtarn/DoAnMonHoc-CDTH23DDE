package com.example.serverchodientu.repository;

import com.example.serverchodientu.entity.ProductImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductId_Id(Integer productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductImage p WHERE p.productId.id = :productId")
    void deleteByProductId(Integer productId);
}
