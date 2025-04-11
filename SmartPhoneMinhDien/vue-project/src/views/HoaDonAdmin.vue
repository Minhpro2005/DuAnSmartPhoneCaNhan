<template>
    <div class="container py-4">
      <h4 class="text-primary mb-3">
        <i class="bi bi-receipt"></i> Danh sách hóa đơn
      </h4>
  
      <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th>Mã HĐ</th>
              <th>Khách hàng</th>
              <th>Ngày tạo</th>
              <th>Khuyến mãi</th>
              <th>Người lập</th>
              <th>Tổng tiền</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="hoa in hoaDons" :key="hoa.maHD">
              <td>{{ hoa.maHD }}</td>
              <td>{{ hoa.khachHang ? hoa.khachHang.tenKH : 'Ẩn' }}</td>
              <td>{{ formatDate(hoa.ngayTao) }}</td>
              <td>{{ hoa.khuyenMai ? hoa.khuyenMai.tenKM : 'Không' }}</td>
              <td>{{ hoa.nguoiTao || 'Ẩn' }}</td>
              <td class="text-danger">
                {{ hoa.thanhTien ? formatVND(hoa.thanhTien) : '0 ₫' }}
              </td>
              <td>
                <router-link :to="`/admin/hoadon/${hoa.maHD}`" class="btn btn-info btn-sm">
                  Chi tiết
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'HoaDonAdmin',
    data() {
      return {
        hoaDons: []
      };
    },
    methods: {
      async fetchHoaDons() {
        try {
          const res = await fetch('http://localhost:8080/smartphone/hoadon');
          this.hoaDons = await res.json();
        } catch (e) {
          console.error('Lỗi khi tải hóa đơn:', e);
        }
      },
      formatDate(date) {
        return date ? new Date(date).toLocaleDateString('vi-VN') : '';
      },
      formatVND(val) {
        if (!val || isNaN(val)) return '0 ₫';
        return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
        }).format(val);
      }
    },
    mounted() {
      this.fetchHoaDons();
    }
  };
  </script>
  
  <style scoped>
  .table img {
    object-fit: cover;
    height: 50px;
    border-radius: 4px;
  }
  </style>
  