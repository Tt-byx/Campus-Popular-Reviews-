<template>
  <AppLayout>
    <div v-if="user" class="user-profile">
      <div class="profile-header card">
        <el-avatar :size="80" :src="user.avatar" class="profile-avatar">{{ user.username?.charAt(0) }}</el-avatar>
        <div class="profile-info">
          <h1>{{ user.username }}</h1>
          <p class="bio">{{ user.bio || user.signature || '这个人很懒，什么都没写' }}</p>
          <div class="profile-stats">
            <span><strong>{{ stats.postCount || 0 }}</strong> 帖子</span>
            <span><strong>{{ stats.followers || 0 }}</strong> 粉丝</span>
            <span><strong>{{ stats.following || 0 }}</strong> 关注</span>
          </div>
        </div>
        <div v-if="!isMe" class="profile-actions">
          <el-button :type="followed ? 'default' : 'primary'" @click="handleFollow">
            {{ followed ? '已关注' : '关注' }}
          </el-button>
          <el-button @click="chatStore.openChat(user.id, user.username, user.avatar)">私信</el-button>
        </div>
      </div>
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="帖子" name="posts">
          <PostCard v-for="p in userPosts" :key="p.id" :post="p" />
          <div v-if="userPosts.length === 0" class="empty-state"><p>暂无帖子</p></div>
        </el-tab-pane>
        <el-tab-pane label="评论" name="comments">
          <div v-for="c in userComments" :key="c.commentId" class="comment-card card" @click="$router.push(`/post/${c.postId}`)">
            <div class="comment-post-title">评论了：{{ c.postTitle }} · {{ c.targetName }}</div>
            <div class="comment-content">{{ c.content }}</div>
            <div class="comment-meta">
              <span v-if="c.score">评分: {{ c.score }}</span>
              <span>{{ relativeTime(c.createdAt) }}</span>
            </div>
          </div>
          <div v-if="userComments.length === 0" class="empty-state"><p>暂无评论</p></div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/layout/AppLayout.vue'
import PostCard from '@/components/post/PostCard.vue'
import { getUserById, getUserPosts, getUserStats } from '@/api/user'
import { getUserPostsMe, getUserCommentsMe } from '@/api/post'
import { toggleFollow, checkFollow, getFollowStats } from '@/api/follow'
import { relativeTime } from '@/utils/time'
import { useUserStore } from '@/stores/user'
import { useChatStore } from '@/stores/chat'

const route = useRoute()
const userStore = useUserStore()
const chatStore = useChatStore()
const user = ref(null)
const stats = ref({})
const followed = ref(false)
const activeTab = ref('posts')
const userPosts = ref([])
const userComments = ref([])

const isMe = computed(() => String(userStore.userId) === String(route.params.id))

async function loadData() {
  const id = route.params.id
  const userRes = await getUserById(id)
  if (userRes.code === 200) user.value = userRes.data

  const [statsRes, postsRes, followStatsRes] = await Promise.all([
    getUserStats(id),
    getUserPosts(id),
    getFollowStats(id)
  ])
  if (statsRes.code === 200) stats.value = { ...statsRes.data, ...followStatsRes.data }
  if (postsRes.code === 200) userPosts.value = postsRes.data || []

  if (userStore.isLoggedIn && !isMe.value) {
    const followRes = await checkFollow(id)
    if (followRes.code === 200) followed.value = followRes.data?.followed
  }

  if (isMe.value) {
    const commentsRes = await getUserCommentsMe()
    if (commentsRes.code === 200) userComments.value = commentsRes.data || []
  }
}

async function handleFollow() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return }
  const res = await toggleFollow(route.params.id)
  if (res.code === 200) {
    followed.value = res.data?.followed
    ElMessage.success(res.message)
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.profile-header { padding: 24px; display: flex; align-items: center; gap: 20px; margin-bottom: 16px; }
.profile-avatar { background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-weight: 700; font-size: 32px; flex-shrink: 0; }
.profile-info { flex: 1; h1 { font-size: 22px; margin-bottom: 4px; } }
.bio { color: var(--text-secondary); font-size: 14px; margin-bottom: 8px; }
.profile-stats { display: flex; gap: 20px; font-size: 14px; color: var(--text-secondary); strong { color: var(--text); } }
.profile-actions { display: flex; gap: 8px; }
.comment-card { padding: 16px; margin-bottom: 8px; cursor: pointer; transition: all 0.2s; &:hover { transform: translateY(-2px); box-shadow: var(--shadow-lg); } }
.comment-post-title { font-size: 12px; color: var(--primary); margin-bottom: 6px; }
.comment-content { font-size: 14px; margin-bottom: 8px; }
.comment-meta { font-size: 12px; color: var(--text-muted); display: flex; gap: 12px; }
@media (max-width: 768px) {
  .profile-header { flex-direction: column; text-align: center; }
  .profile-stats { justify-content: center; }
}
</style>
