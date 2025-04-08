package com.example.demo.controller;

import com.example.demo.model.GioHang;
import com.example.demo.model.GioHangChiTiet;
import com.example.demo.model.SanPham;
import com.example.demo.service.GioHangChiTietService;
import com.example.demo.service.GioHangService;
import com.example.demo.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/smartphone/giohangchitiet")
public class GioHangChiTietController {

    @Autowired
    private GioHangChiTietService chiTietService;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private SanPhamService sanPhamService;

    // ✅ Lấy tất cả chi tiết giỏ hàng
    @GetMapping
    public List<GioHangChiTiet> getAll() {
        return chiTietService.getAll();
    }

    // ✅ Lấy chi tiết theo ID
    @GetMapping("/id/{id}")
    public ResponseEntity<GioHangChiTiet> getById(@PathVariable int id) {
        Optional<GioHangChiTiet> ct = chiTietService.getById(id);
        return ct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Thêm mới chi tiết
    @PostMapping
    public ResponseEntity<GioHangChiTiet> create(@RequestBody GioHangChiTiet chiTiet) {
        GioHangChiTiet created = chiTietService.create(chiTiet);
        return ResponseEntity.ok(created);
    }

    // ✅ Cập nhật chi tiết
    @PutMapping("/id/{id}")
    public ResponseEntity<GioHangChiTiet> update(@PathVariable int id, @RequestBody GioHangChiTiet chiTiet) {
        GioHangChiTiet updated = chiTietService.update(id, chiTiet);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Xoá theo ID
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        chiTietService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Xoá tất cả chi tiết theo giỏ hàng
    @DeleteMapping("/giohang/{maGioHang}")
    public ResponseEntity<Void> deleteByGioHang(@PathVariable int maGioHang) {
        Optional<GioHang> gioHang = gioHangService.getById(maGioHang);
        if (gioHang.isPresent()) {
            chiTietService.deleteByGioHang(gioHang.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Lấy danh sách chi tiết theo giỏ hàng
    @GetMapping("/giohang/{maGioHang}")
    public ResponseEntity<List<GioHangChiTiet>> getByGioHang(@PathVariable int maGioHang) {
        Optional<GioHang> gioHang = gioHangService.getById(maGioHang);
        return gioHang.map(g ->
                ResponseEntity.ok(chiTietService.getByGioHang(g)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Lấy danh sách chi tiết theo sản phẩm
    @GetMapping("/sanpham/{maSP}")
    public ResponseEntity<List<GioHangChiTiet>> getBySanPham(@PathVariable int maSP) {
        Optional<SanPham> sp = sanPhamService.getById(maSP);
        return sp.map(s ->
                ResponseEntity.ok(chiTietService.getBySanPham(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Lấy chi tiết theo giỏ hàng và sản phẩm
    @GetMapping("/giohang/{maGioHang}/sanpham/{maSP}")
    public ResponseEntity<GioHangChiTiet> getByGioHangAndSanPham(@PathVariable int maGioHang, @PathVariable int maSP) {
        Optional<GioHang> gioHang = gioHangService.getById(maGioHang);
        Optional<SanPham> sanPham = sanPhamService.getById(maSP);
        if (gioHang.isPresent() && sanPham.isPresent()) {
            GioHangChiTiet result = chiTietService.getByGioHangAndSanPham(gioHang.get(), sanPham.get());
            if (result != null) {
                return ResponseEntity.ok(result);
            }
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/reset/{maGioHang}")
    public ResponseEntity<Void> reset(@PathVariable int maGioHang) {
        chiTietService.deleteByGioHang(maGioHang); // 👍 Gọi theo ID
        return ResponseEntity.noContent().build();
    }


    
}
