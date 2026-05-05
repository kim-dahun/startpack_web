import { managementDefinitions } from '@/data/mockData'

export const pageResourceKey = 'codeGroups' as const
export const pageDefinition = managementDefinitions[pageResourceKey]
export const pageGuideItems = [
  `${pageDefinition.title} 화면 전용 Main/api/model 조합으로 동작합니다.`,
  '로그인 응답 groups에서 ADMIN이 있는 서비스만 serviceId 전환과 수정이 가능합니다.',
  'Main.vue가 serviceId를 검색 조건과 결합해 코드그룹 목록을 다시 조회합니다.',
]
