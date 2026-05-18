<template>
  <div class="container">
    <div class="card">
      <h2>{{ circle?.name }}</h2>
      <p class="text-secondary">{{ circle?.description }}</p>
      <div class="flex-between mt-16">
        <span>{{ circle?.memberCount }} 成员 | {{ circle?.category }}</span>
        <button class="btn btn-primary" @click="toggleJoin">
          {{ circle?.joined ? '退出圈子' : '加入圈子' }}
        </button>
      </div>
    </div>
    <h3 class="mb-16">圈子动态</h3>
    <div v-for="blog in posts" :key="blog.id" class="card">
      <BlogCard :blog="blog" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCircles, getCirclePosts, joinCircle } from '../api/circle'
import BlogCard from '../components/BlogCard.vue'

const route = useRoute()
const circle = ref(null)
const posts = ref([])

onMounted(async () => {
  const res = await getCircles({ page: 1, size: 1 })
  if (res.success) circle.value = res.data.find(c => String(c.id) === route.params.id)
  const pRes = await getCirclePosts({ circleId: route.params.id, page: 1, size: 20 })
  if (pRes.success) posts.value = pRes.data
})

async function toggleJoin() {
  const res = await joinCircle({ circleId: route.params.id, action: circle.value.joined ? 'leave' : 'join' })
  if (res.success) circle.value.joined = !circle.value.joined
}
</script>
