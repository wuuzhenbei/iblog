<template>
  <div class="container">
    <div class="card">
      <h2 class="mb-16">{{ isEdit ? '编辑博文' : '发布博文' }}</h2>
      <div class="form-group">
        <textarea class="input" v-model="form.content" placeholder="写点什么..." style="min-height:200px"></textarea>
      </div>
      <div class="form-group">
        <label>心情标签</label>
        <input class="input" v-model="form.moodTag" placeholder="如：开心、伤感" />
      </div>
      <div class="form-group">
        <label>场景标签</label>
        <input class="input" v-model="form.sceneTag" placeholder="如：旅行、美食" />
      </div>
      <div class="form-group">
        <label>位置</label>
        <input class="input" v-model="form.location" placeholder="所在位置" />
      </div>
      <div class="form-group">
        <label>可见范围</label>
        <select class="input" v-model="form.visibility">
          <option value="public">公开</option>
          <option value="followers">仅粉丝可见</option>
          <option value="friends">仅好友可见</option>
          <option value="private">仅自己可见</option>
        </select>
      </div>
      <div class="flex gap-8">
        <button class="btn btn-primary" @click="publish">发布</button>
        <button class="btn btn-ghost" @click="saveDraft">存草稿</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { publishBlog, editBlog, getBlogDetail, saveDraft as saveDraftApi } from '../api/blog'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const form = ref({ content: '', moodTag: '', sceneTag: '', location: '', visibility: 'public' })

onMounted(async () => {
  if (isEdit.value) {
    const res = await getBlogDetail(route.params.id)
    if (res.success) {
      const b = res.data.blog
      form.value = { content: b.content, moodTag: b.moodTag || '', sceneTag: b.sceneTag || '', location: b.location || '', visibility: b.visibility }
    }
  }
})

async function publish() {
  if (!form.value.content.trim()) { alert('内容不能为空'); return }
  try {
    if (isEdit.value) {
      await editBlog({ id: route.params.id, ...form.value })
    } else {
      await publishBlog(form.value)
    }
    router.push('/')
  } catch (e) { alert(e.error || '操作失败') }
}

async function saveDraft() {
  if (!form.value.content.trim()) { alert('内容不能为空'); return }
  try {
    await saveDraftApi(form.value)
    alert('已保存到草稿箱')
    router.push('/drafts')
  } catch (e) { alert(e.error || '保存失败') }
}
</script>
