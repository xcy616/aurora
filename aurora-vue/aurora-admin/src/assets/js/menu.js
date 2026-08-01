import Layout from '@/layout/index.vue'
import router from '@/router'
import { useAppStore } from '@/store'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const viewModules = import.meta.glob('@/views/**/*.vue')

export function generaMenu() {
  axios.get('/api/admin/user/menus').then(({ data }) => {
    if (data.flag) {
      const store = useAppStore()
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
    } else {
      ElMessage.error(data.message)
      router.push({ path: '/login' })
    }
  })
}

export const loadView = (view) => {
  return viewModules[`/src/views${view}`]
}
