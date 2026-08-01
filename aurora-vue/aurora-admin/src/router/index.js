import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: '登录',
    hidden: true,
    component: () => import('../views/login/Login.vue')
  }
]
const createRouterFn = () =>
  createRouter({
    history: createWebHistory(),
    routes: routes
  })
const router = createRouterFn()

export function resetRouter() {
  const newRouter = createRouterFn()
  router.matcher = newRouter.matcher
}

export default router
