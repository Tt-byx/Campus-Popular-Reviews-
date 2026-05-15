<template>
  <div class="share-dialog">
    <div class="share-link">
      <el-input :model-value="shareLink" readonly>
        <template #append>
          <el-button @click="copyLink">复制链接</el-button>
        </template>
      </el-input>
    </div>
    <div class="share-qr">
      <p class="share-label">扫码分享</p>
      <div class="qr-placeholder">
        <div class="qr-box">
          <el-icon :size="48"><Iphone /></el-icon>
          <p>二维码生成中...</p>
        </div>
      </div>
    </div>
    <div class="share-actions">
      <el-button @click="shareTo('copy')"><el-icon><Link /></el-icon> 复制链接</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Link, Iphone } from '@element-plus/icons-vue'

const props = defineProps({
  postId: { type: [Number, String], required: true },
  title: { type: String, default: '' }
})

const shareLink = computed(() => `${window.location.origin}/post/${props.postId}`)

function copyLink() {
  navigator.clipboard.writeText(shareLink.value).then(() => {
    ElMessage.success('链接已复制')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

function shareTo(type) {
  if (type === 'copy') copyLink()
}
</script>

<style scoped lang="scss">
.share-dialog { padding: 8px 0; }
.share-link { margin-bottom: 20px; }
.share-label { font-size: 13px; color: var(--text-secondary); margin-bottom: 12px; }
.qr-placeholder { display: flex; justify-content: center; margin-bottom: 20px; }
.qr-box {
  width: 160px; height: 160px;
  border: 1px dashed var(--border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  p { margin-top: 8px; font-size: 12px; }
}
.share-actions { display: flex; gap: 12px; justify-content: center; }
</style>
