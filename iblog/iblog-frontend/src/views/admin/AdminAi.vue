<template>
  <div>
    <h2 class="mb-16">AI 管控</h2>
    <div class="card">
      <h3>AI 功能开关</h3>
      <div class="flex gap-16 mt-16">
        <button :class="['btn', enabled ? 'btn-primary' : 'btn-ghost']" @click="toggle(true)">开启</button>
        <button :class="['btn', !enabled ? 'btn-danger' : 'btn-ghost']" @click="toggle(false)">关闭</button>
      </div>
      <p class="text-secondary mt-16">当前状态：{{ enabled ? '已开启' : '已关闭' }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAiStatus, toggleAi } from '../../api/admin'

const enabled = ref(true)

onMounted(async () => {
  const res = await getAiStatus()
  if (res.success) enabled.value = res.enabled
})

async function toggle(val) {
  await toggleAi({ enabled: val })
  enabled.value = val
}
</script>
