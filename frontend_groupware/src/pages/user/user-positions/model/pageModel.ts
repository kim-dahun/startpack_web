import { managementDefinitions } from '@/data/mockData'

export const pageResourceKey = 'userPositions' as const
export const pageDefinition = managementDefinitions[pageResourceKey]
export const pageGuideItems = [
  `${pageDefinition.title} 화면 전용 Main/api/model 조합으로 동작합니다.`,
  '검색 조건은 SearchView에서 받고 Main.vue가 API 호출과 저장 컨텍스트를 결합합니다.',
  pageDefinition.requiresUserId ? 'userId 변경 시 현재 사용자 기준으로 목록을 다시 조회하고 저장 컨텍스트에도 반영합니다.' : '공통 컨텍스트로 조회와 저장을 처리합니다.',
]
