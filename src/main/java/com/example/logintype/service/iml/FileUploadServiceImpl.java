package com.example.logintype.service.iml;

import com.example.logintype.entity.enumrated.FileProperty;
import com.example.logintype.exception.FileHandlerException;
import com.example.logintype.service.FileUploadService;
import com.example.logintype.service.dto.response.FileUploadResponseDto;
import liquibase.util.file.FilenameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.storage.root-dir}")
    private String rootPath;

    private Path rootDir;

    private static final double MB_IN_BINARY = 0.00000095367432;

    @PostConstruct
    public void init() {

        if(StringUtils.isNoneBlank(rootPath)) {
            rootDir = Path.of(rootPath);
        } else {
            rootDir = Path.of("storages");
        }
        try {
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
            }
        } catch (IOException e) {
            log.error("An error when create root directory", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FileUploadResponseDto uploadFile(MultipartFile file, FileProperty fileProps) {

        String fileName = FilenameUtils.normalize(file.getOriginalFilename());
        String fileExtention = FilenameUtils.getExtension(fileName);
        double fileSizeInMB = file.getSize() * MB_IN_BINARY;

        if (!isExtensionValid(fileExtention, fileProps.getExtensions())) {

            String extentions = fileProps.getExtensions().stream().collect(Collectors.joining(" ,"));
            throw new FileHandlerException(String.format("File extentions only accept %s", extentions));
        }

        Path dir = rootDir.resolve(fileProps.getRootDir());

        if (!Files.exists(dir)) {

            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                log.error("Error occurs when create file directory", e);
                throw new FileHandlerException("An error occurs when upload file");
            }
        }

        String generatedName = generateFileName() + "." + fileExtention;
        Path filePath = dir.resolve(generatedName);

        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("Error occurs when upload file", e);
            throw new FileHandlerException("An error occurs when upload file");
        }

        return FileUploadResponseDto.builder()
                .fileType(fileExtention)
                .fileName(fileName)
                .downloadUri(fileProps.getRootDir() + File.separator + generatedName)
                .size(fileSizeInMB).build();
    }


    @Override
    public void deleteFile(String filePath) {

        try {

            rootDir = Path.of(rootPath);
            Path file = rootDir.resolve(filePath);
            Files.delete(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private static boolean isExtensionValid(String fileName, Set<String> extensions) {
        return extensions.contains(fileName);
    }

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Resource downloadFile(String fileName) {

        Path file = rootDir.resolve(fileName);

        try {

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                throw new FileHandlerException("File download not found!");
            }

            return resource;
        } catch (MalformedURLException e) {

            log.error("Error occurs when download file", e);
            throw new FileHandlerException("Error occurs when download file\"");
        }
    }
}
