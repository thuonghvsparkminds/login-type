package com.example.logintype.service;

import com.example.logintype.entity.enumrated.FileProperty;
import com.example.logintype.service.dto.response.FileUploadResponseDto;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    FileUploadResponseDto uploadFile(MultipartFile file, FileProperty fileProps);

    Resource downloadFile(String filePath);

    void deleteFile(String filePath);
}
