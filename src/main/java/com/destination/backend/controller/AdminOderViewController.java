package com.destination.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.destination.backend.dto.AdminOrderResponse;
import com.destination.backend.dto.MonthlyRevenueDto;
import com.destination.backend.dto.UserDetailsDTO;
import com.destination.backend.entity.Order;
import com.destination.backend.repository.OrderRepository;
import com.destination.backend.service.AdminOderViewService;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOderViewController {

    @Autowired
    private AdminOderViewService adminOderViewService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders(
            @RequestParam(name = "search", required = false) String search) {
        List<AdminOrderResponse> orders = adminOderViewService.getAllOrders(search);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/delete-orders")
    public ResponseEntity<Map<String, Object>> deleteOrders(@RequestBody List<String> orderIds) {

        Map<String, Object> response = new HashMap<>();
        int deletedCount = adminOderViewService.deleteOrders(orderIds);

        response.put("success", true);
        response.put("message", deletedCount + " orders deleted successfully");
        response.put("deletedCount", deletedCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers() {
        List<UserDetailsDTO> users = adminOderViewService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/delivery-order")
    public ResponseEntity<String> deliverOrder(@RequestParam("orderId") String orderId,
            @RequestParam(name = "revenue", required = false) double revenue) {
        adminOderViewService.deliverOrder(orderId, revenue);
        return ResponseEntity.ok("Order delivered and email sent to user");
    }

    @GetMapping("/getAllRevenue")
    public ResponseEntity<List<MonthlyRevenueDto>> getMontlyRevenue() {
        List<MonthlyRevenueDto> revenueList = adminOderViewService.getMonthlyRevenue();
        return ResponseEntity.ok(revenueList);
    }

    @GetMapping("/exportAll")
    public ResponseEntity<byte[]> exportAll() {
        List<Order> orders = orderRepository
                .findAllByOrderByCreatedAtDesc();

        byte[] excel = adminOderViewService
                .exportOrdersToExcel(
                        orders);
        String fileName = "orders_" +
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd_HHmmss"))
                + ".xlsx";

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=" + fileName)
                .header(
                        "Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }

}
