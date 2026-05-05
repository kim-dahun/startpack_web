import type { LoginResponse, MenuItem, MenuPermission, UserGroup } from '@/types/app'

export const GROUPWARE_SERVICE_ID = 'GROUPWARE'

const groupwareMenus: MenuItem[] = [
  { menuId: 'GROUPWARE_ADMIN', parentMenuId: null, menuName: 'Admin', menuUrl: '/admin', i18nCode: 'menu.groupwareAdmin', icon: 'pi pi-shield', menuLevel: 1, sortSeq: 1 },
  { menuId: 'GROUPWARE_ADMIN_USERS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Users', menuUrl: '/admin/users', i18nCode: 'menu.users', icon: 'pi pi-user', menuLevel: 2, sortSeq: 1 },
  { menuId: 'GROUPWARE_ADMIN_DEPARTMENTS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Departments', menuUrl: '/admin/departments', i18nCode: 'menu.departments', icon: 'pi pi-sitemap', menuLevel: 2, sortSeq: 2 },
  { menuId: 'GROUPWARE_ADMIN_JOB_GRADES', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Job Grades', menuUrl: '/admin/job-grades', i18nCode: 'menu.jobGrades', icon: 'pi pi-briefcase', menuLevel: 2, sortSeq: 3 },
  { menuId: 'GROUPWARE_ADMIN_POSITIONS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Positions', menuUrl: '/admin/positions', i18nCode: 'menu.positions', icon: 'pi pi-id-card', menuLevel: 2, sortSeq: 4 },
  { menuId: 'GROUPWARE_ADMIN_USER_POSITIONS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'User Positions', menuUrl: '/admin/user-positions', i18nCode: 'menu.userPositions', icon: 'pi pi-share-alt', menuLevel: 2, sortSeq: 5 },
  { menuId: 'GROUPWARE_ADMIN_SERVICE_ACCESS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Service Access', menuUrl: '/admin/service-accesses', i18nCode: 'menu.serviceAccesses', icon: 'pi pi-key', menuLevel: 2, sortSeq: 6 },
  { menuId: 'GROUPWARE_ADMIN_GROUPS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Groups', menuUrl: '/admin/groups', i18nCode: 'menu.groups', icon: 'pi pi-users', menuLevel: 2, sortSeq: 7 },
  { menuId: 'GROUPWARE_ADMIN_MENUS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Menus', menuUrl: '/admin/menus', i18nCode: 'menu.menus', icon: 'pi pi-list', menuLevel: 2, sortSeq: 8 },
  { menuId: 'GROUPWARE_ADMIN_MENU_PERMISSIONS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Menu Permissions', menuUrl: '/admin/menu-permissions', i18nCode: 'menu.menuPermissions', icon: 'pi pi-lock', menuLevel: 2, sortSeq: 9 },
  { menuId: 'GROUPWARE_ADMIN_CODE_GROUPS', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Code Groups', menuUrl: '/admin/code-groups', i18nCode: 'menu.codeGroups', icon: 'pi pi-folder', menuLevel: 2, sortSeq: 10 },
  { menuId: 'GROUPWARE_ADMIN_CODES', parentMenuId: 'GROUPWARE_ADMIN', menuName: 'Codes', menuUrl: '/admin/codes', i18nCode: 'menu.codes', icon: 'pi pi-hashtag', menuLevel: 2, sortSeq: 11 },
  { menuId: 'GROUPWARE_CHAT', parentMenuId: null, menuName: 'Messenger', menuUrl: '/groupware/chat', i18nCode: 'menu.groupwareChat', icon: 'pi pi-comments', menuLevel: 1, sortSeq: 2 },
  { menuId: 'GROUPWARE_CHAT_PRIVATE', parentMenuId: 'GROUPWARE_CHAT', menuName: 'Private Chat', menuUrl: '/groupware/chat/private', i18nCode: 'menu.groupwareChatPrivate', icon: 'pi pi-user', menuLevel: 2, sortSeq: 1 },
  { menuId: 'GROUPWARE_CHAT_GROUP', parentMenuId: 'GROUPWARE_CHAT', menuName: 'Group Chat', menuUrl: '/groupware/chat/groups', i18nCode: 'menu.groupwareChatGroup', icon: 'pi pi-users', menuLevel: 2, sortSeq: 2 },
  { menuId: 'GROUPWARE_CHAT_ROOMS', parentMenuId: 'GROUPWARE_CHAT', menuName: 'Chat Rooms', menuUrl: '/groupware/chat/rooms', i18nCode: 'menu.groupwareChatRooms', icon: 'pi pi-th-large', menuLevel: 2, sortSeq: 3 },
  { menuId: 'GROUPWARE_SCHEDULE', parentMenuId: null, menuName: 'Schedules', menuUrl: '/groupware/schedules', i18nCode: 'menu.groupwareSchedule', icon: 'pi pi-calendar', menuLevel: 1, sortSeq: 3 },
  { menuId: 'GROUPWARE_SCHEDULES', parentMenuId: 'GROUPWARE_SCHEDULE', menuName: 'Schedule Management', menuUrl: '/groupware/schedules', i18nCode: 'menu.groupwareSchedules', icon: 'pi pi-calendar-clock', menuLevel: 2, sortSeq: 1 },
  { menuId: 'GROUPWARE_PROJECT_SCHEDULES', parentMenuId: 'GROUPWARE_SCHEDULE', menuName: 'Project Schedules', menuUrl: '/groupware/project-schedules', i18nCode: 'menu.groupwareProjectSchedules', icon: 'pi pi-calendar-plus', menuLevel: 2, sortSeq: 2 },
  { menuId: 'GROUPWARE_SCHEDULE_COSTS', parentMenuId: 'GROUPWARE_SCHEDULE', menuName: 'Schedule Costs', menuUrl: '/groupware/schedule-costs', i18nCode: 'menu.groupwareScheduleCosts', icon: 'pi pi-wallet', menuLevel: 2, sortSeq: 3 },
  { menuId: 'GROUPWARE_PROJECT', parentMenuId: null, menuName: 'Projects', menuUrl: '/groupware/projects', i18nCode: 'menu.groupwareProject', icon: 'pi pi-briefcase', menuLevel: 1, sortSeq: 4 },
  { menuId: 'GROUPWARE_PROJECTS', parentMenuId: 'GROUPWARE_PROJECT', menuName: 'Project Management', menuUrl: '/groupware/projects', i18nCode: 'menu.groupwareProjects', icon: 'pi pi-briefcase', menuLevel: 2, sortSeq: 1 },
  { menuId: 'GROUPWARE_PROJECT_TASKS', parentMenuId: 'GROUPWARE_PROJECT', menuName: 'Project Tasks', menuUrl: '/groupware/project-tasks', i18nCode: 'menu.groupwareProjectTasks', icon: 'pi pi-list-check', menuLevel: 2, sortSeq: 2 },
  { menuId: 'GROUPWARE_APPROVAL', parentMenuId: null, menuName: 'Approvals', menuUrl: '/groupware/approvals', i18nCode: 'menu.groupwareApproval', icon: 'pi pi-file-edit', menuLevel: 1, sortSeq: 5 },
  { menuId: 'GROUPWARE_APPROVAL_DRAFTS', parentMenuId: 'GROUPWARE_APPROVAL', menuName: 'Drafts', menuUrl: '/groupware/approvals/drafts', i18nCode: 'menu.groupwareApprovalDrafts', icon: 'pi pi-pencil', menuLevel: 2, sortSeq: 1 },
  { menuId: 'GROUPWARE_APPROVAL_INBOX', parentMenuId: 'GROUPWARE_APPROVAL', menuName: 'Inbox', menuUrl: '/groupware/approvals/inbox', i18nCode: 'menu.groupwareApprovalInbox', icon: 'pi pi-inbox', menuLevel: 2, sortSeq: 2 },
  { menuId: 'GROUPWARE_APPROVAL_LINES', parentMenuId: 'GROUPWARE_APPROVAL', menuName: 'Approval Lines', menuUrl: '/groupware/approvals/lines', i18nCode: 'menu.groupwareApprovalLines', icon: 'pi pi-sitemap', menuLevel: 2, sortSeq: 3 },
  { menuId: 'GROUPWARE_ALERTS', parentMenuId: null, menuName: 'Notifications', menuUrl: '/groupware/notifications', i18nCode: 'menu.groupwareAlerts', icon: 'pi pi-bell', menuLevel: 1, sortSeq: 6 },
  { menuId: 'GROUPWARE_NOTIFICATIONS', parentMenuId: 'GROUPWARE_ALERTS', menuName: 'Notification Center', menuUrl: '/groupware/notifications', i18nCode: 'menu.groupwareNotifications', icon: 'pi pi-bell', menuLevel: 2, sortSeq: 1 },
  { menuId: 'GROUPWARE_ORGANIZATION', parentMenuId: null, menuName: 'Organization', menuUrl: '/groupware/org-chart', i18nCode: 'menu.groupwareOrganization', icon: 'pi pi-sitemap', menuLevel: 1, sortSeq: 7 },
  { menuId: 'GROUPWARE_ORG_CHART', parentMenuId: 'GROUPWARE_ORGANIZATION', menuName: 'Org Chart', menuUrl: '/groupware/org-chart', i18nCode: 'menu.groupwareOrgChart', icon: 'pi pi-share-alt', menuLevel: 2, sortSeq: 1 },
]

const userAccessibleMenuIds = new Set([
  'GROUPWARE_CHAT_PRIVATE',
  'GROUPWARE_CHAT_GROUP',
  'GROUPWARE_CHAT_ROOMS',
  'GROUPWARE_SCHEDULES',
  'GROUPWARE_PROJECT_SCHEDULES',
  'GROUPWARE_PROJECTS',
  'GROUPWARE_PROJECT_TASKS',
  'GROUPWARE_APPROVAL_DRAFTS',
  'GROUPWARE_APPROVAL_INBOX',
  'GROUPWARE_NOTIFICATIONS',
  'GROUPWARE_ORG_CHART',
])

const mergeMenus = (menus: MenuItem[]) => {
  const merged = new Map<string, MenuItem>()

  groupwareMenus.forEach((menu) => {
    merged.set(menu.menuId, menu)
  })

  menus.forEach((menu) => {
    merged.set(menu.menuId, menu)
  })

  return [...merged.values()].sort((left, right) => {
    if (left.menuLevel !== right.menuLevel) {
      return left.menuLevel - right.menuLevel
    }
    return left.sortSeq - right.sortSeq
  })
}

const buildPermission = (menuId: string, writable: boolean): MenuPermission => ({
  menuId,
  permitRead: true,
  permitWrite: writable,
  permitDelete: writable,
  permitExcel: writable,
})

const buildSeedPermissions = (groups: UserGroup[], existingPermissions: MenuPermission[]) => {
  const permissionsByMenuId = new Map<string, MenuPermission>()

  existingPermissions.forEach((permission) => {
    permissionsByMenuId.set(permission.menuId, permission)
  })

  const isAdmin = groups.some((group) => group.serviceId === GROUPWARE_SERVICE_ID && group.groupId === 'ADMIN')
  const isUser = groups.some((group) => group.serviceId === GROUPWARE_SERVICE_ID && group.groupId === 'USER')

  if (isAdmin) {
    groupwareMenus
      .filter((menu) => Boolean(menu.menuUrl) && menu.menuUrl !== '/admin' && menu.menuUrl !== '/groupware/chat' && menu.menuUrl !== '/groupware/schedules' && menu.menuUrl !== '/groupware/projects' && menu.menuUrl !== '/groupware/approvals')
      .forEach((menu) => {
        if (!permissionsByMenuId.has(menu.menuId)) {
          permissionsByMenuId.set(menu.menuId, buildPermission(menu.menuId, true))
        }
      })
  }

  if (isUser) {
    groupwareMenus
      .filter((menu) => userAccessibleMenuIds.has(menu.menuId))
      .forEach((menu) => {
        if (!permissionsByMenuId.has(menu.menuId)) {
          permissionsByMenuId.set(menu.menuId, buildPermission(menu.menuId, menu.menuId !== 'GROUPWARE_NOTIFICATIONS' && menu.menuId !== 'GROUPWARE_ORG_CHART'))
        }
      })
  }

  return [...permissionsByMenuId.values()]
}

export const hydrateGroupwareSession = (session: LoginResponse): LoginResponse => {
  const serviceAccesses = session.user.serviceAccesses ?? session.serviceAccesses ?? []
  const activeServiceId = session.serviceId ?? GROUPWARE_SERVICE_ID

  if (activeServiceId !== GROUPWARE_SERVICE_ID && !serviceAccesses.includes(GROUPWARE_SERVICE_ID)) {
    return session
  }

  const groups = session.groups ?? []

  return {
    ...session,
    serviceId: GROUPWARE_SERVICE_ID,
    serviceAccesses,
    menus: mergeMenus(session.menus ?? []),
    menuPermissions: buildSeedPermissions(groups, session.menuPermissions ?? []),
  }
}
