<template>
    <div>
      <h3 class="mb-3">🛒 Lịch sử mua hàng của khách #{{ id }}</h3>
      <table class="table table-bordered table-hover">
        <thead class="table-light">
          <tr>
            <th>Mã hóa đơn</th>
            <th>Ngày mua</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.maDonHang">
  <td>{{ order.maDonHang }}</td>
  <td>{{ formatDate(order.ngayDat) }}</td>
  <td>{{ formatVND(order.tongTien) }}</td>
  <td>
    <span :class="order.trangThai === 'Đã giao' ? 'text-success' : 'text-warning'">
      {{ order.trangThai }}
    </span>
  </td>
</tr>

        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  export default {
    props: ['id'],
    data() {
      return {
        orders: []
      };
    },
    methods: {
      fetchLichSu() {
        fetch(`http://localhost:8080/smartphone/donhang/user/${this.id}`)
          .then(res => res.json())
          .then(data => (this.orders = data))
          .catch(err => console.error('Lỗi API:', err));
      },
      formatDate(date) {
        return new Date(date).toLocaleDateString('vi-VN');
      },
      formatVND(value) {
        return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
        }).format(value);
      }
    },
    mounted() {
      this.fetchLichSu();
    }
  };
  </script>
  