import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/', component: () => import('../views/Home.vue') },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/register', component: () => import('../views/Register.vue') },
  { path: '/password-reset', component: () => import('../views/PasswordReset.vue') },
  { path: '/profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } },
  { path: '/user/:id', component: () => import('../views/UserPage.vue') },
  { path: '/blog/:id', component: () => import('../views/BlogDetail.vue') },
  { path: '/blog/edit/:id?', component: () => import('../views/BlogEditor.vue'), meta: { requiresAuth: true } },
  { path: '/drafts', component: () => import('../views/Drafts.vue'), meta: { requiresAuth: true } },
  { path: '/circles', component: () => import('../views/Circles.vue') },
  { path: '/circle/:id', component: () => import('../views/CircleDetail.vue') },
  { path: '/messages', component: () => import('../views/Messages.vue'), meta: { requiresAuth: true } },
  { path: '/chat/:userId', component: () => import('../views/ChatWindow.vue'), meta: { requiresAuth: true } },
  { path: '/hot', component: () => import('../views/HotTrends.vue') },
  { path: '/search', component: () => import('../views/Search.vue') },
  { path: '/settings', component: () => import('../views/Settings.vue'), meta: { requiresAuth: true } },
  { path: '/ai', component: () => import('../views/AiTools.vue'), meta: { requiresAuth: true } },
  { path: '/admin', component: () => import('../views/admin/AdminLayout.vue'), meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', component: () => import('../views/admin/AdminDashboard.vue') },
      { path: 'users', component: () => import('../views/admin/AdminUsers.vue') },
      { path: 'content', component: () => import('../views/admin/AdminContent.vue') },
      { path: 'ai', component: () => import('../views/admin/AdminAi.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.user?.role !== 'super_admin') {
    next('/')
  } else {
    next()
  }
})

export default router
