import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount as getNotificationUnread } from '@/api/notification'
import { getUnreadCount as getMessageUnread } from '@/api/message'

export const useNotificationStore = defineStore('notification', () => {
  const notificationCount = ref(0)
  const messageCount = ref(0)

  let pollTimer = null
  let isPolling = false

  async function fetchCounts() {
    try {
      const [notiRes, msgRes] = await Promise.all([
        getNotificationUnread(),
        getMessageUnread()
      ])
      notificationCount.value = notiRes.data?.count || 0
      messageCount.value = msgRes.data?.count || 0
    } catch (e) {
      // 静默失败
    }
  }

  // 页面可见性变化处理
  function handleVisibilityChange() {
    if (document.hidden) {
      // 页面隐藏时暂停轮询
      stopPolling()
    } else {
      // 页面可见时恢复轮询
      startPolling()
    }
  }

  // 开始轮询（全局单例，多次调用只创建一个定时器）
  function startPolling() {
    if (isPolling) return

    isPolling = true
    fetchCounts() // 立即获取一次
    pollTimer = setInterval(fetchCounts, 30000)

    // 监听页面可见性变化
    document.addEventListener('visibilitychange', handleVisibilityChange)
  }

  // 停止轮询
  function stopPolling() {
    if (pollTimer) {
      clearInterval(pollTimer)
      pollTimer = null
    }
    isPolling = false
    document.removeEventListener('visibilitychange', handleVisibilityChange)
  }

  return {
    notificationCount,
    messageCount,
    fetchCounts,
    startPolling,
    stopPolling
  }
})
