<template>
    <div class="container mt-4">
      <h3 class="mb-3 text-primary">📄 Chi tiết hóa đơn #{{ hoaDon?.maHD }}</h3>
  
      <div v-if="hoaDon">
        <div class="row mb-4">
          <div class="col-md-6">
            <p><strong>Khách hàng:</strong> {{ hoaDon.khachHang?.tenKH }}</p>
            <p><strong>Email:</strong> {{ hoaDon.khachHang?.email }}</p>
            <p><strong>SĐT:</strong> {{ hoaDon.khachHang?.sdt }}</p>
          </div>
          <div class="col-md-6">
            <p><strong>Người lập:</strong> {{ hoaDon.nguoiTao }}</p>
            <p><strong>Ngày tạo:</strong> {{ formatDate(hoaDon.ngayTao) }}</p>
            <p><strong>Khuyến mãi:</strong> {{ hoaDon.khuyenMai?.tenKM || 'Không áp dụng' }}</p>
          </div>
        </div>
  
        <table class="table table-bordered align-middle">
          <thead class="table-light">
            <tr>
              <th>Ảnh</th>
              <th>Mã SP</th>
              <th>Tên sản phẩm</th>
              <th>Màu sắc</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Tổng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in chiTiet" :key="item.id">
              <td>
                <img
                  :src="item.bienThe?.hinhAnh || item.sanPham?.hinhAnhSP || placeholder"
                  class="img-thumbnail"
                  style="width: 80px; height: 80px; object-fit: cover"
                />
              </td>
              <td>{{ item.sanPham?.maSP }}</td>
              <td>{{ item.sanPham?.tenSP }}</td>
              <td>{{ item.bienThe?.mauSac || item.sanPham?.mauSac || 'Không rõ' }}</td>
              <td>{{ formatVND(item.giaBan) }}</td>
              <td>{{ item.soLuong }}</td>
              <td>{{ formatVND(item.giaBan * item.soLuong) }}</td>
            </tr>
          </tbody>
        </table>
  
        <div class="text-end fs-5 fw-bold text-danger">
          Thành tiền: {{ formatVND(hoaDon.thanhTien) }}
        </div>
      </div>
      <div v-else class="alert alert-info">Đang tải dữ liệu hóa đơn...</div>
  
      <div class="mt-3">
        <button class="btn btn-outline-secondary" @click="goBack">
          <i class="bi bi-arrow-left"></i> Quay lại danh sách
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  
  const route = useRoute();
  const router = useRouter();
  const maHD = route.params.id;
  
  const hoaDon = ref(null);
  const chiTiet = ref([]);
  const placeholder = 'https://placehold.co/100x100?text=No+Image';
  
  const formatVND = (val) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  
  const formatDate = (dateStr) =>
    new Date(dateStr).toLocaleDateString('vi-VN');
  
  const loadData = async () => {
    try {
      const resHD = await fetch(`http://localhost:8080/smartphone/hoadon/id/${maHD}`);
      hoaDon.value = await resHD.json();
  
      const resCT = await fetch(`http://localhost:8080/smartphone/hoadonchitiet/hoadon/${maHD}`);
      chiTiet.value = await resCT.json();
    } catch (e) {
      console.error('Lỗi tải hóa đơn:', e);
    }
  };
  
  const goBack = () => {
    router.push('/admin/hoadon');
  };
  
  onMounted(loadData);
  </script>
  
  <style scoped>
  .img-thumbnail {
    object-fit: cover;
  }
  .table td,
  .table th {
    vertical-align: middle;
  }
  </style>
  