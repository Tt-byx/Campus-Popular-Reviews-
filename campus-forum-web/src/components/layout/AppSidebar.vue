<template>
  <aside class="sidebar" :class="{ collapsed }">
    <div class="logo" @click="$router.push('/')">
      <span class="logo-icon">校</span>
      <span v-if="!collapsed" class="logo-text">校圈</span>
    </div>

    <div v-if="userStore.isLoggedIn" class="user-card">
      <UserAvatar :size="collapsed ? 36 : 48" :src="userStore.avatar" :username="userStore.username" :user-id="userStore.userId" class-name="user-avatar" />
      <div v-if="!collapsed" class="user-info">
        <div class="username">{{ userStore.username }}</div>
        <div class="signature">{{ userStore.signature || '这个人很懒' }}</div>
      </div>
    </div>
    <div v-else class="login-prompt">
      <el-button type="primary" @click="$router.push('/login')">登录</el-button>
      <el-button v-if="!collapsed" @click="$router.push('/register')">注册</el-button>
    </div>

    <nav class="nav-menu">
      <div v-for="item in menuItems" :key="item.path" class="nav-item"
           :class="{ active: isActive(item.path) }"
           @click="$router.push(item.path)">
        <el-icon :size="20"><component :is="item.icon" /></el-icon>
        <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
        <span v-if="!collapsed && item.badge" class="nav-badge">{{ item.badge }}</span>
      </div>
    </nav>

    <div class="sidebar-footer">
      <div class="nav-item" @click="collapsed = !collapsed">
        <el-icon :size="20"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
        <span v-if="!collapsed" class="nav-label">收起</span>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import UserAvatar from '@/components/common/UserAvatar.vue'

const props = defineProps({ collapsed: Boolean })
const emit = defineEmits(['update:collapsed'])
const collapsed = computed({
  get: () => props.collapsed,
  set: (v) => emit('update:collapsed', v)
})

const route = useRoute()
const userStore = useUserStore()
const notiStore = useNotificationStore()

const menuItems = computed(() => {
  const items = [
    { path: '/', icon: 'HomeFilled', label: '首页' },
    { path: '/categories', icon: 'Grid', label: '分类' },
    { path: '/ranking', icon: 'Trophy', label: '排行' },
    { path: '/favorites', icon: 'Star', label: '收藏', auth: true },
    { path: '/messages', icon: 'ChatDotRound', label: '私信', auth: true, badge: notiStore.messageCount || 0 },
    { path: '/notifications', icon: 'Bell', label: '通知', auth: true, badge: notiStore.notificationCount || 0 },
    { path: '/settings', icon: 'Setting', label: '设置', auth: true },
    { path: '/admin', icon: 'Tools', label: '管理', admin: true },
  ]
  return items.filter(i => {
    if (i.auth && !userStore.isLoggedIn) return false
    if (i.admin && !userStore.isAdmin) return false
    return true
  })
})

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}
</script>

<style scoped lang="scss">
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--bg-card);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition: width 0.3s ease;
  &.collapsed {
    width: 64px;
  }
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  cursor: pointer;
}
.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
  flex-shrink: 0;
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--text);
}
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin: 0 8px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: var(--bg); }
}
.user-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  font-weight: 600;
}
.user-info {
  overflow: hidden;
}
.username {
  font-weight: 600;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.signature {
  font-size: 12px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.login-prompt {
  padding: 12px 16px;
  display: flex;
  gap: 8px;
}
.nav-menu {
  flex: 1;
  padding: 8px;
  overflow-y: auto;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-secondary);
  position: relative;
  &:hover {
    background: var(--bg);
    color: var(--text);
  }
  &.active {
    background: rgba(37, 99, 235, 0.08);
    color: var(--primary);
    font-weight: 500;
  }
}
.nav-label {
  font-size: 14px;
  white-space: nowrap;
}
.nav-badge {
  margin-left: auto;
  background: #ef4444;
  color: white;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}
.sidebar-footer {
  padding: 8px;
  border-top: 1px solid var(--border);
}
@media (max-width: 768px) {
  .sidebar { display: none; }
}
</style>
