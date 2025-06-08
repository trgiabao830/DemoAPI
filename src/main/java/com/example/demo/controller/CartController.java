package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.AddToCartRequest;
import com.example.demo.dto.CartDTO;
import com.example.demo.dto.UpdateQuantityRequest;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Xem giỏ hàng
    @GetMapping
    public ResponseEntity<?> getCurrentCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        CartDTO cart = cartService.getCurrentCart(userDetails.getUser().getUsername());
        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.ok("Giỏ hàng hiện tại đang trống");
        }
        return ResponseEntity.ok(cart);
    }

    // Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<String> addProductToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AddToCartRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        try {
            cartService.addToCart(userDetails.getUser().getUsername(), request.getProductId(), request.getQuantity());
            return ResponseEntity.ok("Thêm sản phẩm vào giỏ hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }
    }

    // Cập nhật số lượng sản phẩm
    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateProductQuantity(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateQuantityRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        try {
            cartService.updateCartItem(userDetails.getUser().getUsername(), productId, request.getQuantity());
            return ResponseEntity.ok("Cập nhật số lượng sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi cập nhật số lượng sản phẩm: " + e.getMessage());
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeProductFromCart(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        try {
            cartService.removeFromCart(userDetails.getUser().getUsername(), productId);
            return ResponseEntity.ok("Xóa sản phẩm khỏi giỏ hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
        }
    }
}
