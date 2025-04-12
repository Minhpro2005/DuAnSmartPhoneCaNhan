<template>
  <div class="container py-4">
    <h2 class="mb-4">Danh sách đơn hàng</h2>

    <!-- Bộ lọc -->
    <div class="row mb-3">
      <div class="col-md-4 mb-2">
        <input v-model="searchMaDH" class="form-control" placeholder="Tìm theo mã đơn hàng..." />
      </div>
      <div class="col-md-4 mb-2">
        <input type="date" v-model="fromDate" class="form-control" />
      </div>
      <div class="col-md-4 mb-2">
        <select v-model="filterTrangThai" class="form-select">
          <option value="">-- Trạng thái --</option>
          <option value="Chờ xử lý">Chờ xử lý</option>
          <option value="Đã xác nhận">Đã xác nhận</option>
          <option value="Đang giao">Đang giao</option>
          <option value="Đã giao">Đã giao</option>
          <option value="Hủy">Hủy</option>
        </select>
      </div>
    </div>

    <!-- Bảng dữ liệu -->
    <table class="table table-bordered table-hover">
      <thead class="table-light">
        <tr>
          <th>Mã ĐH</th>
          <th>Mã KH</th>
          <th>SĐT</th>
          <th>Địa chỉ</th>
          <th>Ngày tạo</th>
          <th>Tổng tiền</th>
          <th>Phương thức</th>
          <th>Trạng thái</th>
          <th>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="dh in paginatedDonHangs" :key="dh.maDonHang">
          <td>{{ dh.maDonHang }}</td>
          <td>{{ dh.khachHang?.maKH }}</td>
          <td>{{ dh.sdtGiaoHang }}</td>
          <td>{{ dh.diaChiGiaoHang }}</td>
          <td>{{ formatDate(dh.ngayDat) }}</td>
          <td>{{ formatCurrency(dh.tongTien) }}</td>
          <td>{{ dh.phuongThucThanhToan }}</td>
          <td>
            <select v-model="dh.trangThai" @change="updateTrangThai(dh)" class="form-select">
              <option value="Chờ xử lý">Chờ xử lý</option>
              <option value="Đã xác nhận">Đã xác nhận</option>
              <option value="Đang giao">Đang giao</option>
              <option value="Đã giao">Đã giao</option>
              <option value="Hủy">Hủy</option>
            </select>
          </td>
          <td class="d-flex gap-1">
            <router-link :to="`/admin/donhang/${dh.maDonHang}`" class="btn btn-sm btn-info">Chi tiết</router-link>
            <button
              class="btn btn-sm btn-success"
              v-if="dh.trangThai === 'Chờ xử lý'"
              @click="xacNhanDonHang(dh.maDonHang)">
              Xác nhận
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Phân trang -->
    <nav v-if="totalPages > 1">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button class="page-link" @click="goToPage(currentPage - 1)">Trước</button>
        </li>
        <li class="page-item" v-for="n in totalPages" :key="n" :class="{ active: currentPage === n }">
          <button class="page-link" @click="goToPage(n)">{{ n }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <button class="page-link" @click="goToPage(currentPage + 1)">Sau</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
export default {
  name: 'DonHangAdmin',
  data() {
    return {
      donHangs: [],
      searchMaDH: '',
      fromDate: '',
      filterTrangThai: '',
      currentPage: 1,
      pageSize: 20
    };
  },
  computed: {
    filteredDonHangs() {
      return this.donHangs.filter(dh => {
        const matchMa = !this.searchMaDH || String(dh.maDonHang).includes(this.searchMaDH);
        const matchDate = !this.fromDate || new Date(dh.ngayDat) >= new Date(this.fromDate);
        const matchTrangThai = !this.filterTrangThai || dh.trangThai === this.filterTrangThai;
        return matchMa && matchDate && matchTrangThai;
      });
    },
    paginatedDonHangs() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredDonHangs.slice(start, start + this.pageSize);
    },
    totalPages() {
      return Math.ceil(this.filteredDonHangs.length / this.pageSize);
    }
  },
  methods: {
    fetchDonHangs() {
      fetch('http://localhost:8080/smartphone/donhang')
        .then(res => res.json())
        .then(data => (this.donHangs = data));
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleDateString('vi-VN') : 'N/A';
    },
    formatCurrency(amount) {
      return amount?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    },
    updateTrangThai(donHang) {
      fetch(`http://localhost:8080/smartphone/donhang/id/${donHang.maDonHang}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(donHang)
      })
        .then(() => alert('✅ Cập nhật trạng thái thành công!'))
        .catch(() => alert('❌ Lỗi khi cập nhật trạng thái!'));
    },
    xacNhanDonHang(maDonHang) {
      fetch(`http://localhost:8080/smartphone/donhang/xacnhan/${maDonHang}`, {
        method: 'PUT'
      })
        .then(async res => {
          const msg = await res.text();
          if (!res.ok) throw new Error(msg);
          alert('✅ ' + msg);
          this.fetchDonHangs();
        })
        .catch(err => {
          alert('❌ Lỗi xác nhận đơn hàng:\n' + err.message);
        });
    },
    goToPage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    }
  },
  mounted() {
    this.fetchDonHangs();
  }
};
</script>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>
