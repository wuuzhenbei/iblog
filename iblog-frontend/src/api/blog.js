import api from './index'

export const getBlogs = (params) => api.get('/blogs/list', { params })
export const getBlogDetail = (id) => api.get('/blogs/detail', { params: { id } })
export const publishBlog = (data) => api.post('/blogs', data)
export const editBlog = (data) => api.put('/blogs/edit', data)
export const deleteBlog = (id) => api.delete('/blogs/edit', { params: { id } })
export const interactBlog = (data) => api.post('/blogs/interact', data)
export const getDrafts = () => api.get('/blogs/drafts')
export const saveDraft = (data) => api.post('/blogs/drafts', data)
export const getComments = (blogId) => api.get('/blogs/detail', { params: { id: blogId } })
export const postComment = (data) => api.post('/comments', data)
export const deleteComment = (id) => api.delete('/comments', { params: { id } })
