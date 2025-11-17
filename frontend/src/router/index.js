import {createRouter, createWebHistory} from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import CustomerList from '../views/CustomerList.vue'
import ProductList from '../views/ProductList.vue'
import PackageList from '../views/PackageList.vue'
import QuotationList from '../views/QuotationList.vue'
import QuotationForm from '../views/QuotationForm.vue'
import UserList from '../views/UserList.vue'
import QuotationPreview from "../views/QuotationPreview.vue";

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: Dashboard
            },
            {
                path: 'customers',
                name: 'CustomerList',
                component: CustomerList
            },
            {
                path: 'products',
                name: 'ProductList',
                component: ProductList
            },
            {
                path: 'packages',
                name: 'PackageList',
                component: PackageList
            },
            {
                path: 'quotations',
                name: 'QuotationList',
                component: QuotationList
            },
            {
                path: 'quotations/new',
                name: 'QuotationNew',
                component: QuotationForm
            },
            {
                path: 'quotations/:id',
                name: 'QuotationEdit',
                component: QuotationForm
            },
            {
                path: 'quotations/:id/preview',
                name: 'QuotationPreview',
                component: QuotationPreview
            },
            {
                path: 'users',
                name: 'UserList',
                component: UserList
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.path === '/login') {
        next()
    } else if (!token) {
        next('/login')
    } else {
        next()
    }
})

export default router

