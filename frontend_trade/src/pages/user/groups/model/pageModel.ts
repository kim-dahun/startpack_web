import { managementDefinitions } from '@/data/mockData'

export const pageResourceKey = 'groups' as const
export const pageDefinition = managementDefinitions[pageResourceKey]
export const pageGuideItems = [
  `${pageDefinition.title} 화면 전용 Main/api/model 조합으로 동작합니다.`,
  '로그인 응답 groups에서 serviceId별 ADMIN 권한을 확인해 수정 가능한 서비스 범위를 결정합니다.',
  'TRADE, ERP, GROUPWARE 중 ADMIN이 있는 서비스만 전환 가능하고 해당 서비스 데이터만 수정합니다.',
]
