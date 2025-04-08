<template>
    <div class="container mt-4">
      <h4 class="mb-3 text-danger">
        🛒 Chi tiết giỏ hàng: {{ maGioHang }}
      </h4>
  
      <div v-if="items.length === 0" class="alert alert-info">
        Giỏ hàng trống.
      </div>
  
      <div v-else>
        <table class="table table-bordered align-middle">
          <thead class="table-light">
            <tr>
              <th>Hình ảnh</th>
              <th>Tên sản phẩm</th>
              <th>Màu sắc</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Tổng</th>
              <th>Xóa</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in items" :key="item.id">
              <td>
                <img
                  :src="item.image || placeholder"
                  class="img-thumbnail"
                  style="width: 80px; height: 80px; object-fit: cover"
                />
              </td>
              <td>{{ item.name }}</td>
              <td><i class="bi bi-palette"></i> {{ item.color }}</td>
              <td class="text-danger">{{ formatVND(item.price) }}</td>
              <td>{{ item.quantity }}</td>
              <td>{{ formatVND(item.price * item.quantity) }}</td>
              <td>
                <button class="btn btn-sm btn-danger" @click="removeItem(item.id)">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
  
        <div class="text-end fw-bold fs-5">
          Tổng cộng: <span class="text-danger">{{ formatVND(total) }}</span>
        </div>
      </div>
  
      <div class="mt-4 d-flex justify-content-between">
        <button class="btn btn-outline-secondary" @click="goBack">
          <i class="bi bi-arrow-left"></i> Quay lại danh sách
        </button>
  
        <button class="btn btn-danger" @click="resetGioHang">
          <i class="bi bi-x-circle"></i> Reset toàn bộ
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  
  const route = useRoute();
  const router = useRouter();
  const maGioHang = route.params.maGioHang;
  
  const placeholder = 'https://placehold.co/100x100?text=No+Image';
  const items = ref([]);
  
  const loadChiTiet = async () => {
    try {
      const res = await fetch(`http://localhost:8080/smartphone/giohangchitiet/giohang/${maGioHang}`);
      const data = await res.json();
      items.value = data.map(item => ({
        id: item.id,
        name: item.bienThe ? item.bienThe.sanPham.tenSP : item.sanPham.tenSP,
        image: item.bienThe?.hinhAnh || item.sanPham.hinhAnhSP,
        price: item.giaBan,
        quantity: item.soLuong,
        color: item.bienThe?.mauSac || item.sanPham?.mauSac || item.mauSac || 'Không rõ'
      }));
    } catch (e) {
      console.error('Lỗi tải chi tiết giỏ hàng:', e);
    }
  };
  
  const formatVND = (val) =>
    new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(val);
  
  const total = computed(() =>
    items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  );
  
  const removeItem = async (id) => {
    if (confirm('Xóa sản phẩm khỏi giỏ hàng?')) {
      try {
        await fetch(`http://localhost:8080/smartphone/giohangchitiet/id/${id}`, {
          method: 'DELETE'
        });
        items.value = items.value.filter(i => i.id !== id);
      } catch (e) {
        alert('❌ Không thể xoá sản phẩm');
      }
    }
  };
  
  const resetGioHang = async () => {
    if (confirm('Bạn chắc chắn muốn xóa toàn bộ sản phẩm trong giỏ hàng này?')) {
      try {
        await fetch(`http://localhost:8080/smartphone/giohangchitiet/giohang/${maGioHang}`, {
          method: 'DELETE'
        });
        items.value = [];
      } catch (e) {
        alert('❌ Không thể reset giỏ hàng!');
      }
    }
  };
  
  const goBack = () => {
    router.push('/admin/giohang');
  };
  
  onMounted(loadChiTiet);
  </script>
  
  <style scoped>
  .img-thumbnail {
    object-fit: cover;
  }
  </style>
  