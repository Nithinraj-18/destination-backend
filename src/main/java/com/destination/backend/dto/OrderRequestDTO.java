package com.destination.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequestDTO {

     private List<OrderItemDTO> items;
    private UserDetailsDTO userDetails;
}
