package com.example.demo.repository;

import com.example.demo.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    // Tìm theo tên (chứa từ khoá, không phân biệt hoa thường)
    List<KhachHang> findByTenKHContainingIgnoreCase(String tenKH);

    // Tìm theo SDT
    List<KhachHang> findBySdt(String sdt);

    // Tìm theo Email
    KhachHang findByEmail(String email);
    
    Optional<KhachHang> findByUser_UserID(int userID); // ✅ Sửa đúng tại đây

}
