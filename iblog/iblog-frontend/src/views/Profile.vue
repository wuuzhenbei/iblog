<template>
  <div class="container">
    <div class="profile-header card">
      <div class="flex gap-16">
        <div class="avatar-large avatar">{{ profile?.nickname?.[0] || '?' }}</div>
        <div>
          <h2>{{ profile?.nickname || userStore.user?.username }}</h2>
          <p class="text-secondary">{{ profile?.signature || '这个人很懒，什么都没写' }}</p>
          <div class="flex gap-16 mt-16">
            <span>关注 {{ profile?.followingCount || 0 }}</span>
            <span>粉丝 {{ profile?.followerCount || 0 }}</span>
            <span>等级 Lv.{{ profile?.level || 1 }}</span>
          </div>
        </div>
      </div>
      <div class="flex gap-8 mt-16">
        <button class="btn btn-primary" @click="$router.push('/blog/edit')">发博</button>
        <button class="btn btn-ghost" @click="$router.push('/settings')">编辑资料</button>
        <button class="btn btn-ghost" @click="$router.push('/drafts')">草稿箱</button>
      </div>
    </div>
    <h3 class="mb-16">我的博文</h3>
    <div v-for="blog in blogs" :key="blog.id" class="card">
      <BlogCard :blog="blog" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getBlogs } from '../api/blog'
import api from '../api/index'
import BlogCard from '../components/BlogCard.vue'

const userStore = useUserStore()
const profile = ref(null)
const blogs = ref([])

onMounted(async () => {
  try {
    const res = await api.get('/user/profile')
    if (res.success) profile.value = res.data
  } catch (e) { console.error(e) }

  try {
    const res = await getBlogs({ userId: userStore.user?.id, page: 1, size: 50 })
    if (res.success) blogs.value = res.data
  } catch (e) { console.error(e) }
})
</script>

<style scoped>
.profile-header { margin: 16px 0; }
.avatar-large { width: 80px; height: 80px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 32px; flex-shrink: 0; }
</style>
