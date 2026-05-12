<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="login-brand">
        <div class="brand-icon">校</div>
        <h1>校圈</h1>
        <p>加入校园社区</p>
      </div>
    </div>
    <div class="login-form-wrapper">
      <div class="login-card">
        <h2>创建账号</h2>
        <p class="subtitle">注册校圈，发现校园精彩</p>
        <el-form @submit.prevent="handleRegister">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleRegister" />
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%">注册</el-button>
        </el-form>
        <div class="login-footer">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '', confirmPassword: '' })

async function handleRegister() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  loading.value = true
  try {
    const res = await register({ username: form.value.username, password: form.value.password })
    if (res.code === 200) {
      userStore.setLoginData(res.data)
      ElMessage.success('注册成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/auth.scss';
</style>
