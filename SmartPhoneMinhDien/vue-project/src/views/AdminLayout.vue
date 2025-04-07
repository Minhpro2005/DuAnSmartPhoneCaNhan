<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <nav class="col-md-2 bg-dark text-white p-3 min-vh-100">
        <div class="text-center">
          <img
            src="https://images.unsplash.com/photo-1633409361618-c73427e4e206"
            class="rounded-circle mb-3"
            width="80"
            alt="Store Logo"
          />
          <h5>Phone Store</h5>
        </div>
        <ul class="nav flex-column">
  <li class="nav-item">
    <router-link
      to="/admin"
      class="nav-link text-white"
      :class="{ 'fw-bold': $route.path === '/admin' }"
    >
      Quản lý sản phẩm
    </router-link>
  </li>
  <li class="nav-item">
    <router-link
      to="/admin/users"
      class="nav-link text-white"
      :class="{ 'fw-bold': $route.path === '/admin/users' }"
    >
      Quản lý người dùng
    </router-link>
  </li>
  <li class="nav-item">
    <router-link
      to="/admin/khachhang"
      class="nav-link text-white"
      :class="{ 'fw-bold': $route.path === '/admin/khachhang' }"
    >
      Quản lý khách hàng
    </router-link>
  </li>
  <li class="nav-item">
    <router-link
      to="/admin/nhanvien"
      class="nav-link text-white"
      :class="{ 'fw-bold': $route.path === '/admin/nhanvien' }"
    >
      Quản lý nhân viên
    </router-link>
  </li>
  <li class="nav-item">
  <router-link
    to="/admin/donhang"
    class="nav-link text-white"
    :class="{ 'fw-bold': $route.path.startsWith('/admin/donhang') }"
  >
    Quản lý đơn hàng
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

<style scoped>
nav a.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  cursor: pointer;
}
</style>
