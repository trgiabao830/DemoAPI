package com.example.demo.controller;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.dto.UserProfileUpdateDTO;
import com.example.demo.dto.ChangePasswordDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Xem thông tin người dùng
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        return ResponseEntity.ok(new UserProfileDTO(user));
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserProfileUpdateDTO updateDTO) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        User currentUser = customUserDetails.getUser();

        User updatedUser = userService.updateProfile(currentUser.getId(), updateDTO);
        return ResponseEntity.ok(new UserProfileDTO(updatedUser));
    }

    // Đổi mật khẩu
    @PutMapping("/changepassword")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ChangePasswordDTO dto) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }
        try {
            userService.changePassword(customUserDetails.getUser().getId(), dto.getOldPassword(), dto.getNewPassword());
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi đổi mật khẩu");
        }
    }

}