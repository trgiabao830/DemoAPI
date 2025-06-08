package com.example.demo.service.impl;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public CartDTO getCurrentCart(String username) {
        User user = getUser(username);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotal(0L);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        recalculateCartTotal(cart);

        List<CartItemDTO> items = cart.getItems().stream().map(item -> new CartItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity())).collect(Collectors.toList());

        return new CartDTO(cart.getId(), items, cart.getTotal());
    }

    @Override
    public void addToCart(String username, Long productId, int quantity) {
        User user = getUser(username);
        Product product = getProduct(productId);

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotal(0L);
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId)).findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        recalculateCartTotal(cart);
    }

    @Override
    public void updateCartItem(String username, Long productId, int quantity) {
        User user = getUser(username);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        recalculateCartTotal(cart);
    }

    @Override
    public void removeFromCart(String username, Long productId) {
        User user = getUser(username);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotal(0L);
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    @Override
    public void clearCart(User user) {
        Cart cart = getCartByUser(user);
        if (cart == null) {
            throw new IllegalArgumentException("Không tìm thấy giỏ hàng của người dùng: " + user.getUsername());
        }
        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();
        cart.setTotal(0L);
        cartRepository.save(cart);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sán phẩm"));
    }

    private void recalculateCartTotal(Cart cart) {
        long total = cart.getItems().stream()
                .mapToLong(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();
        cart.setTotal(total);
        cartRepository.save(cart);
    }
}
