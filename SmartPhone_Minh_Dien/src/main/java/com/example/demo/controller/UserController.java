package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/smartphone/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UsersService usersService;

    // Lấy danh sách người dùng
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable int id) {
        Optional<Users> userOpt = usersService.getUserById(id);
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Tạo mới người dùng
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = usersService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Users loginRequest) {
        Optional<Users> userOpt = usersService.login(loginRequest.getEmail(), loginRequest.getMatKhau());
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(401).build());
    }

    // Cập nhật trạng thái (hoạt động/khóa)
    @PutMapping("/{id}/status")
    public ResponseEntity<Users> updateLoginStatus(@PathVariable int id, @RequestBody LoginStatusDTO body) {
        Optional<Users> userOpt = usersService.getUserById(id);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setTrangThai(body.isTrangThai());
            Users updated = usersService.updateUser(id, user);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // Cập nhật người dùng với multipart/form-data (upload avatar)
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Users> updateUserWithAvatar(
            @PathVariable("id") Integer id,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("email") String email,
            @RequestParam(value = "sdt", required = false) String sdt,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        Optional<Users> userOpt = usersService.getUserById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        Users user = userOpt.get();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setSdt(sdt);

        if (avatar != null && !avatar.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
            String uploadDir = "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            try {
                Path filePath = Paths.get(uploadDir, filename);
                Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setAvatar("/images/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        Users updatedUser = usersService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    // Xóa người dùng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Trả về vai trò người dùng
    @GetMapping("/role/{vaiTro}")
    public ResponseEntity<String> getRoleName(@PathVariable int vaiTro) {
        return ResponseEntity.ok(usersService.getRoleName(vaiTro));
    }
}
