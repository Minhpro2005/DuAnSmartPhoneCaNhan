<template>
  <div class="container mt-5" style="max-width: 400px">
    <h3 class="text-center mb-3">Đăng nhập</h3>
    <form @submit.prevent="handleLogin">
      <div class="mb-3">
        <label class="form-label">Email</label>
        <input v-model="email" type="email" class="form-control" required />
      </div>
      <div class="mb-3">
        <label class="form-label">Mật khẩu</label>
        <input type="password" v-model="password" class="form-control" required />
      </div>
      <div v-if="error" class="text-danger mb-2">{{ error }}</div>
      <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
    </form>
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

        // ✅ Lưu user vào localStorage
        localStorage.setItem("user", JSON.stringify(user));

        // ✅ Cập nhật trạng thái đăng nhập (status)
        await fetch(`http://localhost:8080/smartphone/users/${user.userID}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ trangThai: true })
        });

        // ✅ Điều hướng theo vai trò
        switch (user.vaiTro) {
          case 3:
            this.$router.push('/admin');
            break;
          case 2:
            this.$router.push('/employee');
            break;
          case 1:
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
