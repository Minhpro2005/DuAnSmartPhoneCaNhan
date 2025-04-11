package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "DonHangChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "maDonHang")
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "maSP", nullable = true) // ✅ Cho phép null
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "maBienThe", nullable = true) // ✅ Cho phép null
    private BienTheSanPham bienTheSanPham;

    @Column(nullable = false)
    private int soLuong = 1;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;
}
