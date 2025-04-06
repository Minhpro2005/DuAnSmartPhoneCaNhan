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
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const product = ref({});
const variants = ref([]);
const selectedVariant = ref(null);
const quantity = ref(1);
const placeholderImg = 'https://placehold.co/400x300?text=No+Image';

const fetchProduct = async () => {
  const res = await fetch(`http://localhost:8080/smartphone/sanpham/id/${route.params.id}`);
  product.value = await res.json();
};

const fetchVariants = async () => {
  const res = await fetch(`http://localhost:8080/smartphone/bienthe/sanpham/${route.params.id}`);
  variants.value = await res.json();
};

const formatPrice = (price) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
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
      alert("Vui lòng đăng nhập trước khi thêm vào giỏ hàng.");
      return;
    }

    // ✅ LẤY maKH từ userID
    const resKH = await fetch(`http://localhost:8080/smartphone/user/${user.userID}`);
    if (!resKH.ok) throw new Error("Không tìm thấy thông tin khách hàng");

    const khachHang = await resKH.json();
    const maKH = khachHang.maKH;

    // ✅ Lấy giỏ hàng hiện tại nếu có
    let gioHangRes = await fetch(`http://localhost:8080/smartphone/giohang/khachhang/${maKH}/trangthai/true`);
    let gioHangList = gioHangRes.ok ? await gioHangRes.json() : [];
    let gioHang = gioHangList.length ? gioHangList[0] : null;

    // ✅ Tạo giỏ hàng nếu chưa có
    if (!gioHang) {
      const createRes = await fetch(`http://localhost:8080/smartphone/giohang`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ khachHang: { maKH }, trangThai: true })
      });
      if (!createRes.ok) throw new Error("Tạo giỏ hàng thất bại!");
      gioHang = await createRes.json();
    }

    // ✅ Thêm sản phẩm vào giỏ
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

    alert("✅ Sản phẩm đã được thêm vào giỏ hàng!");

  } catch (error) {
    console.error("❌ Lỗi khi thêm giỏ hàng:", error);
    alert("Thêm vào giỏ hàng thất bại!");
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
}
.variant-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 10px;
  width: 120px;
  text-align: center;
  cursor: pointer;
}
.variant-card.active {
  border: 2px solid red;
}
.variant-card img {
  width: 100%;
  height: 80px;
  object-fit: cover;
  border-radius: 5px;
}
.variant-name {
  margin-top: 5px;
  font-size: 14px;
}
</style>
