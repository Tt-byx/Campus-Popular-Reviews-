<template>
  <AppLayout>
    <h1 class="page-title">管理后台</h1>
    <div class="admin-nav">
      <div class="admin-card card" @click="$router.push('/admin/users')">
        <el-icon :size="32" color="var(--primary)"><User /></el-icon>
        <h3>用户管理</h3>
      </div>
      <div class="admin-card card" @click="$router.push('/admin/posts')">
        <el-icon :size="32" color="#f97316"><Document /></el-icon>
        <h3>帖子管理</h3>
      </div>
      <div class="admin-card card" @click="$router.push('/admin/comments')">
        <el-icon :size="32" color="#22c55e"><ChatDotRound /></el-icon>
        <h3>评论管理</h3>
      </div>
      <div class="admin-card card" @click="$router.push('/admin/banned-words')">
        <el-icon :size="32" color="#ef4444"><Warning /></el-icon>
        <h3>违禁词</h3>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { User, Document, ChatDotRound, Warning } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.admin-nav { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.admin-card { padding: 24px; text-align: center; cursor: pointer; transition: all 0.2s; }
.admin-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.admin-card h3 { margin-top: 12px; }
</style>
