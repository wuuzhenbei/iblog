<template>
  <div class="container">
    <div class="card chat-window">
      <div class="chat-header">
        <button class="btn btn-ghost btn-small" @click="$router.push('/messages')">返回</button>
        <strong>与 {{ targetName }} 的对话</strong>
      </div>
      <div class="chat-messages" ref="msgContainer">
        <div v-for="msg in messages" :key="msg.id" :class="['msg', msg.senderId === userId ? 'msg-self' : 'msg-other']">
          <p>{{ msg.content }}</p>
          <span class="text-secondary">{{ formatTime(msg.createdAt) }}</span>
        </div>
      </div>
      <div class="chat-input">
        <input class="input" v-model="newMsg" @keyup.enter="send" placeholder="输入消息..." />
        <button class="btn btn-primary" @click="send">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getMessages, sendMessage } from '../api/chat'

const route = useRoute()
const userStore = useUserStore()
const userId = computed(() => userStore.user?.id)
const targetId = computed(() => route.params.userId)
const targetName = ref('')
const messages = ref([])
const newMsg = ref('')
const msgContainer = ref(null)

function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }

async function load() {
  const res = await getMessages({ targetId: targetId.value, page: 1, size: 100 })
  if (res.success) {
    messages.value = res.data.reverse()
    if (res.data.length > 0) {
      const other = res.data.find(m => m.senderId !== userId.value)
      if (other) targetName.value = other.senderNickname || '用户'
    }
    await nextTick()
    if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

async function send() {
  if (!newMsg.value.trim()) return
  await sendMessage({ receiverId: Number(targetId.value), content: newMsg.value })
  newMsg.value = ''
  load()
}

onMounted(load)
</script>

<style scoped>
.chat-window { display: flex; flex-direction: column; height: calc(100vh - 100px); }
.chat-header { padding: 12px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 12px; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; }
.msg { margin-bottom: 12px; max-width: 70%; }
.msg-self { margin-left: auto; text-align: right; }
.msg-self p { background: var(--primary); color: #fff; display: inline-block; padding: 8px 12px; border-radius: 12px 12px 0 12px; }
.msg-other p { background: var(--bg); display: inline-block; padding: 8px 12px; border-radius: 12px 12px 12px 0; }
.chat-input { display: flex; gap: 8px; padding: 12px; border-top: 1px solid var(--border); }
</style>
