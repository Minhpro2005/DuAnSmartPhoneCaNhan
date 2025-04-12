<template>
  
  <div>
    <!-- Tiêu đề và nút -->
    <div class="d-flex justify-content-between align-items-center pt-3 pb-2 border-bottom">
      <h2 class="h2">Điều khiển sản phẩm</h2>
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal" @click="openAddModal">
        <i class="fas fa-plus me-1"></i> Thêm sản phẩm
      </button>
    </div>

    <!-- Thống kê -->
    <div class="row mt-3">
      <div class="col-md-4"><div class="alert alert-info">Tổng sản phẩm: {{ products.length }}</div></div>
      <div class="col-md-4"><div class="alert alert-warning">Sắp hết hàng (≤5): {{ lowStockCount }}</div></div>
      <div class="col-md-4"><div class="alert alert-danger">Ngừng bán: {{ stoppedCount }}</div></div>
    </div>

    <!-- Tìm kiếm + Bảng sản phẩm -->
    <div class="card mt-3">
      <div class="card-header bg-light">
        <input type="text" class="form-control" placeholder="Tìm sản phẩm..." v-model="searchQuery" />
      </div>
      <div class="card-body">
        <table class="table table-bordered table-hover">
          <thead class="table-light">
            <tr>
              <th>ID</th><th>Hình ảnh</th><th>Tên</th><th>Giá</th><th>Số lượng</th>
              <th>Màu</th><th>Hãng</th><th>Mô tả</th><th>Trạng thái</th><th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="phone in paginatedProducts" :key="phone.maSP">
              <td>{{ phone.maSP }}</td>
              <td><img :src="phone.hinhAnhSP || 'https://via.placeholder.com/100x80?text=No+Image'" width="50" /></td>
              <td>{{ phone.tenSP }}</td>
              <td>{{ formatVND(phone.giaBan) }}</td>
              <td :class="{ 'text-danger fw-bold': phone.soLuong <= 5 }">{{ phone.soLuong }}</td>
              <td>{{ phone.mauSac }}</td>
              <td>{{ phone.hang }}</td>
              <td>{{ phone.moTa }}</td>
              <td>
                <span :class="phone.trangThai ? 'text-success' : 'text-danger'">
                  {{ phone.trangThai ? 'Có bán' : 'Ngừng bán' }}
                </span>
                <div class="form-check form-switch">
                  <input class="form-check-input" type="checkbox" :checked="phone.trangThai" @change="toggleStatus(phone)" />
                </div>
              </td>
              <td>
                <button class="btn btn-sm btn-warning me-1" @click="editProduct(phone)">Sửa</button>
                <button class="btn btn-sm btn-danger me-1" @click="handleDelete(phone.maSP)">Xóa</button>
                <button class="btn btn-sm btn-info" @click="openVariantModal(phone)">Biến thể</button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Phân trang -->
        <nav>
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 1 }">
              <button class="page-link" @click="currentPage--">Trước</button>
            </li>
            <li class="page-item" v-for="n in totalPages" :key="n" :class="{ active: currentPage === n }">
              <button class="page-link" @click="currentPage = n">{{ n }}</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
              <button class="page-link" @click="currentPage++">Sau</button>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- Modal Thêm/Sửa sản phẩm -->
    <div class="modal fade" id="addProductModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <form @submit.prevent="handleSubmit">
            <div class="modal-header">
              <h5 class="modal-title">{{ isEditing ? 'Cập nhật' : 'Thêm' }} sản phẩm</h5>
              <button class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body row">
              <div class="col-md-6">
                <label class="form-label">Tên sản phẩm</label>
                <input v-model="newProduct.tenSP" class="form-control mb-2" required />
                <label class="form-label">Giá bán</label>
                <input v-model="newProduct.giaBan" type="number" class="form-control mb-2" required />
                <label class="form-label">Số lượng</label>
                <input v-model="newProduct.soLuong" type="number" min="0" class="form-control mb-2" required />
                <label class="form-label">Hãng</label>
                <input v-model="newProduct.hang" class="form-control mb-2" required />
                <label class="form-label">Mô tả</label>
                <textarea v-model="newProduct.moTa" class="form-control mb-2" required></textarea>
                <label class="form-label">Màu sắc</label>
                <input v-model="newProduct.mauSac" class="form-control mb-2" required />
              </div>
              <div class="col-md-6">
                <label class="form-label">Link hình ảnh</label>
                <input v-model="newProduct.hinhAnhSP" class="form-control mb-2" />
                <img v-if="newProduct.hinhAnhSP" :src="newProduct.hinhAnhSP" class="img-thumbnail mb-3" width="100" />
                <label class="form-label">Trạng thái</label>
                <select v-model="newProduct.trangThai" class="form-control">
                  <option :value="true">Có bán</option>
                  <option :value="false">Ngừng bán</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
              <button type="submit" class="btn btn-primary">{{ isEditing ? 'Cập nhật' : 'Lưu' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Bootstrap Toast -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1055">
  <div
    id="liveToast"
    class="toast align-items-center text-white bg-success border-0"
    role="alert"
    aria-live="assertive"
    aria-atomic="true"
  >
    <div class="d-flex">
      <div class="toast-body" id="toastMessage">Thành công!</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
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
      products: [],
      searchQuery: '',
      currentPage: 1,
      perPage: 20,
      isEditing: false,
      newProduct: {
        maSP: null,
        tenSP: '',
        hang: '',
        mauSac: '',
        moTa: '',
        soLuong: 0,
        giaBan: '',
        hinhAnhSP: '',
        trangThai: true
      }
    };
  },
  computed: {
    filteredProducts() {
      return this.searchQuery
        ? this.products.filter(p =>
            p.tenSP.toLowerCase().includes(this.searchQuery.toLowerCase()))
        : this.products;
    },
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.perPage;
      return this.filteredProducts.slice(start, start + this.perPage);
    },
    totalPages() {
      return Math.ceil(this.filteredProducts.length / this.perPage);
    },
    lowStockCount() {
      return this.products.filter(p => p.soLuong <= 5).length;
    },
    stoppedCount() {
      return this.products.filter(p => !p.trangThai).length;
    }
  },
  methods: {
    fetchProducts() {
      fetch('http://localhost:8080/smartphone/sanpham')
        .then(res => res.json())
        .then(data => this.products = data)
        .catch(err => console.error('Lỗi API:', err));
    },
    openAddModal() {
      this.resetForm();
      bootstrap.Modal.getOrCreateInstance(document.getElementById('addProductModal')).show();
    },
    openVariantModal(product) {
      this.$router.push(`/admin/bienthe/${product.maSP}`);
    },
    async handleSubmit() {
  try {
    const method = this.isEditing ? 'PUT' : 'POST';
    const url = this.isEditing
      ? `http://localhost:8080/smartphone/sanpham/id/${this.newProduct.maSP}`
      : 'http://localhost:8080/smartphone/sanpham';

    const productData = { ...this.newProduct };

    // ✅ Tự động cập nhật trạng thái nếu số lượng = 0
    if (productData.soLuong <= 0) {
      productData.trangThai = false;
    }

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productData)
    });

    if (!res.ok) throw new Error("Lỗi khi gửi dữ liệu");

    const savedProduct = await res.json();
    const index = this.products.findIndex(p => p.maSP === savedProduct.maSP);

    if (index !== -1) {
      this.products.splice(index, 1, savedProduct);
    } else {
      this.products.push(savedProduct);
    }

    bootstrap.Modal.getOrCreateInstance(document.getElementById('addProductModal')).hide();

    // ✅ Cảnh báo nếu sản phẩm bị ngừng bán do hết hàng
    if (productData.soLuong <= 0 && productData.trangThai === false) {
      this.showToast('⚠ Số lượng bằng 0 - Sản phẩm tự động ngừng bán', 'warning');
    } else {
      this.showToast(this.isEditing ? 'Cập nhật thành công!' : 'Thêm sản phẩm thành công!');
    }

    this.resetForm();
  } catch (err) {
    console.error(err);
    this.showToast('Đã xảy ra lỗi khi lưu sản phẩm!', 'danger');
  }
},

    handleDelete(id) {
      if (confirm('Xác nhận xóa?')) {
        fetch(`http://localhost:8080/smartphone/sanpham/id/${id}`, { method: 'DELETE' })
          .then(() => {
            this.fetchProducts();
            this.showToast('Xóa sản phẩm thành công!', 'danger');
          })
          .catch(err => {
            console.error(err);
            this.showToast('Lỗi khi xóa sản phẩm!', 'danger');
          });
      }
    },
    editProduct(product) {
      this.isEditing = true;
      this.newProduct = { ...product };
      bootstrap.Modal.getOrCreateInstance(document.getElementById('addProductModal')).show();
    },
    toggleStatus(product) {
      const updated = { ...product, trangThai: !product.trangThai };
      fetch(`http://localhost:8080/smartphone/sanpham/id/${product.maSP}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updated)
      })
        .then(res => res.json())
        .then(data => {
          const index = this.products.findIndex(p => p.maSP === data.maSP);
          if (index !== -1) this.products.splice(index, 1, data);
          this.showToast('Đã cập nhật trạng thái sản phẩm!');
        })
        .catch(err => console.error("Lỗi cập nhật:", err));
    },
    resetForm() {
      this.isEditing = false;
      this.newProduct = {
        maSP: null,
        tenSP: '',
        hang: '',
        mauSac: '',
        moTa: '',
        soLuong: 0,
        giaBan: '',
        hinhAnhSP: '',
        trangThai: true
      };
    },
    formatVND(value) {
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    },
    showToast(message, color = 'success') {
      const toastEl = document.getElementById('liveToast');
      const toastBody = document.getElementById('toastMessage');
      toastEl.className = `toast align-items-center text-white bg-${color} border-0`;
      toastBody.innerText = message;

      const toast = bootstrap.Toast.getOrCreateInstance(toastEl);
      toast.show();
    },
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    }
  },
  mounted() {
    this.fetchProducts();
  }
};
</script>

<style scoped>
.table img {
  object-fit: cover;
  height: 50px;
  border-radius: 4px;
}
.modal-body img {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 3px;
}
</style>
