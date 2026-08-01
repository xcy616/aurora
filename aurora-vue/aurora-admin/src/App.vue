<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { generaMenu } from '@/assets/js/menu'
import { useAppStore } from '@/store'
export default {
  setup() {
    const store = useAppStore()
    return { store }
  },
  created() {
    if (this.store.userInfo != null) {
      generaMenu()
    }
    this.axios.post('/api/report')
  }
}
</script>
