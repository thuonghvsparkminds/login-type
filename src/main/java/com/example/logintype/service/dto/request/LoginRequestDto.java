package com.example.logintype.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDto {
    
    @Email
    @NotEmpty
    private String email;
    
    @NotEmpty
    @Min(value = 6)
    private String password;
}
