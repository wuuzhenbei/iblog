<template>
  <div class="container">
    <div class="card" v-if="blog">
      <div class="flex gap-8 mb-16">
        <div class="avatar">{{ blog.nickname?.[0] || '?' }}</div>
        <div>
          <router-link :to="'/user/' + blog.userId"><strong>{{ blog.nickname }}</strong></router-link>
          <p class="text-secondary">{{ formatDate(blog.createdAt) }}</p>
        </div>
      </div>
      <p style="white-space:pre-wrap">{{ blog.content }}</p>
      <div class="flex gap-16 mt-16 text-secondary">
        <span @click="like" style="cursor:pointer">{{ blog.liked ? '❤️' : '🤍' }} {{ blog.likeCount }}</span>
        <span>💬 {{ blog.commentCount }}</span>
        <span>👁 {{ blog.viewCount }}</span>
      </div>
    </div>

    <div class="card">
      <h3 class="mb-16">评论 ({{ comments.length }})</h3>
      <div v-if="userStore.isLoggedIn" class="mb-16">
        <textarea class="input" v-model="newComment" placeholder="写评论..."></textarea>
        <button class="btn btn-primary mt-16" @click="submitComment">发表评论</button>
      </div>
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="flex gap-8">
          <div class="avatar-small">{{ c.nickname?.[0] || '?' }}</div>
          <div>
            <strong>{{ c.nickname }}</strong>
            <span v-if="c.replyToNickname" class="text-secondary"> 回复 {{ c.replyToNickname }}</span>
            <p>{{ c.content }}</p>
            <span class="text-secondary">{{ formatDate(c.createdAt) }}</span>
            <button class="btn btn-ghost btn-small" @click="replyTo(c)">回复</button>
          </div>
        </div>
        <div v-for="child in c.children" :key="child.id" class="comment-child">
          <strong>{{ child.nickname }}</strong>
          <span v-if="child.replyToNickname" class="text-secondary"> 回复 {{ child.replyToNickname }}</span>
          <p>{{ child.content }}</p>
          <span class="text-secondary">{{ formatDate(child.createdAt) }}</span>
          <button class="btn btn-ghost btn-small" @click="replyTo(child, c.id)">回复</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getBlogDetail, interactBlog, postComment } from '../api/blog'

const route = useRoute()
const userStore = useUserStore()
const blog = ref(null)
const comments = ref([])
const newComment = ref('')
const replyParentId = ref(null)
const replyToUserId = ref(null)

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleString('zh-CN')
}

async function load() {
  const res = await getBlogDetail(route.params.id)
  if (res.success) {
    blog.value = res.data.blog
    comments.value = res.data.comments
  }
}

async function like() {
  if (!userStore.isLoggedIn) { alert('请先登录'); return }
  const res = await interactBlog({ blogId: blog.value.id, type: 'like' })
  if (res.success) {
    blog.value.liked = res.action === 'added'
    blog.value.likeCount += res.action === 'added' ? 1 : -1
  }
}

function replyTo(comment, parentId) {
  replyParentId.value = parentId || comment.id
  replyToUserId.value = comment.userId
  newComment.value = `@${comment.nickname} `
}

async function submitComment() {
  if (!newComment.value.trim()) return
  const data = { blogId: blog.value.id, content: newComment.value }
  if (replyParentId.value) data.parentId = replyParentId.value
  if (replyToUserId.value) data.replyToUserId = replyToUserId.value
  const res = await postComment(data)
  if (res.success) {
    newComment.value = ''
    replyParentId.value = null
    replyToUserId.value = null
    load()
  }
}

onMounted(load)
</script>

<style scoped>
.avatar { width: 40px; height: 40px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: bold; }
.avatar-small { width: 28px; height: 28px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 12px; }
.comment-item { padding: 12px 0; border-bottom: 1px solid var(--border); }
.comment-child { margin-left: 36px; padding: 8px 0; }
</style>
