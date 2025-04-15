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

      <div class="d-flex flex-grow-1 mx-4">
        <input type="text" class="form-control me-2" placeholder="Bạn muốn tìm gì?" />
        <button class="btn btn-outline-dark"><i class="bi bi-search"></i></button>
      </div>

      <ul class="navbar-nav align-items-center">
        <li class="nav-item">
          <RouterLink class="nav-link" to="/"><i class="bi bi-house-door me-1"></i> Trang chủ</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" to="/store"><i class="bi bi-shop me-1"></i> Cửa hàng</RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" to="/contact"><i class="bi bi-telephone-fill fs-5"></i> Liên hệ</RouterLink>
        </li>
        <li class="nav-item ms-3">
          <RouterLink class="nav-link position-relative" to="/giohang">
            <i class="fas fa-shopping-cart me-1"></i> Giỏ hàng
          </RouterLink>
        </li>
        <li class="nav-item ms-3" v-if="currentUser">
  <!-- Avatar hiển thị -->
  <div ref="avatarBtn" class="user-avatar" @click="togglePopover">
    <img
      :src="currentUser.avatar ? 'http://localhost:8080' + currentUser.avatar : 'https://cdn-icons-png.flaticon.com/512/149/149071.png'"
      alt="avatar"
      class="avatar-img"
    />
    <span class="avatar-name">{{ currentUser.hoTen }}</span>
    <i class="bi bi-chevron-down ms-2"></i>
  </div>

  <!-- Popover menu -->
  <div
    v-if="showPopover"
    ref="popover"
    class="popover-menu shadow"
  >
    <RouterLink class="popover-item" :to="'/thongtincanhan/' + currentUser.userID">
      <i class="bi bi-person me-2"></i> Thông tin cá nhân
    </RouterLink>
    <hr class="my-1" />
    <button class="popover-item text-danger" @click="logout">
      <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
    </button>
  </div>
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
  
  <!-- Flash Sale Section -->
  <section class="flash-sale my-5">
    <div class="container">
      <div class="bg-light rounded shadow py-4 px-3">
        <h3 class="fw-bold mb-4 text-danger text-center">🔥 Giá tốt. Nhanh tay. Số lượng ít!</h3>

        <!-- Vùng cuộn ngang -->
        <div class="position-relative">
          <!-- Nút cuộn trái -->
          <button class="btn btn-light shadow position-absolute start-0 top-50 translate-middle-y z-3"
                  @click="scrollFlashSale(-1)">
            <i class="bi bi-chevron-left"></i>
          </button>

          <!-- Danh sách sản phẩm flash sale cuộn ngang -->
          <div class="d-flex overflow-auto px-5 gap-3 scroll-smooth mx-auto"
               ref="flashSaleContainer"
               style="max-width: 100%; scroll-behavior: smooth;">
            <div class="card flex-shrink-0 text-center" style="width: 240px;" v-for="product in flashSaleItems" :key="product.maSP">
              <div class="position-relative">
                <img :src="product.hinhAnhSP" class="card-img-top p-3" style="height: 200px; object-fit: contain;" />
                <span class="badge bg-dark position-absolute top-0 start-0 m-2">Online Siêu Rẻ</span>
              </div>
              <div class="card-body p-2">
                <p class="text-muted text-decoration-line-through small mb-1">{{ formatVND(product.giaGoc) }}</p>
                <p class="text-danger fw-bold mb-1">{{ formatVND(product.giaBan) }}</p>
                <p class="text-success small mb-2">Giảm {{ formatVND(product.giaGoc - product.giaBan) }}</p>
                <h6 class="card-title small mb-0">{{ product.tenSP }}</h6>
              </div>
            </div>
          </div>

          <!-- Nút cuộn phải -->
          <button class="btn btn-light shadow position-absolute end-0 top-50 translate-middle-y z-3"
                  @click="scrollFlashSale(1)">
            <i class="bi bi-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- Danh sách sản phẩm + bộ lọc giống Tiki -->
  <div class="container py-5">
    <div class="row">
      <!-- Bộ lọc -->
      <div class="col-md-3">
        <FilterSidebar :sanPhamList="sanPhamList" :filters="filters" @update:filters="filters = $event" />
      </div>
      
  <!-- Danh sách sản phẩm -->
  <div class="col-md-9">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="mb-0">Danh sách sản phẩm</h4>
      <!-- Dark mode toggle -->
      <div class="form-check form-switch">
        <input class="form-check-input" type="checkbox" v-model="isDark" id="darkModeToggle">
        <label class="form-check-label" for="darkModeToggle">Dark mode</label>
      </div>
    </div>

    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
      <div class="col" v-for="sp in paginatedSanPham" :key="sp.maSP">
        <div class="card h-100 shadow-sm position-relative" @click="goToDetail(sp.maSP)" style="cursor: pointer">
          <!-- Badge giảm giá -->
          <div class="badge-discount" v-if="sp.giamGia">-{{ sp.giamGia }}%</div>

          <img :src="sp.hinhAnhSP || 'https://via.placeholder.com/300'" class="card-img-top" alt="ảnh sản phẩm" />
          <div class="card-body text-center">
            <h6 class="card-title mb-2">{{ sp.tenSP }}</h6>
            <span class="price-badge">{{ formatVND(sp.giaBan) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Không tìm thấy -->
    <div v-if="filteredSanPham.length === 0" class="text-center mt-4 text-muted">
      Không tìm thấy sản phẩm phù hợp.
    </div>

    <!-- Pagination -->
    <nav class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button class="page-link" @click="currentPage--">Trước</button>
        </li>
        <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
          <button class="page-link" @click="currentPage = page">{{ page }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <button class="page-link" @click="currentPage++">Sau</button>
        </li>
      </ul>
    </nav>
  </div>
    </div>
  </div>

  <!-- Footer -->
<footer class="footer-custom bg-light text-dark pt-5 pb-4">
  <div class="container">
    <!-- Logo -->
    <div class="row justify-content-center mb-4">
      <div class="col-md-4 text-center">
        <img src="https://images.unsplash.com/photo-1633409361618-c73427e4e206" width="120" alt="Công Minh Mobile" />
      </div>
    </div>

    <!-- 4 cột nội dung -->
    <div class="row gy-4">
      <div class="col-md-3">
        <h5 class="footer-title">Tổng đài hỗ trợ</h5>
        <ul class="footer-list">
          <li><strong>Mua hàng:</strong> 1800.2097 (7h30 - 22h00)</li>
          <li><strong>Khiếu nại:</strong> 1800.2063 (8h00 - 21h30)</li>
          <li><strong>Bảo hành:</strong> 1800.2064 (8h00 - 21h00)</li>
        </ul>
      </div>

      <div class="col-md-3">
        <h5 class="footer-title">Thông tin & Chính sách</h5>
        <ul class="footer-list">
          <li><a href="#">Thanh toán Online</a></li>
          <li><a href="#">Trả góp bằng thẻ</a></li>
          <li><a href="#">Chính sách giao hàng</a></li>
          <li><a href="#">Tra cứu hóa đơn</a></li>
          <li><a href="#">Bảo hành chính hãng</a></li>
        </ul>
      </div>

      <div class="col-md-3">
        <h5 class="footer-title">Dịch vụ & Hỗ trợ</h5>
        <ul class="footer-list">
          <li><a href="#">Khách hàng doanh nghiệp</a></li>
          <li><a href="#">Chính sách bảo mật</a></li>
          <li><a href="#">Tuyển dụng</a></li>
          <li><a href="#">Hợp tác kinh doanh</a></li>
        </ul>
      </div>

      <div class="col-md-3">
        <h5 class="footer-title">Kết nối với chúng tôi</h5>
        <div class="footer-social">
          <a href="#"><img src="https://cdn2.cellphones.com.vn/44x,webp/media/logo/social/cellphones-facebook.png" alt="Facebook" /></a>
          <a href="#"><img src="https://cdn2.cellphones.com.vn/44x,webp/media/logo/social/cellphones-instagram.png" alt="Instagram" /></a>
          <a href="#"><img src="https://cdn2.cellphones.com.vn/44x,webp/media/logo/social/cellphones-youtube.png" alt="YouTube" /></a>
          <a href="#"><img src="https://cdn2.cellphones.com.vn/44x,webp/media/logo/social/cellphones-zalo.png" alt="Zalo" /></a>
        </div>
        <h6 class="mt-3">Thanh toán</h6>
        <div class="footer-payments">
          <img src="https://cdn2.cellphones.com.vn/x35,webp/media/wysiwyg/apple-pay-og.png" alt="Apple Pay" />
          <img src="https://cdn2.cellphones.com.vn/x/media/wysiwyg/momo_1.png" alt="MoMo" />
          <img src="https://cdn2.cellphones.com.vn/x35,webp/media/logo/payment/vnpay-logo.png" alt="VNPay" />
          <img src="https://cdn2.cellphones.com.vn/x35,webp/media/logo/payment/zalopay-logo.png" alt="ZaloPay" />
        </div>
      </div>
    </div>

    <!-- Line -->
    <hr class="my-4" />

    <!-- Copyright -->
    <div class="row align-items-center">
      <div class="col-md-6 text-center text-md-start">
        <small class="text-muted">
          © 2025 Công ty TNHH Công Minh Mobile - ĐKKD: 0316172372 tại TP.HCM.
        </small>
      </div>
      <div class="col-md-6 text-center text-md-end">
        <a href="#" class="text-dark me-3"><i class="bi bi-facebook"></i></a>
        <a href="#" class="text-dark me-3"><i class="bi bi-youtube"></i></a>
        <a href="#" class="text-dark"><i class="bi bi-instagram"></i></a>
      </div>
    </div>
  </div>
</footer>

</template>

<script>
import FilterSidebar from '@/views/FilterSidebar.vue';

export default {
  name: 'HomeView',
  components: { FilterSidebar },
  data() {
    return {
      sanPhamList: [],
      flashSaleItems: [],
      currentUser: JSON.parse(localStorage.getItem('user')) || null,
      filters: {
        tenSP: '',
        hang: '',
        giaBan: '',
        mauSac: []
      },
      isDark: false,
      currentPage: 1,
      perPage: 20,
      showPopover: false // 👈 dùng cho avatar popover
    };
  },
  computed: {
    filteredSanPham() {
      return this.sanPhamList.filter(sp => {
        const matchTen = sp.tenSP.toLowerCase().includes(this.filters.tenSP.toLowerCase());
        const matchHang = !this.filters.hang || sp.hang === this.filters.hang;
        const matchMau = this.filters.mauSac.length === 0 || this.filters.mauSac.includes(sp.mauSac);
        let matchGia = true;
        switch (this.filters.giaBan) {
          case 'duoi2': matchGia = sp.giaBan < 2000000; break;
          case '2to5': matchGia = sp.giaBan >= 2000000 && sp.giaBan <= 5000000; break;
          case '5to10': matchGia = sp.giaBan > 5000000 && sp.giaBan <= 10000000; break;
          case 'tren10': matchGia = sp.giaBan > 10000000; break;
        }
        return matchTen && matchHang && matchGia && matchMau;
      });
    },
    paginatedSanPham() {
      const start = (this.currentPage - 1) * this.perPage;
      return this.filteredSanPham.slice(start, start + this.perPage);
    },
    totalPages() {
      return Math.ceil(this.filteredSanPham.length / this.perPage);
    }
  },
  methods: {
    fetchSanPham() {
      fetch('http://localhost:8080/smartphone/sanpham')
        .then(res => res.json())
        .then(data => {
          this.sanPhamList = data;
          this.flashSaleItems = data.slice(0, 10).map(sp => ({ ...sp, giaGoc: sp.giaBan + 1000000 }));
        })
        .catch(err => console.error('Lỗi API:', err));
    },
    formatVND(value) {
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    },
    goToDetail(id) {
      this.$router.push(`/sanpham/${id}`);
    },
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },
    logout() {
      localStorage.removeItem('user');
      this.currentUser = null;
      this.$router.push('/dangnhap/1');
    },
    scrollFlashSale(direction) {
      const container = this.$refs.flashSaleContainer;
      const cardWidth = 240 + 12;
      container.scrollBy({ left: direction * cardWidth * 3, behavior: 'smooth' });
    },
    // 👇 Toggle avatar popover
    togglePopover() {
      this.showPopover = !this.showPopover;
    },
    // 👇 Tự đóng khi click ra ngoài
    handleClickOutside(event) {
      if (
        this.showPopover &&
        !this.$refs.avatarBtn?.contains(event.target) &&
        !this.$refs.popover?.contains(event.target)
      ) {
        this.showPopover = false;
      }
    }
  },
  mounted() {
    this.fetchSanPham();
    document.addEventListener('click', this.handleClickOutside); // 👈 bắt click ngoài
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside); // 👈 gỡ khi unmount
  }
};
</script>


<style scoped>
.card {
  border-radius: 16px;
  transition: all 0.3s ease;
  border: none;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}
.card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
}

