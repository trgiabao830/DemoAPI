package com.example.demo.dto;

import com.example.demo.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private AddressDTO address;

    public UserProfileDTO(User user) {
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        if (user.getAddress() != null) {
            this.address = new AddressDTO(user.getAddress());
        }
    }
}