package com.destination.backend.dto;

import lombok.Data;

@Data
public class UserDetailsDTO {

    private String name;
    private String email;
    private String mobileNumber;
    private String address;
    private String pincode;
    private String state;
    private String district;
    private String taluk;
}
