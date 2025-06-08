package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrderFromCart(String username, String paymentMethod);
    List<OrderDTO> getUserOrders(String username);
    OrderDTO getOrderById(Long id, String username);
    void cancelOrder(Long id, String username);

    List<OrderDTO> getAllOrders();
    void updateOrderStatus(Long id, String status);
}