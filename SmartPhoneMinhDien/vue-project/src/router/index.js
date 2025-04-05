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
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
