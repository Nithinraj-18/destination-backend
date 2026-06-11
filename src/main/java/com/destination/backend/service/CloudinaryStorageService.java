package com.destination.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class CloudinaryStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {

        try {

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    new HashMap<>()
            );

            return uploadResult.get("url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }
}
