<template>
  <span v-if="userId" class="avatar-link" @click.stop.prevent="goProfile">
    <el-avatar :size="size" :src="src" :class="className">
      {{ (username || '匿')?.charAt(0) }}
    </el-avatar>
  </span>
  <el-avatar v-else :size="size" :src="src" :class="className">
    {{ (username || '匿')?.charAt(0) }}
  </el-avatar>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  src: { type: String, default: '' },
  size: { type: Number, default: 36 },
  username: { type: String, default: '' },
  userId: { type: [Number, String], default: null },
  className: { type: String, default: '' }
})

const router = useRouter()
function goProfile() {
  router.push(`/user/${props.userId}`)
}
</script>

<style scoped>
.avatar-link {
  display: inline-flex;
  text-decoration: none;
  cursor: pointer;
}
</style>
