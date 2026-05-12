<template>
  <div v-if="visible" class="chat-dialog" :style="dialogStyle">
    <div class="chat-dialog-header" @mousedown="onDragStart">
      <div class="chat-dialog-user">
        <el-avatar :size="32" :src="avatar">{{ username?.charAt(0) }}</el-avatar>
        <span class="chat-dialog-name">{{ username }}</span>
      </div>
      <el-button text @click="$emit('close')"><el-icon><Close /></el-icon></el-button>
    </div>
    <div class="chat-dialog-messages" ref="chatBox">
      <div v-if="messages.length === 0" class="chat-empty">暂无消息，发送第一条吧</div>
      <div v-for="msg in messages" :key="msg.id" class="chat-bubble" :class="{ mine: msg.isMine }">
        <div class="bubble-content">{{ msg.content }}</div>
        <div class="bubble-time">{{ relativeTime(msg.createdAt) }}</div>
      </div>
    </div>
    <div class="chat-dialog-input">
      <el-input v-model="newMessage" placeholder="输入消息..." @keyup.enter="sendMsg" />
      <el-button type="primary" @click="sendMsg" :disabled="!newMessage.trim()">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { Close } from '@element-plus/icons-vue'
import { getConversation, sendMessage, markAsRead } from '@/api/message'
import { useNotificationStore } from '@/stores/notification'
import { relativeTime } from '@/utils/time'

const props = defineProps({
  visible: { type: Boolean, default: false },
  userId: { type: [Number, String], default: null },
  username: { type: String, default: '' },
  avatar: { type: String, default: '' }
})

const emit = defineEmits(['close'])
const notiStore = useNotificationStore()
const messages = ref([])
const newMessage = ref('')
const chatBox = ref(null)

// 拖动状态
const posX = ref(null)
const posY = ref(null)
const dragging = ref(false)
let dragStartX = 0
let dragStartY = 0
let dialogStartX = 0
let dialogStartY = 0

const dialogStyle = computed(() => {
  if (posX.value !== null && posY.value !== null) {
    return { left: posX.value + 'px', top: posY.value + 'px', right: 'auto', bottom: 'auto' }
  }
  return {}
})

function onDragStart(e) {
  if (e.target.closest('.el-button')) return
  dragging.value = true
  dragStartX = e.clientX
  dragStartY = e.clientY
  const el = e.currentTarget.closest('.chat-dialog')
  const rect = el.getBoundingClientRect()
  dialogStartX = rect.left
  dialogStartY = rect.top
  document.addEventListener('mousemove', onDragMove)
  document.addEventListener('mouseup', onDragEnd)
  e.preventDefault()
}

function onDragMove(e) {
  if (!dragging.value) return
  posX.value = dialogStartX + (e.clientX - dragStartX)
  posY.value = dialogStartY + (e.clientY - dragStartY)
}

function onDragEnd() {
  dragging.value = false
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
}

async function loadMessages() {
  if (!props.userId) return
  const res = await getConversation(props.userId)
  if (res.code === 200) {
    messages.value = res.data?.data || []
    await markAsRead(props.userId)
    notiStore.fetchCounts()
    await nextTick()
    scrollToBottom()
  }
}

async function sendMsg() {
  if (!newMessage.value.trim() || !props.userId) return
  const content = newMessage.value.trim()
  newMessage.value = ''
  await sendMessage(props.userId, content)
  await loadMessages()
}

function scrollToBottom() {
  if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
}

watch(() => props.visible, (val) => {
  if (val && props.userId) {
    posX.value = null
    posY.value = null
    loadMessages()
  }
})

watch(() => props.userId, () => {
  if (props.visible && props.userId) {
    posX.value = null
    posY.value = null
    loadMessages()
  }
})

onMounted(() => {
  if (props.visible && props.userId) loadMessages()
})

onUnmounted(() => {
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
})
</script>

<style scoped lang="scss">
.chat-dialog {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 380px;
  height: 500px;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  display: flex;
  flex-direction: column;
  z-index: 2000;
  overflow: hidden;
}
.chat-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border);
  background: var(--bg-card);
  cursor: move;
  user-select: none;
}
.chat-dialog-user {
  display: flex;
  align-items: center;
  gap: 10px;
  pointer-events: none;
}
.chat-dialog-name {
  font-weight: 600;
  font-size: 15px;
}
.chat-dialog-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.chat-empty {
  text-align: center;
  color: var(--text-muted);
  font-size: 13px;
  margin-top: 40px;
}
.chat-bubble {
  max-width: 75%;
  align-self: flex-start;
  &.mine {
    align-self: flex-end;
    .bubble-content {
      background: var(--primary);
      color: white;
    }
  }
}
.bubble-content {
  background: var(--bg);
  padding: 8px 14px;
  border-radius: 16px;
  font-size: 14px;
  word-break: break-word;
}
.bubble-time {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}
.chat-dialog-input {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
}
@media (max-width: 768px) {
  .chat-dialog {
    bottom: 0;
    right: 0;
    left: 0;
    width: 100%;
    height: 70vh;
    border-radius: 12px 12px 0 0;
  }
}
</style>
