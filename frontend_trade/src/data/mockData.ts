import type {
  AccountSummary,
  DailyBalance,
  MenuItem,
  MenuPermission,
  ResourceDefinition,
  RealtimeQuote,
  RealtimeStatus,
  TradeHistory,
  TradeItem,
  UserProfile,
  WatchlistItem,
} from '@/types/app'

export const serviceIds = ['TRADE', 'ERP', 'GROUPWARE'] as const

export const mockUser: UserProfile = {
  comCd: 'COM001',
  userId: 'admin',
  userName: '김관리',
  email: 'admin@startpack.dev',
  phone: '010-1111-2222',
  address: 'Seoul',
  status: 'ACTIVE',
  serviceAccesses: ['ERP', 'TRADE'],
}

export const mockMenus: MenuItem[] = [
  { menuId: 'DASHBOARD', parentMenuId: null, menuName: '대시보드', menuUrl: '/dashboard', i18nCode: 'menu.dashboard', icon: 'pi pi-home', menuLevel: 1, sortSeq: 1 },
  { menuId: 'SYSTEM', parentMenuId: null, menuName: '공통 관리', menuUrl: '/system', i18nCode: 'menu.system', icon: 'pi pi-objects-column', menuLevel: 1, sortSeq: 2 },
  { menuId: 'ORG_ADMIN', parentMenuId: 'SYSTEM', menuName: '사용자/조직', menuUrl: '/system/org-admin', i18nCode: 'menu.orgAdmin', icon: 'pi pi-users', menuLevel: 2, sortSeq: 1 },
  { menuId: 'USERS', parentMenuId: 'ORG_ADMIN', menuName: '사용자 관리', menuUrl: '/users', i18nCode: 'menu.users', icon: 'pi pi-user', menuLevel: 3, sortSeq: 1 },
  { menuId: 'DEPARTMENTS', parentMenuId: 'ORG_ADMIN', menuName: '부서 관리', menuUrl: '/departments', i18nCode: 'menu.departments', icon: 'pi pi-sitemap', menuLevel: 3, sortSeq: 2 },
  { menuId: 'JOB_GRADES', parentMenuId: 'ORG_ADMIN', menuName: '직급 관리', menuUrl: '/job-grades', i18nCode: 'menu.jobGrades', icon: 'pi pi-briefcase', menuLevel: 3, sortSeq: 3 },
  { menuId: 'POSITIONS', parentMenuId: 'ORG_ADMIN', menuName: '직책 관리', menuUrl: '/positions', i18nCode: 'menu.positions', icon: 'pi pi-id-card', menuLevel: 3, sortSeq: 4 },
  { menuId: 'USER_POSITIONS', parentMenuId: 'ORG_ADMIN', menuName: '사용자-직책 매핑', menuUrl: '/user-positions', i18nCode: 'menu.userPositions', icon: 'pi pi-share-alt', menuLevel: 3, sortSeq: 5 },
  { menuId: 'SERVICE_ACCESSES', parentMenuId: 'ORG_ADMIN', menuName: '서비스 접근 관리', menuUrl: '/service-accesses', i18nCode: 'menu.serviceAccesses', icon: 'pi pi-shield', menuLevel: 3, sortSeq: 6 },
  { menuId: 'SYSTEM_ADMIN', parentMenuId: 'SYSTEM', menuName: '권한/코드', menuUrl: '/system/system-admin', i18nCode: 'menu.systemAdmin', icon: 'pi pi-lock', menuLevel: 2, sortSeq: 2 },
  { menuId: 'GROUPS', parentMenuId: 'SYSTEM_ADMIN', menuName: '그룹 관리', menuUrl: '/groups', i18nCode: 'menu.groups', icon: 'pi pi-users', menuLevel: 3, sortSeq: 1 },
  { menuId: 'GROUP_MEMBERS', parentMenuId: 'SYSTEM_ADMIN', menuName: '그룹-사용자 매핑', menuUrl: '/group-members', i18nCode: 'menu.groupMembers', icon: 'pi pi-link', menuLevel: 3, sortSeq: 2 },
  { menuId: 'MENUS', parentMenuId: 'SYSTEM_ADMIN', menuName: '메뉴 관리', menuUrl: '/menus', i18nCode: 'menu.menus', icon: 'pi pi-list', menuLevel: 3, sortSeq: 3 },
  { menuId: 'MENU_PERMISSIONS', parentMenuId: 'SYSTEM_ADMIN', menuName: '메뉴 권한 관리', menuUrl: '/menu-permissions', i18nCode: 'menu.menuPermissions', icon: 'pi pi-key', menuLevel: 3, sortSeq: 4 },
  { menuId: 'CODE_GROUPS', parentMenuId: 'SYSTEM_ADMIN', menuName: '코드그룹 관리', menuUrl: '/code-groups', i18nCode: 'menu.codeGroups', icon: 'pi pi-folder', menuLevel: 3, sortSeq: 5 },
  { menuId: 'CODES', parentMenuId: 'SYSTEM_ADMIN', menuName: '코드 관리', menuUrl: '/codes', i18nCode: 'menu.codes', icon: 'pi pi-hashtag', menuLevel: 3, sortSeq: 6 },
  { menuId: 'TRADE', parentMenuId: null, menuName: 'Trade', menuUrl: '/trade', i18nCode: 'menu.trade', icon: 'pi pi-chart-line', menuLevel: 1, sortSeq: 3 },
  { menuId: 'ACCOUNT_INFO', parentMenuId: 'TRADE', menuName: '계좌 정보', menuUrl: '/trade/accounts', i18nCode: 'menu.accounts', icon: 'pi pi-wallet', menuLevel: 2, sortSeq: 1 },
  { menuId: 'ITEM_SEARCH', parentMenuId: 'TRADE', menuName: '종목 검색', menuUrl: '/trade/items', i18nCode: 'menu.items', icon: 'pi pi-search', menuLevel: 2, sortSeq: 2 },
  { menuId: 'WATCHLIST', parentMenuId: 'TRADE', menuName: '관심종목', menuUrl: '/trade/watchlist', i18nCode: 'menu.watchlist', icon: 'pi pi-star', menuLevel: 2, sortSeq: 3 },
  { menuId: 'REALTIME_MARKET', parentMenuId: 'TRADE', menuName: '실시간 시세/잔고', menuUrl: '/trade/realtime', i18nCode: 'menu.realtime', icon: 'pi pi-bolt', menuLevel: 2, sortSeq: 4 },
]

