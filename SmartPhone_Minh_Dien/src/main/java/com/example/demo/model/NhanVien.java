package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maNV;

    @Column(nullable = false)
    private String hoTen;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String matKhau;

    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    private String sdt;

    @Column(unique = true)
    private String cccd;

    private int vaiTro; 

    private String hinhAnh;

    @ManyToOne
    @JoinColumn(name = "userID")
    private Users user;
    
    // 👇 Lấy thông tin từ Users mà không cần DTO
    @Transient
    public Date getNgayTao() {
        return user != null ? user.getNgayTao() : null;
    }

    @Transient
    public String getAvatar() {
        return hinhAnh;
    }


    @Transient
    public int getUserID() {
        return user != null ? user.getUserID() : 0;
    }
    
    
}
