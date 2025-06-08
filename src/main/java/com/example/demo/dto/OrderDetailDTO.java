package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private Long price;
    // Getters, Setters
}
