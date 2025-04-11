package com.example.demo.service;

import com.example.demo.model.BienTheSanPham;
import com.example.demo.model.DonHang;
import com.example.demo.model.DonHangChiTiet;
import com.example.demo.model.KhachHang;
import com.example.demo.repository.BienTheSanPhamRepository;
import com.example.demo.repository.DonHangChiTietRepository;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.SanPhamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheRepo;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;
    
    @Autowired
    private SanPhamRepository sanPhamRepository;

    public List<DonHang> getAll() {
        return donHangRepository.findAll();
    }

    public Optional<DonHang> getById(int id) {
        return donHangRepository.findById(id);
    }

    public DonHang create(DonHang donHang) {
        return donHangRepository.save(donHang);
    }

    public DonHang update(int id, DonHang updated) {
        Optional<DonHang> existingOpt = donHangRepository.findById(id);
        if (existingOpt.isPresent()) {
            DonHang existing = existingOpt.get();
            existing.setKhachHang(updated.getKhachHang());
            existing.setNgayDat(updated.getNgayDat());
            existing.setTongTien(updated.getTongTien());
            existing.setPhuongThucThanhToan(updated.getPhuongThucThanhToan());
            existing.setTrangThai(updated.getTrangThai());
            existing.setDiaChiGiaoHang(updated.getDiaChiGiaoHang());
            existing.setSdtGiaoHang(updated.getSdtGiaoHang());
            return donHangRepository.save(existing);
        }
        return null;
    }

    public void delete(int id) {
        donHangRepository.deleteById(id);
    }

    public List<DonHang> getByKhachHang(KhachHang khachHang) {
        return donHangRepository.findByKhachHang(khachHang);
    }

    public List<DonHang> getByTrangThai(String trangThai) {
        return donHangRepository.findByTrangThai(trangThai);
    }

    public List<DonHang> getByKhachHangAndTrangThai(KhachHang khachHang, String trangThai) {
        return donHangRepository.findByKhachHangAndTrangThai(khachHang, trangThai);
    }

    @Transactional
    public void xacNhanDonHangVaCapNhatSoLuong(int maDonHang) {
        Optional<DonHang> opt = donHangRepository.findById(maDonHang);
        if (opt.isEmpty()) throw new RuntimeException("Không tìm thấy đơn hàng");

        DonHang dh = opt.get();

        if (!"Chờ xử lý".equals(dh.getTrangThai())) {
            throw new RuntimeException("Đơn hàng không ở trạng thái 'Chờ xử lý'");
        }

        List<DonHangChiTiet> chiTietList = donHangChiTietRepository.findByDonHang_MaDonHang(maDonHang);
        if (chiTietList.isEmpty()) return;

        for (DonHangChiTiet ct : chiTietList) {
            if (ct.getBienTheSanPham() != null) {
                BienTheSanPham bienThe = ct.getBienTheSanPham();
                if (bienThe.getSoLuong() < ct.getSoLuong()) {
                    throw new RuntimeException("Không đủ hàng cho biến thể: " + bienThe.getTenBienThe());
                }
                bienThe.setSoLuong(bienThe.getSoLuong() - ct.getSoLuong());
                bienTheRepo.save(bienThe);
            } else if (ct.getSanPham() != null) {
                var sp = ct.getSanPham();
                if (sp.getSoLuong() < ct.getSoLuong()) {
                    throw new RuntimeException("Không đủ hàng cho sản phẩm: " + sp.getTenSP());
                }
                sp.setSoLuong(sp.getSoLuong() - ct.getSoLuong());
                sanPhamRepository.save(sp);
            } else {
                throw new RuntimeException("Chi tiết đơn hàng không có sản phẩm hoặc biến thể.");
            }
        }

        dh.setTrangThai("Đã xác nhận");
        donHangRepository.save(dh);
    }


}
