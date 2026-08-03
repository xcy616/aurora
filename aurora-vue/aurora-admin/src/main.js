import { createApp, h } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue'
import axios from 'axios'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import VueECharts from 'vue-echarts'
import * as echarts from 'echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, MapChart, PieChart } from 'echarts/charts'
import { LegendComponent, TitleComponent, TooltipComponent, VisualMapComponent } from 'echarts/components'
import { CalendarHeatmap } from 'vue3-calendar-heatmap'
import TagCloud from './components/tag-cloud.vue'
import config from './assets/js/config'
import dayjs from 'dayjs'
import Md_Katex from '@iktakahiro/markdown-it-katex'
import mermaidPlugin from '@agoose77/markdown-it-mermaid'
import './assets/css/index.css'
import './assets/css/element-icons.css'
import './assets/css/iconfont.css'
import './assets/js/china'
import { useAppStore } from './store'
import { generaMenu, areMenuRoutesAdded } from './assets/js/menu'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  MapChart,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  VisualMapComponent
])

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// 路由守卫必须在 app.use(router) 之前注册，否则首次导航不会经过守卫，
// 会导致刷新后（或直接访问 /）路由无匹配、页面空白。
router.beforeEach(async (to) => {
  NProgress.start()
  const store = useAppStore()
  if (to.path == '/login') {
    return true
  }
  if (!store.userInfo) {
    return { path: '/login' }
  }
  if (!areMenuRoutesAdded()) {
    try {
      await generaMenu()
    } catch (e) {
      return { path: '/login' }
    }
    // vue-router 4 在守卫执行前就已解析目标路由；若路由是本次才动态添加的，
    // 需要重新导航一次才能命中新路由，否则会停在"无匹配"的空白页。
    return to.fullPath
  }
  return true
})
router.afterEach(() => {
  NProgress.done()
})

app.use(router)
app.use(ElementPlus)
app.use(mavonEditor)

// 全局注册 Element Plus 图标组件
Object.entries(Icons).forEach(([name, component]) => {
  app.component(name, component)
})

app.component('v-chart', VueECharts)
app.component('calendar-heatmap', CalendarHeatmap)
app.component('tag-cloud', TagCloud)

// 兼容 Element UI 的字符串图标写法：icon="el-icon-xxx" / prefix-icon="el-icon-xxx"
// Element Plus 会把字符串 icon 当作组件名解析，这里注册为渲染 <i class="..."> 的组件，
// 配合 element-icons 字体即可保持原有图标显示。
const FONT_ICON_NAMES = [
  'el-icon-caret-right',
  'el-icon-delete',
  'el-icon-deleteItem',
  'el-icon-download',
  'el-icon-picture',
  'el-icon-plus',
  'el-icon-refresh',
  'el-icon-search',
  'el-icon-s-operation',
  'el-icon-success',
  'el-icon-upload',
  'el-icon-user-solid',
  'el-icon-view'
]
FONT_ICON_NAMES.forEach((name) => {
  app.component(name, {
    render: () => h('i', { class: name })
  })
})

const globalProperties = app.config.globalProperties
globalProperties.config = config
globalProperties.$moment = dayjs
globalProperties.axios = axios
globalProperties.$date = (value, formatStr = 'YYYY-MM-DD') => dayjs(value).format(formatStr)
globalProperties.$dateTime = (value, formatStr = 'YYYY-MM-DD HH:mm:ss') => dayjs(value).format(formatStr)

if (mavonEditor.markdownIt) {
  mavonEditor.markdownIt.set({}).use(Md_Katex).use(mermaidPlugin)
}

NProgress.configure({
  easing: 'ease',
  speed: 500,
  showSpinner: false,
  trickleSpeed: 200,
  minimum: 0.3
})

axios.interceptors.request.use((config) => {
  config.headers['Authorization'] = 'Bearer ' + sessionStorage.getItem('token')
  return config
})

axios.interceptors.response.use(
  (response) => {
    switch (response.data.code) {
      case 40001:
        globalProperties.$message({
          type: 'error',
          message: response.data.message
        })
        router.push({ path: '/login' })
        break
      case 50000:
        globalProperties.$message({
          type: 'error',
          message: response.data.message
        })
        break
    }
    return response
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 从 sessionStorage 恢复并持久化 Pinia 状态
const store = useAppStore()
try {
  const saved = sessionStorage.getItem('aurora-admin')
  if (saved) {
    store.$patch(JSON.parse(saved))
  }
} catch (e) {
  sessionStorage.removeItem('aurora-admin')
}
store.$subscribe((mutation, state) => {
  sessionStorage.setItem('aurora-admin', JSON.stringify(state))
})

app.mount('#app')
