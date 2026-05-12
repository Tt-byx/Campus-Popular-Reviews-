import request from './request'

export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/create', data)
export const getProfile = () => request.get('/user/profile')
export const updateProfile = (data) => request.post('/user/profile/update', data)
export const uploadAvatar = (formData) => request.post('/user/profile/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
export const deleteAccount = () => request.delete('/user/account')
export const getUserById = (id) => request.get(`/user/${id}`)
export const getUserPosts = (id) => request.get(`/user/${id}/posts`)
export const getUserStats = (id) => request.get(`/user/${id}/stats`)
