import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    collapse: false,
    tabList: [{ name: '首页', path: '/' }],
    userInfo: null,
    userMenus: [],
    pageState: {
      articleList: 1,
      category: 1,
      tag: 1,
      comment: 1,
      talkList: 1,
      user: 1,
      online: 1,
      role: 1,
      quartz: 1,
      friendLink: 1,
      operationLog: 1,
      exceptionLog: 1,
      quartzLog: {
        jobId: -1,
        current: 1
      },
      photo: {
        albumId: -1,
        current: 1
      }
    }
  }),
  actions: {
    saveTab(tab) {
      if (this.tabList.findIndex((item) => item.path === tab.path) == -1) {
        this.tabList.push({ name: tab.name, path: tab.path })
      }
    },
    removeTab(tab) {
      const index = this.tabList.findIndex((item) => item.name === tab.name)
      this.tabList.splice(index, 1)
    },
    resetTab() {
      this.tabList = [{ name: '首页', path: '/' }]
    },
    trigger() {
      this.collapse = !this.collapse
    },
    login(user) {
      sessionStorage.setItem('token', user.token)
      this.userInfo = user
    },
    saveUserMenus(userMenus) {
      this.userMenus = userMenus
    },
    logout() {
      this.userInfo = null
      sessionStorage.removeItem('token')
      this.userMenus = []
    },
    updateAvatar(avatar) {
      this.userInfo.avatar = avatar
    },
    updateUserInfo(user) {
      this.userInfo.nickname = user.nickname
      this.userInfo.intro = user.intro
      this.userInfo.webSite = user.webSite
    },
    updateArticleListPageState(current) {
      this.pageState.articleList = current
    },
    updateCategoryPageState(current) {
      this.pageState.category = current
    },
    updateTagPageState(current) {
      this.pageState.tag = current
    },
    updateCommentPageState(current) {
      this.pageState.comment = current
    },
    updateTalkListPageState(current) {
      this.pageState.talkList = current
    },
    updateUserPageState(current) {
      this.pageState.user = current
    },
    updateOnlinePageState(current) {
      this.pageState.online = current
    },
    updateRolePageState(current) {
      this.pageState.role = current
    },
    updateQuartzPageState(current) {
      this.pageState.quartz = current
    },
    updateFriendLinkPageState(current) {
      this.pageState.friendLink = current
    },
    updateOperationLogPageState(current) {
      this.pageState.operationLog = current
    },
    updateExceptionLogPageState(current) {
      this.pageState.exceptionLog = current
    },
    updateQuartzLogPageState(quartzLog) {
      this.pageState.quartzLog.jobId = quartzLog.jobId
      this.pageState.quartzLog.current = quartzLog.current
    },
    updatePhotoPageState(photo) {
      this.pageState.photo.albumId = photo.albumId
      this.pageState.photo.current = photo.current
    }
  }
})
