<template>
  <div class="countdown-wrapper">
    <span>{{ text }}</span>
    <div class="countdown-tip">
      <span class="countdown-dot"></span>
      <span><b class="countdown-num">{{ remaining }}</b>秒后自动关闭</span>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, onUnmounted, ref } from 'vue'

export default defineComponent({
  name: 'CountdownText',
  props: {
    text: {
      type: String,
      default: ''
    }
  },
  setup() {
    const remaining = ref(3)
    let timer: number | null = null

    onMounted(() => {
      timer = window.setInterval(() => {
        remaining.value--
        if (remaining.value <= 0) {
          window.clearInterval(timer as number)
        }
      }, 1000)
    })

    onUnmounted(() => {
      if (timer) {
        window.clearInterval(timer)
      }
    })

    return { remaining }
  }
})
</script>

<style scoped>
/* 为右上角倒计时预留空间，避免长文本顶到关闭按钮 */
.countdown-wrapper {
  padding-right: 130px;
}

/* 倒计时：定位在关闭图标左侧，垂直对齐 */
.countdown-tip {
  position: absolute;
  top: 12px;
  right: 42px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-dim);
  white-space: nowrap;
}

.countdown-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--main-gradient);
}

.countdown-num {
  font-weight: 700;
  color: var(--text-accent);
}
</style>
