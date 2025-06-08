package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserProfileUpdateDTO;
import com.example.demo.model.User;

public interface UserService {
    User register(User user);

    User login(String username, String password);

    void logout();

    User updateProfile(Long userId, UserProfileUpdateDTO updateDTO);

    void changePassword(Long userId, String oldPassword, String newPassword) throws Exception;

    List<UserDTO> getAllUsers();

    void updateUserRole(Long userId, String role);

    void deleteUserById(Long userId);
}
