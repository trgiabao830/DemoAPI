package com.example.demo.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.Order;
import com.example.demo.model.Order.PaymentMethod;
import com.example.demo.model.Order.PaymentStatus;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public OrderDTO createOrderFromCart(String username, String paymentMethodInput) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Cart cart = cartService.getCartByUser(user);
        if (cart.getItems().isEmpty())
            throw new RuntimeException("Giỏ hàng trống");

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setStatus("Pending");

        PaymentMethod method = PaymentMethod.valueOf(paymentMethodInput.toUpperCase());
        order.setPaymentMethod(method);
        order.setPaymentStatus(PaymentStatus.UNPAID);

        List<OrderDetail> orderDetails = cart.getItems().stream().map(cartItem -> {
            OrderDetail item = new OrderDetail();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        long totalAmount = orderDetails.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);

        orderRepository.save(order);
        cartService.clearCart(user);

        return toDTO(order);
    }

    @Override
    public List<OrderDTO> getUserOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return orderRepository.findByUser(user).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Order order = orderRepository.findById(id)
                .filter(o -> o.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return toDTO(order);
    }

    @Override
    public void cancelOrder(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Không có quyền huỷ đơn hàng");
        }

        if (!"Pending".equals(order.getStatus())) {
            throw new IllegalStateException("Không thể huỷ đơn đã xử lý");
        }

        order.setStatus("Cancelled");
        orderRepository.save(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
    }

    private OrderDTO toDTO(Order order) {
        List<OrderDetailDTO> items = order.getOrderDetails().stream().map(item -> {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getProduct().getPrice());
            return dto;
        }).collect(Collectors.toList());

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser().getId());
        dto.setItems(items);
        dto.setTotalAmount(order.getTotalAmount());

        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setPaymentStatus(order.getPaymentStatus().name());

        return dto;
    }
}
