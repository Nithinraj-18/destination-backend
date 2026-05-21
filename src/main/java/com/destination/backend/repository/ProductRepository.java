package com.destination.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.destination.backend.entity.Products;

public interface ProductRepository extends JpaRepository<Products, String> {

      @Query("SELECT p FROM Products p")
      List<Products> getAll();
}
