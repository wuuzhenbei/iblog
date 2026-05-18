<template>
  <div class="container">
    <div class="flex-between mb-16">
      <h2>圈子</h2>
      <button class="btn btn-primary" @click="showCreate = true">创建圈子</button>
    </div>
    <div class="circle-grid">
      <div v-for="c in circles" :key="c.id" class="card circle-card" @click="$router.push('/circle/' + c.id)">
        <h3>{{ c.name }}</h3>
        <p class="text-secondary">{{ c.description?.substring(0, 50) }}</p>
        <div class="flex-between mt-16">
          <span class="text-secondary">{{ c.category }}</span>
          <span class="text-secondary">{{ c.memberCount }} 成员</span>
        </div>
      </div>
    </div>

    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="card" style="width:400px">
        <h3 class="mb-16">创建圈子</h3>
        <div class="form-group">
          <label>名称</label>
          <input class="input" v-model="newCircle.name" />
        </div>
        <div class="form-group">
          <label>分类</label>
          <select class="input" v-model="newCircle.category">
            <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>简介</label>
          <textarea class="input" v-model="newCircle.description"></textarea>
        </div>
        <div class="flex gap-8">
          <button class="btn btn-primary" @click="create">创建</button>
          <button class="btn btn-ghost" @click="showCreate = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCircles, createCircle } from '../api/circle'

const circles = ref([])
const showCreate = ref(false)
const categories = ['娱乐', '情感', '游戏', '美食', '旅行', '职场', '其他']
const newCircle = ref({ name: '', category: '其他', description: '' })

onMounted(async () => {
  const res = await getCircles({ page: 1, size: 50 })
  if (res.success) circles.value = res.data
})

async function create() {
  if (!newCircle.value.name.trim()) { alert('请输入名称'); return }
  const res = await createCircle(newCircle.value)
  if (res.success) {
    showCreate.value = false
    const r = await getCircles({ page: 1, size: 50 })
    if (r.success) circles.value = r.data
  }
}
</script>

<style scoped>
.circle-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.circle-card { cursor: pointer; transition: transform 0.2s; }
.circle-card:hover { transform: translateY(-2px); }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
</style>
