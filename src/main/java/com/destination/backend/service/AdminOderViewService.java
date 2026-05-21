package com.destination.backend.service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.destination.backend.dto.AdminOrderResponse;
import com.destination.backend.dto.MonthlyRevenueDto;
import com.destination.backend.dto.MonthlyRevenueDto;
import com.destination.backend.dto.MonthlyRevenueDto;
import com.destination.backend.dto.MonthlyRevenueDto;
import com.destination.backend.dto.OrderItemDTO;
import com.destination.backend.dto.UserDetailsDTO;
import com.destination.backend.entity.MonthlyRevenue;
import com.destination.backend.entity.Order;
import com.destination.backend.entity.OrderItem;
import com.destination.backend.entity.OrderUserDetails;
import com.destination.backend.repository.MonthlyRevenueRepository;
import com.destination.backend.repository.OrderItemRepository;
import com.destination.backend.repository.OrderRepository;

@Service
public class AdminOderViewService {

    private final OrderItemRepository orderItemRepository;
    private final EmailService emailService;
    private final OrderRepository orderRepository;
    private final MonthlyRevenueRepository monthlyRevenueRepository;

    AdminOderViewService(OrderItemRepository orderItemRepository, EmailService emailService,
            OrderRepository orderRepository, MonthlyRevenueRepository monthlyRevenueRepository) {
        this.orderItemRepository = orderItemRepository;
        this.emailService = emailService;
        this.orderRepository = orderRepository;
        this.monthlyRevenueRepository = monthlyRevenueRepository;
    }

    public List<AdminOrderResponse> getAllOrders(String search) {
        List<Order> orders;
        if (search == null || search.trim().isEmpty()) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository
                    .findByUserDetails_NameContainingIgnoreCase(search);

        }
        List<AdminOrderResponse> responseList = new ArrayList<>();

        for (Order order : orders) {

            AdminOrderResponse response = new AdminOrderResponse();

            // 🔹 Order Details
            response.setOrderId(order.getId());
            response.setTotalPrice(order.getTotalPrice());
            response.setStatus(order.getStatus());
            response.setCreatedAt(java.sql.Timestamp.valueOf(order.getCreatedAt()));

            // 🔹 User Details
            OrderUserDetails user = order.getUserDetails();

            if (user != null) {
                UserDetailsDTO userDTO = new UserDetailsDTO();
                userDTO.setName(user.getName());
                userDTO.setEmail(user.getEmail());
                userDTO.setMobileNumber(user.getMobileNumber());
                userDTO.setAddress(user.getAddress());
                userDTO.setPincode(user.getPincode());
                userDTO.setState(user.getState());
                userDTO.setDistrict(user.getDistrict());
                userDTO.setTaluk(user.getTaluk());

                response.setUserDetails(userDTO);
            }

            // 🔹 Order Items
            List<OrderItemDTO> itemDTOList = new ArrayList<>();

            List<OrderItem> items = orderItemRepository.findByOrder(order);
            // OR better: order.getItems()

            for (OrderItem item : items) {
                OrderItemDTO itemDTO = new OrderItemDTO();

                itemDTO.setProductId(item.getProductId());
                itemDTO.setProductName(item.getProductName());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setTotalPrice(item.getTotalPrice());

                itemDTOList.add(itemDTO);
            }

            response.setItems(itemDTOList);

            responseList.add(response);
        }

        return responseList;
    }

    public boolean deleteOrderById(String orderId) {

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            orderRepository.delete(optionalOrder.get());
            return true;
        }

        return false;
    }

    public List<UserDetailsDTO> getAllUsers() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        List<UserDetailsDTO> users = new ArrayList<>();

        for (OrderItem item : orderItems) {
            UserDetailsDTO userDetails = new UserDetailsDTO();
            userDetails.setName(item.getOrder().getUserDetails().getName());
            userDetails.setEmail(item.getOrder().getUserDetails().getEmail());
            userDetails.setMobileNumber(item.getOrder().getUserDetails().getMobileNumber());
            userDetails.setAddress(item.getOrder().getUserDetails().getAddress());
            userDetails.setPincode(item.getOrder().getUserDetails().getPincode());
            userDetails.setState(item.getOrder().getUserDetails().getState());
            userDetails.setDistrict(item.getOrder().getUserDetails().getDistrict());
            userDetails.setTaluk(item.getOrder().getUserDetails().getTaluk());
            users.add(userDetails);
        }
        return users;
    }

    public void deliverOrder(String orderId, double revenue) {

        // 🔍 Get Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Check Status
        if (order.getStatus() == null ||
                !order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Order is already completed");
        }

        // 👤 Validate User
        if (order.getUserDetails() == null) {
            throw new RuntimeException("User details not found");
        }

        // 📦 Validate Items
        if (order.getItems() == null ||
                order.getItems().isEmpty()) {
            throw new RuntimeException("Order items not found");
        }

        LocalDate currentDate = LocalDate.now();

        String month = currentDate.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = currentDate.getYear();
        MonthlyRevenue monthlyRevenue = monthlyRevenueRepository.findByMonthAndYear(month, year);
        if (monthlyRevenue != null) {
            double existingRevenue = monthlyRevenue.getRevenue();
            monthlyRevenue.setRevenue(existingRevenue + revenue);

        } else {
            monthlyRevenue = new MonthlyRevenue();
            monthlyRevenue.setMonth(month);
            monthlyRevenue.setYear(year);
            monthlyRevenue.setRevenue(revenue);
        }

        // 💾 SAVE REVENUE
        monthlyRevenueRepository.save(monthlyRevenue);
        emailService.sendDeliveryEmail(order);
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        List<MonthlyRevenue> revenues = monthlyRevenueRepository.findAll();
        List<MonthlyRevenueDto> revenueDtos = new ArrayList<>();

        for (MonthlyRevenue revenue : revenues) {
            MonthlyRevenueDto dto = new MonthlyRevenueDto();
            dto.setMonth(revenue.getMonth());
            dto.setYear(revenue.getYear());
            dto.setRevenue(revenue.getRevenue());
            revenueDtos.add(dto);
        }

        return revenueDtos;
    }

}
