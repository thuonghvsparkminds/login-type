package com.example.logintype.service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponseDto {

    private String fileName;
    private String downloadUri;
    private String fileType;
    private double size;
}
