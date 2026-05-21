package com.destination.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.destination.backend.dto.ApiResponse;
import com.destination.backend.dto.OrderRequestDTO;
import com.destination.backend.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createOrder(@RequestBody OrderRequestDTO request) {

        orderService.createOrder(request);

        ApiResponse<Object> response = new ApiResponse<>(
                "success",
                "Order placed successfully",
                null);

        return ResponseEntity.ok(response);
    }
}
