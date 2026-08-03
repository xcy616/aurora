import Layout from '@/layout/index.vue'
import router from '@/router'
import { useAppStore } from '@/store'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const viewModules = import.meta.glob('@/views/**/*.vue')

let menuPromise = null
let routesAdded = false

export function generaMenu() {
  const store = useAppStore()
  if (routesAdded) {
    return Promise.resolve()
  }
  if (menuPromise) {
    return menuPromise
  }
  menuPromise = axios
    .get('/api/admin/user/menus')
    .then(({ data }) => {
      menuPromise = null
      if (data.flag) {
        let userMenus = data.data
        userMenus.forEach((item) => {
          if (item.icon != null) {
            item.icon = 'iconfont ' + item.icon
          }
          if (item.component == 'Layout') {
            item.component = Layout
          }
          if (item.children && item.children.length > 0) {
            item.children.forEach((route) => {
              route.icon = 'iconfont ' + route.icon
              route.component = loadView(route.component)
            })
          }
        })
        store.saveUserMenus(userMenus)
        userMenus.forEach((item) => {
          router.addRoute(item)
        })
        routesAdded = true
      } else {
        ElMessage.error(data.message)
        throw new Error(data.message)
      }
    })
    .catch((err) => {
      menuPromise = null
      throw err
    })
  return menuPromise
}

export function resetMenuRoutes() {
  routesAdded = false
  menuPromise = null
}

export function areMenuRoutesAdded() {
  return routesAdded
}

export const loadView = (view) => {
  return viewModules[`/src/views${view}`]
}
