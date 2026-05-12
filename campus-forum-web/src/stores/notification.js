import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount as getNotificationUnread } from '@/api/notification'
import { getUnreadCount as getMessageUnread } from '@/api/message'

export const useNotificationStore = defineStore('notification', () => {
  const notificationCount = ref(0)
  const messageCount = ref(0)

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

  return { notificationCount, messageCount, fetchCounts }
})
