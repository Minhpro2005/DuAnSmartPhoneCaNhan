<template>
    <div class="container py-5" v-if="user">
      <h2 class="mb-4 text-center">Thông tin cá nhân</h2>
  
      <div class="card mx-auto shadow" style="max-width: 500px; border-radius: 20px;">
        <div class="card-body text-center">
  
          <!-- Avatar -->
          <label for="avatarInput" class="avatar-wrapper">
            <img
              :src="previewImage || (user.avatar ? 'http://localhost:8080' + user.avatar : 'https://cdn-icons-png.flaticon.com/512/149/149071.png')"
              alt="avatar"
              class="avatar"
            />
            <input type="file" id="avatarInput" class="d-none" @change="onImageChange" />
          </label>
  
          <h4 class="mb-1">{{ user.hoTen }}</h4>
          <p class="text-muted">{{ getRoleName(user.vaiTro) }}</p>
  
          <!-- Form Info -->
          <div class="text-start mt-4 px-3">
            <label class="form-label">Họ tên:</label>
            <input type="text" class="form-control mb-3" v-model="user.hoTen" />
  
            <label class="form-label">Email:</label>
            <input type="email" class="form-control mb-3" v-model="user.email" />
  
            <label class="form-label">Số điện thoại:</label>
            <input type="text" class="form-control mb-3" v-model="user.sdt" />
  
            <p><i class="bi bi-calendar text-primary me-2"></i><strong>Ngày tạo:</strong> {{ formatDate(user.ngayTao) }}</p>
          </div>
  
          <!-- Action Buttons -->
          <div class="d-flex justify-content-between mt-4 px-3">
            <button class="btn btn-outline-danger w-45" @click="logout">
              <i class="bi bi-box-arrow-right me-1"></i> Đăng xuất
            </button>
            <button class="btn btn-success w-45" @click="updateUser">
              <i class="bi bi-save me-1"></i> Lưu thay đổi
            </button>
          </div>
  
        </div>
      </div>
    </div>
  
    <div v-else class="text-center mt-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
      <p class="mt-2">Đang tải thông tin người dùng...</p>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ThongTinCaNhan',
    props: ['id'],
    data() {
      return {
        user: null,
        previewImage: null,
        selectedFile: null
      };
    },
    methods: {
      fetchUser() {
        fetch(`http://localhost:8080/smartphone/users/${this.id}`)
          .then(res => res.json())
          .then(data => {
            this.user = data;
          })
          .catch(err => {
            console.error('Lỗi tải thông tin người dùng:', err);
          });
      },
      getRoleName(role) {
        return role === 1 ? 'Quản trị viên' : role === 2 ? 'Nhân viên' : 'Khách hàng';
      },
      formatDate(date) {
        return date ? new Date(date).toLocaleDateString('vi-VN') : '';
      },
      logout() {
        localStorage.removeItem('user');
        this.$router.push('/dangnhap/1');
      },
      onImageChange(e) {
        const file = e.target.files[0];
        if (file) {
          this.selectedFile = file;
          this.previewImage = URL.createObjectURL(file);
        }
      },
      updateUser() {
        const formData = new FormData();
        formData.append('hoTen', this.user.hoTen);
        formData.append('email', this.user.email);
        formData.append('sdt', this.user.sdt || '');
        if (this.selectedFile) {
          formData.append('avatar', this.selectedFile);
        }
  
        fetch(`http://localhost:8080/smartphone/users/${this.id}`, {
          method: 'PUT',
          body: formData
        })
          .then(res => {
            if (!res.ok) throw new Error('Lỗi khi cập nhật thông tin');
            return res.json();
          })
          .then(data => {
            alert('Cập nhật thành công!');
            this.user = data;
            localStorage.setItem('user', JSON.stringify(data));
            this.previewImage = null;
          })
          .catch(err => {
            console.error(err);
            alert('Cập nhật thất bại!');
          });
      }
    },
    mounted() {
      this.fetchUser();
    }
  };
  </script>
  
  <style scoped>
  .avatar-wrapper:hover .avatar {
    box-shadow: 0 0 0 3px #d6001c;
    transition: all 0.2s ease-in-out;
  }
  .avatar {
    width: 110px;
    height: 110px;
    object-fit: cover;
    border-radius: 50%;
    border: 3px solid #eee;
  }
  .card {
    border-radius: 16px;
  }
  input.form-control {
    font-size: 14px;
  }
  .w-45 {
    width: 45%;
  }
  </style>
  