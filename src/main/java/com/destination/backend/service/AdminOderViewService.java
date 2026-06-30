package com.destination.backend.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.destination.backend.dto.AdminOrderResponse;
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
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        } else {
            orders = orderRepository
                    .findByUserDetails_NameContainingIgnoreCaseOrderByCreatedAtDesc(search);
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
                itemDTO.setPaymentMode(item.getPaymentMode());
                itemDTO.setPaymentScreenshot(item.getPaymentScreenshot());
                itemDTOList.add(itemDTO);
            }

            response.setItems(itemDTOList);

            responseList.add(response);
        }

        return responseList;
    }

    public int deleteOrders(List<String> orderIds) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        int count = orders.size();
        orderRepository.deleteAll(orders);

        return count;
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
            monthlyRevenue.setTotalOrders(monthlyRevenue.getTotalOrders() + 1);

        } else {
            monthlyRevenue = new MonthlyRevenue();
            monthlyRevenue.setMonth(month);
            monthlyRevenue.setYear(year);
            monthlyRevenue.setRevenue(revenue);
            monthlyRevenue.setTotalOrders(1L);
        }

        // 💾 SAVE REVENUE
        monthlyRevenueRepository.save(monthlyRevenue);
        emailService.sendDeliveryEmail(order);
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        List<MonthlyRevenue> revenues = monthlyRevenueRepository.findAll();
        Map<String, Integer> monthOrder = Map.ofEntries(
                Map.entry("January", 1),
                Map.entry("February", 2),
                Map.entry("March", 3),
                Map.entry("April", 4),
                Map.entry("May", 5),
                Map.entry("June", 6),
                Map.entry("July", 7),
                Map.entry("August", 8),
                Map.entry("September", 9),
                Map.entry("October", 10),
                Map.entry("November", 11),
                Map.entry("December", 12));

        revenues.sort((a, b) -> {
            int yearCompare = Integer.compare(b.getYear(), a.getYear());

            if (yearCompare != 0) {
                return yearCompare;
            }

            return Integer.compare(
                    monthOrder.get(b.getMonth()),
                    monthOrder.get(a.getMonth()));
        });
        List<MonthlyRevenueDto> revenueDtos = new ArrayList<>();

        for (MonthlyRevenue revenue : revenues) {
            MonthlyRevenueDto dto = new MonthlyRevenueDto();
            dto.setMonth(revenue.getMonth());
            dto.setYear(revenue.getYear());
            dto.setRevenue(revenue.getRevenue());
            dto.setTotalOrders(revenue.getTotalOrders());
            revenueDtos.add(dto);
        }

        return revenueDtos;
    }

    public byte[] exportOrdersToExcel(List<Order> orders) {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(
                    "Orders");
            Row header = sheet.createRow(
                    0);
            String[] columns = {
                    "S.No",
                    "Ordered Date",
                    "Customer Name",
                    "Email",
                    "Mobile Number",
                    "Address",
                    "Product Names",
                    "Total Price",
                    "Payment Mode"
            };

            for (int i = 0; i < columns.length; i++) {
                header
                        .createCell(i)
                        .setCellValue(
                                columns[i]);
            }

            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(
                        rowNum);
                OrderUserDetails user = order.getUserDetails();
                List<OrderItem> items = order.getItems();
                String products = items
                        .stream()
                        .map(
                                OrderItem::getProductName)
                        .collect(
                                Collectors.joining(
                                        ", "));

                String payments = items
                        .stream()
                        .map(
                                OrderItem::getPaymentMode)
                        .distinct()
                        .collect(
                                Collectors.joining(
                                        ", "));

                row.createCell(0)
                        .setCellValue(
                                rowNum);
                row.createCell(1)
                        .setCellValue(
                                String.valueOf(
                                        order.getCreatedAt()));
                row.createCell(2)
                        .setCellValue(
                                user.getName());
                row.createCell(3)
                        .setCellValue(
                                user.getEmail());
                row.createCell(4)
                        .setCellValue(
                                user.getMobileNumber());
                row.createCell(5)
                        .setCellValue(
                                user.getAddress());
                row.createCell(6)
                        .setCellValue(
                                products);
                row.createCell(7)
                        .setCellValue(
                                order.getTotalPrice());
                row.createCell(8)
                        .setCellValue(
                                payments);
                rowNum++;

            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(
                        i);
            }
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
