package com.destination.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.destination.backend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserDetails_NameContainingIgnoreCase(String name);
}
