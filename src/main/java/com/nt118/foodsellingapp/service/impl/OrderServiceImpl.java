package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.dto.OrderDTO;
import com.nt118.foodsellingapp.dto.OrderItemDTO;
import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.entity.Order;
import com.nt118.foodsellingapp.entity.OrderItem;
import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.exception.ResourceNotFoundException;
import com.nt118.foodsellingapp.mapper.OrderMapper;
import com.nt118.foodsellingapp.repository.FoodRepository;
import com.nt118.foodsellingapp.repository.OrderRepository;
import com.nt118.foodsellingapp.repository.UserRepository;
import com.nt118.foodsellingapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            FoodRepository foodRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDTO createOrder(OrderDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItemDTO itemDTO : dto.getOrderItems()) {
            Food food = foodRepository.findById(itemDTO.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + itemDTO.getFoodId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setFood(food);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(food.getPrice());
            totalPrice += food.getPrice() * itemDTO.getQuantity();
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setTotalPrice(totalPrice);
        order.setStatus("pending");
        order.setPaid("online".equalsIgnoreCase(dto.getPaymentMethod()));
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderItems(orderItems);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.convertToOrderDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.convertToOrderDTO(order);
    }

    @Override
    public OrderDTO updateOrderStatus(int id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        return orderMapper.convertToOrderDTO(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> searchOrders(Integer userId, String name, String status, Pageable pageable) {
        Page<Order> orderPage;

        // Nếu chỉ lọc theo userId (ví dụ: client xem lịch sử của mình)
        if (userId != null && (name == null || name.isEmpty())) {
            if (status != null && !status.isEmpty()) {
                orderPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
            } else {
                orderPage = orderRepository.findAllByUserId(userId, pageable); // bạn cần thêm hàm này vào repo
            }

            // Nếu admin lọc theo tên người dùng
        } else if (name != null && !name.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                orderPage = orderRepository.findByUserNameContainingAndStatus(name, status, pageable);
            } else {
                orderPage = orderRepository.findByUserNameContaining(name, pageable);
            }

            // Không lọc gì cả: trả về toàn bộ (paging)
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return orderPage.map(orderMapper::convertToOrderDTO);
    }
}
