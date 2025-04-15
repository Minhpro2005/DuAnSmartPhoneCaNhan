<template>
  <div class="container my-5">
    <div class="row g-5">
      <div class="col-md-6">
        <img :src="selectedVariant?.hinhAnh || product.hinhAnhSP || placeholderImg" class="img-fluid rounded shadow" />
      </div>

      <div class="col-md-6">
        <h2 class="fw-bold mb-3">{{ product.tenSP }}</h2>
        <h4 class="text-danger mb-4">{{ formatPrice(selectedVariant?.giaBan || product.giaBan) }}</h4>

        <p><strong>Hãng:</strong> {{ product.hang }}</p>
        <p><strong>Màu sắc:</strong> {{ selectedVariant?.mauSac || product.mauSac }}</p>
        <p><strong>Số lượng còn:</strong> {{ selectedVariant?.soLuong || product.soLuong }}</p>
        <p><strong>Trạng thái:</strong>
          <span :class="(selectedVariant?.trangThai ?? product.trangThai) ? 'text-success' : 'text-danger'">
            {{ (selectedVariant?.trangThai ?? product.trangThai) ? 'Còn hàng' : 'Hết hàng' }}
          </span>
        </p>

        <div class="mb-4">
          <label class="form-label fw-bold">Chọn màu</label>
          <div class="d-flex gap-3 flex-wrap">
            <div class="variant-card" :class="{ active: !selectedVariant }" @click="selectVariant(null)">
              <img :src="product.hinhAnhSP || placeholderImg" />
              <div class="variant-name">{{ product.mauSac || 'Mặc định' }}</div>
            </div>
            <div v-for="v in variants" :key="v.maBienThe" class="variant-card" :class="{ active: selectedVariant?.maBienThe === v.maBienThe }" @click="selectVariant(v)">
              <img :src="v.hinhAnh || placeholderImg" />
              <div class="variant-name">{{ v.mauSac }}</div>
            </div>
          </div>
        </div>

        <div class="input-group mb-4" style="max-width: 160px">
          <button class="btn btn-outline-secondary" @click="decreaseQty">-</button>
          <input type="text" class="form-control text-center" :value="quantity" readonly />
          <button class="btn btn-outline-secondary" @click="increaseQty">+</button>
        </div>

        <button class="btn btn-primary me-2" @click="addToCart">Thêm vào giỏ hàng</button>
        <RouterLink to="/giohang" class="btn btn-outline-secondary">Xem giỏ hàng</RouterLink>
      </div>
    </div>
  </div>

  <!-- ✅ Toast Thông báo -->
  <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999">
    <div class="toast align-items-center text-white" :class="toastType === 'success' ? 'bg-success' : 'bg-danger'" role="alert" :id="'mainToast'" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">
          {{ toastMessage }}
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, RouterLink } from 'vue-router';
import * as bootstrap from 'bootstrap';

const route = useRoute();
const product = ref({});
const variants = ref([]);
const selectedVariant = ref(null);
const quantity = ref(1);
const placeholderImg = 'https://placehold.co/400x300?text=No+Image';

// Toast state
const toastMessage = ref('');
const toastType = ref('success'); // success | error

const showToast = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;

  const toastEl = document.getElementById('mainToast');
  const toast = new bootstrap.Toast(toastEl);
  toast.show();
};

const fetchProduct = async () => {
  const res = await fetch(`http://localhost:8080/smartphone/sanpham/id/${route.params.id}`);
  product.value = await res.json();
};

const fetchVariants = async () => {
  const res = await fetch(`http://localhost:8080/smartphone/bienthe/sanpham/${route.params.id}`);
  variants.value = await res.json();
};

const formatPrice = (price) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

const increaseQty = () => {
  if (quantity.value < (selectedVariant.value?.soLuong || product.value.soLuong)) quantity.value++;
};
const decreaseQty = () => {
  if (quantity.value > 1) quantity.value--;
};
const selectVariant = (variant) => {
  selectedVariant.value = variant;
  quantity.value = 1;
};

