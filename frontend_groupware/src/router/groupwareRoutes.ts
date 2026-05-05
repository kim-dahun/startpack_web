import { routeMetaMap } from '@/router/routes'

export const groupwareRoutes = [
  {
    path: 'groupware/chat/private',
    name: 'groupwareChatPrivate',
    component: () => import('@/pages/groupware/chat/WorkspaceMain.vue'),
    props: {
      title: 'Private Chats',
      description: '1:1 conversation rooms and recent direct messages.',
      roomType: 'DIRECT',
    },
    meta: routeMetaMap.chatPrivate,
  },
  {
    path: 'groupware/chat/groups',
    name: 'groupwareChatGroups',
    component: () => import('@/pages/groupware/chat/WorkspaceMain.vue'),
    props: {
      title: 'Group Chats',
      description: 'Shared rooms for project and department collaboration.',
      roomType: 'GROUP',
    },
    meta: routeMetaMap.chatGroup,
  },
  {
    path: 'groupware/chat/rooms',
    name: 'groupwareChatRooms',
    component: () => import('@/pages/groupware/chat/RoomsMain.vue'),
    meta: routeMetaMap.chatRooms,
  },
  {
    path: 'groupware/schedules',
    name: 'groupwareSchedules',
    component: () => import('@/pages/groupware/schedules/Main.vue'),
    props: {
      title: 'Schedules',
      description: 'Company and personal schedules within the selected period.',
      projectMode: false,
    },
    meta: routeMetaMap.schedules,
  },
  {
    path: 'groupware/project-schedules',
    name: 'groupwareProjectSchedules',
    component: () => import('@/pages/groupware/schedules/Main.vue'),
    props: {
      title: 'Project Schedules',
      description: 'Schedules linked to a selected project code.',
      projectMode: true,
    },
    meta: routeMetaMap.projectSchedules,
  },
  {
    path: 'groupware/schedule-costs',
    name: 'groupwareScheduleCosts',
    component: () => import('@/pages/groupware/schedule-costs/Main.vue'),
    meta: routeMetaMap.scheduleCosts,
  },
  {
    path: 'groupware/projects',
    name: 'groupwareProjects',
    component: () => import('@/pages/groupware/projects/Main.vue'),
    props: {
      title: 'Projects',
      description: 'Visible projects with progress, members, and task summary.',
      focusMode: 'overview',
    },
    meta: routeMetaMap.projects,
  },
  {
    path: 'groupware/project-tasks',
    name: 'groupwareProjectTasks',
    component: () => import('@/pages/groupware/projects/Main.vue'),
    props: {
      title: 'Project Tasks',
      description: 'Task-centric view for the selected project.',
      focusMode: 'tasks',
    },
    meta: routeMetaMap.projectTasks,
  },
  {
    path: 'groupware/approvals/drafts',
    name: 'groupwareApprovalDrafts',
    component: () => import('@/pages/groupware/approvals/Main.vue'),
    props: {
      title: 'Approval Drafts',
      description: 'Draft and submitted documents initiated by the current user.',
      viewMode: 'drafts',
    },
    meta: routeMetaMap.approvalDrafts,
  },
  {
    path: 'groupware/approvals/inbox',
    name: 'groupwareApprovalInbox',
    component: () => import('@/pages/groupware/approvals/Main.vue'),
    props: {
      title: 'Approval Inbox',
      description: 'Pending and processed approvals visible to the current user.',
      viewMode: 'inbox',
    },
    meta: routeMetaMap.approvalInbox,
  },
  {
    path: 'groupware/approvals/lines',
    name: 'groupwareApprovalLines',
    component: () => import('@/pages/groupware/approvals/Main.vue'),
    props: {
      title: 'Approval Lines',
      description: 'Document lines and reusable approval line templates.',
      viewMode: 'lines',
    },
    meta: routeMetaMap.approvalLines,
  },
  {
    path: 'groupware/notifications',
    name: 'groupwareNotifications',
    component: () => import('@/pages/groupware/notifications/Main.vue'),
    meta: routeMetaMap.notifications,
  },
  {
    path: 'groupware/org-chart',
    name: 'groupwareOrgChart',
    component: () => import('@/pages/groupware/org-chart/Main.vue'),
    meta: routeMetaMap.orgChart,
  },
]
