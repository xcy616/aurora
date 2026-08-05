import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      'cytoscape/dist/cytoscape.umd.js': 'cytoscape/dist/cytoscape.esm.mjs'
    }
  },
  server: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  build: {
    chunkSizeWarningLimit: 1500,
    rolldownOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules/element-plus') || id.includes('node_modules/@element-plus')) {
            return 'element-plus'
          }
          if (id.includes('node_modules/echarts') || id.includes('node_modules/vue-echarts')) {
            return 'echarts'
          }
          if (id.includes('node_modules/mermaid')) {
            return 'mermaid'
          }
          if (id.includes('node_modules/mavon-editor')) {
            return 'mavon-editor'
          }
          if (
            id.includes('node_modules/markdown-it') ||
            id.includes('node_modules/@iktakahiro') ||
            id.includes('node_modules/@agoose77')
          ) {
            return 'markdown-it'
          }
        }
      }
    }
  }
})
