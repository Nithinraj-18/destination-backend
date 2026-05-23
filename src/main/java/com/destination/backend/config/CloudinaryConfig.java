package com.destination.backend.config;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;


@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {

        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", "dgdekv1q7");
        config.put("api_key", "324546228526259");
        config.put("api_secret", "MEmoW4AbvIhbLPSWmSwpwL_idXA");

        return new Cloudinary(config);
    }
}