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

// 🔥 THÊM MỚI
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

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/dangnhap/:id',
    name: 'Login',
    component: Login,
    props: true
  },
  {
    path: '/register', // 🔥 THÊM MỚI
    name: 'Register',
    component: Register
  },
  {
    path: '/thongtincanhan/:id',
    name: 'ThongTinCaNhan',
    component: ThongTinCaNhan,
    props: true
  },
  
  {
    path: '/giohang',
    name: 'Cart',
    component: Cart
  },
  {
    path: '/sanpham/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    props: true
  },
  {
    path: '/dathang/:id',
    name: 'DatHang',
    component: DatHang,
    props: true
  },  
  {
    path: '/hoadon/:id',
    name: 'HoaDon',
    component: HoaDon,
    props: true
  },  
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: '',
        name: 'AdminProduct',
        component: AdminProduct
      },
      {
        path: 'users',
        name: 'UserAdmin',
        component: UserAdmin
      },
      {
        path: 'khachhang', // 🔥 THÊM
        name: 'KhachHangAdmin',
        component: KhachHangAdmin
      },
      {
        path: 'nhanvien', // 🔥 THÊM
        name: 'StaffAdmin',
        component: StaffAdmin
      },
      {
        path: 'lichsu/:id', // 🔥 THÊM
        name: 'LichSuMuaHang',
        component: LichSuMuaHang,
        props: true
      },
      {
        path: 'bienthe/:maSP',
        name: 'BienThe',
        component: BienTheSanPham,
        props: true
      },
      {
        path: 'donhang',
        name: 'DonHangAdmin',
        component: DonHangAdmin
      },
      {
        path: 'donhang/:id',
        name: 'DonHangChiTiet',
        component: DonHangChiTiet,
        props: true
      },
      {
        path: 'giohang',
        name: 'GioHangAdmin',
        component: GioHangAdmin,
        props: true
      },
      {
        path: 'giohang/:maGioHang',
        name: 'ChiTietGioHang',
        component: (ChiTietGioHang) ,
        props: true
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
