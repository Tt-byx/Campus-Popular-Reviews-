<template>
  <nav class="mobile-nav">
    <div v-for="item in items" :key="item.path" class="mobile-nav-item"
         :class="{ active: $route.path === item.path }" @click="$router.push(item.path)">
      <el-badge :value="item.badge" :hidden="!item.badge" :max="99">
        <el-icon :size="22"><component :is="item.icon" /></el-icon>
      </el-badge>
      <span>{{ item.label }}</span>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useNotificationStore } from '@/stores/notification'

const notiStore = useNotificationStore()

const items = computed(() => [
  { path: '/', icon: 'HomeFilled', label: '首页' },
  { path: '/categories', icon: 'Grid', label: '分类' },
  { path: '/post/create', icon: 'Plus', label: '发帖' },
  { path: '/messages', icon: 'ChatDotRound', label: '私信', badge: notiStore.messageCount || 0 },
  { path: '/settings', icon: 'User', label: '我的' },
])
</script>

<style scoped lang="scss">
.mobile-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: var(--bg-card);
  border-top: 1px solid var(--border);
  z-index: 100;
  justify-content: space-around;
  align-items: center;
}
.mobile-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  color: var(--text-muted);
  cursor: pointer;
  &.active {
    color: var(--primary);
  }
}
@media (max-width: 768px) {
  .mobile-nav { display: flex; }
}
</style>
