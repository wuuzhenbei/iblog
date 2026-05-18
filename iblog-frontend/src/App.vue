<template>
  <div :class="['app', themeStore.theme]">
    <NavBar v-if="userStore.isLoggedIn" />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useUserStore } from './stores/user'
import { useThemeStore } from './stores/theme'
import NavBar from './components/NavBar.vue'

const userStore = useUserStore()
const themeStore = useThemeStore()

userStore.initFromStorage()
</script>
