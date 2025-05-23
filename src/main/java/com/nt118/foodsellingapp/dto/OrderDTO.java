package com.nt118.foodsellingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int userId;
    private String deliveryAddress;
    private String paymentMethod;
    private Double totalPrice;
    private String status;
    private boolean isPaid;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> orderItems;
}
