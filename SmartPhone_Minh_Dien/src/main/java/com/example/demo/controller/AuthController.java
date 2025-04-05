package com.example.demo.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.KhachHang;
import com.example.demo.model.RegisterRequest;
import com.example.demo.model.Users;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.UsersRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        // Kiểm tra trùng email
        if (usersRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        // Tạo User mới
        Users user = Users.builder()
                .hoTen(req.getHoTen())
                .email(req.getEmail())
                .matKhau(req.getMatKhau()) // 🔒 có thể mã hóa bằng BCrypt sau
                .sdt(req.getSdt())
                .vaiTro(1) // 1 = Khách hàng
                .trangThai(true)
                .ngayTao(new Date())
                .build();
        usersRepo.save(user);

        // Tạo Khách hàng tương ứng
        KhachHang kh = KhachHang.builder()
                .tenKH(req.getHoTen())
                .email(req.getEmail())
                .sdt(req.getSdt())
                .diaChi(req.getDiaChi())
                .user(user)
                .build();
        khachHangRepo.save(kh);

        return ResponseEntity.ok("Đăng ký thành công!");
    }
}

