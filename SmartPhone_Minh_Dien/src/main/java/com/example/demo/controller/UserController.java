package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.service.UsersService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/smartphone/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable int id) {
        return usersService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Users loginRequest) {
        return usersService.login(loginRequest.getEmail(), loginRequest.getMatKhau())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        if (user.getVaiTro() == 0) user.setVaiTro(3);
        user.setTrangThai(true);
        Users created = usersService.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Users> createUserWithMultipart(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("email") String email,
            @RequestParam("matKhau") String matKhau,
            @RequestParam(value = "sdt", required = false) String sdt,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        Users user = new Users();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setMatKhau(matKhau);
        user.setSdt(sdt);
        user.setVaiTro(2);
        user.setTrangThai(true);

        if (avatar != null && !avatar.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
            try {
                Path uploadPath = Paths.get("uploads");
                Files.createDirectories(uploadPath);
                Files.copy(avatar.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                user.setAvatar("/images/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        Users created = usersService.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Users> updateUser(@PathVariable int id, @RequestBody Users user) {
        return ResponseEntity.ok(usersService.updateUser(id, user));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Users> updateUserWithAvatar(
            @PathVariable("id") Integer id,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("email") String email,
            @RequestParam(value = "sdt", required = false) String sdt,
            @RequestParam(value = "cccd", required = false) String cccd,
            @RequestParam(value = "ngaySinh", required = false) String ngaySinhStr,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        Optional<Users> userOpt = usersService.getUserById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        Users user = userOpt.get();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setSdt(sdt);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path path = Paths.get("uploads");
                Files.createDirectories(path);
                Files.copy(avatar.getInputStream(), path.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                user.setAvatar("/images/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ✅ Đồng bộ với bảng NhanVien
        nhanVienRepository.findByUser_UserID(id).ifPresent(nv -> {
            nv.setHoTen(hoTen);
            nv.setEmail(email);
            nv.setSdt(sdt);
            nv.setCccd(cccd);
            if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                try {
                    nv.setNgaySinh(java.sql.Date.valueOf(ngaySinhStr));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (user.getAvatar() != null) {
                nv.setHinhAnh(user.getAvatar());
            }
            nhanVienRepository.save(nv);
        });

        return ResponseEntity.ok(usersService.updateUser(id, user));
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Users> updateLoginStatus(@PathVariable int id, @RequestBody Map<String, Boolean> payload) {
        boolean trangThai = payload.getOrDefault("trangThai", true);
        Optional<Users> userOpt = usersService.getUserById(id);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setTrangThai(trangThai);
            return ResponseEntity.ok(usersService.updateUser(id, user));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/role/{vaiTro}")
    public ResponseEntity<String> getRoleName(@PathVariable int vaiTro) {
        return ResponseEntity.ok(usersService.getRoleName(vaiTro));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        khachHangRepository.findByUser_UserID(id).ifPresent(kh -> khachHangRepository.delete(kh));
        nhanVienRepository.findByUser_UserID(id).ifPresent(nv -> nhanVienRepository.delete(nv));
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 