<template>
  <!-- Bootstrap & Icon CDN -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

  <!-- Header -->
  <div class="header bg-dark text-white py-2 text-center small">
    <span><img src="https://img.icons8.com/ios/20/ffffff/scooter.png" alt="Delivery"> Giao nhanh - Miễn phí cho đơn từ 300K</span>
    <span class="mx-3">|</span>
    <span><img src="https://img.icons8.com/ios/20/ffffff/certificate.png" alt="Certificate"> Chính hãng - Xuất VAT đầy đủ</span>
    <span class="mx-3">|</span>
    <span><img src="https://img.icons8.com/ios/20/ffffff/mobile-payment.png" alt="App"> Tải App - Tích điểm nhận ưu đãi</span>
  </div>

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

  <!-- Banner -->
  <div id="promoCarousel" class="carousel slide" data-bs-ride="carousel" data-bs-interval="3000">
    <div class="carousel-inner">
      <div class="carousel-item active">
        <img src="https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H1_1440x242_312ca8e8d9.png" class="d-block w-100" alt="Promo 1">
      </div>
      <div class="carousel-item">
        <img src="https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H1_1440x242_d8b364e507.png" class="d-block w-100" alt="Promo 2">
      </div>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#promoCarousel" data-bs-slide="prev">
      <span class="carousel-control-prev-icon"></span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#promoCarousel" data-bs-slide="next">
      <span class="carousel-control-next-icon"></span>
    </button>
  </div>

  <!-- Product List -->
  <div class="container py-5">
  <h2 class="text-center mb-4">Danh sách sản phẩm</h2>
  <div class="row row-cols-1 row-cols-md-5 g-4">
  <div class="col" v-for="sp in sanPhamList" :key="sp.maSP">
    <div class="card h-100 shadow-sm" @click="goToDetail(sp.maSP)" style="cursor: pointer">
      <img :src="sp.hinhAnhSP || 'https://via.placeholder.com/300'" class="card-img-top" alt="ảnh sản phẩm">
      <div class="card-body">
        <h5 class="card-title">{{ sp.tenSP }}</h5>
        <span class="price-badge">{{ formatVND(sp.giaBan) }}</span>
      </div>
    </div>
  </div>
</div>
</div>


  <!-- Footer -->
  <footer class="bg-light text-dark py-4 mt-5">
    <div class="container text-center">
      <img src="https://images.unsplash.com/photo-1633409361618-c73427e4e206" alt="logo" width="300" class="mb-3">
      <p>&copy; 2025 Công Minh Mobile - Mọi quyền được bảo lưu.</p>
    </div>
  </footer>
</template>

<script>

export default {
  name: 'HomeView',
  data() {
    return {
      sanPhamList: []
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
    getImage(fileName) {
      if (!fileName) return 'https://via.placeholder.com/300x200.png?text=No+Image';
      return `http://localhost:8080/images/${fileName}`; // <- cập nhật nếu ảnh ở đường dẫn khác
    },
    formatVND(value) {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(value);
    },
    goToDetail(id) {
  this.$router.push(`/sanpham/${id}`);
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

</style>
