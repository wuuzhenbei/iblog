<template>
  <nav class="navbar">
    <div class="container flex-between">
      <div class="flex gap-16">
        <router-link to="/" class="logo">iBlog</router-link>
        <router-link to="/">首页</router-link>
        <router-link to="/circles">圈子</router-link>
        <router-link to="/hot">热搜</router-link>
        <router-link to="/messages">私信</router-link>
        <router-link to="/ai">AI 工具</router-link>
      </div>
      <div class="flex gap-16">
        <div class="search-box">
          <input class="input" v-model="keyword" @keyup.enter="goSearch" placeholder="搜索..." style="height:32px;font-size:13px" />
        </div>
        <router-link to="/profile">{{ userStore.user?.username }}</router-link>
        <router-link v-if="userStore.user?.role === 'super_admin'" to="/admin">管理</router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

function goSearch() {
  if (keyword.value.trim()) router.push('/search?q=' + keyword.value)
}
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; height: 56px;
  background: var(--bg-card); border-bottom: 1px solid var(--border);
  display: flex; align-items: center; z-index: 50;
  box-shadow: var(--shadow);
}
.logo { font-size: 20px; font-weight: bold; color: var(--primary); }
a { color: var(--text); font-size: 14px; }
a:hover { color: var(--primary); text-decoration: none; }
.search-box { width: 200px; }
</style>
