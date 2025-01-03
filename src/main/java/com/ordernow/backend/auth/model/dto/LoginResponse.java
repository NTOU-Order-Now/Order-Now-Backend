package com.ordernow.backend.auth.model.dto;

import com.ordernow.backend.user.model.entity.LoginType;
import com.ordernow.backend.user.model.entity.Merchant;
import com.ordernow.backend.user.model.entity.Role;
import com.ordernow.backend.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String name;
    private String email;
    private String avatarUrl;
    private Role role;
    private LoginType loginType;
    private String storeId;
    private String token;

    public static LoginResponse createResponse(User user, String token) {
        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .loginType(user.getLoginType())
                .token(token).build();

        if(user instanceof Merchant) {
            response.setStoreId(((Merchant) user).getStoreId());
        }

        return response;
    }
}
