<template>
  <div class="container">
    <div class="home-layout">
      <div class="feed">
        <div class="card" v-if="userStore.isLoggedIn">
          <textarea class="input" v-model="newPost" placeholder="分享你的想法..."></textarea>
          <div class="flex-between mt-16">
            <div class="flex gap-8">
              <button class="btn btn-ghost btn-small" @click="$router.push('/ai')">AI 助手</button>
            </div>
            <button class="btn btn-primary" @click="publish">发布</button>
          </div>
        </div>
        <div v-for="blog in blogs" :key="blog.id" class="card">
          <BlogCard :blog="blog" />
        </div>
        <div v-if="blogs.length === 0" class="card text-secondary" style="text-align:center">暂无内容</div>
        <div v-if="hasMore" class="card" style="text-align:center;cursor:pointer" @click="loadMore">加载更多</div>
      </div>
      <div class="sidebar">
        <div class="card" v-if="userStore.isLoggedIn">
          <h3>个人信息</h3>
          <p class="text-secondary">{{ userStore.user?.username }}</p>
          <button class="btn btn-ghost btn-small mt-16" @click="$router.push('/profile')">我的主页</button>
        </div>
        <div class="card">
          <h3>热搜榜</h3>
          <div v-for="(item, i) in hotTrends" :key="item.id" class="hot-item" @click="$router.push('/search?q=' + item.keyword)">
            <span class="hot-rank">{{ i + 1 }}</span>
            <span>{{ item.keyword }}</span>
            <span class="text-secondary" style="margin-left:auto">{{ item.heat }}</span>
          </div>
          <div v-if="hotTrends.length === 0" class="text-secondary">暂无热搜</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getBlogs, publishBlog } from '../api/blog'
import { getHotTrends } from '../api/search'
import BlogCard from '../components/BlogCard.vue'

const userStore = useUserStore()
const blogs = ref([])
const hotTrends = ref([])
const newPost = ref('')
const page = ref(1)
const hasMore = ref(true)

async function loadBlogs() {
  try {
    const res = await getBlogs({ page: page.value, size: 10 })
    if (res.success) {
      if (page.value === 1) blogs.value = res.data
      else blogs.value.push(...res.data)
      hasMore.value = res.data.length === 10
    }
  } catch (e) { console.error(e) }
}

async function loadHot() {
  try {
    const res = await getHotTrends({ limit: 10 })
    if (res.success) hotTrends.value = res.data
  } catch (e) { console.error(e) }
}

async function publish() {
  if (!newPost.value.trim()) return
  try {
    const res = await publishBlog({ content: newPost.value })
    if (res.success) {
      newPost.value = ''
      page.value = 1
      loadBlogs()
    }
  } catch (e) { alert(e.error || '发布失败') }
}

function loadMore() {
  page.value++
  loadBlogs()
}

onMounted(() => {
  loadBlogs()
  loadHot()
})
</script>

<style scoped>
.home-layout { display: grid; grid-template-columns: 1fr 300px; gap: 16px; margin-top: 16px; }
.feed { min-width: 0; }
.sidebar { position: sticky; top: 76px; align-self: start; }
.hot-item { display: flex; align-items: center; gap: 8px; padding: 8px 0; cursor: pointer; }
.hot-item:hover { color: var(--primary); }
.hot-rank { font-weight: bold; width: 20px; color: var(--primary); }
@media (max-width: 768px) { .home-layout { grid-template-columns: 1fr; } .sidebar { display: none; } }
</style>
