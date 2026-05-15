<template>
  <AppLayout>
    <div class="notifications-page">
      <div class="page-header">
        <h1>通知中心</h1>
        <el-button size="small" @click="markAll">全部已读</el-button>
      </div>
      <div v-for="n in notifications" :key="n.id" class="noti-item card" :class="{ unread: !n.isRead }" @click="handleClick(n)">
        <UserAvatar :size="36" :src="n.fromAvatar" :username="n.fromUsername" :user-id="n.fromUserId" />
        <div class="noti-content">
          <span class="noti-user">{{ n.fromUsername || '系统' }}</span>
          <span class="noti-text">{{ n.content }}</span>
        </div>
        <span class="noti-time">{{ relativeTime(n.createdAt) }}</span>
      </div>
      <div v-if="notifications.length === 0" class="empty-state"><p>暂无通知</p></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import { getNotifications, markNotificationRead, markAllRead } from '@/api/notification'
import UserAvatar from '@/components/common/UserAvatar.vue'
import { relativeTime } from '@/utils/time'
import { useNotificationStore } from '@/stores/notification'

const router = useRouter()
const notiStore = useNotificationStore()
const notifications = ref([])

async function loadData() {
  const res = await getNotifications()
  if (res.code === 200) notifications.value = res.data?.data || []
}

async function markAll() {
  await markAllRead()
  loadData()
}

async function handleClick(n) {
  if (!n.isRead) await markNotificationRead(n.id)
  if (n.targetType === 'post' && n.targetId) router.push(`/post/${n.targetId}`)
  else if (n.targetType === 'review_target' && n.targetId) router.push(`/review-target/${n.targetId}`)
  else if (n.type === 'follow' && n.fromUserId) router.push(`/user/${n.fromUserId}`)
}

onMounted(() => {
  loadData()
  notiStore.fetchCounts()
})
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; h1 { font-size: 24px; } }
.noti-item { display: flex; align-items: center; gap: 12px; padding: 14px; cursor: pointer; margin-bottom: 6px; }
.noti-item.unread { background: rgba(37,99,235,0.03); border-left: 3px solid var(--primary); }
.noti-content { flex: 1; font-size: 14px; }
.noti-user { font-weight: 600; margin-right: 6px; }
.noti-time { font-size: 12px; color: var(--text-muted); white-space: nowrap; }
</style>
