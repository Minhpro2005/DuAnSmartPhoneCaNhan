<template>
   <!-- Bootstrap & Icon CDN -->
   <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

 <!-- Navbar -->
 <nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top shadow-sm">
    <div class="container">
      <a class="navbar-brand" href="#">
        <img src="https://images.unsplash.com/photo-1633409361618-c73427e4e206" alt="Logo" width="40" height="40" class="rounded-circle me-2">
        Công Minh Mobile
      </a>
      <div class="search-bar d-flex ms-auto me-3">
        <input type="text" class="form-control me-2" placeholder="Bạn muốn tìm gì?">
        <button class="btn btn-outline-dark"><i class="bi bi-search"></i></button>
      </div>
      <ul class="navbar-nav">
        <li class="nav-item"><RouterLink class="nav-link" to="/home">Trang chủ</RouterLink></li>
        <li class="nav-item"><RouterLink class="nav-link" to="/store">Cửa hàng</RouterLink></li>
        <li class="nav-item"><RouterLink class="nav-link" to="/contact">Liên hệ</RouterLink></li>
        <li class="nav-item"><RouterLink class="nav-link" to="/dangnhap/1">Tài khoản</RouterLink></li>
        <li class="nav-item"><RouterLink class="nav-link" to="/giohang"><i class="fas fa-shopping-cart"></i> Giỏ hàng</RouterLink></li>
      </ul>
    </div>
  </nav>
  <div class="container mt-5">
    <h4 class="text-center mb-4">Giỏ hàng của bạn</h4>

    <div v-if="cartItems.length === 0" class="alert alert-info text-center">
      Giỏ hàng trống.
    </div>

    <div v-else>
      <!-- Chọn tất cả -->
      <div class="form-check mb-3">
        <input class="form-check-input" type="checkbox" id="selectAll" v-model="selectAll" @change="toggleSelectAll" />
        <label class="form-check-label" for="selectAll">Chọn tất cả</label>
      </div>

      <table class="table table-bordered align-middle">
        <thead class="table-light">
          <tr>
            <th></th>
            <th>Hình ảnh</th>
            <th>Tên sản phẩm</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Tổng</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in cartItems" :key="item.id">
            <td>
              <input type="checkbox" v-model="item.selected" />
            </td>
            <td><img :src="item.image || placeholder" class="img-thumbnail" width="80" /></td>
            <td>{{ item.name }}</td>
            <td>{{ formatVND(getPrice(item.price)) }}</td>
            <td>
              <div class="input-group" style="max-width: 120px;">
                <button class="btn btn-outline-secondary" @click="updateQuantity(index, -1)">-</button>
                <input class="form-control text-center" type="text" :value="item.quantity" readonly />
                <button class="btn btn-outline-secondary" @click="updateQuantity(index, 1)">+</button>
              </div>
            </td>
            <td>{{ formatVND(getPrice(item.price) * item.quantity) }}</td>
            <td>
              <button class="btn btn-danger btn-sm" @click="removeItem(index)">
                <i class="fas fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="text-end fw-bold mt-3">
        Tổng cộng: <span class="text-danger">{{ formatVND(total) }}</span>
      </div>

      <div class="d-flex justify-content-between align-items-center mb-4">
  <button class="btn btn-outline-secondary d-flex align-items-center" @click="goBack">
    <i class="bi bi-arrow-left me-1"></i> 
  </button>
  <button class="btn btn-danger me-2" @click="buyNow">Mua ngay</button>
</div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const placeholder = 'https://placehold.co/100x100?text=No+Image';
const router = useRouter();
const cartItems = ref([]);
const selectAll = ref(false);

const getPrice = (price) => typeof price === 'string' ? parseInt(price.replace(/\D/g, '')) || 0 : price;

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user || !user.userID) return;

  try {
    const resGH = await fetch(`http://localhost:8080/smartphone/giohang/khachhang/${user.userID}/trangthai/true`);
    const gioHangList = await resGH.json();
    const gioHang = gioHangList[0];
    if (!gioHang) return;

    const resCT = await fetch(`http://localhost:8080/smartphone/giohangchitiet/giohang/${gioHang.maGioHang}`);
    const chiTietList = await resCT.json();

    cartItems.value = chiTietList.map(item => ({
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

    // Lưu số lượng về backend ngay khi load
    for (const item of cartItems.value) {
      await updateBackendQuantity(item);
    }

  } catch (error) {
    console.error("Lỗi khi load giỏ hàng:", error);
  }
});

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
      console.error("Lỗi khi xoá sản phẩm:", error);
      alert("❌ Không thể xoá sản phẩm khỏi giỏ hàng!");
    }
  }
};

const toggleSelectAll = () => {
  cartItems.value.forEach(item => item.selected = selectAll.value);
};

const total = computed(() =>
  cartItems.value.reduce((sum, item) =>
    item.selected ? sum + getPrice(item.price) * item.quantity : sum, 0)
);

const formatVND = (value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);

const goBack = () => window.history.back();

const buyNow = () => {
  const selected = cartItems.value.filter(item => item.selected);
  if (selected.length === 0) {
    alert("Vui lòng chọn ít nhất một sản phẩm để mua!");
    return;
  }
  alert(`Bạn đã mua ${selected.length} sản phẩm với tổng ${formatVND(total.value)}`);
};
</script>


<style scoped>
.img-thumbnail {
  object-fit: cover;
  height: 80px;
}
</style>
