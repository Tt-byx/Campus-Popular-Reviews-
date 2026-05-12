<template>
  <AppLayout>
    <h1 class="page-title">排行榜</h1>
    <el-radio-group v-model="sortType" @change="loadRanking" style="margin-bottom: 20px">
      <el-radio-button label="hot">热度排行</el-radio-button>
      <el-radio-button label="new">最新发布</el-radio-button>
      <el-radio-button label="comments">评论最多</el-radio-button>
    </el-radio-group>
    <div class="ranking-list">
      <div v-for="(post, index) in posts" :key="post.id" class="ranking-item card" @click="$router.push(`/post/${post.id}`)">
        <div class="rank-badge" :class="{ gold: index === 0, silver: index === 1, bronze: index === 2 }">
          {{ index + 1 }}
        </div>
        <div class="rank-content">
          <h3>{{ post.title }}</h3>
          <div class="rank-meta">
            <span>{{ post.username }}</span>
            <span><el-icon><View /></el-icon> {{ post.viewCount }}</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ post.commentCount }}</span>
          </div>
        </div>
      </div>
      <div v-if="posts.length === 0" class="empty-state"><p>暂无数据</p></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { View, ChatDotRound } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { getPostsRanking } from '@/api/post'

const sortType = ref('hot')
const posts = ref([])

async function loadRanking() {
  const res = await getPostsRanking(sortType.value)
  if (res.code === 200) posts.value = res.data || []
}

onMounted(loadRanking)
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 16px; }
.ranking-list { display: flex; flex-direction: column; gap: 8px; }
.ranking-item { display: flex; align-items: center; gap: 16px; padding: 16px; cursor: pointer; }
.rank-badge {
  width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 16px; background: var(--bg); color: var(--text-secondary); flex-shrink: 0;
  &.gold { background: #fef3c7; color: #d97706; }
  &.silver { background: #e5e7eb; color: #6b7280; }
  &.bronze { background: #fed7aa; color: #c2410c; }
}
.rank-content { flex: 1; h3 { font-size: 15px; margin-bottom: 4px; } }
.rank-meta { display: flex; gap: 16px; font-size: 12px; color: var(--text-muted); span { display: flex; align-items: center; gap: 4px; } }
</style>
