<template>
  <div class="blog-card" @click="$router.push('/blog/' + blog.id)">
    <div class="flex gap-8 mb-16">
      <div class="avatar" @click.stop="$router.push('/user/' + blog.userId)">{{ blog.nickname?.[0] || '?' }}</div>
      <div>
        <router-link :to="'/user/' + blog.userId" @click.stop>{{ blog.nickname }}</router-link>
        <p class="text-secondary">{{ formatDate(blog.createdAt) }}</p>
      </div>
    </div>
    <p class="blog-content">{{ blog.content }}</p>
    <div class="flex gap-16 mt-16 text-secondary">
      <span>❤️ {{ blog.likeCount }}</span>
      <span>💬 {{ blog.commentCount }}</span>
      <span>👁 {{ blog.viewCount }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({ blog: { type: Object, required: true } })

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleString('zh-CN')
}
</script>

<style scoped>
.blog-card { cursor: pointer; }
.blog-card:hover { background: var(--bg); margin: -4px; padding: 24px; border-radius: var(--radius); }
.blog-content { white-space: pre-wrap; word-break: break-word; }
.avatar { width: 36px; height: 36px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: bold; cursor: pointer; flex-shrink: 0; }
</style>
