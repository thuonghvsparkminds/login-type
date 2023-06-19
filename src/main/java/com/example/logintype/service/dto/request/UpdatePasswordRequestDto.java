package com.example.logintype.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDto {
    
    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String newPassword;
}