.card-img-top {
  width: 100%;
  height: 180px;
  object-fit: contain;
  background-color: #f9f9f9;
  padding: 12px;
  transition: transform 0.3s ease;
}
.card:hover .card-img-top {
  transform: scale(1.08);
}

.price-badge {
  background-color: #d6001c;
  color: #fff;
  padding: 6px 14px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.95rem;
}

.badge-discount {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #ff416c, #ff4b2b);
  color: white;
  padding: 5px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 65, 108, 0.3);
  z-index: 10;
}

.pagination .page-item .page-link {
  border-radius: 12px;
  margin: 0 4px;
  padding: 8px 16px;
  font-weight: 600;
  color: #333;
  border: none;
}
.pagination .page-item.active .page-link {
  background-color: #d6001c;
  color: #fff;
  box-shadow: 0 4px 10px rgba(214, 0, 28, 0.3);
}
.pagination .page-item .page-link:hover {
  background-color: #f2f2f2;
}

.flash-sale .card {
  border-radius: 14px;
  box-shadow: 0 3px 14px rgba(0, 0, 0, 0.04);
}
.flash-sale .card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}
.flash-sale .btn {
  border-radius: 50%;
  width: 42px;
  height: 42px;
  background-color: white;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
}
.flash-sale .btn:hover {
  background-color: #f1f1f1;
}

