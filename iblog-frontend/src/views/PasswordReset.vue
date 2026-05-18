<template>
  <div class="container flex-center" style="min-height:100vh">
    <div class="card" style="width:400px">
      <h2 style="text-align:center;margin-bottom:24px">找回密码</h2>
      <div class="form-group">
        <label>账号</label>
        <input class="input" v-model="form.account" placeholder="用户名或手机号" />
      </div>
      <div class="form-group">
        <label>手机号</label>
        <input class="input" v-model="form.phone" placeholder="注册时的手机号" />
      </div>
      <div class="form-group">
        <label>新密码</label>
        <input class="input" type="password" v-model="form.newPassword" placeholder="至少6位" />
      </div>
      <button class="btn btn-primary" style="width:100%" @click="handleReset">重置密码</button>
      <p class="text-secondary mt-16" style="text-align:center">
        <router-link to="/login">返回登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { resetPassword } from '../api/auth'

const router = useRouter()
const form = reactive({ account: '', phone: '', newPassword: '' })

async function handleReset() {
  if (!form.account || !form.phone || !form.newPassword) { alert('请填写完整信息'); return }
  try {
    const res = await resetPassword(form)
    if (res.success) {
      alert('密码重置成功')
      router.push('/login')
    }
  } catch (e) { alert(e.error || '重置失败') }
}
</script>
