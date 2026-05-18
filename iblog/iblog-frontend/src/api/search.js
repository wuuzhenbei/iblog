import api from './index'

export const search = (params) => api.get('/search', { params })
export const getHotTrends = (params) => api.get('/hot/trends', { params })
