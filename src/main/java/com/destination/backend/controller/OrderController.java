package com.destination.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.destination.backend.dto.ApiResponse;
import com.destination.backend.dto.OrderRequestDTO;
import com.destination.backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> createOrder(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            OrderRequestDTO request = mapper.readValue(requestJson, OrderRequestDTO.class);
            orderService.createOrder(request, file);
            ApiResponse<Object> response = new ApiResponse<>(
                    "success",
                    "Order placed successfully",
                    null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> response = new ApiResponse<>(
                    "error",
                    e.getMessage(),
                    null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
