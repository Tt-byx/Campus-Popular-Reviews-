<template>
  <header class="app-header">
    <div class="header-left">
      <el-button class="menu-btn" :icon="Fold" text @click="$emit('toggle-sidebar')" />
      <div class="search-box">
        <el-input v-model="keyword" placeholder="搜索帖子和点评..." clearable @keyup.enter="doSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </div>
    <div class="header-right">
      <el-button type="primary" @click="$router.push('/post/create')">
        <el-icon><Plus /></el-icon> 发帖
      </el-button>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Fold, Search, Plus } from '@element-plus/icons-vue'

defineEmits(['toggle-sidebar'])
const router = useRouter()
const keyword = ref('')

function doSearch() {
  if (keyword.value.trim()) {
    router.push(`/search?keyword=${encodeURIComponent(keyword.value.trim())}`)
  }
}
</script>

<style scoped lang="scss">
.app-header {
  height: var(--header-height);
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}
.menu-btn {
  display: none;
}
.search-box {
  max-width: 400px;
  flex: 1;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
@media (max-width: 768px) {
  .app-header { padding: 0 12px; }
  .menu-btn { display: flex; }
  .search-box { max-width: none; }
}
</style>
