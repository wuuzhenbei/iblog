<template>
  <div>
    <h2 class="mb-16">用户管理</h2>
    <div class="card">
      <table class="admin-table">
        <thead><tr><th>ID</th><th>用户名</th><th>角色</th><th>状态</th><th>注册时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.username }}</td>
            <td>{{ u.role }}</td>
            <td>{{ u.status }}</td>
            <td>{{ new Date(u.createdAt).toLocaleDateString('zh-CN') }}</td>
            <td>
              <button v-if="u.status === 'active'" class="btn btn-ghost btn-small" @click="freeze(u.id)">冻结</button>
              <button v-if="u.status === 'frozen'" class="btn btn-primary btn-small" @click="activate(u.id)">激活</button>
              <button class="btn btn-danger btn-small" @click="remove(u.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, manageUser } from '../../api/admin'

const users = ref([])

onMounted(async () => {
  const res = await getUsers({ page: 1, size: 50 })
  if (res.success) users.value = res.data
})

async function freeze(id) { await manageUser({ id, action: 'freeze' }); location.reload() }
async function activate(id) { await manageUser({ id, action: 'activate' }); location.reload() }
async function remove(id) { if (confirm('确定删除？')) { await manageUser({ id, action: 'delete' }); location.reload() } }
</script>

<style scoped>
.admin-table { width: 100%; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 10px; text-align: left; border-bottom: 1px solid var(--border); }
.admin-table th { font-weight: bold; }
</style>
