<template>
    <div class="container py-4">
      <h2 class="mb-4">Danh sách đơn hàng</h2>
  
      <!-- Bộ lọc -->
      <div class="row mb-3">
        <div class="col-md-4 mb-2">
          <input 
            v-model="searchMaDH" 
            class="form-control" 
            placeholder="Tìm theo mã đơn hàng..." 
          />
        </div>
        <div class="col-md-4 mb-2">
          <input 
            type="date" 
            v-model="fromDate" 
            class="form-control" 
          />
        </div>
        <div class="col-md-4 mb-2">
          <select v-model="filterTrangThai" class="form-select">
            <option value="">-- Trạng thái --</option>
            <option value="Chờ xử lý">Chờ xử lý</option>
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
          <tr v-for="dh in filteredDonHangs" :key="dh.maDonHang">
            <td>{{ dh.maDonHang }}</td>
            <td>{{ dh.khachHang?.maKH }}</td>
            <td>{{ dh.sdtGiaoHang }}</td>
            <td>{{ dh.diaChiGiaoHang }}</td>
            <td>{{ formatDate(dh.ngayDat) }}</td>
            <td>{{ dh.tongTien?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) }}</td>
            <td>{{ dh.phuongThucThanhToan }}</td>
            <td>
              <select v-model="dh.trangThai" @change="updateTrangThai(dh)" class="form-select">
                <option value="Chờ xử lý">Chờ xử lý</option>
                <option value="Đang giao">Đang giao</option>
                <option value="Đã giao">Đã giao</option>
                <option value="Hủy">Hủy</option>
              </select>
            </td>
            <td>
              <router-link :to="`/admin/donhang/${dh.maDonHang}`" class="btn btn-sm btn-info">
                Chi tiết
              </router-link>
            </td>
          </tr>
        </tbody>
      </table>
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
        filterTrangThai: ''
      };
    },
    computed: {
      filteredDonHangs() {
        return this.donHangs.filter(dh => {
          const maDHMatch = !this.searchMaDH || String(dh.maDonHang).includes(this.searchMaDH);
  
          const ngay = new Date(dh.ngayDat);
          const from = this.fromDate ? new Date(this.fromDate) : null;
          const ngayMatch = !from || ngay >= from;
  
          const trangThaiMatch = !this.filterTrangThai || dh.trangThai === this.filterTrangThai;
  
          return maDHMatch && ngayMatch && trangThaiMatch;
        });
      }
    },
    methods: {
      fetchDonHangs() {
        fetch('http://localhost:8080/smartphone/donhang')
          .then(res => res.json())
          .then(data => {
            this.donHangs = data;
          });
      },
      formatDate(date) {
        return date ? new Date(date).toLocaleDateString("vi-VN") : 'N/A';
      },
      updateTrangThai(donHang) {
        fetch(`http://localhost:8080/smartphone/donhang/id/${donHang.maDonHang}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(donHang)
        })
          .then(() => alert("Cập nhật trạng thái thành công"))
          .catch(() => alert("Có lỗi khi cập nhật trạng thái"));
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
  