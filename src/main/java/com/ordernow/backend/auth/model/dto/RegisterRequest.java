package com.ordernow.backend.auth.model.dto;

import com.ordernow.backend.user.model.entity.Gender;
import com.ordernow.backend.user.model.entity.LoginType;
import com.ordernow.backend.user.model.entity.Role;
import com.ordernow.backend.user.model.entity.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Role role;

    public User toUser() {
        validateRequest();
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .avatarUrl("")
                .gender(Gender.OTHER)
                .role(role)
                .loginType(LoginType.LOCAL)
                .build();
    }

    private void validateRequest() {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if(name.length() > 20) {
            throw new IllegalArgumentException("Name is too long");
        }
        if(role == Role.MERCHANT && phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Merchant phone number can not be empty");
        }
    }
}
