package com.destination.backend.dto;

import lombok.Data;

@Data
public class ProductRequestDto {

    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;

    
}
