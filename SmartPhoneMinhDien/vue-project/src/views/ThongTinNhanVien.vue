<template>
    <div class="container py-5" v-if="user && nhanVien">
      <h2 class="mb-4 text-center">Thông tin nhân viên</h2>
  
      <div class="card mx-auto shadow" style="max-width: 500px; border-radius: 20px;">
        <div class="card-body text-center">
          <!-- Avatar -->
          <label for="avatarInput" class="avatar-wrapper">
            <img
              :src="previewImage || (user.avatar ? 'http://localhost:8080' + user.avatar : defaultAvatar)"
              alt="avatar"
              class="avatar"
            />
            <input type="file" id="avatarInput" class="d-none" @change="onImageChange" />
          </label>
  
          <h4 class="mb-1">{{ user.hoTen }}</h4>
          <p class="text-muted"><i class="bi bi-person-badge-fill me-1"></i>Nhân viên</p>
  
          <div class="text-start mt-4 px-3">
            <label class="form-label">Họ tên:</label>
            <input type="text" class="form-control mb-3" v-model="user.hoTen" />
  
            <label class="form-label">Email:</label>
            <input type="email" class="form-control mb-3" v-model="user.email" />
  
            <label class="form-label">Số điện thoại:</label>
            <input type="text" class="form-control mb-3" v-model="user.sdt" />
  
            <label class="form-label">CCCD:</label>
            <input type="text" class="form-control mb-3" v-model="nhanVien.cccd" />
  
            <p><strong>Ngày tạo:</strong> {{ formatDate(user.ngayTao) }}</p>
          </div>
  
          <div class="d-flex justify-content-between mt-4 px-3">
            <button class="btn btn-outline-danger w-45" @click="logout">
              <i class="bi bi-box-arrow-right me-1"></i> Đăng xuất
            </button>
            <button class="btn btn-success w-45" @click="updateUser">
              <i class="bi bi-save me-1"></i> Lưu
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ThongTinNhanVien',
    data() {
      return {
        user: null,
        nhanVien: null,
        previewImage: null,
        selectedFile: null,
        defaultAvatar: 'https://cdn-icons-png.flaticon.com/512/149/149071.png'
      };
    },
    mounted() {
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        const parsed = JSON.parse(storedUser);
        this.user = parsed;
  
        // Lấy thông tin từ bảng nhân viên
        fetch(`http://localhost:8080/smartphone/nhanvien/user/${parsed.userID}`)
          .then(res => res.json())
          .then(nv => {
            this.nhanVien = nv;
          })
          .catch(() => this.$router.push('/dangnhap'));
      } else {
        this.$router.push('/dangnhap');
      }
    },
    methods: {
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
      async updateUser() {
        try {
          const formData = new FormData();
          formData.append('hoTen', this.user.hoTen);
          formData.append('email', this.user.email);
          formData.append('sdt', this.user.sdt || '');
          formData.append('vaiTro', this.user.vaiTro);
          if (this.selectedFile) {
            formData.append('avatar', this.selectedFile);
          }
  
          // Gửi cập nhật users
          const userRes = await fetch(`http://localhost:8080/smartphone/users/${this.user.userID}`, {
            method: 'PUT',
            body: formData
          });
  
          const updatedUser = await userRes.json();
          localStorage.setItem('user', JSON.stringify(updatedUser));
          this.user = updatedUser;
  
          // Gửi cập nhật nhân viên (cccd)
          await fetch(`http://localhost:8080/smartphone/nhanvien/id/${this.nhanVien.maNV}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              ...this.nhanVien,
              cccd: this.nhanVien.cccd,
              user: { userID: this.user.userID } // liên kết lại user
            })
          });
  
          alert('✅ Cập nhật thành công!');
          this.previewImage = null;
        } catch (e) {
          alert('❌ Lỗi khi cập nhật!');
          console.error(e);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .avatar {
    width: 110px;
    height: 110px;
    object-fit: cover;
    border-radius: 50%;
    border: 3px solid #eee;
  }
  .w-45 {
    width: 45%;
  }
  .avatar-wrapper:hover .avatar {
    box-shadow: 0 0 0 3px #007bff;
    transition: all 0.2s ease-in-out;
  }
  </style>
  