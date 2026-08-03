<template>
  <div ref="wrapper" class="tag-cloud">
    <span
      v-for="(tag, index) in data"
      :key="index"
      ref="tagRefs"
      class="tag-cloud-item"
      @click="clickTag(tag)"
      @dblclick="dbclickTag">
      {{ tag.tagName }}
    </span>
  </div>
</template>

<script>
export default {
  name: 'TagCloud',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    config: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      option: {
        radius: 120,
        maxFont: 24,
        color: null,
        rotateAngleXbase: 500,
        rotateAngleYbase: 500,
        hover: false
      },
      tagList: [],
      timer: null,
      rotateAngleX: 0,
      rotateAngleY: 0
    }
  },
  created() {
    if (this.config != null) {
      this.option = Object.assign({}, this.option, this.config)
    }
  },
  mounted() {
    this.initTags()
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
    if (this.$refs.wrapper) {
      this.$refs.wrapper.onmousemove = null
    }
  },
  watch: {
    data() {
      this.$nextTick(() => {
        this.initTags()
      })
    }
  },
  methods: {
    initTags() {
      const wrapper = this.$refs.wrapper
      if (!wrapper) {
        return
      }
      this.rotateAngleX = Math.PI / this.option.rotateAngleXbase
      this.rotateAngleY = Math.PI / this.option.rotateAngleYbase
      if (this.option.hover) {
        wrapper.onmousemove = (e) => {
          this.rotateAngleY = (e.pageX - wrapper.offsetLeft - wrapper.offsetWidth / 2) / 10000
          this.rotateAngleX = -(e.pageY - wrapper.offsetTop - wrapper.offsetHeight / 2) / 10000
        }
      } else {
        wrapper.onmousemove = null
      }
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
      this.tagList = []
      const tags = this.$refs.tagRefs || []
      const count = this.data.length
      for (let i = 0; i < count; i++) {
        const arc = Math.acos((2 * (i + 1) - 1) / count - 1)
        const rad = arc * Math.sqrt(count * Math.PI)
        const x = this.option.radius * Math.sin(arc) * Math.cos(rad)
        const y = this.option.radius * Math.sin(arc) * Math.sin(rad)
        const z = this.option.radius * Math.cos(arc)
        const el = tags[i]
        if (el) {
          el.style.color =
            this.option.color ||
            `rgb(${Math.round(255 * Math.random())},${Math.round(255 * Math.random())},${Math.round(255 * Math.random())})`
        }
        this.tagList.push({ x, y, z, ele: el })
      }
      this.timer = setInterval(() => {
        for (let i = 0; i < this.tagList.length; i++) {
          this.rotateX(this.tagList[i])
          this.rotateY(this.tagList[i])
          this.setPosition(this.tagList[i], this.option.radius, this.option.maxFont)
        }
      }, 20)
    },
    setPosition(t, radius, maxFont) {
      const wrapper = this.$refs.wrapper
      if (wrapper && t.ele) {
        t.ele.style.transform = `translate(${t.x + wrapper.offsetWidth / 2 - t.ele.offsetWidth / 2}px,${
          t.y + wrapper.offsetHeight / 2 - t.ele.offsetHeight / 2
        }px)`
        t.ele.style.opacity = t.z / radius / 2 + 0.7
        t.ele.style.fontSize = (t.z / radius / 2 + 0.5) * maxFont + 'px'
      }
    },
    rotateX(t) {
      const cos = Math.cos(this.rotateAngleX)
      const sin = Math.sin(this.rotateAngleX)
      const y = t.y * cos - t.z * sin
      const z = t.y * sin + t.z * cos
      t.y = y
      t.z = z
    },
    rotateY(t) {
      const cos = Math.cos(this.rotateAngleY)
      const sin = Math.sin(this.rotateAngleY)
      const x = t.z * sin + t.x * cos
      const z = t.z * cos - t.x * sin
      t.x = x
      t.z = z
    },
    dbclickTag() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      } else {
        this.timer = setInterval(() => {
          for (let i = 0; i < this.tagList.length; i++) {
            this.rotateX(this.tagList[i])
            this.rotateY(this.tagList[i])
            this.setPosition(this.tagList[i], this.option.radius, this.option.maxFont)
          }
        }, 20)
      }
    },
    clickTag(tag) {
      this.$emit('clickTag', tag)
    }
  }
}
</script>

<style scoped>
.tag-cloud {
  position: relative;
  height: 320px;
}
.tag-cloud-item {
  position: absolute;
  top: 0;
  left: 0;
  cursor: pointer;
  white-space: nowrap;
  user-select: none;
}
</style>
