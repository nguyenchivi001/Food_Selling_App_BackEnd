package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO dto);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByStatus(String status);
    OrderDTO getOrderById(int id);
    OrderDTO updateOrderStatus(int id, String status);
    void deleteOrder(int id);
    Page<OrderDTO> searchOrders(Integer userId, String name, String status, Pageable pageable);
}
