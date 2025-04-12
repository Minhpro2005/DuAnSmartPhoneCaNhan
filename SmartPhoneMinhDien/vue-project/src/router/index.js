import { createRouter, createWebHistory } from 'vue-router'

// Pages
import Home from '@/views/Home.vue'
import AdminProduct from '@/views/AdminProduct.vue'
import UserAdmin from '@/views/UserAdmin.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Login from '@/views/Login.vue'
import Cart from '@/views/Cart.vue'
import BienTheSanPham from '@/views/BienTheSanPham.vue'
import AdminLayout from '@/views/AdminLayout.vue'

import KhachHangAdmin from '@/views/KhachHangAdmin.vue'
import LichSuMuaHang from '@/views/LichSuMuaHang.vue'
import Register from '@/views/Register.vue'
import ThongTinCaNhan from '@/views/ThongTinCaNhan.vue'
import StaffAdmin from '@/views/StaffAdmin.vue'
import DonHangAdmin from '@/views/DonHangAdmin.vue'
import DonHangChiTiet from '@/views/DonHangChiTiet.vue'
import DatHang from '@/views/DatHang.vue'
import HoaDon from '@/views/HoaDon.vue'
import GioHangAdmin from '@/views/GioHangAdmin.vue'
import ChiTietGioHang from '@/views/ChiTietGioHang.vue'
import HoaDonAdmin from '@/views/HoaDonAdmin.vue'
import HoaDonChiTiet from '@/views/HoaDonChiTiet.vue'
import ThongTinNhanVien from '@/views/ThongTinNhanVien.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/dangnhap/:id?', name: 'Login', component: Login, props: true },
  { path: '/register', name: 'Register', component: Register },
  { path: '/thongtincanhan/:id', name: 'ThongTinCaNhan', component: ThongTinCaNhan, props: true },
  { path: '/giohang', name: 'Cart', component: Cart },
  { path: '/sanpham/:id', name: 'ProductDetail', component: ProductDetail, props: true },
  { path: '/dathang/:id', name: 'DatHang', component: DatHang, props: true },
  { path: '/hoadon/:id', name: 'HoaDon', component: HoaDon, props: true },

  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', name: 'AdminProduct', component: AdminProduct },
      { path: 'users', name: 'UserAdmin', component: UserAdmin },
      { path: 'khachhang', name: 'KhachHangAdmin', component: KhachHangAdmin },
      { path: 'nhanvien', name: 'StaffAdmin', component: StaffAdmin },
      { path: 'lichsu/:id', name: 'LichSuMuaHang', component: LichSuMuaHang, props: true },
      { path: 'bienthe/:maSP', name: 'BienThe', component: BienTheSanPham, props: true },
      { path: 'donhang', name: 'DonHangAdmin', component: DonHangAdmin },
      { path: 'donhang/:id', name: 'DonHangChiTiet', component: DonHangChiTiet, props: true },
      { path: 'giohang', name: 'GioHangAdmin', component: GioHangAdmin, props: true },
      { path: 'giohang/:maGioHang', name: 'ChiTietGioHang', component: ChiTietGioHang, props: true },
      { path: 'hoadon', name: 'HoaDonAdmin', component: HoaDonAdmin },
      { path: 'hoadon/:id', name: 'HoaDonChiTiet', component: HoaDonChiTiet, props: true },
      { path: 'thongtinnhanvien', name: 'ThongTinNhanVien', component: ThongTinNhanVien }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ✅ Bảo vệ route admin
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'));

  // Chặn khách truy cập /admin nếu không phải admin/nhân viên
  if (to.path.startsWith('/admin')) {
    if (!user || user.vaiTro === 3) {
      alert('🚫 Bạn không có quyền truy cập trang quản trị');
      return next('/');
    }
  }

  // Nếu đã đăng nhập thì không cho vào lại /dangnhap
  if (to.name === 'Login' && user) {
    if (user.vaiTro === 1 || user.vaiTro === 2) return next('/admin');
    if (user.vaiTro === 3) return next('/');
  }

  next();
});

export default router;
