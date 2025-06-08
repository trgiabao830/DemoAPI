package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private Long price;

    public CartItemDTO(Long productId, String productName, Long productPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = productPrice;
        this.quantity = quantity;
    }

}
