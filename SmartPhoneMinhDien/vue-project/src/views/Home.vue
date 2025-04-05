<template>
   <!-- Bootstrap & Icon CDN -->
   <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

  <!-- Header -->
  <div class="header bg-dark text-white py-2 text-center small">
    <span><img src="https://img.icons8.com/ios/20/ffffff/scooter.png" /> Giao nhanh - Miễn phí đơn từ 300K</span>
    <span class="mx-3">|</span>
    <span><img src="https://img.icons8.com/ios/20/ffffff/certificate.png" /> Chính hãng - Xuất VAT đầy đủ</span>
    <span class="mx-3">|</span>
    <span><img src="https://img.icons8.com/ios/20/ffffff/mobile-payment.png" /> Tải App - Tích điểm nhận ưu đãi</span>
  </div>

  <!-- Navbar -->
  <nav class="navbar navbar-expand-lg bg-white sticky-top shadow-sm py-2">
    <div class="container">
      <RouterLink class="navbar-brand d-flex align-items-center" to="/">
        <img src="https://images.unsplash.com/photo-1633409361618-c73427e4e206" width="40" height="40" class="rounded-circle me-2" />
        <strong class="text-dark fs-5">Công Minh Mobile</strong>
      </RouterLink>

      <!-- Search -->
      <div class="d-flex flex-grow-1 mx-4">
        <input type="text" class="form-control me-2" placeholder="Bạn muốn tìm gì?" />
        <button class="btn btn-outline-dark"><i class="bi bi-search"></i></button>
      </div>

      <!-- Navigation -->
      <ul class="navbar-nav align-items-center">
        <li class="nav-item">
    <RouterLink class="nav-link" to="/">
      <i class="bi bi-house-door me-1"></i> Trang chủ
    </RouterLink>
  </li>
  <li class="nav-item">
    <RouterLink class="nav-link" to="/store">
      <i class="bi bi-shop me-1"></i> Cửa hàng
    </RouterLink>
  </li>
  <li class="nav-item">
    <RouterLink class="nav-link" to="/contact">
  <i class="bi bi-telephone-fill fs-5"></i> Liên hệ
</RouterLink>
  </li>

        <!-- Giỏ hàng -->
        <li class="nav-item ms-3">
          <RouterLink class="nav-link position-relative" to="/giohang">
            <i class="fas fa-shopping-cart me-1"></i> Giỏ hàng
          </RouterLink>
        </li>

        <!-- Tài khoản -->
        <li class="nav-item dropdown ms-3" v-if="currentUser">
          <div class="nav-link dropdown-toggle p-0" role="button" data-bs-toggle="dropdown">
            <div
              class="px-3 py-2 text-white d-flex align-items-center"
              style="background-color: #d6001c; border-radius: 30px; font-weight: 500; cursor: pointer"
              @click="$router.push('/thongtincanhan/' + currentUser.userID)"
            >
              <img
              :src="currentUser.avatar ? 'http://localhost:8080' + currentUser.avatar : 'https://cdn-icons-png.flaticon.com/512/149/149071.png'"
                alt="avatar"
                class="rounded-circle me-2"
                style="width: 32px; height: 32px; object-fit: cover"
              />
              <span>{{ currentUser.hoTen }}</span>
            </div>
          </div>
          <ul class="dropdown-menu dropdown-menu-end mt-2 shadow-sm">
            <li>
              <RouterLink class="dropdown-item" :to="'/thongtincanhan/' + currentUser.userID">
                <i class="bi bi-info-circle me-2"></i> Thông tin cá nhân
              </RouterLink>
            </li>
            <li>
              <button class="dropdown-item text-danger" @click="logout">
                <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
              </button>
            </li>
          </ul>
        </li>
        <li class="nav-item" v-else>
          <RouterLink class="nav-link" to="/dangnhap/1">Tài khoản</RouterLink>
        </li>
      </ul>
    </div>
  </nav>

  <!-- Banner -->
  <div id="promoCarousel" class="carousel slide" data-bs-ride="carousel">
    <div class="carousel-inner">
      <div class="carousel-item active">
        <img src="https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H1_1440x242_312ca8e8d9.png" class="d-block w-100" alt="Promo 1" />
      </div>
      <div class="carousel-item">
        <img src="https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H1_1440x242_d8b364e507.png" class="d-block w-100" alt="Promo 2" />
      </div>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#promoCarousel" data-bs-slide="prev">
      <span class="carousel-control-prev-icon"></span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#promoCarousel" data-bs-slide="next">
      <span class="carousel-control-next-icon"></span>
    </button>
  </div>

  <!-- Danh sách sản phẩm -->
  <div class="container py-5">
    <h2 class="text-center mb-4">Danh sách sản phẩm</h2>
    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-5 g-4">
      <div class="col" v-for="sp in sanPhamList" :key="sp.maSP">
        <div class="card h-100 shadow-sm border-0" @click="goToDetail(sp.maSP)" style="cursor: pointer">
          <img :src="sp.hinhAnhSP || 'https://via.placeholder.com/300'" class="card-img-top" alt="ảnh sản phẩm" />
          <div class="card-body text-center">
            <h6 class="card-title mb-2">{{ sp.tenSP }}</h6>
            <span class="price-badge">{{ formatVND(sp.giaBan) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Footer -->
  <footer class="bg-light text-dark py-4 mt-5">
    <div class="container text-center">
      <img src="https://images.unsplash.com/photo-1633409361618-c73427e4e206" width="300" class="mb-3" />
      <p>&copy; 2025 Công Minh Mobile - Mọi quyền được bảo lưu.</p>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'HomeView',
  data() {
    return {
      sanPhamList: [],
      currentUser: JSON.parse(localStorage.getItem('user')) || null
    };
  },
  methods: {
    fetchSanPham() {
      fetch('http://localhost:8080/smartphone/sanpham')
        .then(res => res.json())
        .then(data => {
          this.sanPhamList = data;
        })
        .catch(err => console.error('Lỗi API:', err));
    },
    formatVND(value) {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(value);
    },
    goToDetail(id) {
      this.$router.push(`/sanpham/${id}`);
    },
    logout() {
      localStorage.removeItem('user');
      this.currentUser = null;
      this.$router.push('/dangnhap/1');
    }
  },
  mounted() {
    this.fetchSanPham();
  }
};
</script>

<style scoped>
.card-img-top {
  width: 100%;
  height: 180px;
  object-fit: contain;
  background-color: #f8f8f8;
  padding: 8px;
}
.price-badge {
  display: inline-block;
  background-color: #dc3545;
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: bold;
}
.card {
  transition: transform 0.2s ease;
}
.card:hover {
  transform: scale(1.03);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
.navbar-nav .nav-link {
  padding: 8px 14px;
  font-weight: 500;
  color: #333;
}
.navbar-nav .nav-link:hover {
  color: #d6001c;
}
.dropdown-toggle::after {
  display: none !important;
}
.dropdown-menu {
  min-width: 200px;
  border-radius: 12px;
  padding: 10px 0;
  font-size: 14px;
}
.dropdown-item {
  padding: 10px 20px;
}
.dropdown-item:hover {
  background-color: #f8f9fa;
}
</style>
