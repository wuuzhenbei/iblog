import api from './index'

export const getConversations = () => api.get('/messages/conversations')
export const getMessages = (params) => api.get('/messages', { params })
export const sendMessage = (data) => api.post('/messages', data)
export const recallMessage = (id) => api.delete('/messages', { params: { id } })
