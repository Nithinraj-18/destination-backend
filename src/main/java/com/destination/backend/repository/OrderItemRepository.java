package com.destination.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.destination.backend.entity.Order;
import com.destination.backend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByOrder(Order order);

}
