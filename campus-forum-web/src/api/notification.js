import request from './request'

export const getNotifications = (page = 1, size = 20) => request.get('/notification/list', { params: { page, size } })
export const markNotificationRead = (id) => request.put(`/notification/read/${id}`)
export const markAllRead = () => request.put('/notification/read-all')
export const getUnreadCount = () => request.get('/notification/unread-count')
