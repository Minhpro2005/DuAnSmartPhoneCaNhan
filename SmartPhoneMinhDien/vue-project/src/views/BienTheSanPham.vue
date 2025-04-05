<template>
    <div class="container py-4">
      <h3 class="mb-4">Quản lý biến thể cho sản phẩm ID: {{ maSP }}</h3>
  
      <div class="d-flex mb-3">
        <input type="text" v-model="newVariant.tenBienThe" placeholder="Tên biến thể" class="form-control me-2" />
        <input type="number" v-model="newVariant.soLuong" placeholder="Số lượng" class="form-control me-2" />
        <input type="number" v-model="newVariant.giaBan" placeholder="Giá bán" class="form-control me-2" />
        <input type="text" v-model="newVariant.mauSac" placeholder="Màu sắc" class="form-control me-2" />
        <input type="text" v-model="newVariant.hinhAnh" placeholder="Link ảnh" class="form-control me-2" />
        <button class="btn btn-primary" @click="addVariant">{{ editingId ? 'Cập nhật' : 'Thêm' }}</button>
        <button v-if="editingId" class="btn btn-secondary ms-2" @click="cancelEdit">Hủy</button>
      </div>
  
      <table class="table table-bordered">
        <thead class="table-light">
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Màu sắc</th>
            <th>Hình ảnh</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="bienThe in bienTheList" :key="bienThe.maBienThe">
            <td>{{ bienThe.maBienThe }}</td>
            <td>{{ bienThe.tenBienThe }}</td>
            <td>{{ formatVND(bienThe.giaBan) }}</td>
            <td>{{ bienThe.soLuong }}</td>
            <td>{{ bienThe.mauSac }}</td>
            <td><img :src="bienThe.hinhAnh || placeholder" width="60" /></td>
            <td>
              <button class="btn btn-sm" :class="bienThe.trangThai ? 'btn-success' : 'btn-secondary'" @click="toggleTrangThai(bienThe)">
                {{ bienThe.trangThai ? 'Còn hàng' : 'Hết hàng' }}
              </button>
            </td>
            <td>
              <button class="btn btn-sm btn-warning me-2" @click="editVariant(bienThe)">Sửa</button>
              <button class="btn btn-sm btn-danger" @click="deleteVariant(bienThe.maBienThe)">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  
  const route = useRoute();
  const maSP = route.params.maSP;
  const bienTheList = ref([]);
  const placeholder = 'https://placehold.co/100x100';
  const editingId = ref(null);
  const newVariant = ref({
    tenBienThe: '',
    soLuong: 1,
    giaBan: 0,
    mauSac: '',
    hinhAnh: '',
    trangThai: true
  });
  
  const fetchVariants = () => {
    fetch(`http://localhost:8080/smartphone/bienthe/sanpham/${maSP}`)
      .then(res => res.json())
      .then(data => bienTheList.value = data)
      .catch(err => console.error(err));
  };
  
  const addVariant = async () => {
    if (!newVariant.value.tenBienThe) return alert('Nhập tên biến thể');
  
    const variantToSend = {
      ...newVariant.value,
      sanPham: { maSP: parseInt(maSP) }
    };
  
    const url = editingId.value
      ? `http://localhost:8080/smartphone/bienthe/id/${editingId.value}`
      : 'http://localhost:8080/smartphone/bienthe';
  
    const method = editingId.value ? 'PUT' : 'POST';
  
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(variantToSend)
    });
  
    if (res.ok) {
      fetchVariants();
      resetForm();
    } else {
      alert('Lỗi khi lưu biến thể');
    }
  };
  
  const editVariant = (bienThe) => {
    editingId.value = bienThe.maBienThe;
    newVariant.value = {
      tenBienThe: bienThe.tenBienThe,
      soLuong: bienThe.soLuong,
      giaBan: bienThe.giaBan,
      mauSac: bienThe.mauSac,
      hinhAnh: bienThe.hinhAnh,
      trangThai: bienThe.trangThai
    };
  };
  
  const cancelEdit = () => {
    editingId.value = null;
    resetForm();
  };
  
  const resetForm = () => {
    newVariant.value = {
      tenBienThe: '',
      soLuong: 1,
      giaBan: 0,
      mauSac: '',
      hinhAnh: '',
      trangThai: true
    };
  };
  
  const toggleTrangThai = async (bienThe) => {
    const updated = { ...bienThe, trangThai: !bienThe.trangThai, sanPham: { maSP: parseInt(maSP) } };
    await fetch(`http://localhost:8080/smartphone/bienthe/id/${bienThe.maBienThe}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updated)
    });
    fetchVariants();
  };
  
  const deleteVariant = async (id) => {
    if (!confirm('Xóa biến thể này?')) return;
    await fetch(`http://localhost:8080/smartphone/bienthe/id/${id}`, { method: 'DELETE' });
    fetchVariants();
  };
  
  const formatVND = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  
  onMounted(fetchVariants);
  </script>
  
  <style scoped>
  img {
    object-fit: cover;
    height: 60px;
    border-radius: 4px;
  }
  </style>