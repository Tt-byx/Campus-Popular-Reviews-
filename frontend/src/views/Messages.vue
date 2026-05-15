<template>
  <AppLayout>
    <h1 class="page-title">私信</h1>
    <div class="conversation-list">
      <div v-for="conv in conversations" :key="conv.userId" class="conv-item card" @click="openChat(conv)">
        <UserAvatar :size="44" :src="conv.avatar" :username="conv.username" :user-id="conv.userId" />
        <div class="conv-info">
          <div class="conv-name">{{ conv.username }}</div>
          <div class="conv-msg">{{ conv.lastMessage }}</div>
        </div>
        <div class="conv-meta">
          <span class="conv-time">{{ relativeTime(conv.lastTime) }}</span>
          <el-badge v-if="conv.unread > 0" :value="conv.unread" />
        </div>
      </div>
      <div v-if="conversations.length === 0" class="empty-state"><p>暂无私信</p></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import UserAvatar from '@/components/common/UserAvatar.vue'
import { getConversations } from '@/api/message'
import { getUserById } from '@/api/user'
import { relativeTime } from '@/utils/time'
import { useChatStore } from '@/stores/chat'
import { useNotificationStore } from '@/stores/notification'

const route = useRoute()
const chatStore = useChatStore()
const notiStore = useNotificationStore()
const conversations = ref([])

async function loadConversations() {
  const res = await getConversations()
  if (res.code === 200) conversations.value = res.data || []
}

function openChat(conv) {
  chatStore.openChat(conv.userId, conv.username, conv.avatar)
}

onMounted(async () => {
  await loadConversations()
  notiStore.fetchCounts()
  if (route.query.user) {
    const conv = conversations.value.find(c => String(c.userId) === route.query.user)
    if (conv) {
      chatStore.openChat(conv.userId, conv.username, conv.avatar)
    } else {
      // 没有历史对话，获取用户信息后直接打开聊天
      const userRes = await getUserById(route.query.user)
      if (userRes.code === 200 && userRes.data) {
        chatStore.openChat(userRes.data.id, userRes.data.username, userRes.data.avatar)
      }
    }
  }
})
</script>

<style scoped lang="scss">
.page-title { font-size: 24px; margin-bottom: 16px; }
.conv-item { display: flex; align-items: center; gap: 12px; padding: 14px; cursor: pointer; margin-bottom: 6px; }
.conv-info { flex: 1; overflow: hidden; }
.conv-name { font-weight: 600; font-size: 14px; }
.conv-msg { font-size: 13px; color: var(--text-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.conv-meta { text-align: right; }
.conv-time { font-size: 11px; color: var(--text-muted); }
</style>
