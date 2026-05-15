<template>
  <router-view v-slot="{ Component, route }">
    <transition name="page" mode="out-in">
      <keep-alive :max="10">
        <component :is="Component" :key="route.path" />
      </keep-alive>
    </transition>
  </router-view>
  <ChatDialog
    :visible="chatStore.open"
    :user-id="chatStore.targetUser?.userId"
    :username="chatStore.targetUser?.username"
    :avatar="chatStore.targetUser?.avatar"
    @close="chatStore.closeChat()"
  />
</template>

<script setup>
import ChatDialog from '@/components/social/ChatDialog.vue'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()
</script>

<style>
.page-enter-active {
  transition: all 0.25s ease-out;
}
.page-leave-active {
  transition: all 0.15s ease-in;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.page-leave-to {
  opacity: 0;
}
</style>
