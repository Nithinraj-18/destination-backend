package com.destination.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MonthlyRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String month;
    private int year;
    private double revenue;

}
