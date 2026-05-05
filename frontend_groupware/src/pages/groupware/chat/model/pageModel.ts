import type { GroupwareChatMessage, GroupwareChatRoom } from '@/types/groupware'

export const messageColumns = [
  { field: 'author', title: 'Author' },
  { field: 'content', title: 'Message' },
  { field: 'createdAt', title: 'Created At' },
]

export const roomColumns = [
  { field: 'roomId', title: 'Room ID' },
  { field: 'roomType', title: 'Type' },
  { field: 'roomName', title: 'Room Name' },
  { field: 'createdByUserId', title: 'Owner' },
]

export const toMessageRows = (messages: GroupwareChatMessage[]) =>
  messages.map((message) => ({
    messageId: message.messageId,
    author: message.createdByUserId,
    content: message.content,
    createdAt: message.createdAt,
  }))

export const toRoomRows = (rooms: GroupwareChatRoom[]) =>
  rooms.map((room) => ({
    roomId: room.roomId,
    roomType: room.roomType,
    roomName: room.roomName || '-',
    createdByUserId: room.createdByUserId,
  }))
