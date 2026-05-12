<template>
  <AppLayout>
    <div class="admin-page">
      <el-button text @click="$router.push('/admin')"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h1>违禁词管理</h1>
      <div class="add-word">
        <el-input v-model="newWord" placeholder="添加违禁词..." @keyup.enter="add" style="max-width: 300px" />
        <el-button type="primary" @click="add">添加</el-button>
      </div>
      <div class="word-list">
        <el-tag v-for="w in words" :key="w.id" closable @close="remove(w)" size="large" style="margin: 4px">{{ w.word }}</el-tag>
      </div>
      <div v-if="words.length === 0" class="empty-state"><p>暂无违禁词</p></div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/layout/AppLayout.vue'
import { getBannedWords, addBannedWord, deleteBannedWord } from '@/api/admin'

const words = ref([])
const newWord = ref('')

async function loadData() {
  const res = await getBannedWords()
  if (res.code === 200) words.value = res.data || []
}

async function add() {
  if (!newWord.value.trim()) return
  const res = await addBannedWord(newWord.value.trim())
  if (res.code === 200) { ElMessage.success('已添加'); newWord.value = ''; loadData() }
  else ElMessage.error(res.message)
}

async function remove(w) {
  const res = await deleteBannedWord(w.id)
  if (res.code === 200) { ElMessage.success('已删除'); loadData() }
}

onMounted(loadData)
</script>

<style scoped>
.admin-page h1 { font-size: 20px; margin: 16px 0; }
.add-word { display: flex; gap: 8px; margin-bottom: 20px; }
.word-list { margin-top: 12px; }
</style>
