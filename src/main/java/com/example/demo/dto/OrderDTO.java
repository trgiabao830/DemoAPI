package com.example.demo.dto;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp orderDate;

    private String status;
    private Long totalAmount;
    private Long userId;

    private List<OrderDetailDTO> items;

    private String paymentMethod;
    private String paymentStatus;
}
