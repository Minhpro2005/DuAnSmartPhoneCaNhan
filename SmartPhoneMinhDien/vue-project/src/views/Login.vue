<template>
  <div class="container d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow" style="width: 100%; max-width: 400px;">
      <h3 class="text-center mb-3 text-primary">Đăng nhập</h3>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" class="form-control" placeholder="Nhập email..." required />
        </div>

        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input type="password" v-model="password" class="form-control" placeholder="Nhập mật khẩu..." required />
        </div>

        <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>

        <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
      </form>

      <div class="text-center mt-3">
        <small>
          Chưa có tài khoản?
          <router-link to="/register" class="text-decoration-none text-primary">Đăng ký ngay</router-link>
        </small>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      error: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const res = await fetch('http://localhost:8080/smartphone/users/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            email: this.email,
            matKhau: this.password
          })
        });

        if (!res.ok) {
          this.error = 'Tài khoản hoặc mật khẩu không đúng';
          return;
        }

        const user = await res.json();

        if (!user || !user.userID) {
          this.error = 'Không xác định được người dùng';
          return;
        }

        localStorage.setItem("user", JSON.stringify(user));

        // Cập nhật trạng thái hoạt động
        await fetch(`http://localhost:8080/smartphone/users/${user.userID}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ trangThai: true })
        });

        // Điều hướng theo vai trò
        switch (user.vaiTro) {
  case 1: // Admin
    this.$router.push('/admin');
    break;
  case 2: // Nhân viên
    this.$router.push('/employee');
    break;
  case 3: // Khách hàng
    this.$router.push('/');
    break;
  default:
    this.error = 'Không xác định vai trò người dùng';
}


      } catch (err) {
        console.error('Lỗi đăng nhập:', err);
        this.error = 'Đã xảy ra lỗi. Vui lòng thử lại.';
      }
    }
  }
};
</script>

<style scoped>
.card {
  border-radius: 1rem;
}
</style>
