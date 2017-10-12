/**
 * vue的路由配置
 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import store from 'store'
import { request_login, result_code } from 'common/request_api'

Vue.use(VueRouter)
//使用AMD方式 懒加载
// component: resolve => require(['pages/home'),
// () => import('../../component/historyTab/historyTab.vue')
const routes = [{
  path: '/', name: 'home',
  meta: { title: "主页" },
  components: { default: () => import('pages/home') }
},

  // 用户管理路由   开始
  {
    path: '/user/base', name: 'userBase',
    meta: { title: "用户列表" },
    components: { default: () => import('pages/user/base') },
  }, {
    path: '/user/update/:id', name: 'userUpdate',
    meta: { title: "用户修改" },
    components: { default: () => import('pages/user/save') },
  }, {
    path: '/user/add', name: 'userAdd',
    meta: { title: "添加用户" },
    components: { default: () => import('pages/user/save') },
  }, {
    path: '/user/changeGroup/:id', name: 'userChangeGroup',
    meta: { title: "换岗" },
    components: { default: () => import('pages/user/changeGroup') },
  }, {
    path: '/user/setHierarchy/:id', name: 'setHierarchy',
    meta: { title: "设置层级" },
    components: { default: () => import('pages/user/setHierarchy') },
  }, {
    path: '/user/disableRole/:id', name: 'disableRole',
    meta: { title: "角色禁用" },
    components: { default: () => import('pages/user/disableRole') },
  }, {
    path: '/user/disablePrivilege/:id', name: 'disablePrivilege',
    meta: { title: "权限禁用" },
    components: { default: () => import('pages/user/disablePrivilege') },
  },
  {
    path: '/user/viewPrivilege/:id', name: 'viewPrivilege',
    meta: { title: "视图权限" },
    components: { default: () => import('pages/user/viewPrivilege') },
  },
  {
    path: '/user/setRole/:id', name: 'userSetRole',
    meta: { title: "用户角色授权" },
    components: { default: () => import('pages/user/setRole') },
  },
  // 用户管理路由   结束

  // 权限管理路由   开始
  {
    path: '/privilege/base', name: 'privilegeBase',
    meta: { title: "权限列表" },
    components: { default: () => import('pages/privilege/base') },
  }, {
    path: '/privilege/update/:id', name: 'privilegeUpdate',
    meta: { title: "权限修改" },
    components: { default: () => import('pages/privilege/save') },
  }, {
    path: '/privilege/add', name: 'privilegeAdd',
    meta: { title: "添加权限" },
    components: { default: () => import('pages/privilege/save') },
  }, {
    path: '/privilege/setProcess/:id', name: 'setPrivilegeProcess',
    meta: { title: "设置审核流程" },
    components: { default: () => import('pages/privilege/setProcess') },
  },
  {
    path: '/privilege/setOwner/:id', name: 'setPrivilegeOwner',
    meta: { title: "设置权限owner" },
    components: { default: () => import('pages/privilege/setOwner') },
  },
  {
    path: '/privilege/getJoinUser/:id', name: 'getPrivilegeJoinUser',
    meta: { title: "权限关联用户" },
    components: { default: () => import('pages/privilege/getJoinUser') },
  },
  {
    path: '/privilege/getJoinRole/:id', name: 'getPrivilegeJoinRole',
    meta: { title: "权限关联角色" },
    components: { default: () => import('pages/privilege/getJoinRole') },
  },
  {
    path: '/privilege/getJoinApply/:id', name: 'getPrivilegeJoinApply',
    meta: { title: "权限被申请中列表" },
    components: { default: () => import('pages/privilege/getJoinApply') },
  },
  // 权限管理路由   结束

  // 角色管理路由   开始
  {
    path: '/role/base', name: 'roleBase',
    meta: { title: "角色列表" },
    components: { default: () => import('pages/role/base') },
  }, {
    path: '/role/update/:id', name: 'roleUpdate',
    meta: { title: "角色修改" },
    components: { default: () => import('pages/role/save') },
  }, {
    path: '/role/add', name: 'roleAdd',
    meta: { title: "添加角色" },
    components: { default: () => import('pages/role/save') },
  }, {
    path: '/role/setProcess/:id', name: 'setRoleProcess',
    meta: { title: "设置审核流程" },
    components: { default: () => import('pages/role/setProcess') },
  },
  {
    path: '/role/setOwner/:id', name: 'setRoleOwner',
    meta: { title: "设置角色owner" },
    components: { default: () => import('pages/role/setOwner') },
  },
  {
    path: '/role/setRoleHasPrivilege/:id', name: 'setRoleHasPrivilege',
    meta: { title: "设置角色拥有权限" },
    components: { default: () => import('pages/role/setRoleHasPrivilege') },
  },
  {
    path: '/role/getRoleViewPrivilege/:id', name: 'getRoleViewPrivilege',
    meta: { title: "角色视图权限" },
    components: { default: () => import('pages/role/getRoleViewPrivilege') },
  },
  {
    path: '/role/getJoinUser/:id', name: 'getRoleJoinUser',
    meta: { title: "角色关联用户" },
    components: { default: () => import('pages/role/getJoinUser') },
  },
  {
    path: '/role/getJoinGroup/:id', name: 'getRoleJoinGroup',
    meta: { title: "角色关联部门" },
    components: { default: () => import('pages/role/getJoinGroup') },
  },
  {
    path: '/role/getJoinApply/:id', name: 'getRoleJoinApply',
    meta: { title: "角色被申请中列表" },
    components: { default: () => import('pages/role/getJoinApply') },
  },
  // 角色管理路由   结束

  // 层级管理路由   开始
  {
    path: '/hierarchy/base', name: 'hierarchyBase',
    meta: { title: "层级列表" },
    components: { default: () => import('pages/hierarchy/base') },
  }, {
    path: '/hierarchy/add', name: 'hierarchyAdd',
    meta: { title: "添加层级" },
    components: { default: () => import('pages/hierarchy/save') },
  },
  {
    path: '/hierarchy/update/:id', name: 'hierarchyUpdate',
    meta: { title: "修改层级" },
    components: { default: () => import('pages/hierarchy/save') },
  },
  //层级管理路由   结束

  // 商家管理路由   开始
  {
    path: '/business/base', name: 'businessBase',
    meta: { title: "商家列表" },
    components: { default: () => import('pages/business/base') },
  }, {
    path: '/business/add', name: 'businessAdd',
    meta: { title: "添加商家" },
    components: { default: () => import('pages/business/save') },
  },
  {
    path: '/business/update/:id', name: 'businessUpdate',
    meta: { title: "修改商家" },
    components: { default: () => import('pages/business/save') },
  }, {
    path: '/business/setBusinessHasPrivilege/:id', name: 'setBusinessHasPrivilege',
    meta: { title: "设置商家权限" },
    components: { default: () => import('pages/business/setBusinessHasPrivilege') },
  }, {
    path: '/business/setBusinessHasRole/:id', name: 'setBusinessHasRole',
    meta: { title: "导入商家权限" },
    components: { default: () => import('pages/business/setBusinessHasRole') },
  },

  // 商家管理路由   结束

  // 部门管理路由   开始
  {
    path: '/group/base', name: 'groupBase',
    meta: { title: "部门列表" },
    components: { default: () => import('pages/group/base') },
  }, {
    path: '/group/add', name: 'groupAdd',
    meta: { title: "添加部门" },
    components: { default: () => import('pages/group/save') },
  },
  {
    path: '/group/update/:id', name: 'groupUpdate',
    meta: { title: "修改部门" },
    components: { default: () => import('pages/group/save') },
  }, {
    path: '/group/groupHasUser/:id', name: 'groupHasUser',
    meta: { title: "设置部门成员" },
    components: { default: () => import('pages/group/groupHasUser') },
  }, {
    path: '/group/groupHasLeader/:id', name: 'groupHasLeader',
    meta: { title: "设置部门Leader" },
    components: { default: () => import('pages/group/groupHasLeader') },
  }, {
    path: '/group/groupHasRole/:id', name: 'groupHasRole',
    meta: { title: "设置部门角色" },
    components: { default: () => import('pages/group/groupHasRole') },
  },
  // 部门管理路由   结束

  // 权限查询路由  开始
  {
    path: '/searchPrivilege/base', name: 'searchPrivilegeBase',
    meta: { title: "基本表格" },
    components: { default: () => import('pages/searchPrivilege/base') },
  }, {
    path: '/searchPrivilege/add', name: 'searchPrivilegeAdd',
    meta: { title: "数据修改" },
    components: { default: () => import('pages/searchPrivilege/save') },
  },
  // 权限查询路由  结束

  // 审核流程管理路由   开始
  {
    path: '/process/base', name: 'processBase',
    meta: { title: "审核流程列表" },
    components: { default: () => import('pages/process/base') },
  }, {
    path: '/process/add', name: 'processAdd',
    meta: { title: "添加审核流程" },
    components: { default: () => import('pages/process/save') },
  }, {
    path: '/process/update/:id', name: 'processUpdate',
    meta: { title: "修改审核流程" },
    components: { default: () => import('pages/process/save') },
  }, {
    path: '/process/lowRisk', name: 'lowRisk',
    meta: { title: "低风险模板" },
    components: { default: () => import('pages/process/lowRisk') },
  }, {
    path: '/process/middleRisk', name: 'middleRisk',
    meta: { title: "中风险模板" },
    components: { default: () => import('pages/process/middleRisk') },
  }, {
    path: '/process/highRisk', name: 'highRisk',
    meta: { title: "高风险模板" },
    components: { default: () => import('pages/process/highRisk') },
  },
  // 审核流程管理路由   结束

  // 审核管理路由   开始
  {
    path: '/approval/base', name: 'approvalBase',
    meta: { title: "审核列表" },
    components: { default: () => import('pages/approval/base') },
  }, {
    path: '/approval/agentEdit', name: 'agentEdit',
    meta: { title: "设置代签" },
    components: { default: () => import('pages/approval/agentEdit') },
  }, {
    path: '/approval/changeSign/:id', name: 'changeSign',
    meta: { title: "转签" },
    components: { default: () => import('pages/approval/changeSign') },
  }, {
    path: '/approval/addSign/:id', name: 'addSign',
    meta: { title: "加签" },
    components: { default: () => import('pages/approval/addSign') },
  },
  // 审核管理路由   结束

  // 用户角色授权管理路由   开始

  // 用户角色授权管理路由   结束

  //申请管理路由 开始
  {
    path: '/apply/base', name: 'applyBase',
    meta: { title: "申请列表" },
    components: { default: () => import('pages/apply/base') },
  }, {
    path: '/apply/add', name: 'applyAdd',
    meta: { title: "权限申请" },
    components: { default: () => import('pages/apply/privilegeSave') },
  }, {
    path: '/apply/add', name: 'applyRoleAdd',
    meta: { title: "角色申请" },
    components: { default: () => import('pages/apply/roleSave') },
  },
  //申请管理路由 结束

  //监控配置管理路由 开始
  {
    path: '/monitor/base', name: 'monitorBase',
    meta: { title: "监控配置管理列表" },
    components: { default: () => import('pages/monitor/base') },
  }, {
    path: '/monitor/add', name: 'monitorAdd',
    meta: { title: "添加监控配置" },
    components: { default: () => import('pages/monitor/save') },
  }, {
    path: '/monitor/update/:id', name: 'monitorUpdate',
    meta: { title: "修改监控配置" },
    components: { default: () => import('pages/monitor/save') },
  },

  //监控配置管理路由 结束

  //监控结果管理路由 开始
  {
    path: '/monitorResult/base', name: 'monitorResultBase',
    meta: { title: "监控结果列表" },
    components: { default: () => import('pages/monitorResult/base') },
  },
  //监控结果管理路由 结束

  {
    path: '/table/base', name: 'tableBase',
    meta: { title: "基本表格" },
    components: { default: () => import('pages/table/base') },
  }, {
    path: '/table/update/:id', name: 'tableUpdate',
    meta: { title: "数据修改" },
    components: { default: () => import('pages/table/save') },
  }, {
    path: '/table/add', name: 'tableAdd',
    meta: { title: "添加数据" },
    components: { default: () => import('pages/table/save') },
  }, {
    path: '/500', name: '500',
    components: { fullView: () => import('pages/error/500') }
  }, {
    path: '*', name: 'notPage',
    components: { fullView: () => import('pages/error/404') }
  },

]

const router = new VueRouter({
  routes,
  mode: 'hash', //default: hash ,history
  scrollBehavior (to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { x: 0, y: 0 }
    }
  }
})
const setUserInfo = (data) => {
  store.dispatch('setUser', data)
}

//路由完成之后的操作
router.afterEach(route => {
  Vue.prototype.$NProgress.done()
})

//全局路由配置
//路由开始之前的操作
router.beforeEach(
  (to, from, next) => {
    Vue.prototype.$NProgress.start()
    let is_login = store.state.user.is_login
    let to_name = to.name
    if (to_name !== "500" && !is_login) {
      //在本地存储中登录状态失效的时候,尝试获取请求用户信息接口,判断是是否真的已经退出登录
      getUserInfo(next)
    } else {
      next()
    }
  }
)

/**
 * 进行用户登录校验，如果登录获取用户信息并设置到vuex
 */
const getUserInfo = (next) => {
  Vue.axios.get(request_login.user_info).then((responseData) => {
    if (responseData.code === result_code.success) {
      setUserInfo({ login: true, userInfo: responseData.data })
      next()
    }
  }, (error) => {
    next("/500")
  })
}
export default router
