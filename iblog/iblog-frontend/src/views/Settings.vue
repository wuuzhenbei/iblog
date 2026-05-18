<template>
  <div class="container">
    <div class="card">
      <h2 class="mb-16">个人设置</h2>
      <div class="form-group">
        <label>昵称</label>
        <input class="input" v-model="profile.nickname" />
      </div>
      <div class="form-group">
        <label>性别</label>
        <select class="input" v-model="profile.gender">
          <option>男</option><option>女</option><option>保密</option>
        </select>
      </div>
      <div class="form-group">
        <label>生日</label>
        <input class="input" type="date" v-model="profile.birthday" />
      </div>
      <div class="form-group">
        <label>地区</label>
        <input class="input" v-model="profile.region" />
      </div>
      <div class="form-group">
        <label>个性签名</label>
        <input class="input" v-model="profile.signature" />
      </div>
      <div class="form-group">
        <label>兴趣爱好</label>
        <input class="input" v-model="profile.interests" />
      </div>
      <div class="form-group">
        <label>座右铭</label>
        <input class="input" v-model="profile.motto" />
      </div>
      <button class="btn btn-primary" @click="save">保存</button>
    </div>

    <div class="card">
      <h3 class="mb-16">隐私设置</h3>
      <div class="form-group">
        <label><input type="checkbox" v-model="privacy.hideOnline" /> 隐藏在线状态</label>
      </div>
      <div class="form-group">
        <label>访客权限</label>
        <select class="input" v-model="privacy.visitPermission">
          <option value="all">所有人</option><option value="friends">仅好友</option><option value="none">不允许</option>
        </select>
      </div>
      <div class="form-group">
        <label>动态范围</label>
        <select class="input" v-model="privacy.dynamicScope">
          <option value="all">所有人</option><option value="friends">仅好友</option><option value="self">仅自己</option>
        </select>
      </div>
      <button class="btn btn-primary" @click="savePrivacy">保存隐私设置</button>
    </div>

    <div class="card">
      <h3 class="mb-16">主题</h3>
      <button class="btn btn-ghost" @click="themeStore.toggle()">
        当前：{{ themeStore.theme === 'light' ? '浅色' : '深色' }}，点击切换
      </button>
    </div>

    <div class="card">
      <button class="btn btn-danger" @click="handleLogout">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useThemeStore } from '../stores/theme'
import api from '../api/index'
import { logout } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const profile = ref({ nickname: '', gender: '保密', birthday: '', region: '', signature: '', interests: '', motto: '' })
const privacy = ref({ hideOnline: false, visitPermission: 'all', dynamicScope: 'all' })

onMounted(async () => {
  try {
    const res = await api.get('/user/profile')
    if (res.success) Object.assign(profile.value, res.data)
  } catch (e) { console.error(e) }
  try {
    const res = await api.get('/user/privacy')
    if (res.success) Object.assign(privacy.value, res.data)
  } catch (e) { console.error(e) }
})

async function save() {
  try {
    await api.put('/user/profile', profile.value)
    alert('保存成功')
  } catch (e) { alert(e.error || '保存失败') }
}

async function savePrivacy() {
  try {
    await api.put('/user/privacy', privacy.value)
    alert('保存成功')
  } catch (e) { alert(e.error || '保存失败') }
}

async function handleLogout() {
  await logout()
  userStore.logout()
  router.push('/login')
}
</script>
