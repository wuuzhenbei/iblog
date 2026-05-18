<template>
  <div class="container flex-center" style="min-height:100vh">
    <div class="card" style="width:400px">
      <h2 style="text-align:center;margin-bottom:24px">注册 iBlog</h2>
      <div class="form-group">
        <label>用户名</label>
        <input class="input" v-model="form.username" placeholder="3-50个字符" />
      </div>
      <div class="form-group">
        <label>手机号</label>
        <input class="input" v-model="form.phone" placeholder="手机号（选填）" />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input class="input" type="password" v-model="form.password" placeholder="至少6位" />
      </div>
      <div class="form-group">
        <label>确认密码</label>
        <input class="input" type="password" v-model="form.confirmPassword" placeholder="再次输入密码" />
      </div>
      <button class="btn btn-primary" style="width:100%" @click="handleRegister">注册</button>
      <p class="text-secondary mt-16" style="text-align:center">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()
const form = reactive({ username: '', phone: '', password: '', confirmPassword: '' })

async function handleRegister() {
  if (!form.username || !form.password) { alert('请填写用户名和密码'); return }
  if (form.password !== form.confirmPassword) { alert('两次密码不一致'); return }
  if (form.password.length < 6) { alert('密码至少6位'); return }
  try {
    const res = await register({ username: form.username, phone: form.phone, password: form.password })
    if (res.success) {
      alert('注册成功，请登录')
      router.push('/login')
    }
  } catch (e) { alert(e.error || '注册失败') }
}
</script>
