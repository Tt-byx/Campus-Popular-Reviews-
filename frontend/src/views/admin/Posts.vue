<template>
  <AppLayout>
    <div class="admin-page">
      <el-button text @click="$router.push('/admin')"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h1>帖子管理</h1>
      <el-table :data="posts" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="username" label="作者" width="100" />
        <el-table-column prop="tag" label="标签" width="80" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/post/${row.id}`)">查看</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppLayout from '@/components/layout/AppLayout.vue'
import { getPosts, deletePostAdmin } from '@/api/admin'

const posts = ref([])

async function loadData() {
  const res = await getPosts()
  if (res.code === 200) posts.value = res.data || []
}

async function del(row) {
  try {
    await ElMessageBox.confirm(`确定删除帖子「${row.title}」？`, '确认')
    const res = await deletePostAdmin(row.id)
    if (res.code === 200) { ElMessage.success('已删除'); loadData() }
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.admin-page h1 { font-size: 20px; margin: 16px 0; }
</style>