const addToCart = async () => {
  try {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.userID) {
      showToast('Vui lòng đăng nhập trước khi thêm vào giỏ hàng!', 'error');
      return;
    }

    const resKH = await fetch(`http://localhost:8080/smartphone/khachhang/user/${user.userID}`);
    if (!resKH.ok) throw new Error("Không tìm thấy thông tin khách hàng");
    const khachHang = await resKH.json();
    const maKH = khachHang.maKH;

    let gioHangRes = await fetch(`http://localhost:8080/smartphone/giohang/khachhang/${maKH}/trangthai/true`);
    let gioHangList = gioHangRes.ok ? await gioHangRes.json() : [];
    let gioHang = gioHangList.length ? gioHangList[0] : null;

    if (!gioHang) {
      const now = new Date().toISOString().split('T')[0];
      const createRes = await fetch(`http://localhost:8080/smartphone/giohang`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          khachHang: { maKH },
          trangThai: true,
          ngayTao: now
        })
      });
      if (!createRes.ok) throw new Error("Tạo giỏ hàng thất bại!");
      gioHang = await createRes.json();
    }

    const chiTietRes = await fetch(`http://localhost:8080/smartphone/giohangchitiet/giohang/${gioHang.maGioHang}`);
    const chiTietList = await chiTietRes.json();

    const existingItem = chiTietList.find(item =>
      item.sanPham.maSP === (selectedVariant.value?.sanPham?.maSP || product.value.maSP) &&
      (item.bienThe?.maBienThe || null) === (selectedVariant.value?.maBienThe || null)
    );

    if (existingItem) {
      const updatedSoLuong = existingItem.soLuong + quantity.value;
      await fetch(`http://localhost:8080/smartphone/giohangchitiet/id/${existingItem.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          id: existingItem.id,
          gioHang: { maGioHang: gioHang.maGioHang },
          sanPham: { maSP: existingItem.sanPham.maSP },
          bienThe: existingItem.bienThe ? { maBienThe: existingItem.bienThe.maBienThe } : null,
          giaBan: existingItem.giaBan,
          mauSac: existingItem.mauSac,
          soLuong: updatedSoLuong
        })
      });
    } else {
      await fetch(`http://localhost:8080/smartphone/giohangchitiet`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          gioHang: { maGioHang: gioHang.maGioHang },
          sanPham: { maSP: selectedVariant.value?.sanPham?.maSP || product.value.maSP },
          bienThe: selectedVariant.value?.maBienThe ? { maBienThe: selectedVariant.value.maBienThe } : null,
          mauSac: selectedVariant.value?.mauSac || product.value.mauSac,
          soLuong: quantity.value,
          giaBan: selectedVariant.value?.giaBan || product.value.giaBan
        })
      });
    }

    showToast('✅ Sản phẩm đã được thêm vào giỏ hàng!', 'success');

  } catch (error) {
    console.error("❌ Lỗi khi thêm giỏ hàng:", error);
    showToast('Thêm vào giỏ hàng thất bại!', 'error');
  }
};

onMounted(() => {
  fetchProduct();
  fetchVariants();
});
</script>

<style scoped>
img {
  max-height: 400px;
  object-fit: contain;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.variant-card {
  border: 2px solid #dee2e6;
  border-radius: 12px;
  padding: 10px;
  width: 120px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(to bottom right, #fff, #f8f9fa);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.variant-card:hover {
  transform: scale(1.03);
  border-color: #007bff;
}

.variant-card.active {
  border: 2px solid #007bff;
  background: #e7f1ff;
}

.variant-card img {
  width: 100%;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.variant-name {
  margin-top: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.input-group {
  border-radius: 8px;
  overflow: hidden;
}

.btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn:hover {
  opacity: 0.9;
}

h2, h4, p {
  color: #333;
}
</style>
