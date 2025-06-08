package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private Integer quantity;
    private String imageUrl;
    private Long categoryId;
}
