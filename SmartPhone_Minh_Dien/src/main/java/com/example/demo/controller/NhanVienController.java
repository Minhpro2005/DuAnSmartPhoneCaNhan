package com.example.demo.controller;

import com.example.demo.model.NhanVien;
import com.example.demo.model.Users;
import com.example.demo.service.NhanVienService;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/smartphone/nhanvien")
@CrossOrigin(origins = "*")
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private UsersService usersService;

    // ✅ Lấy danh sách tất cả nhân viên
    @GetMapping
    public List<NhanVien> getAll() {
        return nhanVienService.getAllNhanVien();
    }

    // ✅ Lấy nhân viên theo ID
    @GetMapping("/id/{id}")
    public ResponseEntity<NhanVien> getById(@PathVariable int id) {
        Optional<NhanVien> nv = nhanVienService.getNhanVienById(id);
        return nv.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Thêm nhân viên mới với multipart
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<NhanVien> createNhanVienWithMultipart(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("email") String email,
            @RequestParam("matKhau") String matKhau,
            @RequestParam(value = "sdt", required = false) String sdt,
            @RequestParam(value = "cccd", required = false) String cccd,
            @RequestParam(value = "ngaySinh", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date ngaySinh,
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
            try {
                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                String uploadDir = "uploads";
                Files.createDirectories(Paths.get(uploadDir));
                Path filePath = Paths.get(uploadDir, filename);
                Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setAvatar("/images/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Users createdUser = usersService.createUser(user);

        NhanVien nv = new NhanVien();
        nv.setHoTen(hoTen);
        nv.setEmail(email);
        nv.setMatKhau(matKhau);
        nv.setSdt(sdt);
        nv.setCccd(cccd);
        nv.setNgaySinh(ngaySinh);
        nv.setVaiTro(2);
        nv.setHinhAnh(user.getAvatar());
        nv.setUser(createdUser);

        return ResponseEntity.ok(nhanVienService.createNhanVien(nv));
    }

    // ✅ Cập nhật nhân viên
    @PutMapping("/id/{id}")
    public ResponseEntity<NhanVien> update(@PathVariable int id, @RequestBody NhanVien nhanVien) {
        NhanVien updated = nhanVienService.updateNhanVien(id, nhanVien);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ✅ Xoá nhân viên
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        nhanVienService.deleteNhanVien(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Tìm kiếm theo tên
    @GetMapping("/search")
    public List<NhanVien> searchByName(@RequestParam("keyword") String keyword) {
        return nhanVienService.searchByTen(keyword);
    }

    // ✅ Tìm theo email
    @GetMapping("/email/{email}")
    public ResponseEntity<NhanVien> searchByEmail(@PathVariable String email) {
        NhanVien nv = nhanVienService.searchByEmail(email);
        return nv != null ? ResponseEntity.ok(nv) : ResponseEntity.notFound().build();
    }

    // ✅ Tìm theo SDT
    @GetMapping("/sdt/{sdt}")
    public List<NhanVien> searchBySdt(@PathVariable String sdt) {
        return nhanVienService.searchBySdt(sdt);
    }

    // ✅ Tìm theo CCCD
    @GetMapping("/cccd/{cccd}")
    public ResponseEntity<NhanVien> searchByCccd(@PathVariable String cccd) {
        NhanVien nv = nhanVienService.searchByCccd(cccd);
        return nv != null ? ResponseEntity.ok(nv) : ResponseEntity.notFound().build();
    }
}