<template>
  
  <div class="container py-3">
    <h3>Chi tiết đơn hàng #{{ $route.params.id }}</h3>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>Sản phẩm</th>
          <th>Màu sắc</th>
          <th>Số lượng</th>
          <th>Giá bán</th>
          <th>Thành tiền</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="ct in chiTiet" :key="ct.id">
          <td>{{ ct.sanPham?.tenSP }}</td>
          <td>{{ ct.bienThe?.mauSac || ct.sanPham?.mauSac }}</td>
          <td>{{ ct.soLuong }}</td>
          <td>{{ ct.giaBan.toLocaleString('vi-VN') }}đ</td>
          <td>{{ (ct.giaBan * ct.soLuong).toLocaleString('vi-VN') }}đ</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      chiTiet: []
    };
  },
  mounted() {
    const id = this.$route.params.id;
    fetch(`http://localhost:8080/smartphone/donhangchitiet/donhang/${id}`)
      .then(res => res.json())
      .then(data => this.chiTiet = data);
  }
};
</script>
