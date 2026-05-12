import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  const signature = ref(localStorage.getItem('signature') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin' || role.value === 'super_admin')

  function setLoginData(data) {
    token.value = data.token
    username.value = data.username
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
  }

  function setProfile(data) {
    userId.value = data.id
    username.value = data.username
    avatar.value = data.avatar || ''
    signature.value = data.signature || ''
    role.value = data.role || role.value
    localStorage.setItem('userId', data.id)
    localStorage.setItem('username', data.username)
    localStorage.setItem('avatar', data.avatar || '')
    localStorage.setItem('signature', data.signature || '')
    localStorage.setItem('role', data.role || role.value)
  }

  function logout() {
    token.value = ''
    username.value = ''
    role.value = ''
    userId.value = ''
    avatar.value = ''
    signature.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    localStorage.removeItem('avatar')
    localStorage.removeItem('signature')
  }

  return { token, username, role, userId, avatar, signature, isLoggedIn, isAdmin, setLoginData, setProfile, logout }
})
