<template>
  <AppLayout>
    <h1 class="page-title">搜索</h1>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索帖子和点评..." size="large" clearable @keyup.enter="doSearch">
        <template #append><el-button @click="doSearch">搜索</el-button></template>
      </el-input>
    </div>
    <div v-if="searched" class="search-results">
      <p class="result-count">找到 {{ totalResults }} 个结果</p>
      <h3 v-if="posts.length > 0">帖子 ({{ posts.length }})</h3>
      <PostCard v-for="p in posts" :key="p.id" :post="p" />
      <h3 v-if="reviewTargets.length > 0" style="margin-top: 20px">点评目标 ({{ reviewTargets.length }})</h3>
      <div v-for="t in reviewTargets" :key="t.id" class="target-item card" @click="$router.push(`/review-target/${t.id}`)">
        <strong>{{ t.targetName }}</strong>
      </div>
      <div v-if="totalResults === 0" class="empty-state"><p>未找到相关结果</p></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import PostCard from '@/components/post/PostCard.vue'
import { search } from '@/api/post'

const route = useRoute()
const keyword = ref(route.query.keyword || '')
const searched = ref(false)
const posts = ref([])
const reviewTargets = ref([])
const totalResults = ref(0)

async function doSearch() {
  if (!keyword.value.trim()) return
  const res = await search(keyword.value.trim())
  if (res.code === 200) {
    posts.value = res.data?.posts || []
    reviewTargets.value = res.data?.reviewTargets || []
    totalResults.value = res.data?.totalResults || 0
    searched.value = true
  }
}

onMounted(() => { if (keyword.value) doSearch() })
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 16px; }
.search-bar { max-width: 600px; margin-bottom: 24px; }
.result-count { color: var(--text-muted); margin-bottom: 16px; }
.target-item { padding: 16px; margin-bottom: 8px; cursor: pointer; }
</style>
