<template>
  <div class="app-layout">
    <AppSidebar v-model:collapsed="sidebarCollapsed" />
    <div class="main-area" :class="{ collapsed: sidebarCollapsed }">
      <AppHeader @toggle-sidebar="sidebarCollapsed = !sidebarCollapsed" />
      <main class="content">
        <slot />
      </main>
    </div>
    <!-- 移动端底部导航 -->
    <MobileNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppSidebar from './AppSidebar.vue'
import AppHeader from './AppHeader.vue'
import MobileNav from './MobileNav.vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import { getProfile } from '@/api/user'

const sidebarCollapsed = ref(false)
const userStore = useUserStore()
const notiStore = useNotificationStore()

onMounted(async () => {
  if (userStore.token) {
    try {
      const res = await getProfile()
      if (res.code === 200 && res.data) {
        userStore.setProfile(res.data)
      }
    } catch (e) {}
    // 启动全局轮询（多次调用只创建一个定时器）
    notiStore.startPolling()
  }
})
</script>

<style scoped lang="scss">
.app-layout {
  display: flex;
  min-height: 100vh;
}
.main-area {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s ease;
  &.collapsed {
    margin-left: 64px;
  }
}
.content {
  flex: 1;
  padding: 24px;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
}
@media (max-width: 768px) {
  .main-area {
    margin-left: 0 !important;
  }
  .content {
    padding: 12px;
    padding-bottom: 70px;
  }
}
</style>
