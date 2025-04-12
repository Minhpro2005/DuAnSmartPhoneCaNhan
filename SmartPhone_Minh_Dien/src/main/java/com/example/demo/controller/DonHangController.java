package com.example.demo.controller;

import com.example.demo.model.DonHang;
import com.example.demo.model.KhachHang;
import com.example.demo.service.DonHangService;
import com.example.demo.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/smartphone/donhang")
public class DonHangController {

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private KhachHangService khachHangService;

    // ✅ Lấy tất cả đơn hàng
    @GetMapping
    public List<DonHang> getAll() {
        return donHangService.getAll();
    }

    // ✅ Lấy đơn hàng theo ID
    @GetMapping("/id/{id}")
    public ResponseEntity<DonHang> getById(@PathVariable int id) {
        Optional<DonHang> dh = donHangService.getById(id);
        return dh.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DonHang>> getByUserIdAndFilters(
            @PathVariable int userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date start = (startDate != null) ? sdf.parse(startDate) : new Date(0); // từ năm 1970
            Date end = (endDate != null) ? sdf.parse(endDate) : new Date();        // hiện tại

            if (status != null && !status.isBlank()) {
                return ResponseEntity.ok(donHangService.getByUserIdAndFilters(userId, start, end, status));
            } else {
                // Nếu không có status thì chỉ lọc theo ngày
                List<DonHang> all = donHangService.getByUserId(userId);
                return ResponseEntity.ok(
                    all.stream()
                       .filter(dh -> !dh.getNgayDat().before(start) && !dh.getNgayDat().after(end))
                       .toList()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }



    // ✅ Thêm đơn hàng
    @PostMapping
    public ResponseEntity<DonHang> create(@RequestBody DonHang donHang) {
        DonHang created = donHangService.create(donHang);
        return ResponseEntity.ok(created);
    }

    // ✅ Cập nhật đơn hàng
    @PutMapping("/id/{id}")
    public ResponseEntity<DonHang> update(@PathVariable int id, @RequestBody DonHang donHang) {
        DonHang updated = donHangService.update(id, donHang);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Xoá đơn hàng
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        donHangService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Lấy đơn hàng theo mã khách hàng
    @GetMapping("/khachhang/{maKH}")
    public ResponseEntity<List<DonHang>> getByKhachHang(@PathVariable int maKH) {
        Optional<KhachHang> kh = khachHangService.getKhachHangById(maKH);
        return kh.map(value -> ResponseEntity.ok(donHangService.getByKhachHang(value)))
                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Lấy đơn hàng theo trạng thái
    @GetMapping("/trangthai/{trangThai}")
    public List<DonHang> getByTrangThai(@PathVariable String trangThai) {
        return donHangService.getByTrangThai(trangThai);
    }

    // ✅ Lọc đơn hàng theo khách hàng và trạng thái
    @GetMapping("/khachhang/{maKH}/trangthai/{trangThai}")
    public ResponseEntity<List<DonHang>> getByKhachHangAndTrangThai(@PathVariable int maKH,
                                                                     @PathVariable String trangThai) {
        Optional<KhachHang> kh = khachHangService.getKhachHangById(maKH);
        return kh.map(value -> ResponseEntity.ok(donHangService.getByKhachHangAndTrangThai(value, trangThai)))
                 .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/xacnhan/{id}")
    public ResponseEntity<String> xacNhanDonHang(@PathVariable int id) {
        try {
            donHangService.xacNhanDonHangVaCapNhatSoLuong(id);
            return ResponseEntity.ok("Đơn hàng đã được xác nhận và cập nhật tồn kho.");
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi xác nhận đơn hàng: " + e.getMessage());
            return ResponseEntity.badRequest().body("Có lỗi xảy ra: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi không xác định.");
        }
    }

}
