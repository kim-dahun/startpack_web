const twoDigit = (value: number) => value.toString().padStart(2, '0')

export const formatDateTime = (input: Date | string | number) => {
  const date = input instanceof Date ? input : new Date(input)

  return [
    date.getFullYear(),
    twoDigit(date.getMonth() + 1),
    twoDigit(date.getDate()),
  ].join('-')
    + ` ${twoDigit(date.getHours())}:${twoDigit(date.getMinutes())}:${twoDigit(date.getSeconds())}`
}

export const formatDate = (input: Date | string | number) => {
  const date = input instanceof Date ? input : new Date(input)

  return [
    date.getFullYear(),
    twoDigit(date.getMonth() + 1),
    twoDigit(date.getDate()),
  ].join('-')
}

export const formatCurrency = (value: number, currency = 'KRW') =>
  new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency,
    maximumFractionDigits: currency === 'KRW' ? 0 : 2,
  }).format(value)

export const formatPercent = (value: number) => `${value > 0 ? '+' : ''}${value.toFixed(2)}%`
