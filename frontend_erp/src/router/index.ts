import { createRouter, createWebHistory } from 'vue-router'

import AppShellLayout from '@/layouts/AppShellLayout.vue'
import AccessDeniedMain from '@/pages/auth/access-denied/Main.vue'
import LoginMain from '@/pages/auth/login/Main.vue'
import NotFoundMain from '@/pages/auth/not-found/Main.vue'
import DashboardMain from '@/pages/dashboard/Main.vue'


import { useSessionStore } from '@/stores/session'
import { routeMetaMap } from '@/router/routes'
import { adminRoutes } from '@/router/adminRoutes'
import { erpRoutes } from '@/router/erpRoutes'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginMain,
      meta: {
        public: true,
      },
    },
    {
      path: '/',
      component: AppShellLayout,
      children: [
          ...adminRoutes,
          ...erpRoutes,
        {
          path: '',
          redirect: '/dashboard',
        },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: DashboardMain,
          meta: routeMetaMap.dashboard,
        },


        {
          path: 'forbidden',
          name: 'forbidden',
          component: AccessDeniedMain,
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: NotFoundMain,
    },
  ],
})

router.beforeEach((to) => {
  const sessionStore = useSessionStore()

  if (to.meta.public) {
    if (to.path === '/login' && sessionStore.isAuthenticated) {
      return '/dashboard'
    }
    return true
  }

  if (!sessionStore.isAuthenticated) {
    return '/login'
  }

  const isErpRoute = to.path.startsWith('/erp')

  if (isErpRoute && !sessionStore.hasErpAccess) {
    return '/forbidden'
  }

  const menuId = typeof to.meta.menuId === 'string' ? to.meta.menuId : null
  const menuUrl = typeof to.meta.menuUrl === 'string' ? to.meta.menuUrl : null

  if (menuId && !sessionStore.canAccess(menuId)) {
    return '/forbidden'
  }

  sessionStore.setCurrentMenu(menuId, menuUrl)
  return true
})

export default router
