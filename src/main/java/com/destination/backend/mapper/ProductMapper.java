package com.destination.backend.mapper;

import com.destination.backend.dto.ProductRequestDto;
import com.destination.backend.dto.ProductResponseDto;
import com.destination.backend.entity.Products;

public class ProductMapper {

    public static Products toEntity(ProductRequestDto request) {
        return Products.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory()) 
                .outOfStock(request.getOutOfStock() != null ? request.getOutOfStock() : false)
                .build();
    }

    public static ProductResponseDto toResponse(Products product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory()) 
                .outOfStock(product.getOutOfStock())
                .build();
    }
}
