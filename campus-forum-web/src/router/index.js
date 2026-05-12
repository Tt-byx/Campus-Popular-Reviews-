import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
  { path: '/login', component: () => import('@/views/Login.vue'), meta: { guest: true, title: '登录' } },
  { path: '/register', component: () => import('@/views/Register.vue'), meta: { guest: true, title: '注册' } },
  { path: '/post/create', component: () => import('@/views/PostCreate.vue'), meta: { auth: true, title: '发帖' } },
  { path: '/post/:id', component: () => import('@/views/PostDetail.vue'), meta: { title: '帖子详情' } },
  { path: '/review-target/:id', component: () => import('@/views/ReviewTargetDetail.vue'), meta: { title: '点评详情' } },
  { path: '/user/:id', component: () => import('@/views/UserProfile.vue'), meta: { title: '用户主页' } },
  { path: '/categories', component: () => import('@/views/Categories.vue'), meta: { title: '分类' } },
  { path: '/category/:tag', component: () => import('@/views/Categories.vue'), meta: { title: '分类' } },
  { path: '/ranking', component: () => import('@/views/Ranking.vue'), meta: { title: '排行' } },
  { path: '/search', component: () => import('@/views/Search.vue'), meta: { title: '搜索' } },
  { path: '/messages', component: () => import('@/views/Messages.vue'), meta: { auth: true, title: '私信' } },
  { path: '/notifications', component: () => import('@/views/Notifications.vue'), meta: { auth: true, title: '通知' } },
  { path: '/favorites', component: () => import('@/views/Favorites.vue'), meta: { auth: true, title: '收藏' } },
  { path: '/settings', component: () => import('@/views/Settings.vue'), meta: { auth: true, title: '设置' } },
  { path: '/admin', component: () => import('@/views/admin/Dashboard.vue'), meta: { auth: true, admin: true, title: '管理后台' } },
  { path: '/admin/users', component: () => import('@/views/admin/Users.vue'), meta: { auth: true, admin: true, title: '用户管理' } },
  { path: '/admin/posts', component: () => import('@/views/admin/Posts.vue'), meta: { auth: true, admin: true, title: '帖子管理' } },
  { path: '/admin/comments', component: () => import('@/views/admin/Comments.vue'), meta: { auth: true, admin: true, title: '评论管理' } },
  { path: '/admin/banned-words', component: () => import('@/views/admin/BannedWords.vue'), meta: { auth: true, admin: true, title: '违禁词' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 校圈` : '校圈'
  const userStore = useUserStore()

  if (to.meta.auth && !userStore.token) {
    next('/login')
    return
  }
  if (to.meta.admin && !userStore.isAdmin) {
    next('/')
    return
  }
  // 允许已登录用户访问登录/注册页（方便切换账号）
  next()
})

export default router
