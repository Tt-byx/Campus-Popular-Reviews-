import request from './request'

export const toggleLike = (targetType, targetId) => request.post('/like', { targetType, targetId })
export const getLikeStatus = (targetType, targetId) => request.get('/like/status', { params: { targetType, targetId } })
export const getLikeCount = (targetType, targetId) => request.get('/like/count', { params: { targetType, targetId } })
