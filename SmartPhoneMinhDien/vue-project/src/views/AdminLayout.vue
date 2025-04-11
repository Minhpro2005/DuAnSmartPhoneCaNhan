<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <nav class="col-md-2 bg-dark text-white p-3 min-vh-100">
        <!-- Avatar + tên người dùng -->
        <div class="text-center mb-4">
          <img
            :src="user?.avatar ? 'http://localhost:8080' + user.avatar : defaultAvatar"
            class="rounded-circle mb-2"
            width="80"
            height="80"
            alt="User Avatar"
            style="object-fit: cover; border: 3px solid #fff;"
          />
          <h6 class="mb-0">{{ user?.hoTen || 'Admin' }}</h6>
          <small class="text-muted">{{ getRoleName(user?.vaiTro) }}</small>
        </div>

        <ul class="nav flex-column">
          <li class="nav-item">
            <router-link to="/admin" class="nav-link text-white" :class="{ 'fw-bold': $route.path === '/admin' }">
              Quản lý sản phẩm
            </router-link>
          </li>

          <li class="nav-item" v-if="isStaffOrAdmin">
            <router-link to="/admin/users" class="nav-link text-white" :class="{ 'fw-bold': $route.path === '/admin/users' }">
              Quản lý người dùng
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/admin/khachhang" class="nav-link text-white" :class="{ 'fw-bold': $route.path === '/admin/khachhang' }">
              Quản lý khách hàng
            </router-link>
          </li>

          <li class="nav-item" v-if="isStaffOrAdmin">
            <router-link to="/admin/nhanvien" class="nav-link text-white" :class="{ 'fw-bold': $route.path === '/admin/nhanvien' }">
              Quản lý nhân viên
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/admin/donhang" class="nav-link text-white" :class="{ 'fw-bold': $route.path.startsWith('/admin/donhang') }">
              Quản lý đơn hàng
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/admin/giohang" class="nav-link text-white" :class="{ 'fw-bold': $route.path.startsWith('/admin/giohang') }">
              Quản lý giỏ hàng
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/admin/hoadon" class="nav-link text-white" :class="{ 'fw-bold': $route.path.startsWith('/admin/hoadon') }">
              Quản lý hóa đơn
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/admin/thongtinnhanvien" class="nav-link text-white"
  :class="{ 'fw-bold': $route.path === '/admin/thongtinnhanvien' }">
  Thông tin cá nhân
</router-link>

          </li>
        </ul>
      </nav>

      <!-- Main content -->
      <main class="col-md-10 px-4">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  data() {
    return {
      user: null,
      defaultAvatar: 'https://cdn-icons-png.flaticon.com/512/149/149071.png'
    };
  },
  computed: {
    isAdmin() {
      return this.user?.vaiTro === 1;
    },
    isStaffOrAdmin() {
      return this.user?.vaiTro === 1 || this.user?.vaiTro === 2;
    }
  },
  mounted() {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
    } else {
      this.$router.push('/dangnhap');
    }
  },
  methods: {
    getRoleName(vaiTro) {
      if (vaiTro === 1) return 'Quản trị viên';
      if (vaiTro === 2) return 'Nhân viên';
      return 'Khách hàng';
    }
  }
};
</script>

<style scoped>
nav a.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  cursor: pointer;
}
</style>
