<template>
  <div class="container">
    <h2 class="mb-16">私信</h2>
    <div v-for="conv in conversations" :key="conv.id" class="card conv-item" @click="$router.push('/chat/' + getOtherId(conv))">
      <div class="flex gap-8">
        <div class="avatar">{{ getOtherName(conv)?.[0] || '?' }}</div>
        <div>
          <strong>{{ getOtherName(conv) }}</strong>
          <p class="text-secondary">{{ conv.content }}</p>
        </div>
        <span class="text-secondary" style="margin-left:auto">{{ formatTime(conv.createdAt) }}</span>
      </div>
    </div>
    <div v-if="conversations.length === 0" class="card text-secondary" style="text-align:center">暂无私信</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getConversations } from '../api/chat'

const userStore = useUserStore()
const conversations = ref([])

function getOtherId(conv) {
  return conv.senderId === userStore.user?.id ? conv.receiverId : conv.senderId
}
function getOtherName(conv) {
  return conv.senderId === userStore.user?.id ? conv.receiverNickname : conv.senderNickname
}
function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }

onMounted(async () => {
  const res = await getConversations()
  if (res.success) conversations.value = res.data
})
</script>

<style scoped>
.conv-item { cursor: pointer; }
.conv-item:hover { background: var(--bg); }
.avatar { width: 40px; height: 40px; border-radius: 50%; background: var(--primary); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: bold; }
</style>
