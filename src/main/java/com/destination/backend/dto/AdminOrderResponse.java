package com.destination.backend.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AdminOrderResponse {

    private String orderId;
    private double totalPrice;
    private String status; 
    private Date createdAt;
    private UserDetailsDTO userDetails; 
    private List<OrderItemDTO> items;

}
