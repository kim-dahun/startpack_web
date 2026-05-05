import { routeMetaMap } from '@/router/routes'


export const tradeRoutes = [
    {
        path: 'trade/accounts',
        name: 'accounts',
        component: () => import('@/pages/trade/account/Main.vue'),
        meta: routeMetaMap.accounts,
    },
    {
        path: 'trade/items',
        name: 'items',
        component: () => import('@/pages/trade/item/Main.vue'),
        meta: routeMetaMap.items,
    },
    {
        path: 'trade/watchlist',
        name: 'watchlist',
        component: () => import('@/pages/trade/watchlist/Main.vue'),
        meta: routeMetaMap.watchlist,
    },
    {
        path: 'trade/realtime',
        name: 'realtime',
        component: () => import('@/pages/trade/market/Main.vue'),
        meta: routeMetaMap.realtime,
    },
    {
        path: 'trade/analysis',
        name: 'tradeAnalysis',
        component: () => import('@/pages/trade/analysis/Main.vue'),
        meta: {
            menuUrl: '/trade/analysis',
        },
    },
    {
        path: 'trade/ops',
        name: 'tradeOps',
        component: () => import('@/pages/trade/ops/Main.vue'),
        meta: {
            menuUrl: '/trade/ops',
        },
    },
    {
        path: 'trade/masters',
        name: 'masters',
        component: () => import('@/pages/trade/masters/Main.vue'),
        meta: {
            menuUrl: '/trade/masters',
        },
    },
]
