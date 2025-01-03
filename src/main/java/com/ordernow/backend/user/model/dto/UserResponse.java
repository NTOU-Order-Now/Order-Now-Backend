package com.ordernow.backend.user.model.dto;

import com.ordernow.backend.user.model.entity.LoginType;
import com.ordernow.backend.user.model.entity.Merchant;
import com.ordernow.backend.user.model.entity.Role;
import com.ordernow.backend.user.model.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String avatarUrl;
    private Role role;
    private LoginType loginType;
    private String storeId;

    public static UserResponse fromUser(User user) {
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .loginType(user.getLoginType())
                .build();

        if(user instanceof Merchant) {
            response.setStoreId(((Merchant) user).getStoreId());
        }

        return response;
    }
}
