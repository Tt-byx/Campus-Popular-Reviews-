<template>
  <AppLayout>
    <div class="create-post">
      <h1>发布帖子</h1>
      <div class="form-card card">
        <el-form label-position="top">
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="输入帖子标题" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="分类">
            <el-radio-group v-model="form.tag">
              <el-radio-button v-for="t in tags" :key="t" :label="t">{{ t }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="内容">
            <el-input v-model="form.content" type="textarea" :rows="8" placeholder="分享你的校园生活..." />
          </el-form-item>
          <el-form-item label="配图">
            <el-upload
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :on-remove="() => imageFile = null"
              accept="image/*"
              list-type="picture-card"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="submit">发布</el-button>
        </el-form>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { createPost } from '@/api/post'

const router = useRouter()
const loading = ref(false)
const tags = ['食堂', '课程', '社团', '活动', '其他']
const form = ref({ title: '', content: '', tag: '食堂' })
const imageFile = ref(null)

function handleFileChange(file) {
  imageFile.value = file.raw
}

async function submit() {
  if (!form.value.title.trim()) { ElMessage.warning('请输入标题'); return }
  if (!form.value.content.trim()) { ElMessage.warning('请输入内容'); return }
  loading.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.value.title)
    fd.append('content', form.value.content)
    fd.append('tag', form.value.tag)
    if (imageFile.value) fd.append('image', imageFile.value)
    const res = await createPost(fd)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      router.push(`/post/${res.data?.id}`)
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error('发布失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.create-post {
  max-width: 760px;
  margin: 0 auto;
  h1 { font-size: 24px; margin-bottom: 20px; }
}
.form-card { padding: 24px; }
</style>
