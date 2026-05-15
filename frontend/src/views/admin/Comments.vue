<template>
  <AppLayout>
    <div class="admin-page">
      <el-button text @click="$router.push('/admin')"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h1>评论管理</h1>
      <el-table :data="comments" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="评论者" width="100" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="score" label="评分" width="60" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
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
import { getCommentsAdmin, deleteCommentAdmin } from '@/api/admin'

const comments = ref([])

async function loadData() {
  const res = await getCommentsAdmin()
  if (res.code === 200) comments.value = res.data || []
}

async function del(row) {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '确认')
    const res = await deleteCommentAdmin(row.id)
    if (res.code === 200) { ElMessage.success('已删除'); loadData() }
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.admin-page h1 { font-size: 20px; margin: 16px 0; }
</style>
