package com.example.logintype.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    
    @NotEmpty
    private String username;
    
    @NotEmpty
    @Email
    private String email;
    
    @NotEmpty
    private String role;
    
    @NotEmpty
    private String password;}
