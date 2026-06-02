package com.destination.backend.dto;

import lombok.Data;

@Data
public class MonthlyRevenueDto {

    private String month;
    private int year;
    private double revenue;
    private Long totalOrders;

}
