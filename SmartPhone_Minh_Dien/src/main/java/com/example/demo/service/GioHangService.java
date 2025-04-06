// Updated `GioHangService.java` to validate and link KhachHang before creating a new GioHang

package com.example.demo.service;

import com.example.demo.model.GioHang;
import com.example.demo.model.KhachHang;
import com.example.demo.repository.GioHangRepository;
import com.example.demo.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<GioHang> getAll() {
        return gioHangRepository.findAll();
    }

    public Optional<GioHang> getById(int id) {
        return gioHangRepository.findById(id);
    }

    public GioHang create(GioHang gioHang) {
        // Kiểm tra khách hàng null
        if (gioHang.getKhachHang() == null || gioHang.getKhachHang().getMaKH() == 0) {
            throw new IllegalArgumentException("Khách hàng không hợp lệ");
        }

        // Load khách hàng đầy đủ từ DB
        KhachHang kh = khachHangRepository.findById(gioHang.getKhachHang().getMaKH())
                                          .orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại"));

        gioHang.setKhachHang(kh);
        return gioHangRepository.save(gioHang);
    }


    public GioHang update(int id, GioHang updated) {
        Optional<GioHang> existingOpt = gioHangRepository.findById(id);
        if (existingOpt.isPresent()) {
            GioHang existing = existingOpt.get();
            existing.setKhachHang(updated.getKhachHang());
            existing.setNgayTao(updated.getNgayTao());
            existing.setTrangThai(updated.isTrangThai());
            return gioHangRepository.save(existing);
        }
        return null;
    }

    public void delete(int id) {
        gioHangRepository.deleteById(id);
    }

    public List<GioHang> getByKhachHang(KhachHang khachHang) {
        return gioHangRepository.findByKhachHang(khachHang);
    }

    public List<GioHang> getByKhachHangAndTrangThai(KhachHang khachHang, boolean trangThai) {
        return gioHangRepository.findByKhachHangAndTrangThai(khachHang, trangThai);
    }

    public List<GioHang> getByTrangThai(boolean trangThai) {
        return gioHangRepository.findByTrangThai(trangThai);
    }
    
    
}