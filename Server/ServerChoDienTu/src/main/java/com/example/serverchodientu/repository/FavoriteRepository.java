package com.example.serverchodientu.repository;

import com.example.serverchodientu.entity.Favorite;
import com.example.serverchodientu.entity.Product;
import com.example.serverchodientu.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findFavoriteByUserAndProduct(User user, Product product);

    @Modifying
    @Transactional
    @Query("SELECT f.product FROM Favorite f WHERE f.user.id = :userId")
    List<Product> findFavoriteProductsByUserId(@Param("userId") Integer userId);
}