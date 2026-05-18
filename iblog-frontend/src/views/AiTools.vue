<template>
  <div class="container">
    <h2 class="mb-16">AI 工具箱</h2>

    <div class="card">
      <h3 class="mb-16">AI 文案生成</h3>
      <div class="flex gap-8 mb-16">
        <button v-for="s in styles" :key="s" :class="['btn btn-small', copyStyle === s ? 'btn-primary' : 'btn-ghost']" @click="copyStyle = s">{{ s }}</button>
      </div>
      <div class="form-group">
        <input class="input" v-model="copyTopic" placeholder="输入主题..." />
      </div>
      <button class="btn btn-primary" @click="genCopywriting" :disabled="loading">生成</button>
      <div v-if="copyResult" class="mt-16 result-box">
        <p>{{ copyResult }}</p>
        <button class="btn btn-ghost btn-small mt-16" @click="useAsContent(copyResult)">一键发布</button>
      </div>
    </div>

    <div class="card">
      <h3 class="mb-16">AI 润色</h3>
      <div class="form-group">
        <textarea class="input" v-model="polishText" placeholder="粘贴你的文字..."></textarea>
      </div>
      <div class="flex gap-8">
        <button class="btn btn-primary" @click="doPolish('润色')" :disabled="loading">润色</button>
        <button class="btn btn-ghost" @click="doPolish('扩写')" :disabled="loading">扩写</button>
        <button class="btn btn-ghost" @click="doPolish('缩写')" :disabled="loading">缩写</button>
      </div>
      <div v-if="polishResult" class="mt-16 result-box">{{ polishResult }}</div>
    </div>

    <div class="card">
      <h3 class="mb-16">AI 评论助手</h3>
      <div class="form-group">
        <textarea class="input" v-model="commentBlog" placeholder="粘贴博文内容..."></textarea>
      </div>
      <button class="btn btn-primary" @click="genComment" :disabled="loading">生成评论</button>
      <div v-if="commentResult" class="mt-16 result-box">
        <p>{{ commentResult }}</p>
        <button class="btn btn-ghost btn-small mt-16" @click="copyText(commentResult)">复制</button>
      </div>
    </div>

    <div class="card">
      <h3 class="mb-16">情绪分析</h3>
      <div class="form-group">
        <textarea class="input" v-model="emotionText" placeholder="输入文字..."></textarea>
      </div>
      <button class="btn btn-primary" @click="doEmotion" :disabled="loading">分析</button>
      <div v-if="emotionResult" class="mt-16 result-box">情绪：{{ emotionResult }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { generateCopywriting, polishText as polishApi, generateComment, analyzeEmotion } from '../api/ai'

const router = useRouter()
const loading = ref(false)
const styles = ['治愈', '文艺', '搞笑', '伤感', '励志']

const copyStyle = ref('治愈')
const copyTopic = ref('')
const copyResult = ref('')

const polishText = ref('')
const polishResult = ref('')

const commentBlog = ref('')
const commentResult = ref('')

const emotionText = ref('')
const emotionResult = ref('')

async function genCopywriting() {
  if (!copyTopic.value.trim()) return
  loading.value = true
  try {
    const res = await generateCopywriting({ style: copyStyle.value, topic: copyTopic.value })
    if (res.success) copyResult.value = res.data
  } catch (e) { alert('AI 服务不可用') }
  loading.value = false
}

async function doPolish(action) {
  if (!polishText.value.trim()) return
  loading.value = true
  try {
    const res = await polishApi({ text: polishText.value, action })
    if (res.success) polishResult.value = res.data
  } catch (e) { alert('AI 服务不可用') }
  loading.value = false
}

async function genComment() {
  if (!commentBlog.value.trim()) return
  loading.value = true
  try {
    const res = await generateComment({ blogContent: commentBlog.value })
    if (res.success) commentResult.value = res.data
  } catch (e) { alert('AI 服务不可用') }
  loading.value = false
}

async function doEmotion() {
  if (!emotionText.value.trim()) return
  loading.value = true
  try {
    const res = await analyzeEmotion({ text: emotionText.value })
    if (res.success) emotionResult.value = res.data
  } catch (e) { alert('AI 服务不可用') }
  loading.value = false
}

function useAsContent(text) {
  router.push({ path: '/blog/edit', query: { content: text } })
}

function copyText(text) {
  navigator.clipboard.writeText(text)
  alert('已复制')
}
</script>

<style scoped>
.result-box { background: var(--bg); padding: 16px; border-radius: var(--radius); white-space: pre-wrap; }
</style>
