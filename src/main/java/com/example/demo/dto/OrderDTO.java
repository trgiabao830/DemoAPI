package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private Timestamp orderDate;
    private String status;
    private Long totalAmount;
    private Long userId;

    private List<OrderDetailDTO> items;

    private String paymentMethod;
    private String paymentStatus;
}
