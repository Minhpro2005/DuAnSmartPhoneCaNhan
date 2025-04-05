<template>
    <div class="container d-flex justify-content-center align-items-center vh-100">
      <div class="card p-4 shadow" style="width: 100%; max-width: 450px;">
        <h3 class="text-center text-success mb-3">Đăng ký tài khoản</h3>
  
        <form @submit.prevent="handleRegister">
          <div class="mb-3">
            <label class="form-label">Họ tên</label>
            <input v-model="form.hoTen" class="form-control" required />
          </div>
  
          <div class="mb-3">
            <label class="form-label">Email</label>
            <input v-model="form.email" type="email" class="form-control" required />
          </div>
  
          <div class="mb-3">
            <label class="form-label">Mật khẩu</label>
            <input v-model="form.matKhau" type="password" class="form-control" required />
          </div>
  
          <div class="mb-3">
            <label class="form-label">Số điện thoại</label>
            <input v-model="form.sdt" class="form-control" />
          </div>
  
          <div class="mb-3">
            <label class="form-label">Địa chỉ</label>
            <input v-model="form.diaChi" class="form-control" />
          </div>
  
          <div v-if="message" class="alert" :class="messageClass">{{ message }}</div>
  
          <button type="submit" class="btn btn-success w-100">Đăng ký</button>
        </form>
  
        <div class="text-center mt-3">
          <small>
            Đã có tài khoản?
            <router-link to="/dangnhap/0" class="text-primary text-decoration-none">Đăng nhập</router-link>
          </small>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        form: {
          hoTen: '',
          email: '',
          matKhau: '',
          sdt: '',
          diaChi: ''
        },
        message: '',
        messageClass: ''
      };
    },
    methods: {
      handleRegister() {
        fetch('http://localhost:8080/api/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.form)
        })
          .then(async res => {
            const text = await res.text();
            if (res.ok) {
              this.message = text || 'Đăng ký thành công!';
              this.messageClass = 'alert-success';
  
              // ⏳ Chờ 1.5 giây rồi chuyển về trang đăng nhập
              setTimeout(() => {
                this.$router.push('/dangnhap/0');
              }, 1500);
            } else {
              this.message = text || 'Đăng ký thất bại!';
              this.messageClass = 'alert-danger';
            }
          })
          .catch(() => {
            this.message = 'Lỗi kết nối máy chủ!';
            this.messageClass = 'alert-danger';
          });
      }
    }
  };
  </script>
  
  <style scoped>
  .card {
    border-radius: 1rem;
  }
  </style>
  