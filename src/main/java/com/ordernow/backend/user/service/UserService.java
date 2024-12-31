package com.ordernow.backend.user.service;

import com.ordernow.backend.user.model.dto.UserResponse;
import com.ordernow.backend.user.model.entity.User;
import com.ordernow.backend.auth.repository.UserRepository;
import com.ordernow.backend.user.model.dto.UserProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public UserResponse getUserResponseById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        return UserResponse.fromUser(user);
    }

    public void updateProfile(String userId, UserProfileRequest userProfileRequest)
            throws NoSuchElementException {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }

        if(!user.getName().equals(userProfileRequest.getName()))
            user.setName(userProfileRequest.getName());
        if(!user.getPhoneNumber().equals(userProfileRequest.getPhoneNumber()))
            user.setPhoneNumber(userProfileRequest.getPhoneNumber());
        if(!user.getAvatarUrl().equals(userProfileRequest.getAvatarUrl()))
            user.setAvatarUrl(userProfileRequest.getAvatarUrl());
        if(!user.getGender().equals(userProfileRequest.getGender()))
            user.setGender(userProfileRequest.getGender());

        userRepository.save(user);
    }
}
