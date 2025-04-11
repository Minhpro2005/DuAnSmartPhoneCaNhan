<template>
   <!-- Bootstrap & Icon CDN -->
   <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  
  <div class="container mt-4">
    <div class="d-flex align-items-center mb-3">
      <button class="btn btn-link text-danger" @click="goBack">
        <i class="bi bi-arrow-left"></i>
      </button>
      <h4 class="mb-0 ms-2 text-danger fw-bold">Giỏ hàng</h4>
    </div>

    <div class="form-check mb-3">
      <input class="form-check-input" type="checkbox" v-model="selectAll" @change="toggleSelectAll" />
      <label class="form-check-label">Chọn tất cả</label>
    </div>

    <div v-for="(item, index) in cartItems" :key="item.id" class="card mb-3 shadow-sm">
      <div class="card-body d-flex align-items-center">
        <input type="checkbox" v-model="item.selected" class="me-3" />
        <img :src="item.image || placeholder" class="img-thumbnail me-3" width="80" height="80" />

        <div class="flex-grow-1">
          <h6 class="mb-1">{{ item.name }} {{ item.color ? '(' + item.color + ')' : '' }}</h6>
          <div class="text-danger fw-bold mb-2">{{ formatVND(getPrice(item.price)) }}</div>
          <div class="text-muted small mb-2">
            <i class="bi bi-tag-fill me-1"></i> Chương trình khuyến mãi:
          </div>
        </div>

        <div class="d-flex align-items-center me-3">
          <button class="btn btn-outline-secondary btn-sm" @click="updateQuantity(index, -1)">-</button>
          <input type="text" class="form-control text-center mx-1" :value="item.quantity" style="width: 40px;" readonly />
          <button class="btn btn-outline-secondary btn-sm" @click="updateQuantity(index, 1)">+</button>
        </div>

        <button class="btn btn-link text-danger" @click="removeItem(index)">
          <i class="bi bi-trash-fill fs-5"></i>
        </button>
      </div>
    </div>

    <div class="bg-light p-3 d-flex justify-content-between align-items-center rounded">
      <div class="fw-bold fs-5">
        Tạm tính: <span class="text-danger">{{ formatVND(total) }}</span>
      </div>
      <button class="btn btn-danger" @click="buyNow">Mua ngay ({{ cartItems.filter(i => i.selected).length }})</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const placeholder = 'https://placehold.co/100x100?text=No+Image';
const cartItems = ref([]);
const selectAll = ref(false);

const getPrice = (price) => typeof price === 'string' ? parseInt(price.replace(/\D/g, '')) || 0 : price;

const formatVND = (value) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);

const goBack = () => window.history.back();

const toggleSelectAll = () => {
  cartItems.value.forEach((item) => (item.selected = selectAll.value));
};

const total = computed(() =>
  cartItems.value.reduce(
    (sum, item) => item.selected ? sum + getPrice(item.price) * item.quantity : sum,
    0
  )
);

const updateBackendQuantity = async (item) => {
  try {
    await fetch(`http://localhost:8080/smartphone/giohangchitiet/id/${item.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        id: item.id,
        gioHang: { maGioHang: item.maGioHang },
        sanPham: { maSP: item.maSP },
        bienThe: item.maBienThe ? { maBienThe: item.maBienThe } : null,
        giaBan: item.price,
        mauSac: item.color,
        soLuong: item.quantity
      })
    });
  } catch (e) {
    console.error('Lỗi khi cập nhật số lượng:', e);
  }
};

const updateQuantity = async (index, delta) => {
  const item = cartItems.value[index];
  const newQty = item.quantity + delta;
  if (newQty > 0) {
    item.quantity = newQty;
    await updateBackendQuantity(item);
  }
};

const removeItem = async (index) => {
  const item = cartItems.value[index];
  if (confirm('Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?')) {
    try {
      await fetch(`http://localhost:8080/smartphone/giohangchitiet/id/${item.id}`, { method: 'DELETE' });
      cartItems.value.splice(index, 1);
    } catch (error) {
      console.error('Lỗi khi xoá sản phẩm:', error);
      alert('❌ Không thể xoá sản phẩm khỏi giỏ hàng!');
    }
  }
};

const loadCart = async () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (!user || !user.userID) return;

  try {
    // ✅ Sửa lại đúng API khách hàng theo user
    const resKH = await fetch(`http://localhost:8080/smartphone/khachhang/user/${user.userID}`);
    if (!resKH.ok) throw new Error('Không tìm thấy thông tin khách hàng');
    const khachHang = await resKH.json();
    const maKH = khachHang.maKH;

    const resGH = await fetch(`http://localhost:8080/smartphone/giohang/khachhang/${maKH}/trangthai/true`);
    const gioHangList = await resGH.json();
    const gioHang = gioHangList[0];
    if (!gioHang) return;

    const resCT = await fetch(`http://localhost:8080/smartphone/giohangchitiet/giohang/${gioHang.maGioHang}`);
    const chiTietList = await resCT.json();

    cartItems.value = chiTietList.map((item) => ({
      id: item.id,
      name: item.bienThe ? item.bienThe.sanPham.tenSP : item.sanPham.tenSP,
      image: item.bienThe?.hinhAnh || item.sanPham.hinhAnhSP,
      price: item.giaBan,
      quantity: item.soLuong,
      color: item.bienThe?.mauSac || item.mauSac,
      selected: false,
      maGioHang: item.gioHang.maGioHang,
      maSP: item.sanPham.maSP,
      maBienThe: item.bienThe?.maBienThe || null
    }));

    for (const item of cartItems.value) {
      await updateBackendQuantity(item); // Cập nhật số lượng nếu cần
    }

  } catch (error) {
    console.error('Lỗi khi load giỏ hàng:', error);
  }
};

const buyNow = () => {
  const selected = cartItems.value.filter((item) => item.selected);
  if (selected.length === 0) {
    alert('Vui lòng chọn ít nhất một sản phẩm để mua!');
    return;
  }
  localStorage.setItem('selectedItems', JSON.stringify(selected));

  const user = JSON.parse(localStorage.getItem('user'));
  if (!user || !user.userID) {
    alert('Vui lòng đăng nhập để tiếp tục');
    return;
  }

  // ✅ Sửa: gọi đúng API để lấy Khách Hàng
  fetch(`http://localhost:8080/smartphone/khachhang/user/${user.userID}`)
    .then((res) => res.json())
    .then((khachHang) => {
      const maKH = khachHang.maKH;
      router.push(`/dathang/${maKH}`);
    })
    .catch((err) => {
      console.error('Lỗi lấy thông tin khách hàng:', err);
      alert('Không thể chuyển sang trang đặt hàng');
    });
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.img-thumbnail {
  object-fit: cover;
  height: 80px;
}
</style>
