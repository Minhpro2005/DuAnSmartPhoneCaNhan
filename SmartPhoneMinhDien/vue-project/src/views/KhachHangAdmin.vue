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
                <button class="btn btn-sm btn-danger me-1" @click="confirmDelete(kh)">Xoá</button>
                <router-link :to="`/admin/lichsu/${kh.maKH}`" class="btn btn-sm btn-secondary">
                  Lịch sử mua hàng
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal xác nhận xoá -->
    <div class="modal fade" id="deleteConfirmModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title text-danger">Xác nhận xoá</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc chắn muốn xoá khách hàng <strong>{{ selectedCustomer?.tenKH }}</strong>?
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">Huỷ</button>
            <button class="btn btn-danger" @click="deleteCustomer">Đồng ý xoá</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast thông báo -->
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
  name: 'KhachHangAdmin',
  data() {
    return {
      customers: [],
      searchText: '',
      selectedCustomer: null
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
    confirmDelete(customer) {
      this.selectedCustomer = customer;
      bootstrap.Modal.getOrCreateInstance(document.getElementById('deleteConfirmModal')).show();
    },
    deleteCustomer() {
      if (!this.selectedCustomer) return;

      fetch(`http://localhost:8080/smartphone/khachhang/${this.selectedCustomer.maKH}`, {
        method: 'DELETE'
      })
        .then(() => {
          this.fetchCustomers();
          this.showToast('Xoá khách hàng thành công!');
        })
        .catch(err => {
          console.error('Lỗi xoá:', err);
          this.showToast('Xoá thất bại!', 'danger');
        })
        .finally(() => {
          this.selectedCustomer = null;
          bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal')).hide();
        });
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
