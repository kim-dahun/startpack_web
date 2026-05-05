import type { GroupwareRealtimeEvent } from '@/types/groupware'

const buildWebSocketUrl = () => {
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  return `${protocol}//${window.location.host}/ws/groupware`
}

const encodeFrame = (command: string, headers: Record<string, string>, body = '') => {
  const headerLines = Object.entries(headers).map(([key, value]) => `${key}:${value}`)
  return `${command}\n${headerLines.join('\n')}\n\n${body}\u0000`
}

const decodeFrames = (raw: string) =>
  raw
    .split('\u0000')
    .map((frame) => frame.trim())
    .filter(Boolean)
    .map((frame) => {
      const [headerBlock, ...bodyBlocks] = frame.split('\n\n')
      const [command, ...headerLines] = headerBlock.split('\n')
      const headers = headerLines.reduce<Record<string, string>>((accumulator, line) => {
        const separatorIndex = line.indexOf(':')
        if (separatorIndex > -1) {
          const key = line.slice(0, separatorIndex)
          const value = line.slice(separatorIndex + 1)
          accumulator[key] = value
        }
        return accumulator
      }, {})

      return {
        command,
        headers,
        body: bodyBlocks.join('\n\n'),
      }
    })

export const createGroupwareRealtimeSocket = () => new WebSocket(buildWebSocketUrl())

export const createConnectFrame = (token: string | null, userId: string, comCd: string) =>
  encodeFrame('CONNECT', {
    'accept-version': '1.2',
    'heart-beat': '10000,10000',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    'x-user-id': userId,
    'x-com-cd': comCd,
  })

export const createSubscribeFrame = (id: string, destination: string) =>
  encodeFrame('SUBSCRIBE', {
    id,
    destination,
  })

export const createDisconnectFrame = () => encodeFrame('DISCONNECT', {})

export const parseRealtimeFrames = (raw: string) => decodeFrames(raw)

export const parseRealtimeEvent = (body: string): GroupwareRealtimeEvent | null => {
  try {
    return JSON.parse(body) as GroupwareRealtimeEvent
  } catch {
    return null
  }
}
