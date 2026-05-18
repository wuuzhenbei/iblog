<template>
  <div class="container flex-center" style="min-height:100vh">
    <div class="card" style="width:400px">
      <h2 style="text-align:center;margin-bottom:24px">登录 iBlog</h2>
      <div class="form-group">
        <label>账号</label>
        <input class="input" v-model="account" placeholder="用户名或手机号" />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input class="input" type="password" v-model="password" placeholder="密码" />
      </div>
      <div class="flex-between mb-16">
        <label><input type="checkbox" v-model="remember" /> 记住登录</label>
        <router-link to="/password-reset">忘记密码？</router-link>
      </div>
      <button class="btn btn-primary" style="width:100%" @click="handleLogin">登录</button>
      <p class="text-secondary mt-16" style="text-align:center">
        没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { login } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()
const account = ref('')
const password = ref('')
const remember = ref(false)

async function handleLogin() {
  if (!account.value || !password.value) { alert('请输入账号和密码'); return }
  try {
    const res = await login({ account: account.value, password: password.value, remember: String(remember.value) })
    if (res.success) {
      userStore.setUser(res.user)
      router.push('/')
    }
  } catch (e) { alert(e.error || '登录失败') }
}
</script>
