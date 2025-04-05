<template>
    <div class="container py-3">
      <h2 class="mb-3">Quản lý người dùng</h2>
      <button v-if="isAdmin" class="btn btn-primary mb-3" @click="openAddModal">
        <i class="fas fa-user-plus me-1"></i> Thêm người dùng
      </button>
  
      <div class="card">
        <div class="card-header">
          <input v-model="searchUser" type="text" class="form-control" placeholder="Tìm người dùng..." />
        </div>
        <div class="card-body">
          <table class="table table-bordered table-hover">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>SĐT</th>
                <th>Ngày tạo</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.userID">
                <td>{{ user.userID }}</td>
                <td>{{ user.hoTen }}</td>
                <td>{{ user.email }}</td>
                <td>{{ user.sdt }}</td>
                <td>{{ formatDate(user.ngayTao) }}</td>
                <td>{{ getRoleName(user.vaiTro) }}</td>
                <td>
                  <span :class="user.trangThai ? 'text-success' : 'text-danger'">
                    {{ user.trangThai ? 'Hoạt động' : 'Đã khóa' }}
                  </span>
                </td>
                <td>
                  <button v-if="isAdmin" class="btn btn-sm btn-info me-1" @click="editUser(user)">Sửa</button>
                  <button v-if="isAdmin" class="btn btn-sm btn-danger me-1" @click="deleteUser(user.userID)">Xóa</button>
                  <button v-if="isAdmin" class="btn btn-sm btn-warning me-1" @click="confirmToggle(user)">
                    {{ user.trangThai ? 'Khóa' : 'Mở khóa' }}
                  </button>
                  <button v-if="isAdmin" class="btn btn-sm btn-secondary" @click="resetPassword(user.userID)">
                    Reset mật khẩu
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
  
      <!-- Modal Thêm/Sửa -->
      <div class="modal fade" id="userModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <form @submit.prevent="handleSubmit">
              <div class="modal-header">
                <h5 class="modal-title">{{ isEditing ? 'Cập nhật' : 'Thêm' }} người dùng</h5>
                <button class="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body">
                <input v-model="formUser.hoTen" class="form-control mb-2" placeholder="Họ tên" required />
                <input v-model="formUser.email" type="email" class="form-control mb-2" placeholder="Email" required />
                <input v-if="!isEditing" v-model="formUser.matKhau" type="password" class="form-control mb-2" placeholder="Mật khẩu" required />
                <input v-model="formUser.sdt" class="form-control mb-2" placeholder="Số điện thoại" />
                <select v-model="formUser.vaiTro" class="form-control mb-2">
                  <option :value="1">Quản trị viên</option>
                  <option :value="2">Nhân viên</option>
                  <option :value="3">Khách hàng</option>
                </select>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">{{ isEditing ? 'Cập nhật' : 'Thêm' }}</button>
              </div>
            </form>
          </div>
        </div>
      </div>
  
      <!-- Bootstrap Toast -->
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
    data() {
      return {
        users: [],
        searchUser: '',
        isEditing: false,
        formUser: {
          userID: null,
          hoTen: '',
          email: '',
          matKhau: '',
          sdt: '',
          vaiTro: 3,
          trangThai: true
        },
        currentUser: JSON.parse(localStorage.getItem('currentUser')) || { vaiTro: 1 }
      };
    },
    computed: {
      filteredUsers() {
        return this.searchUser
          ? this.users.filter(u =>
              u.hoTen.toLowerCase().includes(this.searchUser.toLowerCase()) ||
              u.email.toLowerCase().includes(this.searchUser.toLowerCase())
            )
          : this.users;
      },
      isAdmin() {
        return this.currentUser.vaiTro === 1;
      }
    },
    methods: {
      getRoleName(role) {
        return role === 1 ? 'Quản trị viên' : role === 2 ? 'Nhân viên' : 'Khách hàng';
      },
      fetchUsers() {
        fetch('http://localhost:8080/smartphone/users')
          .then(res => res.json())
          .then(data => (this.users = data));
      },
      openAddModal() {
        this.isEditing = false;
        this.formUser = {
          userID: null,
          hoTen: '',
          email: '',
          matKhau: '',
          sdt: '',
          vaiTro: 3,
          trangThai: true
        };
        bootstrap.Modal.getOrCreateInstance(document.getElementById('userModal')).show();
      },
      editUser(user) {
        this.isEditing = true;
        this.formUser = { ...user };
        bootstrap.Modal.getOrCreateInstance(document.getElementById('userModal')).show();
      },
      handleSubmit() {
        const method = this.isEditing ? 'PUT' : 'POST';
        const url = this.isEditing
          ? `http://localhost:8080/smartphone/users/${this.formUser.userID}`
          : 'http://localhost:8080/smartphone/users';
  
        fetch(url, {
          method,
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.formUser)
        })
          .then(res => res.json())
          .then(() => {
            this.fetchUsers();
            bootstrap.Modal.getOrCreateInstance(document.getElementById('userModal')).hide();
            this.showToast(this.isEditing ? 'Cập nhật thành công' : 'Thêm thành công');
          });
      },
      deleteUser(id) {
        if (confirm("Bạn có chắc chắn muốn xóa người dùng này?")) {
          fetch(`http://localhost:8080/smartphone/users/${id}`, {
            method: 'DELETE'
          })
            .then(() => {
              this.fetchUsers();
              this.showToast('Đã xóa người dùng', 'danger');
            });
        }
      },
      confirmToggle(user) {
        const action = user.trangThai ? 'khóa' : 'mở khóa';
        if (confirm(`Bạn muốn ${action} tài khoản này?`)) {
          this.toggleUserStatus(user);
        }
      },
      toggleUserStatus(user) {
        fetch(`http://localhost:8080/smartphone/users/${user.userID}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ trangThai: !user.trangThai })
        })
          .then(res => res.json())
          .then(() => {
            this.fetchUsers();
            this.showToast('Cập nhật trạng thái', 'warning');
          });
      },
      resetPassword(id) {
        if (confirm("Reset mật khẩu người dùng này?")) {
          fetch(`http://localhost:8080/smartphone/users/reset-password/${id}`, {
            method: 'POST'
          }).then(() => {
            this.showToast('Mật khẩu đã reset', 'info');
          });
        }
      },
      formatDate(date) {
  return date ? new Date(date).toLocaleDateString('vi-VN') : '';
},

      showToast(message, color = 'success') {
        const toastEl = document.getElementById('liveToast');
        const toastBody = document.getElementById('toastMessage');
        toastEl.className = `toast align-items-center text-white bg-${color} border-0`;
        toastBody.innerText = message;
        const toast = bootstrap.Toast.getOrCreateInstance(toastEl);
        toast.show();
      }
    },
    mounted() {
      this.fetchUsers();
    }
  };
  </script>
  
  <style scoped>
  .table img {
    object-fit: cover;
    height: 50px;
    border-radius: 4px;
  }
  </style>
  