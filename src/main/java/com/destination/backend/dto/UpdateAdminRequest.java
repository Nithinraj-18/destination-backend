package com.destination.backend.dto;

import lombok.Data;

@Data
public class UpdateAdminRequest {

    private Long id;
    private String username;
    private String email;
    private String password;

   
}
