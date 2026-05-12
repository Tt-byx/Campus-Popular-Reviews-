<template>
  <div class="post-card card" @click="$router.push(`/post/${post.id}`)">
    <div class="post-header">
      <div class="post-author">
        <UserAvatar :size="36" :src="post.avatar" :username="post.username" :user-id="post.userId" class-name="author-avatar" />
        <div class="author-info">
          <span class="author-name">{{ post.username || '匿名用户' }}</span>
          <span class="post-time">{{ relativeTime(post.createdAt) }}</span>
        </div>
      </div>
      <el-tag v-if="post.tag" size="small" effect="plain" class="post-tag">{{ post.tag }}</el-tag>
    </div>
    <h3 class="post-title">{{ post.title }}</h3>
    <p class="post-content">{{ post.content?.substring(0, 150) }}{{ post.content?.length > 150 ? '...' : '' }}</p>
    <img v-if="post.imageUrl" :src="post.imageUrl" class="post-image" loading="lazy" />
    <div class="post-footer">
      <span class="stat"><el-icon><View /></el-icon> {{ post.viewCount || 0 }}</span>
      <span class="stat"><el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }}</span>
      <span class="stat"><el-icon><Pointer /></el-icon> {{ post.likeCount || 0 }}</span>
    </div>
  </div>
</template>

<script setup>
import { View, ChatDotRound, Pointer } from '@element-plus/icons-vue'
import { relativeTime } from '@/utils/time'
import UserAvatar from '@/components/common/UserAvatar.vue'

defineProps({ post: { type: Object, required: true } })
</script>

<style scoped lang="scss">
.post-card {
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-lg);
  }
}
.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.post-author {
  display: flex;
  align-items: center;
  gap: 10px;
}
.author-avatar {
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  font-weight: 600;
  flex-shrink: 0;
}
.author-name {
  font-weight: 600;
  font-size: 14px;
}
.post-time {
  font-size: 12px;
  color: var(--text-muted);
}
.post-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  line-height: 1.4;
}
.post-content {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
}
.post-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  margin-bottom: 12px;
}
.post-footer {
  display: flex;
  gap: 20px;
  .stat {
    display: flex;
    align-items: center;
    gap: 4px;
    color: var(--text-muted);
    font-size: 13px;
  }
}
</style>
