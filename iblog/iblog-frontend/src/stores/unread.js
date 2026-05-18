import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUnreadStore = defineStore('unread', () => {
  const count = ref(0)

  function set(val) { count.value = val }
  function clear() { count.value = 0 }

  return { count, set, clear }
})
