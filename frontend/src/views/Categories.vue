<template>
  <AppLayout>
    <h1 class="page-title">分类浏览</h1>
    <div class="tag-list">
      <el-button v-for="t in tags" :key="t" :type="activeTag === t ? 'primary' : 'default'" @click="selectTag(t)">{{ t }}</el-button>
    </div>
    <div class="post-list">
      <PostCard v-for="p in posts" :key="p.id" :post="p" />
      <div v-if="posts.length === 0" class="empty-state"><p>该分类暂无帖子</p></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import PostCard from '@/components/post/PostCard.vue'
import { getPostsByTag } from '@/api/post'

const route = useRoute()
const tags = ['食堂', '课程', '社团', '活动', '其他']
const activeTag = ref(route.params.tag || '食堂')
const posts = ref([])

async function selectTag(tag) {
  activeTag.value = tag
  const res = await getPostsByTag(tag)
  if (res.code === 200) posts.value = res.data || []
}

onMounted(() => selectTag(activeTag.value))
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 16px; }
.tag-list { display: flex; gap: 8px; margin-bottom: 20px; flex-wrap: wrap; }
.post-list { display: flex; flex-direction: column; gap: 12px; }
</style>
