package com.example.demo.service.impl;

import com.example.demo.dto.AddressDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserProfileUpdateDTO;
import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    @Override
    public User register(User user) {
        if (user.getFullName() == null || user.getFullName().trim().length() < 2) {
            throw new IllegalArgumentException("Họ tên phải có ít nhất 2 ký tự");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email đã tồn tại");
            }
        } else {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        if (user.getPassword() == null || user.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Mật khẩu và xác nhận mật khẩu không được để trống");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không đúng");
        }
        if (!isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        return userRepository.save(user);
    }

    public boolean isValidPassword(String password) {
        // Regex: ít nhất 8 ký tự, ít nhất 1 chữ hoa, ít nhất 1 chữ thường, ít nhất 1 số
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    @Override
    public User login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user", user);
                return user;
            }
        }
        return null;
    }

    @Override
    public void logout() {
        session.invalidate();
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getAddress() != null) {
            Address addr = user.getAddress();
            if (addr == null) {
                addr = new Address();
                user.setAddress(addr);
            }
            AddressDTO addressDTO = updateDTO.getAddress();
            if (addressDTO.getStreet() != null)
                addr.setStreet(addressDTO.getStreet());
            if (addressDTO.getWard() != null)
                addr.setWard(addressDTO.getWard());
            if (addressDTO.getDistrict() != null)
                addr.setDistrict(addressDTO.getDistrict());
            if (addressDTO.getProvince() != null)
                addr.setProvince(addressDTO.getProvince());
        }

        return userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }

        if (!isValidPassword(newPassword)) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getFullName(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserRole(Long userId, String role) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + userId);
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}