.scroll-smooth {
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.scroll-smooth::-webkit-scrollbar {
  display: none;
}


/* NAVBAR */
.navbar {
  background-color: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  padding: 12px 0;
  transition: background-color 0.3s ease;
}

.navbar .navbar-brand strong {
  font-size: 1.4rem;
  font-weight: bold;
  color: #d6001c;
}

.navbar .form-control {
  border-radius: 30px;
  padding: 8px 20px;
  border: 1px solid #ccc;
  box-shadow: none;
  transition: all 0.2s ease;
}
.navbar .form-control:focus {
  border-color: #d6001c;
  box-shadow: 0 0 0 0.1rem rgba(214, 0, 28, 0.2);
}

.navbar .btn-outline-dark {
  border-radius: 30px;
  padding: 8px 16px;
  transition: all 0.2s ease;
}
.navbar .btn-outline-dark:hover {
  background-color: #d6001c;
  color: white;
  border-color: #d6001c;
}

.navbar-nav .nav-link {
  font-weight: 500;
  color: #333;
  margin: 0 8px;
  transition: color 0.2s ease;
}
.navbar-nav .nav-link:hover {
  color: #d6001c;
}

.navbar .dropdown-menu {
  border-radius: 12px;
  border: none;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.navbar .dropdown-item {
  padding: 10px 16px;
  font-weight: 500;
}
.navbar .dropdown-item:hover {
  background-color: #f8f8f8;
  color: #d6001c;
}

.user-avatar {
  display: flex;
  align-items: center;
  background-color: #ffffff;
  padding: 6px 14px;
  border-radius: 30px;
  border: 1px solid #e4e4e4;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background-color: #f8f8f8;
}

.avatar-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  border: 2px solid white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.avatar-name {
  font-weight: 600;
  font-size: 0.95rem;
  color: #333;
}

.popover-menu {
  position: absolute;
  top: 60px;
  right: 0;
  background-color: white;
  border-radius: 12px;
  padding: 10px;
  z-index: 9999;
  width: 220px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.popover-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  text-decoration: none;
  width: 100%;
  transition: background-color 0.2s ease;
}

.popover-item:hover {
  background-color: #f6f6f6;
  color: #d6001c;
}

.footer-custom {
  background-color: #f8f9fa;
  font-size: 14px;
  color: #444;
}

.footer-title {
  font-size: 16px;
  font-weight: 600;
  color: #111;
  margin-bottom: 16px;
}

.footer-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
}
.footer-list li {
  margin-bottom: 10px;
}
.footer-list a {
  text-decoration: none;
  color: #444;
  transition: color 0.2s ease;
}
.footer-list a:hover {
  color: #d6001c;
}

.footer-social img,
.footer-payments img {
  height: 32px;
  margin: 4px 6px;
  transition: transform 0.2s ease;
}
.footer-social img:hover,
.footer-payments img:hover {
  transform: scale(1.1);
}


</style>


