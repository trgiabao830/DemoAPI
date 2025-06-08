package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Xem danh sách đơn hàng
    @GetMapping
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        var orders = orderService.getUserOrders(userDetails.getUser().getUsername());
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.ok("Bạn chưa có đơn hàng nào");
        }

        return ResponseEntity.ok(orders);
    }

    // Xem đơn hàng cụ thể
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        try {
            return ResponseEntity.ok(orderService.getOrderById(id, userDetails.getUser().getUsername()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xem đơn hàng này");
        }
    }

    // Tạo đơn hàng từ giỏ hàng
    @PostMapping
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateOrderRequestDTO request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        try {
            var order = orderService.createOrderFromCart(
                    userDetails.getUser().getUsername(),
                    request.getPaymentMethod());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.ok("Giỏ hàng hiện tại đang trống");
        }
    }

    // Hủy đơn hàng
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        try {
            orderService.cancelOrder(id, userDetails.getUser().getUsername());
            return ResponseEntity.ok("Huỷ đơn hàng thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn hàng");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền huỷ đơn hàng này");
        }
    }
}
