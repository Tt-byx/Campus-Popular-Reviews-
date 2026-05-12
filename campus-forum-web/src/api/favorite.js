import request from './request'

export const toggleFavorite = (postId) => request.post(`/favorite/${postId}`)
export const checkFavorite = (postId) => request.get(`/favorite/check/${postId}`)
export const getFavorites = (page = 1, size = 10) => request.get('/favorite/list', { params: { page, size } })
