import { managementDefinitions } from '@/data/mockData'

export const pageResourceKey = 'users' as const
export const pageDefinition = managementDefinitions[pageResourceKey]
export const pageGuideItems = [
  `${pageDefinition.title} 화면 전용 Main/api/model 조합으로 동작합니다.`,
  '검색 조건은 SearchView에서 받고 Main.vue가 API 호출과 저장 컨텍스트를 결합합니다.',
  pageDefinition.serviceScoped ? 'serviceId 변경 시 해당 서비스 기준으로 목록을 다시 조회합니다.' : '서비스 공통 마스터 데이터로 조회와 저장을 처리합니다.',
]
