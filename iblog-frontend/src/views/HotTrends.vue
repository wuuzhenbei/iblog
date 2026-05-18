<template>
  <div class="container">
    <h2 class="mb-16">热搜榜</h2>
    <div class="card">
      <div v-for="(item, i) in trends" :key="item.id" class="trend-item" @click="$router.push('/search?q=' + item.keyword)">
        <span class="trend-rank" :class="{ top3: i < 3 }">{{ i + 1 }}</span>
        <span class="trend-keyword">{{ item.keyword }}</span>
        <span class="text-secondary" style="margin-left:auto">热度 {{ item.heat }}</span>
      </div>
      <div v-if="trends.length === 0" class="text-secondary" style="text-align:center;padding:20px">暂无热搜</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotTrends } from '../api/search'

const trends = ref([])

onMounted(async () => {
  const res = await getHotTrends({ limit: 30 })
  if (res.success) trends.value = res.data
})
</script>

<style scoped>
.trend-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--border); cursor: pointer; }
.trend-item:hover { color: var(--primary); }
.trend-rank { font-weight: bold; width: 24px; text-align: center; }
.trend-rank.top3 { color: #f56c6c; }
</style>
