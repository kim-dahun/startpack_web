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
        name: 'users',
        component: UsersMain,
        meta: routeMetaMap.users,
    },
    {
        path: 'admin/departments',
        name: 'departments',
        component: DepartmentsMain,
        meta: routeMetaMap.departments,
    },
    {
        path: 'admin/job-grades',
        name: 'jobGrades',
        component: JobGradesMain,
        meta: routeMetaMap.jobGrades,
    },
    {
        path: 'admin/positions',
        name: 'positions',
        component: PositionsMain,
        meta: routeMetaMap.positions,
    },
    {
        path: 'admin/user-positions',
        name: 'userPositions',
        redirect: '/admin/departments',
        meta: routeMetaMap.userPositions,
    },
    {
        path: 'admin/service-accesses',
        name: 'serviceAccesses',
        component: ServiceAccessesMain,
        meta: routeMetaMap.serviceAccesses,
    },
    {
        path: 'admin/groups',
        name: 'groups',
        component: GroupsMain,
        meta: routeMetaMap.groups,
    },
    {
        path: 'admin/group-members',
        name: 'groupMembers',
        redirect: '/admin/groups',
        meta: routeMetaMap.groupMembers,
    },
    {
        path: 'admin/menus',
        name: 'menus',
        component: MenusMain,
        meta: routeMetaMap.menus,
    },
    {
        path: 'admin/menu-permissions',
        name: 'menuPermissions',
        component: MenuPermissionsMain,
        meta: routeMetaMap.menuPermissions,
    },
    {
        path: 'admin/code-groups',
        name: 'codeGroups',
        redirect: '/admin/codes',
        meta: routeMetaMap.codeGroups,
    },
    {
        path: 'admin/codes',
        name: 'codes',
        component: CodesMain,
        meta: routeMetaMap.codes,
    },
]
