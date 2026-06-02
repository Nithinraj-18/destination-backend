package com.destination.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto{

    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private Boolean outOfStock;

    // getters & setters
}
