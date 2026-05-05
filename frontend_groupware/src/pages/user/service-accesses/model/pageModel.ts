import { managementDefinitions } from '@/data/mockData'

export const pageResourceKey = 'serviceAccesses' as const
export const pageDefinition = managementDefinitions[pageResourceKey]
export const pageGuideItems = [
  `${pageDefinition.title} 화면 전용 Main/api/model 조합으로 동작합니다.`,
  '로그인 사용자와 조회 대상 userId를 분리해 SearchView에서 제어합니다.',
  'Main.vue가 service access 조회와 저장 payload를 결합해 화면 전용 흐름으로 처리합니다.',
]
