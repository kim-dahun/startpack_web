import CodeGroupsMain from '@/pages/user/code-groups/Main.vue'
import CodesMain from '@/pages/user/codes/Main.vue'
import DepartmentsMain from '@/pages/user/departments/Main.vue'
import GroupMembersMain from '@/pages/user/group-members/Main.vue'
import GroupsMain from '@/pages/user/groups/Main.vue'
import JobGradesMain from '@/pages/user/job-grades/Main.vue'
import MenuPermissionsMain from '@/pages/user/menu-permissions/Main.vue'
import MenusMain from '@/pages/user/menus/Main.vue'
import PositionsMain from '@/pages/user/positions/Main.vue'
import ServiceAccessesMain from '@/pages/user/service-accesses/Main.vue'
import UserPositionsMain from '@/pages/user/user-positions/Main.vue'
import UsersMain from '@/pages/user/users/Main.vue'
import { routeMetaMap } from '@/router/routes'
export const adminRoutes = [
    {
        path: 'users',
        name: 'users',
        component: UsersMain,
        meta: routeMetaMap.users,
    },
    {
        path: 'departments',
        name: 'departments',
        component: DepartmentsMain,
        meta: routeMetaMap.departments,
    },
    {
        path: 'job-grades',
        name: 'jobGrades',
        component: JobGradesMain,
        meta: routeMetaMap.jobGrades,
    },
    {
        path: 'positions',
        name: 'positions',
        component: PositionsMain,
        meta: routeMetaMap.positions,
    },
    {
        path: 'user-positions',
        name: 'userPositions',
        component: UserPositionsMain,
        meta: routeMetaMap.userPositions,
    },
    {
        path: 'service-accesses',
        name: 'serviceAccesses',
        component: ServiceAccessesMain,
        meta: routeMetaMap.serviceAccesses,
    },
    {
        path: 'groups',
        name: 'groups',
        component: GroupsMain,
        meta: routeMetaMap.groups,
    },
    {
        path: 'group-members',
        name: 'groupMembers',
        component: GroupMembersMain,
        meta: routeMetaMap.groupMembers,
    },
    {
        path: 'menus',
        name: 'menus',
        component: MenusMain,
        meta: routeMetaMap.menus,
    },
    {
        path: 'menu-permissions',
        name: 'menuPermissions',
        component: MenuPermissionsMain,
        meta: routeMetaMap.menuPermissions,
    },
    {
        path: 'code-groups',
        name: 'codeGroups',
        component: CodeGroupsMain,
        meta: routeMetaMap.codeGroups,
    },
    {
        path: 'codes',
        name: 'codes',
        component: CodesMain,
        meta: routeMetaMap.codes,
    },
]