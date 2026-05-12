<template>
  <AppLayout>
    <div v-if="post" class="post-detail">
      <div class="post-main card">
        <div class="post-header">
          <div class="post-author">
            <UserAvatar :size="40" :src="post.avatar" :username="post.username" :user-id="post.userId" />
            <div>
              <div class="author-name">{{ post.username || '匿名用户' }}</div>
              <div class="post-time">{{ relativeTime(post.createdAt) }}</div>
            </div>
          </div>
          <el-tag v-if="post.tag" effect="plain">{{ post.tag }}</el-tag>
        </div>
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-body">{{ post.content }}</div>
        <img v-if="post.imageUrl" :src="post.imageUrl" class="post-image" @click="showImage = true" />
        <div class="post-stats">
          <span><el-icon><View /></el-icon> {{ post.viewCount || 0 }} 浏览</span>
          <span><el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }} 评论</span>
        </div>
        <div class="post-actions">
          <LikeButton target-type="post" :target-id="post.id" />
          <el-button :type="favorited ? 'warning' : 'default'" @click="toggleFav">
            <el-icon><Star /></el-icon> {{ favorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button @click="showShare = true"><el-icon><Share /></el-icon> 分享</el-button>
        </div>
      </div>

      <!-- 点评目标列表 -->
      <div class="review-targets-section">
        <div class="section-header">
          <h2>点评目标</h2>
          <el-button v-if="userStore.isLoggedIn" type="primary" size="small" @click="showAddTarget = true">
            <el-icon><Plus /></el-icon> 添加
          </el-button>
        </div>
        <div v-if="showAddTarget" class="add-target-form card">
          <el-input v-model="newTargetName" placeholder="输入点评目标名称（如：食堂二楼麻辣烫）" @keyup.enter="addTarget" />
          <el-button type="primary" :loading="addingTarget" @click="addTarget">确认添加</el-button>
        </div>
        <div class="targets-grid">
          <div v-for="target in reviewTargets" :key="target.id" class="target-card card"
               @click="$router.push(`/review-target/${target.id}`)">
            <div class="target-name">{{ target.targetName }}</div>
            <div class="target-score" :style="{ color: getScoreColor(target.avgScore) }">
              {{ target.avgScore ? target.avgScore.toFixed(1) : '-' }}
            </div>
            <div class="target-label" :class="'level-' + getScoreLevel(target.avgScore)">
              {{ getScoreLabel(target.avgScore) }}
            </div>
            <div class="target-meta">{{ target.commentCount || 0 }} 条评论</div>
          </div>
        </div>
        <div v-if="reviewTargets.length === 0" class="empty-state">
          <p>暂无点评目标，快来添加第一个吧！</p>
        </div>
      </div>
    </div>
    <div v-else class="loading-box"><el-icon class="is-loading"><Loading /></el-icon></div>

    <el-dialog v-model="showShare" title="分享" width="400px">
      <ShareDialog :post-id="post?.id" :title="post?.title" />
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, ChatDotRound, Star, Share, Plus, Loading } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import LikeButton from '@/components/social/LikeButton.vue'
import ShareDialog from '@/components/social/ShareDialog.vue'
import UserAvatar from '@/components/common/UserAvatar.vue'
import { getPostById, getReviewTargets, createReviewTarget } from '@/api/post'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { relativeTime } from '@/utils/time'
import { getScoreLabel, getScoreLevel, getScoreColor } from '@/utils/score'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const post = ref(null)
const reviewTargets = ref([])
const favorited = ref(false)
const showShare = ref(false)
const showAddTarget = ref(false)
const newTargetName = ref('')
const addingTarget = ref(false)
const showImage = ref(false)

async function loadData() {
  const id = route.params.id
  const [postRes, targetsRes] = await Promise.all([
    getPostById(id),
    getReviewTargets(id)
  ])
  if (postRes.code === 200) post.value = postRes.data
  if (targetsRes.code === 200) reviewTargets.value = targetsRes.data || []
  if (userStore.isLoggedIn) {
    const favRes = await checkFavorite(id)
    if (favRes.code === 200) favorited.value = favRes.data?.favorited
  }
}

async function addTarget() {
  if (!newTargetName.value.trim()) return
  addingTarget.value = true
  try {
    const res = await createReviewTarget(route.params.id, { targetName: newTargetName.value.trim() })
    if (res.code === 200) {
      ElMessage.success('添加成功')
      newTargetName.value = ''
      showAddTarget.value = false
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    addingTarget.value = false
  }
}

async function toggleFav() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return }
  const res = await toggleFavorite(route.params.id)
  if (res.code === 200) {
    favorited.value = res.data?.favorited
    ElMessage.success(res.message)
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.post-main {
  padding: 24px;
  margin-bottom: 24px;
}
.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.author-name { font-weight: 600; }
.post-time { font-size: 12px; color: var(--text-muted); }
.post-title { font-size: 24px; font-weight: 700; margin-bottom: 16px; line-height: 1.4; }
.post-body { font-size: 15px; line-height: 1.8; color: var(--text); margin-bottom: 16px; white-space: pre-wrap; }
.post-image {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  cursor: pointer;
}
.post-stats {
  display: flex;
  gap: 20px;
  color: var(--text-muted);
  font-size: 13px;
  margin-bottom: 16px;
  span { display: flex; align-items: center; gap: 4px; }
}
.post-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  h2 { font-size: 18px; }
}
.add-target-form {
  display: flex;
  gap: 12px;
  padding: 16px;
  margin-bottom: 16px;
}
.targets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}
.target-card {
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
}
.target-name { font-weight: 600; margin-bottom: 8px; }
.target-score { font-size: 32px; font-weight: 700; }
.target-label { font-size: 13px; margin-bottom: 4px; }
.target-meta { font-size: 12px; color: var(--text-muted); }
.loading-box { display: flex; justify-content: center; padding: 60px; }
</style>
