<template>
    <div class="container py-5">
      <div class="text-center mb-4">
        <img src="https://images.unsplash.com/photo-1633409361618-c73427e4e206" alt="Logo" height="80" class="mb-3" />
        <h2 class="text-primary fw-bold">Đặt hàng thành công</h2>
        <div class="alert alert-success mt-3">
          Cảm ơn bạn đã đặt hàng! Chúng tôi sẽ liên hệ với bạn sớm nhất.
        </div>
      </div>
  
      <div class="card p-3 mb-4">
        <h5 class="fw-bold border-bottom pb-2">Thông tin đơn hàng</h5>
        <p><strong>Họ và tên:</strong> {{ hoaDon.hoTen }}</p>
        <p><strong>Số điện thoại:</strong> {{ hoaDon.sdt }}</p>
        <p><strong>Địa chỉ:</strong> {{ hoaDon.diaChi }}</p>
        <p v-if="hoaDon.ghiChu"><strong>Ghi chú:</strong> {{ hoaDon.ghiChu }}</p>
        <p><strong>Ngày đặt hàng:</strong> {{ hoaDon.ngayDat }}</p>
      </div>
  
      <div class="card p-3 mb-4">
        <h5 class="fw-bold border-bottom pb-2">Sản phẩm đã đặt</h5>
        <div v-for="item in hoaDon.sanPhamDat" :key="item.id" class="d-flex align-items-center border-bottom py-2">
          <img :src="item.image" alt="sp" width="60" height="60" class="me-3 rounded" />
          <div class="flex-grow-1">
            <div class="fw-semibold">{{ item.name }}</div>
            <small class="text-muted">Số lượng: {{ item.quantity }}</small>
          </div>
          <div class="fw-bold">{{ formatVND(item.price) }}</div>
        </div>
      </div>
  
      <div class="card p-3 mb-4">
        <h5 class="fw-bold mb-2">Tổng tiền</h5>
        <div class="text-danger fs-4 fw-bold">{{ formatVND(hoaDon.tongTien) }}</div>
      </div>
  
      <div class="text-center">
        <button class="btn btn-primary" @click="goHome">Tiếp tục mua sắm</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  import { ref, onMounted } from 'vue';
  
  const router = useRouter();
  const hoaDon = ref({
    hoTen: '',
    sdt: '',
    diaChi: '',
    ghiChu: '',
    ngayDat: '',
    tongTien: 0,
    sanPhamDat: []
  });
  
  const formatVND = (val) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  
  const goHome = () => router.push('/');
  
  onMounted(() => {
    const data = localStorage.getItem('hoaDonInfo');
    if (data) {
      hoaDon.value = JSON.parse(data);
    } else {
      alert('Không tìm thấy hóa đơn!');
      router.push('/');
    }
  });
  </script>
  
  <style scoped>
  .card {
    max-width: 800px;
    margin: auto;
  }
  </style>
  