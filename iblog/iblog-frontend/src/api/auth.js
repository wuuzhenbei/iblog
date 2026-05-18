import api from './index'

export const login = (data) => api.post('/auth/login', data)
export const register = (data) => api.post('/auth/register', data)
export const logout = () => api.post('/auth/logout')
export const getCaptcha = () => api.get('/auth/captcha')
export const resetPassword = (data) => api.post('/auth/reset-password', data)
