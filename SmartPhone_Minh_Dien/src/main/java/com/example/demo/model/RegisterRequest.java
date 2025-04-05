package com.example.demo.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String hoTen;
    private String email;
    private String matKhau;
    private String sdt;
    private String diaChi;
}

