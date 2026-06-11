package com.destination.backend.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productId;
private String productName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private String paymentMode;
    private String paymentScreenshot; // 🔥 NEW FIELD FOR SCREENSHOT
}
