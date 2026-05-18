import api from './index'

export const getStats = () => api.get('/admin/stats')
export const getUsers = (params) => api.get('/admin/users', { params })
export const manageUser = (data) => api.put('/admin/users', data)
export const deleteBlog = (data) => api.delete('/admin/content', { data })
export const getHotList = (params) => api.get('/admin/hot', { params })
export const addHot = (data) => api.post('/admin/hot', data)
export const deleteHot = (data) => api.delete('/admin/hot', { data })
export const getAiStatus = () => api.get('/admin/ai')
export const toggleAi = (data) => api.put('/admin/ai', data)
