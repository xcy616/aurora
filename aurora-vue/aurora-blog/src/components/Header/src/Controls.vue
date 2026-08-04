<template>
  <div class="header-controls absolute top-10 right-0 flex flex-row" @keydown.k="handleOpenModel" tabindex="0">
    <span class="ob-drop-shadow" data-dia="search" @click="handleOpenModel">
      <svg-icon icon-class="search" />
    </span>
    <Dropdown v-if="multiLanguage === 1" @command="handleClick">
      <span class="ob-drop-shadow" data-dia="language">
        <svg-icon icon-class="globe" />
        <span v-if="$i18n.locale == 'cn'">中文</span>
        <span v-if="$i18n.locale == 'en'">EN</span>
      </span>
      <DropdownMenu>
        <DropdownItem name="en">English</DropdownItem>
        <DropdownItem name="cn">中文</DropdownItem>
      </DropdownMenu>
    </Dropdown>
    <template v-if="userInfo === ''">
      <span class="mr-3" @click="openLoginDialog">{{ t('settings.login') }}</span>
    </template>
    <template v-if="userInfo !== ''">
      <Dropdown hover>
        <span class="mr-2">
          <div class="flex-shrink-0">
            <div class="rounded-full ring-gray-100 overflow-hidden shaodw-lg w-9">
              <img class="avatar-img" :src="userInfo.avatar" alt="" />
            </div>
          </div>
        </span>
        <DropdownMenu>
          <template v-if="!isMobile">
            <DropdownItem @click="openUserCenter">{{ t('settings.personal-center') }}</DropdownItem>
          </template>
          <DropdownItem @click="logout">{{ t('settings.logout') }}</DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </template>
    <span no-hover-effect class="ob-drop-shadow" data-dia="light-switch">
      <ThemeToggle />
    </span>
  </div>
  <el-dialog v-model="loginDialogVisible" width="450px" :fullscreen="isMobile" custom-class="login-dialog" :show-close="false" :modal="false">
    <form class="login-card form" @submit.prevent="login">
      <svg class="login-close-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" @click="loginDialogVisible = false"><path fill="currentColor" d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z"></path></svg>
      <div class="flex-column">
        <label>Email</label>
      </div>
      <div class="inputForm">
        <svg height="20" viewBox="0 0 32 32" width="20" xmlns="http://www.w3.org/2000/svg"><g id="Layer_3" data-name="Layer 3"><path d="m30.853 13.87a15 15 0 0 0 -29.729 4.082 15.1 15.1 0 0 0 12.876 12.918 15.6 15.6 0 0 0 2.016.13 14.85 14.85 0 0 0 7.715-2.145 1 1 0 1 0 -1.031-1.711 13.007 13.007 0 1 1 5.458-6.529 2.149 2.149 0 0 1 -4.158-.759v-10.856a1 1 0 0 0 -2 0v1.726a8 8 0 1 0 .2 10.325 4.135 4.135 0 0 0 7.83.274 15.2 15.2 0 0 0 .823-7.455zm-14.853 8.13a6 6 0 1 1 6-6 6.006 6.006 0 0 1 -6 6z"></path></g></svg>
        <input v-model="loginInfo.username" type="text" class="input" placeholder="Enter your Email">
      </div>

      <div class="flex-column">
        <label>Password</label>
      </div>
      <div class="inputForm">
        <svg height="20" viewBox="-64 0 512 512" width="20" xmlns="http://www.w3.org/2000/svg"><path d="m336 512h-288c-26.453125 0-48-21.523438-48-48v-224c0-26.476562 21.546875-48 48-48h288c26.453125 0 48 21.523438 48 48v224c0 26.476562-21.546875 48-48 48zm-288-288c-8.8125 0-16 7.167969-16 16v224c0 8.832031 7.1875 16 16 16h288c8.8125 0 16-7.167969 16-16v-224c0-8.832031-7.1875-16-16-16zm0 0"></path><path d="m304 224c-8.832031 0-16-7.167969-16-16v-80c0-52.929688-43.070312-96-96-96s-96 43.070312-96 96v80c0 8.832031-7.167969 16-16 16s-16-7.167969-16-16v-80c0-70.59375 57.40625-128 128-128s128 57.40625 128 128v80c0 8.832031-7.167969 16-16 16zm0 0"></path></svg>
        <input v-model="loginInfo.password" :type="showPassword ? 'text' : 'password'" class="input" placeholder="Enter your Password">
        <svg class="password-eye" viewBox="0 0 576 512" height="1em" xmlns="http://www.w3.org/2000/svg" @click="showPassword = !showPassword"><path d="M288 32c-80.8 0-145.5 36.8-192.6 80.6C48.6 156 17.3 208 2.5 243.7c-3.3 7.9-3.3 16.7 0 24.6C17.3 304 48.6 356 95.4 399.4C142.5 443.2 207.2 480 288 480s145.5-36.8 192.6-80.6c46.8-43.5 78.1-95.4 93-131.1c3.3-7.9 3.3-16.7 0-24.6c-14.9-35.7-46.2-87.7-93-131.1C433.5 68.8 368.8 32 288 32zM144 256a144 144 0 1 1 288 0 144 144 0 1 1 -288 0zm144-64c0 35.3-28.7 64-64 64c-7.1 0-13.9-1.2-20.3-3.3c-5.5-1.8-11.9 1.6-11.7 7.4c.3 6.9 1.3 13.8 3.2 20.7c13.7 51.2 66.4 81.6 117.6 67.9s81.6-66.4 67.9-117.6c-11.1-41.5-47.8-69.4-88.6-71.1c-5.8-.2-9.2 6.1-7.4 11.7c2.1 6.4 3.3 13.2 3.3 20.3z"></path></svg>
      </div>

      <div class="flex-row">
        <div>
          <input id="remember-me" type="checkbox" v-model="rememberMe">
          <label for="remember-me">Remember me</label>
        </div>
        <span class="span" @click="openForgetPasswordDialog">Forgot password?</span>
      </div>
      <button class="button-submit" type="submit" :disabled="loginLoading">
        <span v-if="loginLoading" class="btn-loading-spinner"></span>
        Sign In
      </button>
      <p class="p">Don't have an account? <span class="span" @click="openRegisterDialog">Sign Up</span></p>
      <p class="p line">Or With</p>
      <div class="flex-row">
        <button class="btn qq-login-btn" type="button" @click="qqLogin">
          <svg viewBox="0 0 32 32" width="18" height="18" xmlns="http://www.w3.org/2000/svg"><path d="M29.606 20.855c-0.977-0.552-2.063-1.104-3.146-1.489 0.273-1.042 0.479-2.318 0.479-3.575 0-6.494-5.37-11.792-11.97-11.792-6.6 0-11.97 5.298-11.97 11.792 0 1.257 0.205 2.533 0.479 3.575-1.083 0.385-2.168 0.937-3.146 1.489-0.958 0.541-0.958 1.448 0 1.99 0.703 0.397 1.492 0.803 2.292 1.173 0.799 0.37 1.613 0.702 2.385 0.954-0.172 1.054-0.188 2.155-0.003 3.22 0.477 2.745 2.518 4.788 4.552 4.788 1.646 0 2.975-1.082 3.585-2.48 0.706 1.33 2.047 2.317 3.673 2.317 1.882 0 3.778-1.813 4.32-4.322 0.209-0.969 0.224-1.971 0.045-2.94 0.772-0.252 1.586-0.584 2.385-0.954 0.8-0.37 1.589-0.776 2.292-1.173 0.958-0.542 0.958-1.449 0-1.99zM12.887 25.246c-1.132 0-2.05-1.562-2.05-3.489 0-1.927 0.918-3.489 2.05-3.489s2.05 1.562 2.05 3.489c0 1.927-0.918 3.489-2.05 3.489zM19.188 25.246c-1.132 0-2.05-1.562-2.05-3.489 0-1.927 0.918-3.489 2.05-3.489s2.05 1.562 2.05 3.489c0 1.927-0.918 3.489-2.05 3.489z"></path></svg>
          QQ 登录
        </button>
      </div>
    </form>
  </el-dialog>
  <el-dialog v-model="registerDialogVisible" width="30%" :fullscreen="isMobile" custom-class="login-dialog" :modal="false">
    <el-form>
      <el-form-item model="userInfo" class="mt-5">
        <el-input v-model="loginInfo.username" placeholder="邮箱" />
      </el-form-item>
      <el-form-item model="userInfo" class="mt-8">
        <el-input v-model="loginInfo.code" placeholder="验证码">
          <template #append>
            <span class="text" @click="sendCode">发送</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item model="userInfo" type="password" class="mt-8">
        <el-input v-model="loginInfo.password" type="password" show-password placeholder="密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="register" size="large" class="mx-auto mt-3">注册</el-button>
      </el-form-item>
      <span class="text" @click="returnLoginDialog">已有帐号?登录</span>
    </el-form>
  </el-dialog>
  <el-dialog v-model="forgetPasswordDialogVisible" width="30%" :fullscreen="isMobile" custom-class="login-dialog" :modal="false">
    <el-form>
      <el-form-item model="userInfo" class="mt-5">
        <el-input v-model="loginInfo.username" placeholder="邮箱" />
      </el-form-item>
      <el-form-item model="userInfo" class="mt-8">
        <el-input v-model="loginInfo.code" placeholder="验证码">
          <template #append>
            <span class="text" @click="sendCode">发送</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item model="userInfo" type="password" class="mt-8">
        <el-input v-model="loginInfo.password" type="password" show-password placeholder="新密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updatePassword" size="large" class="mx-auto mt-3">确定</el-button>
      </el-form-item>
      <span class="text" @click="returnLoginDialog">返回登录</span>
    </el-form>
  </el-dialog>
  <el-dialog v-model="articlePasswordDialogVisible" width="30%" :fullscreen="isMobile" custom-class="login-dialog" :modal="false">
    <el-form @submit.native.prevent @keyup.enter.native="accessArticle">
      <el-form-item model="userInfo" class="mt-5">
        <el-input id="article-password-input" v-model="articlePassword" placeholder="文章受密码保护,请输入密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="accessArticle" size="large" class="mx-auto mt-3">校验密码</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
  <teleport to="body">
    <SearchModel />
  </teleport>
