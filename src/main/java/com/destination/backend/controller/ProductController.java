package com.destination.backend.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.destination.backend.dto.ApiResponse;
import com.destination.backend.dto.ProductRequestDto;
import com.destination.backend.dto.ProductResponseDto;
import com.destination.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ CREATE
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(

            @RequestPart("request") String requestJson,
            @RequestPart("image") MultipartFile image

    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDto request = mapper.readValue(requestJson, ProductRequestDto.class);

        ProductResponseDto response = productService.createProduct(request, image);

        return ResponseEntity.ok(
                new ApiResponse<>("success", "Product created successfully", response));
    }

    // ✅ GET ALL
    @GetMapping("/getAll")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> data = productService.getAllProducts();
        return ResponseEntity.ok(data);
    }

    // ✅ GET BY ID
    @GetMapping("/findById") // fixed typo
    public ResponseEntity<ProductResponseDto> getById(
            @RequestParam String id) {
        ProductResponseDto data = productService.getProductById(id);
        return ResponseEntity.ok(data);
    }

    // ✅ UPDATE
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> update(

            @RequestParam String id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image

    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDto request = mapper.readValue(requestJson, ProductRequestDto.class);

        ProductResponseDto res = productService.updateProduct(id, request, image);

        return ResponseEntity.ok(
                new ApiResponse<>("success", "Product updated successfully", res));
    }

    // ✅ DELETE
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Product deleted successfully", null));
    }
}
