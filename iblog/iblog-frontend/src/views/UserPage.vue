<template>
  <div class="container">
    <div class="card">
      <div class="flex gap-16">
        <div class="avatar-box">{{ profile?.nickname?.[0] || '?' }}</div>
        <div>
          <h2>{{ profile?.nickname || profile?.username }}</h2>
          <p class="text-secondary">{{ profile?.signature }}</p>
          <div class="flex gap-16 mt-16">
            <span>关注 {{ profile?.followingCount || 0 }}</span>
            <span>粉丝 {{ profile?.followerCount || 0 }}</span>
          </div>
          <div class="flex gap-8 mt-16">
            <button v-if="!isSelf" class="btn btn-primary" @click="toggleFollow">
              {{ isFollowing ? '取消关注' : '关注' }}
            </button>
            <button v-if="!isSelf" class="btn btn-ghost" @click="$router.push('/chat/' + userId)">私信</button>
          </div>
        </div>
      </div>
    </div>
    <h3 class="mb-16">TA 的博文</h3>
    <div v-for="blog in blogs" :key="blog.id" class="card">
      <BlogCard :blog="blog" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getBlogs } from '../api/blog'
import { followUser } from '../api/social'
import api from '../api/index'
import BlogCard from '../components/BlogCard.vue'

const route = useRoute()
const userStore = useUserStore()
const userId = computed(() => route.params.id)
const isSelf = computed(() => String(userStore.user?.id) === String(userId.value))
const profile = ref(null)
const blogs = ref([])
const isFollowing = ref(false)

onMounted(async () => {
  try {
    const res = await api.get('/user/profile', { params: { userId: userId.value } })
    if (res.success) { profile.value = res.data; isFollowing.value = res.data.isFollowing }
  } catch (e) { console.error(e) }
  try {
    const res = await getBlogs({ userId: userId.value, page: 1, size: 50 })
    if (res.success) blogs.value = res.data
  } catch (e) { console.error(e) }
})

async function toggleFollow() {
  try {
    const res = await followUser({ targetId: Number(userId.value), action: isFollowing.value ? 'unfollow' : 'follow' })
    if (res.success) isFollowing.value = !isFollowing.value
  } catch (e) { alert(e.error || '操作失败') }
}
</script>

<style scoped>
.avatar-box { width: 80px; height: 80px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 32px; flex-shrink: 0; }
</style>
