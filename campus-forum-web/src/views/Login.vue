<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="login-brand">
        <div class="brand-icon">校</div>
        <h1>校圈</h1>
        <p>发现校园新动态</p>
      </div>
    </div>
    <div class="login-form-wrapper">
      <div class="login-card">
        <h2>欢迎回来</h2>
        <p class="subtitle">登录你的校圈账号</p>
        <el-form @submit.prevent="handleLogin">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%">登录</el-button>
        </el-form>
        <div class="login-footer">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    if (res.code === 200) {
      userStore.setLoginData(res.data)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/auth.scss';
</style>
