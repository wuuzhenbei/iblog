import api from './index'

export const generateCopywriting = (data) => api.post('/ai/copywriting', data)
export const polishText = (data) => api.post('/ai/polish', data)
export const generateComment = (data) => api.post('/ai/comment', data)
export const analyzeEmotion = (data) => api.post('/ai/emotion', data)
