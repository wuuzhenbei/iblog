import api from './index'

export const followUser = (data) => api.post('/social/follow', data)
export const getFollowList = (params) => api.get('/social/list', { params })
export const getVisitors = () => api.get('/social/visitors')
