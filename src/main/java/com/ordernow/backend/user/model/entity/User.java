package com.ordernow.backend.user.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
@Builder
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String avatarUrl;
    private Gender gender;
    private Role role;
    private LoginType loginType;

    public User(String name, String email, String password, Role role, LoginType loginType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.loginType = loginType;
    }

    public User(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.avatarUrl = user.getAvatarUrl();
        this.gender = user.getGender();
        this.role = user.getRole();
        this.loginType = user.getLoginType();
    }
}
