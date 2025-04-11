<template>
  <div class="container my-5">
    <div class="card p-4 shadow">
      <h4 class="text-center mb-4">🛒 <strong>CÔNG MINH MOBILE</strong></h4>
      <div class="row">
        <div class="col-md-8">
          <h5 class="mb-3">Thông tin nhận hàng</h5>
          <form @submit.prevent="submitOrder">
            <input v-model="form.hoTen" class="form-control mb-3" placeholder="Họ và tên" required />
            <input v-model="form.sdtGiaoHang" class="form-control mb-3" placeholder="Số điện thoại" required pattern="0[0-9]{9}" />
            <input v-model="form.diaChiGiaoHang" class="form-control mb-3" placeholder="Địa chỉ" required />
            <textarea v-model="form.ghiChu" class="form-control mb-3" placeholder="Ghi chú (tuỳ chọn)"></textarea>
            <h6 class="fw-bold">Hình thức thanh toán</h6>
            <div class="form-check">
              <input v-model="form.phuongThucThanhToan" class="form-check-input" type="radio" value="COD" id="cod" checked />
              <label class="form-check-label" for="cod">Thanh toán khi nhận hàng</label>
            </div>
            <div class="form-check mb-4">
              <input v-model="form.phuongThucThanhToan" class="form-check-input" type="radio" value="Online" id="online" />
              <label class="form-check-label" for="online">Thanh toán online</label>
            </div>
            <button type="submit" class="btn btn-danger">🛒 ĐẶT HÀNG</button>
          </form>
        </div>

        <div class="col-md-4">
          <div class="bg-light p-3 rounded">
            <h5>Đơn hàng của bạn</h5>
            <div v-for="item in selectedItems" :key="item.id" class="d-flex mb-2 border-bottom pb-2">
              <img :src="item.image" width="50" height="50" class="me-2 rounded" />
              <div class="flex-grow-1">
                <div class="fw-bold">{{ item.name }}</div>
                <div class="text-muted">Số lượng: {{ item.quantity }}</div>
              </div>
              <div class="fw-bold">{{ formatVND(item.price) }}</div>
            </div>
            <div class="fw-bold text-danger fs-5 mt-3 mb-3">Tổng cộng: {{ formatVND(tongTien) }}</div>
            <button class="btn btn-primary w-100" @click="goBack">Quay về giỏ hàng</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const maKH = route.params.id;

const selectedItems = ref([]);
const form = ref({
  hoTen: '',
  sdtGiaoHang: '',
  diaChiGiaoHang: '',
  ghiChu: '',
  phuongThucThanhToan: 'COD'
});

const formatVND = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const tongTien = computed(() =>
  selectedItems.value.reduce((sum, item) => {
    const gia = typeof item.price === 'string' ? parseInt(item.price.replace(/\D/g, '')) : item.price;
    return sum + gia * item.quantity;
  }, 0)
);

const loadSelectedItems = () => {
  const data = localStorage.getItem('selectedItems');
  if (data) selectedItems.value = JSON.parse(data);
};

onMounted(() => {
  loadSelectedItems();
});

const submitOrder = async () => {
  try {
    // Gửi đơn hàng
    const resDonHang = await fetch('http://localhost:8080/smartphone/donhang', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        khachHang: { maKH },
        ngayDat: new Date().toISOString(),
        tongTien: tongTien.value,
        phuongThucThanhToan: form.value.phuongThucThanhToan,
        trangThai: 'Chờ xử lý',
        diaChiGiaoHang: form.value.diaChiGiaoHang,
        sdtGiaoHang: form.value.sdtGiaoHang
      })
    });

    if (!resDonHang.ok) throw new Error('Lỗi khi tạo đơn hàng');
    const donHang = await resDonHang.json();
    const maDonHang = donHang.maDonHang;

    // Gửi chi tiết đơn hàng
    for (const item of selectedItems.value) {
      const chiTiet = {
        donHang: { maDonHang },
        soLuong: item.quantity,
        giaBan: item.price
      };

      if (item.maBienThe) {
        chiTiet.bienTheSanPham = { maBienThe: item.maBienThe };
      } else {
        chiTiet.sanPham = { maSP: item.maSP };
      }

      const resCT = await fetch('http://localhost:8080/smartphone/donhangchitiet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(chiTiet)
      });

      if (!resCT.ok) throw new Error('Lỗi khi tạo chi tiết đơn hàng');
    }

    // Gửi hóa đơn
    const resHD = await fetch('http://localhost:8080/smartphone/hoadon', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        khachHang: { maKH },
        tongSoLuong: selectedItems.value.reduce((sum, i) => sum + i.quantity, 0),
        thanhTien: tongTien.value,
        hinhThucThanhToan: form.value.phuongThucThanhToan,
        ngayTao: new Date().toISOString(),
        nguoiTao: form.value.hoTen,
        trangThai: true
      })
    });

    if (!resHD.ok) throw new Error('Lỗi khi tạo hóa đơn');
    const hoaDon = await resHD.json();
    const maHD = hoaDon.maHD;

    // Gửi chi tiết hóa đơn
    for (const item of selectedItems.value) {
      const chiTietHD = {
        hoaDon: { maHD },
        soLuong: item.quantity,
        giaBan: item.price,
        sanPham: { maSP: item.maSP }
      };

      const resCTHD = await fetch('http://localhost:8080/smartphone/hoadonchitiet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(chiTietHD)
      });

      if (!resCTHD.ok) throw new Error('Lỗi khi tạo chi tiết hóa đơn');
    }

    // Lưu dữ liệu vào localStorage
    localStorage.setItem('hoaDonInfo', JSON.stringify({
      hoTen: form.value.hoTen,
      sdt: form.value.sdtGiaoHang,
      diaChi: form.value.diaChiGiaoHang,
      ghiChu: form.value.ghiChu,
      ngayDat: new Date().toLocaleString(),
      tongTien: tongTien.value,
      sanPhamDat: selectedItems.value
    }));

    localStorage.removeItem('selectedItems');
    router.push(`/hoadon/${maKH}`);
  } catch (error) {
    console.error('❌ Đặt hàng thất bại:', error);
    alert('❌ Có lỗi xảy ra khi đặt hàng: ' + error.message);
  }
};

const goBack = () => router.push('/giohang');
</script>

<style scoped>
.card {
  max-width: 1000px;
  margin: auto;
}
</style>
