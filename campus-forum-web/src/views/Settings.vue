<template>
  <AppLayout>
    <h1 class="page-title">设置</h1>
    <div class="settings-card card">
      <h3>个人资料</h3>
      <el-form label-position="top">
        <el-form-item label="头像">
          <el-upload :auto-upload="false" :show-file-list="false" :on-change="handleAvatar" accept="image/*">
            <el-avatar :size="80" :src="userStore.avatar" class="avatar-upload">{{ userStore.username?.charAt(0) }}</el-avatar>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="个性签名">
          <el-input v-model="form.signature" type="textarea" :rows="2" />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
      </el-form>
    </div>
    <div class="settings-card card" style="margin-top: 16px">
      <h3>账号操作</h3>
      <el-button @click="handleLogout">退出登录</el-button>
      <el-button type="danger" @click="handleDeleteAccount">注销账号</el-button>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppLayout from '@/components/layout/AppLayout.vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, uploadAvatar, deleteAccount } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const saving = ref(false)
const form = ref({ username: '', signature: '' })

onMounted(() => {
  form.value.username = userStore.username
  form.value.signature = userStore.signature
})

async function handleAvatar(file) {
  const fd = new FormData()
  fd.append('image', file.raw)
  const res = await uploadAvatar(fd)
  if (res.code === 200) {
    userStore.avatar = res.data
    localStorage.setItem('avatar', res.data)
    ElMessage.success('头像已更新')
  }
}

async function saveProfile() {
  saving.value = true
  try {
    const res = await updateProfile({ username: form.value.username, signature: form.value.signature })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      if (res.data?.user) userStore.setProfile(res.data.user)
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    saving.value = false
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

async function handleDeleteAccount() {
  try {
    await ElMessageBox.confirm('注销后所有数据将被永久删除，确定要注销账号吗？', '注销确认', { type: 'warning', confirmButtonText: '确定注销', cancelButtonText: '取消' })
    const res = await deleteAccount()
    if (res.code === 200) {
      userStore.logout()
      router.push('/login')
      ElMessage.success('账号已注销')
    }
  } catch (e) {}
}
</script>

<style scoped>
.page-title { font-size: 24px; margin-bottom: 20px; }
.settings-card { padding: 24px; h3 { margin-bottom: 16px; } }
.avatar-upload { cursor: pointer; }
</style>
