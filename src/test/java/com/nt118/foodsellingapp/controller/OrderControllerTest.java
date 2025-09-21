package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.OrderDTO;
import com.nt118.foodsellingapp.dto.response.ApiResponse;
import com.nt118.foodsellingapp.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1);
        orderDTO.setUserId(1);
        orderDTO.setStatus("PENDING");
    }

    // Test createOrder
    @Test
    void createOrder_ValidRequest_ReturnsCreatedOrder() {
        when(orderService.createOrder(orderDTO)).thenReturn(orderDTO);

        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderDTO, response.getBody().getData());
        verify(orderService).createOrder(orderDTO);
    }

    // Test getAllOrders
    @Test
    void getAllOrders_ValidRequest_ReturnsOrderList() {
        List<OrderDTO> mockOrders = Arrays.asList(orderDTO);

        when(orderService.getAllOrders()).thenReturn(mockOrders);

        ResponseEntity<ApiResponse<List<OrderDTO>>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrders, response.getBody().getData());
        verify(orderService).getAllOrders();
    }

    // Test searchOrders
    @Test
    void searchOrders_ValidParams_ReturnsOrderPage() {
        Integer userId = 1;
        String name = "Test Order";
        String status = "PENDING";
        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<OrderDTO> orderList = Arrays.asList(orderDTO);
        Page<OrderDTO> mockPage = new PageImpl<>(orderList, pageable, 1);

        when(orderService.searchOrders(userId, name, status, pageable)).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<OrderDTO>>> response = orderController.searchOrders(userId, name, status, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockPage, response.getBody().getData());
        verify(orderService).searchOrders(userId, name, status, pageable);
    }

    // Test updateOrderStatus
    @Test
    void updateOrderStatus_ValidRequest_ReturnsUpdatedOrder() {
        int orderId = 1;
        String status = "COMPLETED";
        OrderDTO updatedOrder = new OrderDTO();
        updatedOrder.setId(orderId);
        updatedOrder.setUserId(1);
        updatedOrder.setStatus(status);

        when(orderService.updateOrderStatus(orderId, status)).thenReturn(updatedOrder);

        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.updateOrderStatus(orderId, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedOrder, response.getBody().getData());
        assertEquals(status, response.getBody().getData().getStatus());
        verify(orderService).updateOrderStatus(orderId, status);
    }

    // Test deleteOrder
    @Test
    void deleteOrder_ValidId_ReturnsSuccess() {
        int orderId = 1;

        doNothing().when(orderService).deleteOrder(orderId);

        ResponseEntity<ApiResponse<Void>> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).deleteOrder(orderId);
    }
}