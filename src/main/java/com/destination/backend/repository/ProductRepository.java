package com.destination.backend.repository;

import java.util.List;

import com.destination.backend.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Products, String> {

      @Query("SELECT p FROM Products p")
      List<Products> getAll();

      @Query("SELECT p FROM Products p WHERE LOWER(p.name) LIKE LOWER(:search)")
      List<Products> searchByProductName(@Param("search") String search);
}
