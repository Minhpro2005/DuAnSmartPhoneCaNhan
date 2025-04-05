<template>
    <div class="container py-3">
      <h2 class="mb-3">Quản lý khách hàng</h2>
  
      <div class="card">
        <div class="card-header">
          <input v-model="searchText" class="form-control" placeholder="Tìm khách hàng theo tên, email..." />
        </div>
        <div class="card-body">
          <table class="table table-bordered table-hover">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>SĐT</th>
                <th>Địa chỉ</th>
                <th>UserID</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="kh in filteredCustomers" :key="kh.maKH">
                <td>{{ kh.maKH }}</td>
                <td>{{ kh.tenKH }}</td>
                <td>{{ kh.email }}</td>
                <td>{{ kh.sdt }}</td>
                <td>{{ kh.diaChi }}</td>
                <td>{{ kh.user?.userID || 'Không có' }}</td>
                <td>
                  <button class="btn btn-sm btn-info me-1" @click="editCustomer(kh)">Sửa</button>
                  <button class="btn btn-sm btn-danger me-1" @click="deleteCustomer(kh.maKH)">Xoá</button>
                  <router-link :to="`/admin/lichsu/${kh.maKH}`" class="btn btn-sm btn-secondary">
                    Lịch sử mua hàng
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
  
      <!-- Modal chỉnh sửa -->
      <div class="modal fade" id="customerModal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <form @submit.prevent="handleUpdate">
              <div class="modal-header">
                <h5 class="modal-title">Cập nhật khách hàng</h5>
                <button class="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body">
                <input v-model="form.tenKH" class="form-control mb-2" placeholder="Tên khách hàng" required />
                <input v-model="form.email" class="form-control mb-2" placeholder="Email" required />
                <input v-model="form.sdt" class="form-control mb-2" placeholder="Số điện thoại" />
                <input v-model="form.diaChi" class="form-control mb-2" placeholder="Địa chỉ" />
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                <button type="submit" class="btn btn-primary">Lưu</button>
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
        customers: [],
        searchText: '',
        form: {
          maKH: null,
          tenKH: '',
          sdt: '',
          diaChi: '',
          email: ''
        }
      };
    },
    computed: {
      filteredCustomers() {
        const query = this.searchText.toLowerCase();
        return this.customers.filter(c =>
          (c.tenKH?.toLowerCase() || '').includes(query) ||
          (c.email?.toLowerCase() || '').includes(query)
        );
      }
    },
    methods: {
      fetchCustomers() {
        fetch('http://localhost:8080/smartphone/khachhang')
          .then(res => res.json())
          .then(data => (this.customers = data))
          .catch(err => console.error('Lỗi khi tải khách hàng:', err));
      },
      editCustomer(customer) {
        this.form = { ...customer };
        bootstrap.Modal.getOrCreateInstance(document.getElementById('customerModal')).show();
      },
      handleUpdate() {
        fetch(`http://localhost:8080/smartphone/khachhang/${this.form.maKH}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.form)
        })
          .then(() => {
            this.fetchCustomers();
            bootstrap.Modal.getOrCreateInstance(document.getElementById('customerModal')).hide();
            this.showToast('Cập nhật thành công!');
          })
          .catch(err => {
            console.error('Lỗi cập nhật:', err);
            this.showToast('Cập nhật thất bại!', 'danger');
          });
      },
      deleteCustomer(maKH) {
        if (confirm('Bạn có chắc chắn muốn xoá khách hàng này?')) {
          fetch(`http://localhost:8080/smartphone/khachhang/${maKH}`, {
            method: 'DELETE'
          })
            .then(() => {
              this.fetchCustomers();
              this.showToast('Xoá khách hàng thành công!');
            })
            .catch(err => {
              console.error('Lỗi xoá:', err);
              this.showToast('Xoá thất bại!', 'danger');
            });
        }
      },
      showToast(message, color = 'success') {
        const toastEl = document.getElementById('liveToast');
        const toastBody = document.getElementById('toastMessage');
        toastEl.className = `toast align-items-center text-white bg-${color} border-0`;
        toastBody.innerText = message;
        bootstrap.Toast.getOrCreateInstance(toastEl).show();
      }
    },
    mounted() {
      this.fetchCustomers();
    }
  };
  </script>
  
  <style scoped>
  .table td,
  .table th {
    vertical-align: middle;
  }
  </style>
  