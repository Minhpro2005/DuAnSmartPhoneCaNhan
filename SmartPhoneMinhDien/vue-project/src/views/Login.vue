<template>
  <div class="container d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow" style="width: 100%; max-width: 400px;">
      <h3 class="text-center mb-3 text-primary">Đăng nhập</h3>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input
            v-model="email"
            type="email"
            class="form-control"
            placeholder="Nhập email..."
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input
            v-model="password"
            type="password"
            class="form-control"
            placeholder="Nhập mật khẩu..."
            required
          />
        </div>

        <div v-if="error" class="alert alert-danger py-2">
          {{ error }}
        </div>

        <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
      </form>

      <div class="text-center mt-3">
        <small>
          Chưa có tài khoản?
          <router-link to="/register" class="text-decoration-none text-primary">
            Đăng ký ngay
          </router-link>
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
      this.error = '';

      try {
        const res = await fetch('http://localhost:8080/api/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            email: this.email,
            matKhau: this.password
          })
        });

        if (res.status === 401) {
          this.error = '❌ Sai email hoặc mật khẩu';
          return;
        }

        if (res.status === 403) {
          this.error = '🚫 Tài khoản của bạn đã bị khóa';
          return;
        }

        if (!res.ok) {
          this.error = '⚠️ Lỗi không xác định';
          return;
        }

        const user = await res.json();
        localStorage.setItem('user', JSON.stringify(user));

        // Điều hướng theo vai trò
        switch (user.vaiTro) {
          case 1: // Quản trị viên
          case 2: // Nhân viên
            this.$router.push('/admin');
            break;
          case 3: // Khách hàng
            this.$router.push('/');
            break;
          default:
            this.error = '❌ Vai trò không hợp lệ';
        }
      } catch (err) {
        console.error('Lỗi đăng nhập:', err);
        this.error = '🚫 Không thể kết nối máy chủ';
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
