package com.example.logintype.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDto {

    private HttpStatus status;
    private String jwt;
    private String jwtRefreshToken;
}