export const mockPermissions: MenuPermission[] = mockMenus
  .filter((menu) => !['/system', '/system/org-admin', '/system/system-admin', '/trade'].includes(menu.menuUrl))
  .map((menu) => ({
    menuId: menu.menuId,
    permitRead: true,
    permitWrite: !['REALTIME_MARKET', 'ACCOUNT_INFO'].includes(menu.menuId),
    permitDelete: !['REALTIME_MARKET', 'ACCOUNT_INFO'].includes(menu.menuId),
    permitExcel: true,
  }))

export const managementDefinitions: Record<string, ResourceDefinition> = {
  users: {
    key: 'users',
    title: '사용자 관리',
    description: '모든 프론트가 공통으로 사용하는 사용자 마스터 화면이다.',
    menuId: 'USERS',
    searchPlaceholder: 'userId, userName, email 검색',
    stats: [
      { label: '공통 사용자', value: '3', hint: 'frontend_trade / erp / groupware 공용' },
      { label: '기본 서비스', value: 'TRADE', hint: '현재 앱 기본 serviceId' },
      { label: '확장 필드', value: 'jobGradeId', hint: '조직 체계 연동' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'userId', field: 'userId', editor: 'input' },
      { title: 'userName', field: 'userName', editor: 'input' },
      { title: 'jobGradeId', field: 'jobGradeId', editor: 'select' },
      { title: 'email', field: 'email', editor: 'input' },
      { title: 'phone', field: 'phone', editor: 'input' },
      { title: 'address', field: 'address', editor: 'input' },
      { title: 'status', field: 'status', editor: 'select', defaultValue: 'ACTIVE' },
    ],
  },
  departments: {
    key: 'departments',
    title: '부서 관리',
    description: '부서 트리와 부서장 사용자/직책을 함께 관리한다.',
    menuId: 'DEPARTMENTS',
    searchPlaceholder: 'departmentId, departmentName 검색',
    stats: [
      { label: '조직 구조', value: 'Tree', hint: 'parentDepartmentId 기반' },
      { label: '부서장 방식', value: '사용자 + 직책', hint: '전자결재 연계' },
      { label: '공용 마스터', value: 'Yes', hint: '서비스 공통' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'departmentId', field: 'departmentId', editor: 'input' },
      { title: 'departmentName', field: 'departmentName', editor: 'input' },
      { title: 'parentDepartmentId', field: 'parentDepartmentId', editor: 'select' },
      { title: 'departmentHeadUserId', field: 'departmentHeadUserId', editor: 'select' },
      { title: 'departmentHeadPositionId', field: 'departmentHeadPositionId', editor: 'select' },
      { title: 'sortSeq', field: 'sortSeq', editor: 'number', defaultValue: 1 },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  jobGrades: {
    key: 'jobGrades',
    title: '직급 관리',
    description: '연구/일반/커스텀 직급 체계를 공통 마스터로 관리한다.',
    menuId: 'JOB_GRADES',
    searchPlaceholder: 'jobGradeId, jobGradeName 검색',
    stats: [
      { label: '직급 유형', value: 'RESEARCH/GENERAL/CUSTOM', hint: 'backend_user 기준' },
      { label: '사용자 1:1', value: '1개', hint: '사용자당 단일 직급' },
      { label: '공용 마스터', value: 'Yes', hint: '서비스 공통' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'jobGradeId', field: 'jobGradeId', editor: 'input' },
      { title: 'jobGradeName', field: 'jobGradeName', editor: 'input' },
      { title: 'jobGradeType', field: 'jobGradeType', editor: 'select', defaultValue: 'GENERAL' },
      { title: 'sortSeq', field: 'sortSeq', editor: 'number', defaultValue: 1 },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  positions: {
    key: 'positions',
    title: '직책 관리',
    description: '부서 직책자 선택과 겸직 구조를 지원하는 직책 마스터 화면이다.',
    menuId: 'POSITIONS',
    searchPlaceholder: 'positionId, positionName 검색',
    stats: [
      { label: '직책 유형', value: 'DEFAULT/CUSTOM', hint: 'backend_user 기준' },
      { label: '겸직', value: '허용', hint: 'user-position 매핑 원천' },
      { label: '공용 마스터', value: 'Yes', hint: '서비스 공통' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'positionId', field: 'positionId', editor: 'input' },
      { title: 'positionName', field: 'positionName', editor: 'input' },
      { title: 'positionType', field: 'positionType', editor: 'select', defaultValue: 'DEFAULT' },
      { title: 'sortSeq', field: 'sortSeq', editor: 'number', defaultValue: 1 },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  userPositions: {
    key: 'userPositions',
    title: '사용자-직책 매핑',
    description: '사용자별 부서/직책 겸직 구조를 한 화면에서 관리한다.',
    menuId: 'USER_POSITIONS',
    searchPlaceholder: 'departmentId, positionId 검색',
    requiresUserId: true,
    stats: [
      { label: '조회 기준', value: 'userId', hint: 'GET /api/users/user-positions' },
      { label: '겸직', value: '허용', hint: '복수 부서/직책 가능' },
      { label: '원천 데이터', value: 'organization', hint: '부서 소속 판정 기준' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'userPositionId', field: 'userPositionId', editor: 'input' },
      { title: 'userId', field: 'userId', editor: 'select' },
      { title: 'departmentId', field: 'departmentId', editor: 'select' },
      { title: 'positionId', field: 'positionId', editor: 'select' },
      { title: 'primaryYn', field: 'primaryYn', editor: 'tickCross', defaultValue: false },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  serviceAccesses: {
    key: 'serviceAccesses',
    title: '서비스 접근 관리',
    description: '사용자별 ERP/GROUPWARE/TRADE 접근 가능 여부를 관리한다.',
    menuId: 'SERVICE_ACCESSES',
    searchPlaceholder: 'serviceId 검색',
    requiresUserId: true,
    stats: [
      { label: '서비스 수', value: '3', hint: 'ERP / GROUPWARE / TRADE' },
      { label: '조회 기준', value: 'userId', hint: 'GET /api/users/service-accesses' },
      { label: '로그인 영향', value: '즉시', hint: '요청 serviceId 접근권한 검사' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'userId', field: 'userId', editor: 'select' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'accessible', field: 'accessible', editor: 'tickCross', defaultValue: true },
    ],
  },
  groups: {
    key: 'groups',
    title: '그룹 관리',
    description: '서비스별 권한 그룹을 같은 화면에서 필터링해 관리한다.',
    menuId: 'GROUPS',
    searchPlaceholder: 'groupId, groupName 검색',
    serviceScoped: true,
    stats: [
      { label: '기본 필터', value: 'TRADE', hint: 'TRADE 그룹 기준으로 고정' },
      { label: '서비스 스코프', value: 'Yes', hint: 'serviceId 함께 전송' },
      { label: '공통 구조', value: 'shared', hint: 'frontend_erp/groupware 동일 사용' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'groupId', field: 'groupId', editor: 'input' },
      { title: 'groupName', field: 'groupName', editor: 'input' },
      { title: 'description', field: 'description', editor: 'input' },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  groupMembers: {
    key: 'groupMembers',
    title: '그룹-사용자 매핑',
    description: '서비스별 그룹에 사용자를 연결하는 보조 화면이다.',
    menuId: 'GROUP_MEMBERS',
    searchPlaceholder: 'groupId, userId 검색',
    serviceScoped: true,
    stats: [
      { label: '조회 기준', value: 'serviceId', hint: '그룹 서비스 범위 사용' },
      { label: '저장 방식', value: 'bulk', hint: 'added/updated/deleted' },
      { label: '공통 구조', value: 'shared', hint: '다른 프론트와 동일' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'groupId', field: 'groupId', editor: 'select' },
      { title: 'userId', field: 'userId', editor: 'select' },
    ],
  },
  menus: {
    key: 'menus',
    title: '메뉴 관리',
    description: '서비스별 메뉴 구조를 같은 공통 화면에서 유지한다.',
    menuId: 'MENUS',
    searchPlaceholder: 'menuId, menuName, menuUrl 검색',
    serviceScoped: true,
    stats: [
      { label: '3depth', value: '지원', hint: '공통 메뉴 렌더링 규칙' },
      { label: '기본 필터', value: 'TRADE', hint: 'TRADE 메뉴 기준으로 고정' },
      { label: '아이콘', value: 'PrimeIcon', hint: '기본값 허용' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'menuId', field: 'menuId', editor: 'input' },
      { title: 'menuParentId', field: 'menuParentId', editor: 'select' },
      { title: 'menuName', field: 'menuName', editor: 'input' },
      { title: 'menuUrl', field: 'menuUrl', editor: 'input' },
      { title: 'menuLevel', field: 'menuLevel', editor: 'select', defaultValue: 1 },
      { title: 'sortSeq', field: 'sortSeq', editor: 'number', defaultValue: 1 },
      { title: 'icon', field: 'icon', editor: 'input', defaultValue: 'pi pi-circle' },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  menuPermissions: {
    key: 'menuPermissions',
    title: '메뉴 권한 관리',
    description: '서비스별 그룹 권한을 route/button 기준으로 관리한다.',
    menuId: 'MENU_PERMISSIONS',
    searchPlaceholder: 'groupId, menuId 검색',
    serviceScoped: true,
    stats: [
      { label: '기본 필터', value: 'TRADE', hint: '허용 서비스만 선택 가능' },
      { label: '버튼 제어', value: 'write/delete/excel', hint: '이중 권한 체크' },
      { label: '공통 구조', value: 'shared', hint: 'frontend_erp/groupware 공용' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'groupId', field: 'groupId', editor: 'select' },
      { title: 'menuId', field: 'menuId', editor: 'select' },
      { title: 'permitRead', field: 'permitRead', editor: 'tickCross', defaultValue: true },
      { title: 'permitWrite', field: 'permitWrite', editor: 'tickCross', defaultValue: false },
      { title: 'permitDelete', field: 'permitDelete', editor: 'tickCross', defaultValue: false },
      { title: 'permitExcel', field: 'permitExcel', editor: 'tickCross', defaultValue: false },
    ],
  },
  codeGroups: {
    key: 'codeGroups',
    title: '코드그룹 관리',
    description: '서비스별 코드그룹을 같은 구조로 관리한다.',
    menuId: 'CODE_GROUPS',
    searchPlaceholder: 'codeGroupId, codeGroupName 검색',
    serviceScoped: true,
    stats: [
      { label: '기본 필터', value: 'TRADE', hint: '서비스별 분리' },
      { label: '재사용', value: 'shared', hint: '세 프론트 동일 구조' },
      { label: 'enabled', value: '분리', hint: '신규 선택 제어' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'codeGroupId', field: 'codeGroupId', editor: 'input' },
      { title: 'codeGroupName', field: 'codeGroupName', editor: 'input' },
      { title: 'description', field: 'description', editor: 'input' },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
  codes: {
    key: 'codes',
    title: '코드 관리',
    description: '서비스별 코드와 코드그룹 관계를 공통 그리드 패턴으로 관리한다.',
    menuId: 'CODES',
    searchPlaceholder: 'codeGroupId, codeId, codeName 검색',
    serviceScoped: true,
    stats: [
      { label: '기본 필터', value: 'TRADE', hint: '서비스별 코드 분리' },
      { label: '포맷', value: 'flat', hint: 'parentCode 지원' },
      { label: '공통 구조', value: 'shared', hint: 'frontend_erp/groupware 공용' },
    ],
    columns: [
      { title: 'comCd', field: 'comCd', editor: 'input', defaultValue: 'COM001' },
      { title: 'serviceId', field: 'serviceId', editor: 'select', defaultValue: 'TRADE' },
      { title: 'codeGroupId', field: 'codeGroupId', editor: 'select' },
      { title: 'codeId', field: 'codeId', editor: 'input' },
      { title: 'codeName', field: 'codeName', editor: 'input' },
      { title: 'parentCodeGroupId', field: 'parentCodeGroupId', editor: 'select' },
      { title: 'parentCodeId', field: 'parentCodeId', editor: 'select' },
      { title: 'subInfo1', field: 'subInfo1', editor: 'input' },
      { title: 'subInfo2', field: 'subInfo2', editor: 'input' },
      { title: 'subInfo3', field: 'subInfo3', editor: 'input' },
      { title: 'sortSeq', field: 'sortSeq', editor: 'number', defaultValue: 1 },
      { title: 'enabled', field: 'enabled', editor: 'tickCross', defaultValue: true },
    ],
  },
}

export const initialManagementData: Record<string, Array<Record<string, unknown>>> = {
  users: [
    { comCd: 'COM001', userId: 'admin', userName: '김관리', jobGradeId: 'SENIOR', email: 'admin@startpack.dev', phone: '010-1111-2222', address: 'Seoul', status: 'ACTIVE' },
    { comCd: 'COM001', userId: 'trader01', userName: '박트레이더', jobGradeId: 'MANAGER', email: 'trader01@startpack.dev', phone: '010-3333-4444', address: 'Busan', status: 'ACTIVE' },
    { comCd: 'COM001', userId: 'risk02', userName: '이리스크', jobGradeId: 'SENIOR', email: 'risk02@startpack.dev', phone: '010-5555-6666', address: 'Incheon', status: 'LOCKED' },
  ],
  departments: [
    { comCd: 'COM001', departmentId: 'HQ', departmentName: '본사', parentDepartmentId: '', departmentHeadUserId: 'admin', departmentHeadPositionId: 'DIRECTOR', sortSeq: 1, enabled: true },
    { comCd: 'COM001', departmentId: 'TRADE_DIV', departmentName: '트레이드본부', parentDepartmentId: 'HQ', departmentHeadUserId: 'trader01', departmentHeadPositionId: 'HEAD', sortSeq: 2, enabled: true },
    { comCd: 'COM001', departmentId: 'RISK_DIV', departmentName: '리스크관리팀', parentDepartmentId: 'HQ', departmentHeadUserId: 'risk02', departmentHeadPositionId: 'MANAGER', sortSeq: 3, enabled: true },
  ],
  jobGrades: [
    { comCd: 'COM001', jobGradeId: 'SENIOR', jobGradeName: '선임', jobGradeType: 'GENERAL', sortSeq: 1, enabled: true },
    { comCd: 'COM001', jobGradeId: 'MANAGER', jobGradeName: '책임', jobGradeType: 'GENERAL', sortSeq: 2, enabled: true },
    { comCd: 'COM001', jobGradeId: 'PRINCIPAL', jobGradeName: '수석', jobGradeType: 'RESEARCH', sortSeq: 3, enabled: true },
  ],
  positions: [
    { comCd: 'COM001', positionId: 'DIRECTOR', positionName: '이사', positionType: 'DEFAULT', sortSeq: 1, enabled: true },
    { comCd: 'COM001', positionId: 'HEAD', positionName: '본부장', positionType: 'DEFAULT', sortSeq: 2, enabled: true },
    { comCd: 'COM001', positionId: 'MANAGER', positionName: '팀장', positionType: 'DEFAULT', sortSeq: 3, enabled: true },
  ],
  userPositions: [
    { comCd: 'COM001', userPositionId: 'admin_HQ_DIRECTOR', userId: 'admin', departmentId: 'HQ', positionId: 'DIRECTOR', primaryYn: true, enabled: true },
    { comCd: 'COM001', userPositionId: 'trader01_TRADE_DIV_HEAD', userId: 'trader01', departmentId: 'TRADE_DIV', positionId: 'HEAD', primaryYn: true, enabled: true },
    { comCd: 'COM001', userPositionId: 'risk02_RISK_DIV_MANAGER', userId: 'risk02', departmentId: 'RISK_DIV', positionId: 'MANAGER', primaryYn: true, enabled: true },
  ],
  serviceAccesses: [
    { comCd: 'COM001', userId: 'admin', serviceId: 'TRADE', accessible: true },
    { comCd: 'COM001', userId: 'admin', serviceId: 'ERP', accessible: true },
    { comCd: 'COM001', userId: 'admin', serviceId: 'GROUPWARE', accessible: false },
    { comCd: 'COM001', userId: 'trader01', serviceId: 'TRADE', accessible: true },
    { comCd: 'COM001', userId: 'trader01', serviceId: 'ERP', accessible: true },
    { comCd: 'COM001', userId: 'risk02', serviceId: 'ERP', accessible: true },
  ],
  groups: [
    { comCd: 'COM001', serviceId: 'TRADE', groupId: 'TRADE_ADMIN', groupName: '트레이드 관리자', description: 'TRADE 전체 권한', enabled: true },
    { comCd: 'COM001', serviceId: 'TRADE', groupId: 'TRADE_USER', groupName: '트레이드 사용자', description: 'TRADE 일반 권한', enabled: true },
    { comCd: 'COM001', serviceId: 'ERP', groupId: 'ERP_ADMIN', groupName: 'ERP 관리자', description: 'ERP 전체 권한', enabled: true },
    { comCd: 'COM001', serviceId: 'GROUPWARE', groupId: 'GW_APPROVER', groupName: '그룹웨어 결재자', description: '그룹웨어 결재 권한', enabled: true },
  ],
  groupMembers: [
    { comCd: 'COM001', serviceId: 'TRADE', groupId: 'TRADE_ADMIN', userId: 'admin', primaryYn: true, enabled: true },
    { comCd: 'COM001', serviceId: 'TRADE', groupId: 'TRADE_USER', userId: 'trader01', primaryYn: true, enabled: true },
    { comCd: 'COM001', serviceId: 'ERP', groupId: 'ERP_ADMIN', userId: 'admin', primaryYn: true, enabled: true },
  ],
  menus: [
    { comCd: 'COM001', serviceId: 'TRADE', menuId: 'TRADE_DASHBOARD', menuParentId: '', menuName: '대시보드', menuUrl: '/dashboard', menuLevel: 1, sortSeq: 1, icon: 'pi pi-home', enabled: true },
    { comCd: 'COM001', serviceId: 'TRADE', menuId: 'TRADE_WATCHLIST', menuParentId: 'TRADE_DASHBOARD', menuName: '관심종목', menuUrl: '/trade/watchlist', menuLevel: 2, sortSeq: 2, icon: 'pi pi-star', enabled: true },
    { comCd: 'COM001', serviceId: 'ERP', menuId: 'ERP_MASTER', menuParentId: '', menuName: '기준정보', menuUrl: '/erp/master', menuLevel: 1, sortSeq: 1, icon: 'pi pi-database', enabled: true },
    { comCd: 'COM001', serviceId: 'GROUPWARE', menuId: 'GW_APPROVAL', menuParentId: '', menuName: '전자결재', menuUrl: '/groupware/approval', menuLevel: 1, sortSeq: 1, icon: 'pi pi-file-edit', enabled: true },
  ],
  menuPermissions: [
    { comCd: 'COM001', serviceId: 'TRADE', groupId: 'TRADE_ADMIN', menuId: 'TRADE_DASHBOARD', permitRead: true, permitWrite: true, permitDelete: true, permitExcel: true },
    { comCd: 'COM001', serviceId: 'TRADE', groupId: 'TRADE_USER', menuId: 'TRADE_WATCHLIST', permitRead: true, permitWrite: true, permitDelete: false, permitExcel: true },
    { comCd: 'COM001', serviceId: 'ERP', groupId: 'ERP_ADMIN', menuId: 'ERP_MASTER', permitRead: true, permitWrite: true, permitDelete: true, permitExcel: true },
    { comCd: 'COM001', serviceId: 'GROUPWARE', groupId: 'GW_APPROVER', menuId: 'GW_APPROVAL', permitRead: true, permitWrite: true, permitDelete: false, permitExcel: false },
  ],
  codeGroups: [
    { comCd: 'COM001', serviceId: 'TRADE', codeGroupId: 'ORDER_TYPE', codeGroupName: '주문 유형', description: 'TRADE 주문 기준', enabled: true },
    { comCd: 'COM001', serviceId: 'TRADE', codeGroupId: 'ALERT_TYPE', codeGroupName: '알림 유형', description: 'TRADE 알림 기준', enabled: true },
    { comCd: 'COM001', serviceId: 'ERP', codeGroupId: 'TAX_TYPE', codeGroupName: '세금 구분', description: 'ERP 세금 기준', enabled: true },
    { comCd: 'COM001', serviceId: 'GROUPWARE', codeGroupId: 'APPROVAL_STATUS', codeGroupName: '결재 상태', description: '그룹웨어 결재 상태', enabled: true },
  ],
  codes: [
    { comCd: 'COM001', serviceId: 'TRADE', codeGroupId: 'ORDER_TYPE', codeId: 'LIMIT', codeName: '지정가', parentCodeGroupId: '', parentCodeId: '', subInfo1: '', subInfo2: '', subInfo3: '', sortSeq: 1, enabled: true },
    { comCd: 'COM001', serviceId: 'TRADE', codeGroupId: 'ORDER_TYPE', codeId: 'MARKET', codeName: '시장가', parentCodeGroupId: '', parentCodeId: '', subInfo1: '', subInfo2: '', subInfo3: '', sortSeq: 2, enabled: true },
    { comCd: 'COM001', serviceId: 'TRADE', codeGroupId: 'ALERT_TYPE', codeId: 'UP_BREAK', codeName: '상향 돌파', parentCodeGroupId: '', parentCodeId: '', subInfo1: '', subInfo2: '', subInfo3: '', sortSeq: 1, enabled: true },
    { comCd: 'COM001', serviceId: 'ERP', codeGroupId: 'TAX_TYPE', codeId: 'VAT', codeName: '부가세', parentCodeGroupId: '', parentCodeId: '', subInfo1: '', subInfo2: '', subInfo3: '', sortSeq: 1, enabled: true },
    { comCd: 'COM001', serviceId: 'GROUPWARE', codeGroupId: 'APPROVAL_STATUS', codeId: 'PENDING', codeName: '대기', parentCodeGroupId: '', parentCodeId: '', subInfo1: '', subInfo2: '', subInfo3: '', sortSeq: 1, enabled: true },
  ],
}

export const accountSummaries: AccountSummary[] = [
  { accountNo: 'paper-account', accountName: '모의투자 계좌', totalAssetAmount: 523320000, cashAmount: 142500000 },
  { accountNo: 'live-account', accountName: '실거래 계좌', totalAssetAmount: 189420000, cashAmount: 38400000 },
]

export const dailyBalances: DailyBalance[] = [
  { accountNo: 'paper-account', baseDate: '2026-04-29', totalAssetAmount: 520100000, profitLossAmount: 980000 },
  { accountNo: 'paper-account', baseDate: '2026-04-30', totalAssetAmount: 523320000, profitLossAmount: 3220000 },
  { accountNo: 'live-account', baseDate: '2026-04-30', totalAssetAmount: 189420000, profitLossAmount: -180000 },
]

export const tradeItems: TradeItem[] = [
  { itemCode: '005930', itemName: '삼성전자', marketCode: 'KOSPI', price: 86200, changeRate: 1.41 },
  { itemCode: '000660', itemName: 'SK하이닉스', marketCode: 'KOSPI', price: 241500, changeRate: -0.62 },
  { itemCode: '035420', itemName: 'NAVER', marketCode: 'KOSPI', price: 214500, changeRate: -1.47 },
  { itemCode: 'AAPL', itemName: 'Apple', marketCode: 'NASDAQ', price: 214.34, changeRate: 0.86 },
  { itemCode: 'MSFT', itemName: 'Microsoft', marketCode: 'NASDAQ', price: 456.12, changeRate: 1.12 },
]

export const tradeHistories: TradeHistory[] = [
  { id: 'H-001', accountNo: 'paper-account', itemCode: '005930', itemName: '삼성전자', side: 'BUY', quantity: 10, price: 85500, tradedAt: '2026-05-01 09:12:20' },
  { id: 'H-002', accountNo: 'paper-account', itemCode: 'AAPL', itemName: 'Apple', side: 'SELL', quantity: 5, price: 213.2, tradedAt: '2026-05-01 09:30:11' },
]

export const watchlistItems: WatchlistItem[] = [
  { id: 'W-001', userId: 'admin', itemCode: '005930', itemName: '삼성전자' },
  { id: 'W-002', userId: 'admin', itemCode: 'AAPL', itemName: 'Apple' },
  { id: 'W-003', userId: 'trader01', itemCode: 'MSFT', itemName: 'Microsoft' },
]

export const realtimeStatus: RealtimeStatus = {
  kisConnected: true,
  sessionCount: 3,
  subscriptionCount: 5,
  cachedEventCount: 4,
}

export const realtimeQuotes: RealtimeQuote[] = [
  { symbol: '005930', stream: 'PRICE', price: 86200, basePrice: 85000, changeRate: 1.41, occurredAt: '2026-05-01T01:24:00Z' },
  { symbol: '000660', stream: 'PRICE', price: 241500, basePrice: 243000, changeRate: -0.62, occurredAt: '2026-05-01T01:24:00Z' },
  { symbol: 'AAPL', stream: 'PRICE', price: 214.34, basePrice: 212.50, changeRate: 0.86, occurredAt: '2026-05-01T01:24:00Z' },
  { symbol: 'paper-account', stream: 'BALANCE', price: 523320000, basePrice: 520100000, changeRate: 0.62, occurredAt: '2026-05-01T01:24:00Z' },
]
