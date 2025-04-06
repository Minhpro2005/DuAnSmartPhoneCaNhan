<template>
    <div class="container py-3">
      <h2 class="mb-3">Quản lý nhân viên</h2>
  
      <button class="btn btn-primary mb-3" @click="openAddModal">
        <i class="fas fa-user-plus me-1"></i> Thêm nhân viên
      </button>
  
      <div class="card">
        <div class="card-header">
          <input v-model="searchText" class="form-control" placeholder="Tìm theo tên, email..." />
        </div>
        <div class="card-body">
          <table class="table table-bordered table-hover">
            <thead class="table-light">
              <tr>
                <th>Ảnh</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>SĐT</th>
                <th>CCCD</th>
                <th>Ngày tạo</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="nv in filteredNhanViens" :key="nv.userID">
                <td>
                  <img :src="nv.avatar || 'https://via.placeholder.com/50'" alt="avatar" class="rounded-circle" width="50" />
                </td>
                <td>{{ nv.hoTen }}</td>
                <td>{{ nv.email }}</td>
                <td>{{ nv.sdt }}</td>
                <td>{{ nv.cccd || '---' }}</td>
                <td>{{ formatDate(nv.ngayTao) }}</td>
                <td>
                  <span :class="nv.trangThai ? 'text-success' : 'text-danger'">
                    {{ nv.trangThai ? 'Hoạt động' : 'Đã khóa' }}
                  </span>
                </td>
                <td>
                  <button class="btn btn-sm btn-info me-1" @click="editNhanVien(nv)">Sửa</button>
                  <button class="btn btn-sm btn-warning me-1" @click="toggleTrangThai(nv)">
                    {{ nv.trangThai ? 'Khóa' : 'Mở khóa' }}
                  </button>
                  <button class="btn btn-sm btn-danger" @click="deleteNhanVien(nv.userID)">Xóa</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
  
      <!-- Modal Thêm/Sửa nhân viên -->
      <div class="modal fade" id="nvModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <form @submit.prevent="submitNhanVien">
              <div class="modal-header">
                <h5 class="modal-title">{{ isEditing ? 'Cập nhật' : 'Thêm' }} nhân viên</h5>
                <button class="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body">
                <input v-model="form.hoTen" class="form-control mb-2" placeholder="Họ tên" required />
                <input v-model="form.email" type="email" class="form-control mb-2" placeholder="Email" required />
                <input v-if="!isEditing" v-model="form.matKhau" type="password" class="form-control mb-2" placeholder="Mật khẩu" required />
                <input v-model="form.sdt" class="form-control mb-2" placeholder="Số điện thoại" />
                <input v-model="form.cccd" class="form-control mb-2" placeholder="CCCD" />
  
                <label class="form-label">Ảnh đại diện</label>
                <input type="file" class="form-control mb-2" @change="handleImageUpload" />
                <div v-if="previewImage" class="mb-2 text-center">
                  <img :src="previewImage" class="rounded-circle" width="100" />
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Huỷ</button>
                <button class="btn btn-primary" type="submit">{{ isEditing ? 'Cập nhật' : 'Thêm' }}</button>
              </div>
            </form>
          </div>
        </div>
      </div>
  
      <!-- Toast -->
      <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1055">
        <div id="liveToast" class="toast align-items-center text-white bg-success border-0" role="alert">
          <div class="d-flex">
            <div class="toast-body" id="toastMessage">Thành công!</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import * as bootstrap from 'bootstrap';
  
  export default {
    name: 'StaffAdmin',
    data() {
      return {
        nhanViens: [],
        searchText: '',
        isEditing: false,
        form: {
          userID: null,
          hoTen: '',
          email: '',
          matKhau: '',
          sdt: '',
          cccd: '',
          avatar: '',
          vaiTro: 2,
          trangThai: true
        },
        selectedImage: null,
        previewImage: ''
      };
    },
    computed: {
      filteredNhanViens() {
        const q = this.searchText.toLowerCase();
        return this.nhanViens.filter(u =>
          u.hoTen?.toLowerCase().includes(q) || u.email?.toLowerCase().includes(q)
        );
      }
    },
    methods: {
      fetchNhanVien() {
        fetch("http://localhost:8080/smartphone/users")
          .then(res => res.json())
          .then(data => {
            this.nhanViens = data.filter(u => u.vaiTro === 2);
          });
      },
      openAddModal() {
        this.isEditing = false;
        this.form = {
          userID: null,
          hoTen: '',
          email: '',
          matKhau: '',
          sdt: '',
          cccd: '',
          avatar: '',
          vaiTro: 2,
          trangThai: true
        };
        this.previewImage = '';
        this.selectedImage = null;
        bootstrap.Modal.getOrCreateInstance(document.getElementById('nvModal')).show();
      },
      editNhanVien(nv) {
        this.isEditing = true;
        this.form = { ...nv };
        this.previewImage = nv.avatar;
        this.selectedImage = null;
        bootstrap.Modal.getOrCreateInstance(document.getElementById('nvModal')).show();
      },
      handleImageUpload(e) {
        const file = e.target.files[0];
        this.selectedImage = file;
        this.previewImage = URL.createObjectURL(file);
      },
      submitNhanVien() {
        const formData = new FormData();
        formData.append('hoTen', this.form.hoTen);
        formData.append('email', this.form.email);
        if (!this.isEditing) formData.append('matKhau', this.form.matKhau);
        formData.append('sdt', this.form.sdt || '');
        formData.append('cccd', this.form.cccd || '');
        formData.append('vaiTro', 2);
        formData.append('trangThai', true);
        if (this.selectedImage) formData.append('avatar', this.selectedImage);
  
        const method = this.isEditing ? 'PUT' : 'POST';
        const url = this.isEditing
          ? `http://localhost:8080/smartphone/users/${this.form.userID}`
          : `http://localhost:8080/smartphone/users`;
  
        fetch(url, {
          method,
          body: formData
        })
          .then(() => {
            this.fetchNhanVien();
            bootstrap.Modal.getInstance(document.getElementById('nvModal')).hide();
            this.showToast(this.isEditing ? 'Cập nhật nhân viên' : 'Thêm nhân viên');
          });
      },
      toggleTrangThai(nv) {
        fetch(`http://localhost:8080/smartphone/users/${nv.userID}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ trangThai: !nv.trangThai })
        }).then(() => {
          this.fetchNhanVien();
          this.showToast("Cập nhật trạng thái");
        });
      },
      deleteNhanVien(id) {
        if (confirm("Bạn chắc chắn muốn xoá nhân viên này?")) {
          fetch(`http://localhost:8080/smartphone/users/${id}`, {
            method: "DELETE"
          }).then(() => {
            this.fetchNhanVien();
            this.showToast("Xoá thành công");
          });
        }
      },
      formatDate(date) {
        return new Date(date).toLocaleDateString("vi-VN");
      },
      showToast(message, color = "success") {
        const toastEl = document.getElementById("liveToast");
        const toastBody = document.getElementById("toastMessage");
        toastEl.className = `toast align-items-center text-white bg-${color} border-0`;
        toastBody.innerText = message;
        bootstrap.Toast.getOrCreateInstance(toastEl).show();
      }
    },
    mounted() {
      this.fetchNhanVien();
    }
  };
  </script>
  
  <style scoped>
  .table td,
  .table th {
    vertical-align: middle;
  }
  </style>
  