</template>

<script lang="ts">
import { computed, defineComponent, toRef, toRefs, reactive, getCurrentInstance, nextTick } from 'vue'
import { Dropdown, DropdownMenu, DropdownItem } from '@/components/Dropdown'
import { useAppStore } from '@/stores/app'
import { useCommonStore } from '@/stores/common'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
import ThemeToggle from '@/components/ToggleSwitch/ThemeToggle.vue'
import api from '@/api/api'
import SearchModel from '@/components/SearchModel.vue'
import { useSearchStore } from '@/stores/search'
import config from '@/config/config'
import { useI18n } from 'vue-i18n'
import emitter from '@/utils/mitt'

export default defineComponent({
  name: 'Controls',
  components: {
    Dropdown,
    DropdownMenu,
    DropdownItem,
    ThemeToggle,
    SearchModel
  },
  setup() {
    const { t } = useI18n()
    const proxy: any = getCurrentInstance()?.appContext.config.globalProperties
    const appStore = useAppStore()
    const commonStore = useCommonStore()
    const userStore = useUserStore()
    const searchStore = useSearchStore()
    const route = useRoute()
    const router = useRouter()
    const loginInfo = reactive({
      username: '' as any,
      password: '' as any,
      code: '' as any
    })
    const reactiveDate = reactive({
      loginDialogVisible: false,
      registerDialogVisible: false,
      forgetPasswordDialogVisible: false,
      articlePasswordDialogVisible: false,
      articlePassword: '',
      articleId: '',
      rememberMe: false,
      showPassword: false,
      loginLoading: false
    })
    emitter.on('changeArticlePasswordDialogVisible', (articleId: any) => {
      reactiveDate.articlePasswordDialogVisible = true
      reactiveDate.articlePassword = ''
      reactiveDate.articleId = articleId
      nextTick(() => {
        document.getElementById('article-password-input')?.focus()
      })
    })
    const handleClick = (name: string): void => {
      appStore.changeLocale(name)
    }
    const login = () => {
      if (reactiveDate.loginLoading) {
        return
      }
      if (loginInfo.username.trim().length == 0 || loginInfo.password.trim().length == 0) {
        proxy.$notify({
          title: 'Warning',
          message: '账号或者密码不能为空',
          type: 'warning'
        })
        return
      }
      reactiveDate.loginLoading = true
      let params = new URLSearchParams()
      params.append('username', loginInfo.username)
      params.append('password', loginInfo.password)
      api
        .login(params)
        .then(({ data }) => {
          if (data.flag) {
            userStore.userInfo = data.data
            sessionStorage.setItem('token', data.data.token)
            userStore.token = data.data.token
            // 勾选“记住我”后，关闭浏览器重新打开仍保持登录
            if (reactiveDate.rememberMe) {
              localStorage.setItem('rememberUserInfo', JSON.stringify(data.data))
              localStorage.setItem('rememberToken', data.data.token)
            } else {
              localStorage.removeItem('rememberUserInfo')
              localStorage.removeItem('rememberToken')
            }
            proxy.$notify({
              title: 'Success',
              message: '登录成功',
              type: 'success'
            })
            reactiveDate.loginDialogVisible = false
          } else {
            proxy.$notify({
              title: 'Warning',
              message: data.message,
              type: 'warning'
            })
          }
        })
        .catch(() => {
          proxy.$notify({
            title: 'Error',
            message: '网络异常，请稍后重试',
            type: 'error'
          })
        })
        .finally(() => {
          reactiveDate.loginLoading = false
        })
    }
    const logout = () => {
      api.logout().then(({ data }) => {
        if (data.flag) {
          userStore.userInfo = ''
          userStore.token = ''
          userStore.accessArticles = []
          sessionStorage.removeItem('token')
          localStorage.removeItem('rememberUserInfo')
          localStorage.removeItem('rememberToken')
          proxy.$notify({
            title: 'Success',
            message: '登出成功',
            type: 'success'
          })
        }
      })
    }
    const openUserCenter = () => {
      userStore.userVisible = true
    }
    const openLoginDialog = () => {
      reactiveDate.loginDialogVisible = true
    }
    const openRegisterDialog = () => {
      loginInfo.code = ''
      reactiveDate.loginDialogVisible = false
      reactiveDate.registerDialogVisible = true
    }
    const returnLoginDialog = () => {
      reactiveDate.registerDialogVisible = false
      reactiveDate.forgetPasswordDialogVisible = false
      reactiveDate.loginDialogVisible = true
    }
    const openForgetPasswordDialog = () => {
      loginInfo.code = ''
      reactiveDate.loginDialogVisible = false
      reactiveDate.forgetPasswordDialogVisible = true
    }
    const sendCode = () => {
      api.sendValidationCode(loginInfo.username).then(({ data }) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '验证码已发送',
            type: 'success'
          })
        }
      })
    }
    const register = () => {
      let params = {
        code: loginInfo.code,
        username: loginInfo.username,
        password: loginInfo.password
      }
      api.register(params).then(({ data }) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '注册成功',
            type: 'success'
          })
          reactiveDate.registerDialogVisible = false
          reactiveDate.loginDialogVisible = true
        }
      })
    }
    const handleOpenModel: any = (status: boolean) => {
      searchStore.setOpenModal(status)
    }

    const qqLogin = () => {
      userStore.currentUrl = route.path
      reactiveDate.loginDialogVisible = false
      if (commonStore.isMobile) {
        //@ts-ignore
        QC.Login.showPopup({
          appId: config.qqLogin.QQ_APP_ID,
          redirectURI: config.qqLogin.QQ_REDIRECT_URI
        })
      } else {
        window.open(
          'https://graph.qq.com/oauth2.0/show?which=Login&display=pc&client_id=' +
            +config.qqLogin.QQ_APP_ID +
            '&response_type=token&scope=all&redirect_uri=' +
            config.qqLogin.QQ_REDIRECT_URI,
          '_self'
        )
      }
    }
    const updatePassword = () => {
      api.updatePassword(loginInfo).then(({ data }) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '修改成功',
            type: 'success'
          })
          reactiveDate.forgetPasswordDialogVisible = false
          reactiveDate.loginDialogVisible = true
        }
      })
    }
    const accessArticle = () => {
      if (reactiveDate.articlePassword.trim().length == 0) {
        proxy.$notify({
          title: 'Warning',
          message: '密码不能为空',
          type: 'warning'
        })
        return
      }
      api
        .accessArticle({
          articleId: reactiveDate.articleId,
          articlePassword: reactiveDate.articlePassword
        })
        .then(({ data }) => {
          if (data.flag) {
            reactiveDate.articlePasswordDialogVisible = false
            userStore.accessArticles.push(reactiveDate.articleId)
            router.push({ path: '/articles/' + reactiveDate.articleId })
          }
        })
    }
    return {
      handleOpenModel,
      loginInfo,
      ...toRefs(reactiveDate),
      userInfo: toRef(userStore.$state, 'userInfo'),
      isMobile: toRef(commonStore.$state, 'isMobile'),
      login,
      qqLogin,
      logout,
      handleClick,
      openUserCenter,
      openLoginDialog,
      openRegisterDialog,
      returnLoginDialog,
      sendCode,
      register,
      updatePassword,
      openForgetPasswordDialog,
      accessArticle,
      multiLanguage: computed(() => {
        let websiteConfig: any = appStore.websiteConfig
        return websiteConfig.multiLanguage
      }),
      t
    }
  }
})
</script>
<style lang="scss">
.el-dialog.login-dialog {
  background-color: transparent !important;
  border-radius: 20px !important;
  overflow: hidden !important;
  padding: 0 !important;
}
.login-dialog .el-dialog__body {
  padding: 0;
}
/* 注册/忘记密码/文章密码弹窗：el-form 本身作为圆角卡片 */
.login-dialog .el-form {
  background-color: var(--background-secondary);
  border-radius: 20px;
  padding: 30px;
  margin: 0;
}
/* 清除组件内部自动渲染的空 header（无标题时仍会渲染） */
.login-dialog .el-dialog__header {
  display: none;
}
/* 自定义关闭图标：放在卡片圆角框内右上角 */
.login-close-icon {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  padding: 6px;
  box-sizing: border-box;
  background: var(--background-primary);
  border-radius: 8px;
  color: var(--text-normal);
  cursor: pointer;
  z-index: 1;
  transition: background 0.2s ease-in-out, transform 0.2s ease-in-out;
}
.login-close-icon:hover {
  background: var(--text-sub-accent);
  color: #fff;
  transform: rotate(90deg);
}
/* 登录卡片表单样式（适配亮/暗主题） */
.login-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: var(--background-secondary);
  padding: 30px;
  width: 100%;
  border-radius: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  box-sizing: border-box;
  ::placeholder {
    color: var(--text-faint);
  }
  .flex-column > label {
    color: var(--text-normal);
    font-weight: 600;
  }
  .inputForm {
    border: 1.5px solid var(--background-primary-alt);
    border-radius: 10px;
    height: 50px;
    display: flex;
    align-items: center;
    padding-left: 10px;
    transition: 0.2s ease-in-out;
    background: var(--background-primary);
    svg {
      color: var(--text-dim);
      flex-shrink: 0;
    }
  }
  .input {
    margin-left: 10px;
    border-radius: 10px;
    border: none;
    width: 85%;
    height: 100%;
    background: transparent;
    color: var(--text-normal);
    font-size: 14px;
    &:focus {
      outline: none;
    }
  }
  .inputForm:focus-within {
    border: 1.5px solid var(--text-sub-accent);
  }
  .flex-row {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 10px;
    justify-content: space-between;
    > div > label {
      font-size: 14px;
      color: var(--text-normal);
      font-weight: 400;
      margin-left: 4px;
      cursor: pointer;
    }
  }
  .span {
    font-size: 14px;
    margin-left: 5px;
    color: var(--text-sub-accent);
    font-weight: 500;
    cursor: pointer;
  }
  .button-submit {
    margin: 20px 0 10px 0;
    background: var(--main-gradient);
    border: none;
    color: white;
    font-size: 15px;
    font-weight: 500;
    border-radius: 10px;
    height: 50px;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    cursor: pointer;
    transition: opacity 0.2s ease-in-out;
    &:hover:not(:disabled) {
      opacity: 0.85;
    }
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
    .btn-loading-spinner {
      width: 16px;
      height: 16px;
      border: 2px solid rgba(255, 255, 255, 0.35);
      border-top-color: #fff;
      border-radius: 50%;
      animation: btn-loading-rotate 0.6s linear infinite;
    }
    @keyframes btn-loading-rotate {
      to {
        transform: rotate(360deg);
      }
    }
  }
  .p {
    text-align: center;
    color: var(--text-normal);
    font-size: 14px;
    margin: 5px 0;
    &.line {
      display: flex;
      align-items: center;
      gap: 10px;
      color: var(--text-dim);
      &::before,
      &::after {
        content: '';
        flex: 1;
        height: 1px;
        background: var(--background-primary-alt);
      }
    }
  }
  .btn {
    margin-top: 10px;
    width: 100%;
    height: 50px;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: 500;
    gap: 10px;
    border: 1px solid var(--background-primary-alt);
    background-color: var(--background-primary);
    color: var(--text-normal);
    cursor: pointer;
    transition: 0.2s ease-in-out;
    &:hover {
      border: 1px solid var(--text-sub-accent);
    }
  }
  .qq-login-btn {
    color: #12b7f5;
    font-size: 15px;
  }
  .password-eye {
    cursor: pointer;
    margin-right: 10px;
  }
  input[type='checkbox'] {
    accent-color: var(--text-sub-accent);
    cursor: pointer;
  }
}
.my-el-button {
  width: 300px !important;
}
.el-button {
  width: 300px;
}
.el-dialog__headerbtn {
  outline: none !important;
}
.el-input-group__append {
  background-color: var(--background-primary-alt) !important;
}
.el-form-item__label {
  text-align: left;
  width: 70px;
  color: var(--text-normal) !important;
}
.el-input__inner {
  color: var(--text-normal) !important;
  background-color: var(--background-primary-alt) !important;
}
.el-input__wrapper {
  background: var(--background-primary-alt) !important;
}
</style>
<style lang="scss" scoped>
.text {
  color: var(--text-normal);
  cursor: pointer;
}
#submit-button {
  outline: none;
  background: #0fb6d6;
}
.header-controls {
  span {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    cursor: pointer;
    transition: opacity 250ms ease;
    padding-right: 0.5rem;
    &[no-hover-effect] {
      &:hover {
        opacity: 1;
      }
    }
    &:hover {
      opacity: 0.5;
    }
    .svg-icon {
      stroke: #fff;
      height: 2rem;
      width: 2rem;
      margin-right: 0.5rem;
      pointer-events: none;
    }
  }
  .search-bar {
    @apply bg-transparent flex flex-row px-0 mr-2 rounded-full;
    opacity: 0;
    width: 0;
    transition: 300ms all ease-out;
    &.active {
      @apply bg-ob-deep-800;
      opacity: 0.95;
      width: 200px;
      imput {
        width: initial;
      }
    }
    &:focus {
      appearance: none;
      outline: none;
    }
    input {
      @apply flex flex-1 bg-transparent text-ob-normal px-6 box-border;
      width: 0;
      appearance: none;
      outline: none;
    }
    svg {
      @apply float-right;
    }
  }
}
.avatar-img {
  transition-property: transform;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 800ms;
  transform: rotate(-360deg);
}
.avatar-img:hover {
  transform: rotate(360deg);
}
</style>
