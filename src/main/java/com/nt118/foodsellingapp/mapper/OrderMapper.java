package com.nt118.foodsellingapp.mapper;

import com.nt118.foodsellingapp.dto.OrderDTO;
import com.nt118.foodsellingapp.dto.OrderItemDTO;
import com.nt118.foodsellingapp.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderDTO convertToOrderDTO(Order order) {
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream().map(item -> {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setFoodId(item.getFood().getId());
            orderItemDTO.setQuantity(item.getQuantity());
            orderItemDTO.setPrice(item.getPrice());
            return orderItemDTO;
        }).toList();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setDeliveryAddress(order.getDeliveryAddress());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setPaid(order.isPaid());
        orderDTO.setCreatedAt(order.getCreatedAt());
        orderDTO.setOrderItems(orderItemDTOs);

        return orderDTO;
    }
}
