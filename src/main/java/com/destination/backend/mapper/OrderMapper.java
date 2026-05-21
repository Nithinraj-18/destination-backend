package com.destination.backend.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.destination.backend.dto.OrderItemDTO;
import com.destination.backend.dto.UserDetailsDTO;
import com.destination.backend.entity.Order;
import com.destination.backend.entity.OrderItem;
import com.destination.backend.entity.OrderUserDetails;

@Component
public class OrderMapper {

    // 👉 Convert UserDetailsDTO → Entity
    public OrderUserDetails mapUserDetails(UserDetailsDTO dto) {

        return OrderUserDetails.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .mobileNumber(dto.getMobileNumber())
                .address(dto.getAddress())
                .pincode(dto.getPincode())
                .state(dto.getState())
                .district(dto.getDistrict())
                .taluk(dto.getTaluk())
                .build();
    }

    // 👉 Convert OrderItemDTO → OrderItem (WITHOUT product)
    public List<OrderItem> mapOrderItems(List<OrderItemDTO> itemDTOs, Order order) {

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDTO dto : itemDTOs) {

            OrderItem item = new OrderItem();

            // ⚠️ product will be set in service
            item.setOrder(order);
            item.setQuantity(dto.getQuantity());

            items.add(item);
        }

        return items;
    }
}