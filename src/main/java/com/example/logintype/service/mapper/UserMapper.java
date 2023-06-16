package com.example.logintype.service.mapper;

import com.example.logintype.entity.User;
import com.example.logintype.service.dto.response.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toDto(User entity) {
        return UserResponseDto
                .builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }
}
