<template>
  <AppLayout>
    <h1 class="page-title">我的收藏</h1>
    <PostCard v-for="item in favorites" :key="item.id" :post="item.post" />
    <div v-if="favorites.length === 0" class="empty-state"><div class="empty-icon">⭐</div><p>暂无收藏</p></div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import PostCard from '@/components/post/PostCard.vue'
import { getFavorites } from '@/api/favorite'

const favorites = ref([])

onMounted(async () => {
  const res = await getFavorites()
  if (res.code === 200) favorites.value = res.data?.data || []
})
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
</style>
