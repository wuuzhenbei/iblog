<template>
  <div class="container">
    <h2 class="mb-16">草稿箱</h2>
    <div v-for="d in drafts" :key="d.id" class="card">
      <p>{{ d.content?.substring(0, 100) }}...</p>
      <div class="flex-between mt-16">
        <span class="text-secondary">{{ new Date(d.updatedAt).toLocaleString('zh-CN') }}</span>
        <div class="flex gap-8">
          <button class="btn btn-primary btn-small" @click="$router.push('/blog/edit/' + d.id)">编辑</button>
          <button class="btn btn-danger btn-small" @click="remove(d.id)">删除</button>
        </div>
      </div>
    </div>
    <div v-if="drafts.length === 0" class="card text-secondary" style="text-align:center">暂无草稿</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDrafts, deleteBlog } from '../api/blog'

const drafts = ref([])

onMounted(async () => {
  const res = await getDrafts()
  if (res.success) drafts.value = res.data
})

async function remove(id) {
  if (!confirm('确定删除？')) return
  await deleteBlog(id)
  drafts.value = drafts.value.filter(d => d.id !== id)
}
</script>
