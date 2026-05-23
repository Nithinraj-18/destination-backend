package com.destination.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.destination.backend.dto.OrderItemDTO;
import com.destination.backend.dto.OrderRequestDTO;
import com.destination.backend.entity.Order;
import com.destination.backend.entity.OrderItem;
import com.destination.backend.entity.OrderUserDetails;
import com.destination.backend.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository; 
    private final EmailService emailService; 

    public Order createOrder(OrderRequestDTO request) {

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order items cannot be empty");
        }

        if (request.getUserDetails() == null) {
            throw new RuntimeException("User details are required");
        }

        Order order = new Order();

        // ✅ USER DETAILS (DIRECT SAVE)
        OrderUserDetails user = OrderUserDetails.builder()
                .name(request.getUserDetails().getName())
                .email(request.getUserDetails().getEmail())
                .mobileNumber(request.getUserDetails().getMobileNumber())
                .address(request.getUserDetails().getAddress())
                .pincode(request.getUserDetails().getPincode())
                .state(request.getUserDetails().getState())
                .district(request.getUserDetails().getDistrict())
                .taluk(request.getUserDetails().getTaluk())
                .build();

        order.setUserDetails(user);

        // ✅ ORDER ITEMS (NO CALCULATION)
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO dto : request.getItems()) {

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(dto.getProductId())
                    .productName(dto.getProductName())
                    .price(dto.getPrice())
                    .quantity(dto.getQuantity())
                    .totalPrice(dto.getTotalPrice()) // 🔥 FROM FRONTEND
                    .build();

            orderItems.add(item);
        }

        order.setItems(orderItems);

        // 🔥 NO BACKEND CALCULATION
        order.setTotalPrice(
                request.getItems().stream()
                        .mapToDouble(OrderItemDTO::getTotalPrice)
                        .sum());

        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        // ✅ SEND EMAIL (SAFE)
        try {
            emailService.sendOrderEmail(savedOrder);
        } catch (Exception e) {
            System.out.println("Email trigger failed: " + e.getMessage());
        }

        return savedOrder;
    }

}