<template>
  <div class="container">
    <div class="card">
      <div class="flex gap-8">
        <input class="input" v-model="keyword" @keyup.enter="doSearch" placeholder="搜索..." />
        <button class="btn btn-primary" @click="doSearch">搜索</button>
      </div>
      <div class="flex gap-8 mt-16">
        <button :class="['btn btn-small', type === 'all' ? 'btn-primary' : 'btn-ghost']" @click="type = 'all'; doSearch()">全部</button>
        <button :class="['btn btn-small', type === 'blog' ? 'btn-primary' : 'btn-ghost']" @click="type = 'blog'; doSearch()">博文</button>
        <button :class="['btn btn-small', type === 'user' ? 'btn-primary' : 'btn-ghost']" @click="type = 'user'; doSearch()">用户</button>
      </div>
    </div>
    <div v-for="blog in results.blogs" :key="blog.id" class="card">
      <BlogCard :blog="blog" />
    </div>
    <div v-for="u in results.users" :key="u.id" class="card" @click="$router.push('/user/' + u.id)" style="cursor:pointer">
      <div class="flex gap-8">
        <div class="avatar">{{ u.nickname?.[0] || '?' }}</div>
        <div>
          <strong>{{ u.nickname || u.username }}</strong>
          <p class="text-secondary">{{ u.signature }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { search } from '../api/search'
import BlogCard from '../components/BlogCard.vue'

const route = useRoute()
const keyword = ref(route.query.q || '')
const type = ref('all')
const results = ref({ blogs: [], users: [] })

async function doSearch() {
  if (!keyword.value.trim()) return
  const res = await search({ q: keyword.value, type: type.value })
  if (res.success) results.value = res.data
}

onMounted(() => { if (keyword.value) doSearch() })
</script>
