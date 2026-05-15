<template>
  <AppLayout>
    <div v-if="target" class="review-detail">
      <div class="target-header card">
        <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
        <h1 class="target-name">{{ target.targetName }}</h1>
        <div class="target-stats">
          <div class="big-score" :style="{ color: getScoreColor(target.avgScore) }">
            {{ target.avgScore ? target.avgScore.toFixed(1) : '-' }}
          </div>
          <div class="score-label" :class="'level-' + getScoreLevel(target.avgScore)">
            {{ getScoreLabel(target.avgScore) }}
          </div>
          <div class="stats-meta">{{ comments.length }} 条评论</div>
        </div>
        <!-- 评分分布 -->
        <div v-if="stats.distribution" class="score-distribution">
          <div v-for="i in 5" :key="i" class="dist-row">
            <span class="dist-label">{{ SCORE_EMOJIS[6-i] }} {{ SCORE_LABELS[6-i] }}</span>
            <div class="dist-bar">
              <div class="dist-fill" :style="{ width: getDistPercent(6-i) + '%', backgroundColor: getScoreColor(6-i) }"></div>
            </div>
            <span class="dist-count">{{ stats.distribution[6-i] || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 发表评论 -->
      <div v-if="userStore.isLoggedIn" class="comment-form card">
        <h3>发表评论</h3>
        <div class="score-selector">
          <span v-for="i in 5" :key="i" class="score-emoji" :class="{ active: commentForm.score === i }" @click="commentForm.score = i">
            {{ SCORE_EMOJIS[i] }}
            <span class="emoji-label">{{ SCORE_LABELS[i] }}</span>
          </span>
        </div>
        <el-input v-model="commentForm.content" type="textarea" :rows="3" placeholder="写下你的评价..." />
        <el-button type="primary" :loading="submitting" @click="submitComment" style="margin-top:12px">提交评论</el-button>
      </div>

      <!-- 评论列表 -->
      <div class="comments-section">
        <h3>全部评论 ({{ comments.length }})</h3>
        <div v-for="c in comments" :key="c.id" class="comment-item card">
          <div class="comment-header">
            <UserAvatar :size="32" :src="c.avatar" :username="c.username" :user-id="c.userId" />
            <span class="comment-user">{{ c.username || '匿名用户' }}</span>
            <span class="comment-score" :style="{ color: getScoreColor(c.score) }">
              {{ SCORE_EMOJIS[c.score] }} {{ SCORE_LABELS[c.score] }}
            </span>
            <span class="comment-time">{{ relativeTime(c.createdAt) }}</span>
          </div>
          <div class="comment-content">{{ c.content }}</div>
        </div>
        <div v-if="comments.length === 0" class="empty-state"><p>暂无评论</p></div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import UserAvatar from '@/components/common/UserAvatar.vue'
import { getReviewTargetById, getReviewTargetStats, addComment, getComments } from '@/api/post'
import { relativeTime } from '@/utils/time'
import { getScoreLabel, getScoreLevel, getScoreColor, SCORE_LABELS, SCORE_EMOJIS } from '@/utils/score'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const target = ref(null)
const comments = ref([])
const stats = ref({})
const commentForm = ref({ score: 5, content: '' })
const submitting = ref(false)

function getDistPercent(score) {
  const total = Object.values(stats.value.distribution || {}).reduce((a, b) => a + b, 0)
  if (!total) return 0
  return ((stats.value.distribution[score] || 0) / total * 100).toFixed(0)
}

async function loadData() {
  const id = route.params.id
  const [targetRes, statsRes, commentsRes] = await Promise.all([
    getReviewTargetById(id),
    getReviewTargetStats(id),
    getComments(id)
  ])
  if (targetRes.code === 200) target.value = targetRes.data
  if (statsRes.code === 200) stats.value = statsRes.data || {}
  if (commentsRes.code === 200) comments.value = commentsRes.data || []
}

async function submitComment() {
  if (!commentForm.value.content.trim()) { ElMessage.warning('请输入评论内容'); return }
  submitting.value = true
  try {
    const res = await addComment(route.params.id, { content: commentForm.value.content, score: commentForm.value.score })
    if (res.code === 200) {
      ElMessage.success('评论成功')
      commentForm.value = { score: 5, content: '' }
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.target-header { padding: 24px; margin-bottom: 16px; }
.target-name { font-size: 22px; font-weight: 700; margin: 12px 0; }
.target-stats { display: flex; align-items: baseline; gap: 12px; margin-bottom: 20px; }
.big-score { font-size: 48px; font-weight: 700; }
.score-label { font-size: 16px; font-weight: 500; }
.stats-meta { color: var(--text-muted); font-size: 14px; }
.score-distribution { max-width: 400px; }
.dist-row { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.dist-label { width: 70px; font-size: 12px; text-align: right; }
.dist-bar { flex: 1; height: 8px; background: var(--bg); border-radius: 4px; overflow: hidden; }
.dist-fill { height: 100%; border-radius: 4px; transition: width 0.3s; }
.dist-count { width: 30px; font-size: 12px; color: var(--text-muted); }
.comment-form { padding: 20px; margin-bottom: 16px; h3 { margin-bottom: 12px; } }
.score-selector { display: flex; gap: 12px; margin-bottom: 12px; }
.score-emoji {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  font-size: 28px; cursor: pointer; padding: 8px; border-radius: var(--radius-md);
  transition: all 0.2s;
  &.active { background: rgba(37,99,235,0.1); transform: scale(1.1); }
  &:hover { transform: scale(1.1); }
}
.emoji-label { font-size: 11px; color: var(--text-muted); }
.comments-section { h3 { margin-bottom: 16px; } }
.comment-item { padding: 16px; margin-bottom: 8px; }
.comment-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.comment-user { font-weight: 600; font-size: 14px; }
.comment-score { font-size: 13px; }
.comment-time { margin-left: auto; font-size: 12px; color: var(--text-muted); }
.comment-content { font-size: 14px; line-height: 1.6; }
</style>
