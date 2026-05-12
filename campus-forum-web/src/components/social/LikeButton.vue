<template>
  <el-button :type="liked ? 'primary' : 'default'" @click.stop="handleLike">
    <el-icon><Pointer /></el-icon>
    {{ liked ? '已赞' : '点赞' }} {{ count > 0 ? count : '' }}
  </el-button>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Pointer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { toggleLike, getLikeStatus, getLikeCount } from '@/api/like'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  targetType: { type: String, required: true },
  targetId: { type: [Number, String], required: true }
})

const userStore = useUserStore()
const liked = ref(false)
const count = ref(0)

async function loadStatus() {
  try {
    const [statusRes, countRes] = await Promise.all([
      getLikeStatus(props.targetType, props.targetId),
      getLikeCount(props.targetType, props.targetId)
    ])
    if (statusRes.code === 200) liked.value = statusRes.data?.liked
    if (countRes.code === 200) count.value = countRes.data?.count || 0
  } catch (e) {}
}

async function handleLike() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return }
  try {
    const res = await toggleLike(props.targetType, props.targetId)
    if (res.code === 200) {
      liked.value = res.data?.liked
      count.value += liked.value ? 1 : -1
    }
  } catch (e) {}
}

onMounted(loadStatus)
</script>
