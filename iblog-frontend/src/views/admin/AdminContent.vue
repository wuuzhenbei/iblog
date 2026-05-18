<template>
  <div>
    <h2 class="mb-16">内容管理</h2>
    <p class="text-secondary mb-16">博文审核和下架功能</p>
    <div class="card">
      <p>输入博文 ID 进行下架：</p>
      <div class="flex gap-8 mt-16">
        <input class="input" v-model="blogId" placeholder="博文 ID" style="width:200px" />
        <button class="btn btn-danger" @click="removeBlog">下架</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { deleteBlog } from '../../api/admin'

const blogId = ref('')

async function removeBlog() {
  if (!blogId.value) return
  if (!confirm('确定下架该博文？')) return
  await deleteBlog({ id: Number(blogId.value) })
  alert('已下架')
  blogId.value = ''
}
</script>
