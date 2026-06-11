package com.destination.backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("local")
public class LocalFileStorageService implements FileStorageService {

    @Override
    public String uploadFile(MultipartFile file) {

        try {
            String fileName =
                    System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get("E:\\Destination\\payment-screenshots");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            return "http://localhost:8082/payment-screenshots/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Local upload failed", e);
        }
    }
}
