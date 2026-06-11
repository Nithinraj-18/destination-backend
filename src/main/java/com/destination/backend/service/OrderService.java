package com.destination.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileStorageService fileStorageService;

    public Order createOrder(OrderRequestDTO request, MultipartFile file) {

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
                    .paymentMode(dto.getPaymentMode())// 🔥 FROM FRONTEND
                    .build();

            // String paymentScreenshotUrl = null;

            // try {

            // if (file != null && !file.isEmpty()) {

            // String fileName = System.currentTimeMillis() + "_" +
            // file.getOriginalFilename();

            // // Save in D drive
            // Path uploadPath = Paths.get("E:\\Destination\\payment-screenshots");

            // // Create folder if not exists
            // if (!Files.exists(uploadPath)) {
            // Files.createDirectories(uploadPath);
            // }

            // // Save file
            // Path filePath = uploadPath.resolve(fileName);

            // Files.copy(
            // file.getInputStream(),
            // filePath,
            // StandardCopyOption.REPLACE_EXISTING);

            // // URL to save in DB
            // paymentScreenshotUrl = "http://localhost:8082/payment-screenshots/" +
            // fileName;
            // }

            // } catch (Exception e) {
            // throw new RuntimeException("Failed to upload screenshot", e);
            // }

            String paymentScreenshotUrl = fileStorageService.uploadFile(file);
            item.setPaymentScreenshot(paymentScreenshotUrl);

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
            new Thread(() -> {
                try {
                    emailService.sendOrderEmail(savedOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Email trigger failed: " + e.getMessage());
        }

        return savedOrder;
    }

}