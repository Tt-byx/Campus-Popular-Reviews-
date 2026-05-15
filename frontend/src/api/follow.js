import request from './request'

export const toggleFollow = (userId) => request.post(`/follow/${userId}`)
export const checkFollow = (userId) => request.get(`/follow/check/${userId}`)
export const getFollowStats = (userId) => request.get(`/follow/stats/${userId}`)
export const getFollowers = (userId, page = 1, size = 20) => request.get(`/follow/followers/${userId}`, { params: { page, size } })
export const getFollowing = (userId, page = 1, size = 20) => request.get(`/follow/following/${userId}`, { params: { page, size } })
