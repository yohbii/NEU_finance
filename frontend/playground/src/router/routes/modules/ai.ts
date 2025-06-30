import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'ic:baseline-view-in-ar',
      keepAlive: true,
      order: 1001,
      title: $t('ai.title'),
    },
    name: 'ai',
    path: '/ai',
    children: [],
  },
];

export default routes;
