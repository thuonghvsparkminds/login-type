package com.example.logintype.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    
    @NotEmpty
    private String bookName;

    @NotEmpty
    private MultipartFile imageFile;

    @NotEmpty
    private Integer number;
}
