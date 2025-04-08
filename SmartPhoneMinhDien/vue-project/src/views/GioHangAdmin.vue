<template>
    <div class="container mt-4">
      <h4 class="mb-3">🛒 Danh sách giỏ hàng</h4>
      <table class="table table-bordered align-middle">
        <thead class="table-light">
          <tr>
            <th>Mã giỏ</th>
            <th>Khách hàng</th>
            <th>Số sản phẩm</th>
            <th>Ngày tạo</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="gh in gioHangList" :key="gh.maGioHang">
            <td>{{ gh.maGioHang }}</td>
            <td>{{ gh.khachHang?.tenKH }}</td>
            <td>{{ gh.soLuong }}</td>
            <td>{{ formatDate(gh.ngayTao) }}</td>
            <td>{{ gh.trangThai ? 'Hoạt động' : 'Đã đặt hàng' }}</td>
            <td>
              <router-link
                :to="`/admin/giohang/${gh.maGioHang}`"
                class="btn btn-sm btn-primary me-1"
              >
                Chi tiết
              </router-link>
              <button class="btn btn-sm btn-danger" @click="resetGioHang(gh.maGioHang)">
                Reset
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  
  const gioHangList = ref([]);
  
  const formatDate = (val) => {
    if (!val) return '';
    const date = new Date(val);
    return date.toISOString().slice(0, 10); // Trích yyyy-MM-dd
  };
  
  const loadGioHang = async () => {
    try {
      const res = await fetch('http://localhost:8080/smartphone/giohang');
      const list = await res.json();
  
      // Gọi chi tiết từng giỏ để đếm số sản phẩm
      for (const gh of list) {
        const resCT = await fetch(
          `http://localhost:8080/smartphone/giohangchitiet/giohang/${gh.maGioHang}`
        );
        const chiTiet = await resCT.json();
        gh.soLuong = chiTiet.length;
      }
  
      gioHangList.value = list;
    } catch (err) {
      console.error('❌ Lỗi khi tải giỏ hàng:', err);
    }
  };
  
  const resetGioHang = async (maGioHang) => {
    if (confirm('Bạn chắc chắn muốn reset giỏ hàng này?')) {
      try {
        await fetch(`http://localhost:8080/smartphone/giohangchitiet/reset/${maGioHang}`, {
          method: 'DELETE',
        });
        alert('✅ Giỏ hàng đã được reset!');
        loadGioHang();
      } catch (e) {
        alert('❌ Lỗi khi reset giỏ hàng');
      }
    }
  };
  
  onMounted(loadGioHang);
  </script>
  