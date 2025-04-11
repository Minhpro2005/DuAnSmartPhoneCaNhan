package com.example.demo.controller;

import com.example.demo.model.KhachHang;
import com.example.demo.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/smartphone")
@CrossOrigin("*")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    // ✅ 1. Lấy danh sách tất cả khách hàng
    @GetMapping("/khachhang")
    public List<KhachHang> getAllKhachHang() {
        return khachHangService.getAllKhachHang();
    }

    // ✅ 2. Lấy khách hàng theo mã ID (primary key)
    @GetMapping("/khachhang/{id}")
    public ResponseEntity<KhachHang> getKhachHangById(@PathVariable int id) {
        Optional<KhachHang> kh = khachHangService.getKhachHangById(id);
        return kh.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 3. Lấy khách hàng theo userID (liên kết với bảng Users)
    @GetMapping("/khachhang/user/{userID}")
    public ResponseEntity<KhachHang> getByUserID(@PathVariable int userID) {
        return khachHangService.getByUserID(userID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 4. Tạo khách hàng mới
    @PostMapping("/khachhang")
    public ResponseEntity<KhachHang> createKhachHang(@RequestBody KhachHang khachHang) {
        KhachHang created = khachHangService.createKhachHang(khachHang);
        return ResponseEntity.ok(created);
    }

    // ✅ 5. Cập nhật khách hàng (dựa vào ID khách hàng)
    @PutMapping("/khachhang/{id}")
    public ResponseEntity<KhachHang> updateKhachHang(@PathVariable int id, @RequestBody KhachHang khachHang) {
        KhachHang updated = khachHangService.updateKhachHang(id, khachHang);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    // ✅ 6. Xóa khách hàng
    @DeleteMapping("/khachhang/{id}")
    public ResponseEntity<Void> deleteKhachHang(@PathVariable int id) {
        khachHangService.deleteKhachHang(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 7. Tìm kiếm theo tên (keyword)
    @GetMapping("/khachhang/search")
    public List<KhachHang> searchByName(@RequestParam("keyword") String keyword) {
        return khachHangService.searchByTen(keyword);
    }

    // ✅ 8. Tìm theo số điện thoại
    @GetMapping("/khachhang/sdt/{sdt}")
    public List<KhachHang> searchBySdt(@PathVariable String sdt) {
        return khachHangService.searchBySdt(sdt);
    }

    // ✅ 9. Tìm theo Email
    @GetMapping("/khachhang/email/{email}")
    public ResponseEntity<KhachHang> searchByEmail(@PathVariable String email) {
        KhachHang kh = khachHangService.searchByEmail(email);
        return kh != null
                ? ResponseEntity.ok(kh)
                : ResponseEntity.notFound().build();
    }

    // ✅ 10. Cập nhật địa chỉ theo userID (cho Thông tin cá nhân khách hàng)
    @PutMapping("/khachhang/user/{userID}")
    public ResponseEntity<KhachHang> updateDiaChi(@PathVariable int userID, @RequestBody Map<String, String> body) {
        Optional<KhachHang> optionalKH = khachHangService.getByUserID(userID);
        if (optionalKH.isEmpty()) return ResponseEntity.notFound().build();

        KhachHang kh = optionalKH.get();
        kh.setDiaChi(body.getOrDefault("diaChi", ""));
        return ResponseEntity.ok(khachHangService.save(kh));
    }
}
