package com.example.demo.service;

import com.example.demo.dto.CartDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.User;

public interface CartService {
    CartDTO getCurrentCart(String username);
    void addToCart(String username, Long productId, int quantity);
    void updateCartItem(String username, Long productId, int quantity);
    void removeFromCart(String username, Long productId);
    Cart getCartByUser(User user);
    void clearCart(User user);
}