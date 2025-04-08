package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.GioHang;
import com.example.demo.model.GioHangChiTiet;
import com.example.demo.model.SanPham;

import java.util.List;


@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

   
    List<GioHangChiTiet> findByGioHang(GioHang gioHang);

 
    List<GioHangChiTiet> findBySanPham(SanPham sanPham);


    GioHangChiTiet findByGioHangAndSanPham(GioHang gioHang, SanPham sanPham);


    void deleteByGioHang(GioHang gioHang);
    
    
}
