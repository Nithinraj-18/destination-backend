package com.destination.backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.destination.backend.dto.ProductRequestDto;
import com.destination.backend.dto.ProductResponseDto;
import com.destination.backend.entity.Products;
import com.destination.backend.mapper.ProductMapper;
import com.destination.backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    public ProductService(ProductRepository productRepository, Cloudinary cloudinary) {
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
    }

    public ProductResponseDto createProduct(ProductRequestDto request, MultipartFile image) {

        try {

            // 🔥 file name
            // String fileName = System.currentTimeMillis() + "_" +
            // image.getOriginalFilename();

            // // 🔥 D DRIVE folder
            // Path uploadPath = Paths.get("D:\\Destination\\product-image\\");

            // // 🔥 create folder if not exists
            // if (!Files.exists(uploadPath)) {
            // Files.createDirectories(uploadPath);
            // }

            // // 🔥 save file
            // Path filePath = uploadPath.resolve(fileName);

            // Files.copy(
            // image.getInputStream(),
            // filePath,
            // StandardCopyOption.REPLACE_EXISTING);

            // // 🔥 image URL
            // String imageUrl = "http://localhost:8082/product-image/" + fileName;

            // // 🔥 map DTO → entity
            // Products product = ProductMapper.toEntity(request);
            // product.setImageUrl(imageUrl);

            // // 🔥 save DB
            // Products saved = productRepository.save(product);

            // return ProductMapper.toResponse(saved);

            // 🔥 Upload image to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    new java.util.HashMap<>());

            // 🔥 Get image URL
            String imageUrl = uploadResult.get("url").toString();

            // 🔥 Map DTO → Entity
            Products product = ProductMapper.toEntity(request);

            // 🔥 Save image URL
            product.setImageUrl(imageUrl);

            // 🔥 Save DB
            Products saved = productRepository.save(product);

            return ProductMapper.toResponse(saved);

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.getAll()
                .stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(String id) {
        Products product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductMapper.toResponse(product);
    }

    public ProductResponseDto updateProduct(String id,
            ProductRequestDto request,
            MultipartFile image) {

        Products product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());

        try {
            if (image != null && !image.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

                Path uploadPath = Paths.get("uploads/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String imageUrl = "http://localhost:8082/uploads/" + fileName;

                product.setImageUrl(imageUrl);
            }

        } catch (Exception e) {
            throw new RuntimeException("Image update failed");
        }

        Products saved = productRepository.save(product);

        return ProductMapper.toResponse(saved);
    }

    public void deleteProduct(String id) {
        Products product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }
}
