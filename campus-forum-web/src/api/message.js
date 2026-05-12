import request from './request'

export const sendMessage = (receiverId, content) => request.post('/message/send', { receiverId, content })
export const getConversations = () => request.get('/message/conversations')
export const getConversation = (userId, page = 1, size = 50) => request.get(`/message/conversation/${userId}`, { params: { page, size } })
export const markAsRead = (userId) => request.put(`/message/read/${userId}`)
export const getUnreadCount = () => request.get('/message/unread-count')
