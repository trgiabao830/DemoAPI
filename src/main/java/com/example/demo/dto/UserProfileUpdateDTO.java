package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private AddressDTO address;

}