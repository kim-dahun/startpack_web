import CodeGroupsMain from '@/pages/user/code-groups/Main.vue'
import CodesMain from '@/pages/user/codes/Main.vue'
import DepartmentsMain from '@/pages/user/departments/Main.vue'
import GroupsMain from '@/pages/user/groups/Main.vue'
import JobGradesMain from '@/pages/user/job-grades/Main.vue'
import MenuPermissionsMain from '@/pages/user/menu-permissions/Main.vue'
import MenusMain from '@/pages/user/menus/Main.vue'
import PositionsMain from '@/pages/user/positions/Main.vue'
import ServiceAccessesMain from '@/pages/user/service-accesses/Main.vue'
import UsersMain from '@/pages/user/users/Main.vue'
import { routeMetaMap } from '@/router/routes'

export const adminRoutes = [
  {
    path: 'admin/users',
    name: 'adminUsers',
    component: UsersMain,
    meta: routeMetaMap.adminUsers,
  },
  {
    path: 'admin/departments',
    name: 'adminDepartments',
    component: DepartmentsMain,
    meta: routeMetaMap.adminDepartments,
  },
  {
    path: 'admin/job-grades',
    name: 'adminJobGrades',
    component: JobGradesMain,
    meta: routeMetaMap.adminJobGrades,
  },
  {
    path: 'admin/positions',
    name: 'adminPositions',
    component: PositionsMain,
    meta: routeMetaMap.adminPositions,
  },
  {
    path: 'admin/user-positions',
    name: 'adminUserPositions',
    redirect: '/admin/departments',
    meta: routeMetaMap.adminUserPositions,
  },
  {
    path: 'admin/service-accesses',
    name: 'adminServiceAccesses',
    component: ServiceAccessesMain,
    meta: routeMetaMap.adminServiceAccesses,
  },
  {
    path: 'admin/groups',
    name: 'adminGroups',
    component: GroupsMain,
    meta: routeMetaMap.adminGroups,
  },
  {
    path: 'admin/group-members',
    name: 'adminGroupMembers',
    redirect: '/admin/groups',
    meta: routeMetaMap.adminGroupMembers,
  },
  {
    path: 'admin/menus',
    name: 'adminMenus',
    component: MenusMain,
    meta: routeMetaMap.adminMenus,
  },
  {
    path: 'admin/menu-permissions',
    name: 'adminMenuPermissions',
    component: MenuPermissionsMain,
    meta: routeMetaMap.adminMenuPermissions,
  },
  {
    path: 'admin/code-groups',
    name: 'adminCodeGroups',
    redirect: '/admin/codes',
    meta: routeMetaMap.adminCodeGroups,
  },
  {
    path: 'admin/codes',
    name: 'adminCodes',
    component: CodesMain,
    meta: routeMetaMap.adminCodes,
  },
]
