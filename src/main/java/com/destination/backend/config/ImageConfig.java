package com.destination.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/product-image/**")
                .addResourceLocations("file:///E:/Destination/product-image/");
        registry.addResourceHandler("/payment-screenshots/**")
                .addResourceLocations("file:///E:/Destination/payment-screenshots/");
    }
}
