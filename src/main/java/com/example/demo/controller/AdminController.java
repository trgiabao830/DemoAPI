package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.RoleUpdateRequest;
import com.example.demo.dto.UpdateOrderStatusRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Chào mừng admin!");
    }

    // Lấy danh sách tất cả người dùng
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Cập nhật vai trò người dùng
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok("Cập nhật vai trò thành công");
    }

    // Xoá tài khoản người dùng
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Xoá tài khoản thành công");
    }

    // Xem tất cả đơn hàng
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Cập nhật trạng thái đơn hàng
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {

        String status = request.getStatus();
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu tham số 'status'");
        }

        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
    }
}
