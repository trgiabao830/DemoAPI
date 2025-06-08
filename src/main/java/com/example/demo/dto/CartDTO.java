package com.example.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartDTO {
    private Long userId;
    private List<CartItemDTO> items;
    private Long total;
}