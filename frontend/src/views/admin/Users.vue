<template>
  <AppLayout>
    <div class="admin-page">
      <el-button text @click="$router.push('/admin')"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h1>用户管理</h1>
      <el-table :data="users" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="头像" width="60">
          <template #default="{ row }"><UserAvatar :size="32" :src="row.avatar" :username="row.username" :user-id="row.id" /></template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="mute(row)">{{ row.status === 'active' ? '禁言' : '解禁' }}</el-button>
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
import { getUsers, muteUser, deleteUser } from '@/api/admin'
import UserAvatar from '@/components/common/UserAvatar.vue'

const users = ref([])

async function loadData() {
  const res = await getUsers()
  if (res.code === 200) users.value = res.data || []
}

async function mute(row) {
  const res = await muteUser(row.username)
  if (res.code === 200) { ElMessage.success(res.message); loadData() }
  else ElMessage.error(res.message)
}

async function del(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户 ${row.username}？`, '确认')
    const res = await deleteUser(row.username)
    if (res.code === 200) { ElMessage.success('已删除'); loadData() }
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.admin-page h1 { font-size: 20px; margin: 16px 0; }
</style>
