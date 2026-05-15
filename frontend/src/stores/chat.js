import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const open = ref(false)
  const targetUser = ref(null) // { userId, username, avatar }

  function openChat(userId, username, avatar) {
    targetUser.value = { userId, username, avatar }
    open.value = true
  }

  function closeChat() {
    open.value = false
  }

  return { open, targetUser, openChat, closeChat }
})
