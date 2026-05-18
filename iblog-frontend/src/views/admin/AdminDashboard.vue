<template>
  <div>
    <h2 class="mb-16">数据概览</h2>
    <div class="stats-grid">
      <div class="card stat-card" v-for="(val, key) in stats" :key="key">
        <h3>{{ val }}</h3>
        <p class="text-secondary">{{ labels[key] || key }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStats } from '../../api/admin'

const stats = ref({})
const labels = {
  totalUsers: '总用户数', todayNewUsers: '今日新增用户',
  totalBlogs: '总博文数', todayNewBlogs: '今日新博文',
  totalComments: '总评论数', totalCircles: '圈子总数', pendingReports: '待处理举报'
}

onMounted(async () => {
  const res = await getStats()
  if (res.success) stats.value = res.data
})
</script>

<style scoped>
.stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.stat-card { text-align: center; }
.stat-card h3 { font-size: 28px; color: var(--primary); }
</style>
