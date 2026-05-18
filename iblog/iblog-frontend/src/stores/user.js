import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(null)
  const isLoggedIn = computed(() => !!user.value)

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('iblog_user', JSON.stringify(userData))
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('iblog_user')
  }

  function initFromStorage() {
    const saved = localStorage.getItem('iblog_user')
    if (saved) {
      try { user.value = JSON.parse(saved) } catch (e) { localStorage.removeItem('iblog_user') }
    }
  }

  return { user, token, isLoggedIn, setUser, logout, initFromStorage }
})
