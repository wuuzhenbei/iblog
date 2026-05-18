import api from './index'

export const getCircles = (params) => api.get('/circles', { params })
export const createCircle = (data) => api.post('/circles/manage', data)
export const getCirclePosts = (params) => api.get('/circles/posts', { params })
export const joinCircle = (data) => api.post('/circles/members', data)
