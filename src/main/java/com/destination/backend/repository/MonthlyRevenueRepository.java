package com.destination.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.destination.backend.entity.MonthlyRevenue;

public interface MonthlyRevenueRepository extends JpaRepository<MonthlyRevenue, String> {

    MonthlyRevenue findByMonthAndYear(String month, int year);

}
