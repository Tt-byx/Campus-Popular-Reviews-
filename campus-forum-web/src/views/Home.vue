<template>
  <AppLayout>
    <div class="home-header">
      <h1>发现校园新动态</h1>
      <div class="header-actions">
        <el-radio-group v-model="feedType" size="small">
          <el-radio-button label="recommend">推荐</el-radio-button>
          <el-radio-button v-if="userStore.isLoggedIn" label="following">关注</el-radio-button>
        </el-radio-group>
        <el-button :icon="Refresh" circle @click="loadPosts" />
      </div>
    </div>
    <div class="post-list">
      <PostCard v-for="post in posts" :key="post.id" :post="post" />
      <div v-if="loading" class="loading-box">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>
      <div v-if="!loading && posts.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <p>暂无帖子，快来发第一篇吧！</p>
      </div>
      <div v-if="noMore && posts.length > 0" class="no-more">- 没有更多了 -</div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Refresh, Loading } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import PostCard from '@/components/post/PostCard.vue'
import { getPersonalized, getPostList } from '@/api/post'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const posts = ref([])
const loading = ref(false)
const noMore = ref(false)
const feedType = ref('recommend')
const previousIds = ref('')

async function loadPosts() {
  loading.value = true
  try {
    let res
    if (feedType.value === 'following') {
      res = await getPostList(1, 50)
    } else {
      res = await getPersonalized(previousIds.value)
    }
    if (res.code === 200 && res.data) {
      posts.value = res.data
      previousIds.value = res.data.map(p => p.id).join(',')
      if (res.data.length < 5) noMore.value = true
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

watch(feedType, () => {
  posts.value = []
  noMore.value = false
  previousIds.value = ''
  loadPosts()
})

onMounted(loadPosts)
</script>

<style scoped lang="scss">
.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  h1 { font-size: 24px; }
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.post-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.loading-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  color: var(--text-muted);
}
.no-more {
  text-align: center;
  color: var(--text-muted);
  padding: 20px;
  font-size: 13px;
}
</style>
