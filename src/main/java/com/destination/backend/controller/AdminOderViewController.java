package com.destination.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.destination.backend.dto.AdminOrderResponse;
import com.destination.backend.dto.MonthlyRevenueDto;
import com.destination.backend.dto.UserDetailsDTO;
import com.destination.backend.service.AdminOderViewService;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOderViewController {

    @Autowired
    private AdminOderViewService adminOderViewService;

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders(
            @RequestParam(name = "search", required = false) String search) {
        List<AdminOrderResponse> orders = adminOderViewService.getAllOrders(search);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/delete-order")
    public ResponseEntity<Map<String, Object>> deleteOrder(@RequestParam String orderId) {

        boolean deleted = adminOderViewService.deleteOrderById(orderId);

        Map<String, Object> response = new HashMap<>();

        if (deleted) {
            response.put("success", true);
            response.put("message", "Deleted successfully");
            response.put("orderId", orderId);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Order not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
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

}
