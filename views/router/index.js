/**
 * vue的路由配置
 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import store from 'store'
import {request_login, result_code} from 'common/request_api'

Vue.use(VueRouter)
//使用AMD方式 懒加载
// component: resolve => require(['pages/home'], resolve),
const routes = [{
  path: '/', name: 'home',
  meta: {title: "主页"},
  components: {default: resolve => require(['pages/home'], resolve)}
},

  // 用户管理路由   开始
  {
    path: '/user/base', name: 'userBase',
    meta: {title: "用户列表"},
    components: {default: resolve => require(['pages/user/base'], resolve)},
  }, {
    path: '/user/update/:id', name: 'userUpdate',
    meta: {title: "用户修改"},
    components: {default: resolve => require(['pages/user/save'], resolve)},
  }, {
    path: '/user/add', name: 'userAdd',
    meta: {title: "添加用户"},
    components: {default: resolve => require(['pages/user/save'], resolve)},
  }, {
    path: '/user/changeGroup/:id', name: 'userChangeGroup',
    meta: {title: "换岗"},
    components: {default: resolve => require(['pages/user/changeGroup'], resolve)},
  }, {
    path: '/user/setHierarchy/:id', name: 'setHierarchy',
    meta: {title: "设置层级"},
    components: {default: resolve => require(['pages/user/setHierarchy'], resolve)},
  }, {
    path: '/user/disableRole/:id', name: 'disableRole',
    meta: {title: "角色禁用"},
    components: {default: resolve => require(['pages/user/disableRole'], resolve)},
  }, {
    path: '/user/disablePrivilege/:id', name: 'disablePrivilege',
    meta: {title: "权限禁用"},
    components: {default: resolve => require(['pages/user/disablePrivilege'], resolve)},
  },
  // 用户管理路由   结束

  // 权限管理路由   开始
  {
    path: '/privilege/base', name: 'privilegeBase',
    meta: {title: "权限列表"},
    components: {default: resolve => require(['pages/privilege/base'], resolve)},
  }, {
    path: '/privilege/update/:id', name: 'privilegeUpdate',
    meta: {title: "权限修改"},
    components: {default: resolve => require(['pages/privilege/save'], resolve)},
  }, {
    path: '/privilege/add', name: 'privilegeAdd',
    meta: {title: "添加权限"},
    components: {default: resolve => require(['pages/privilege/save'], resolve)},
  }, {
    path: '/privilege/setProcess/:id', name: 'setPrivilegeProcess',
    meta: {title: "设置审核流程"},
    components: {default: resolve => require(['pages/privilege/setProcess'], resolve)},
  },
  {
    path: '/privilege/setOwner/:id', name: 'setPrivilegeOwner',
    meta: {title: "设置权限owner"},
    components: {default: resolve => require(['pages/privilege/setOwner'], resolve)},
  },
  // 权限管理路由   结束

  // 角色管理路由   开始
  {
    path: '/role/base', name: 'roleBase',
    meta: {title: "角色列表"},
    components: {default: resolve => require(['pages/role/base'], resolve)},
  }, {
    path: '/role/update/:id', name: 'roleUpdate',
    meta: {title: "角色修改"},
    components: {default: resolve => require(['pages/role/save'], resolve)},
  }, {
    path: '/role/add', name: 'roleAdd',
    meta: {title: "添加角色"},
    components: {default: resolve => require(['pages/role/save'], resolve)},
  }, {
    path: '/role/setProcess/:id', name: 'setroleProcess',
    meta: {title: "设置审核流程"},
    components: {default: resolve => require(['pages/role/setProcess'], resolve)},
  },
  {
    path: '/role/setOwner/:id', name: 'setRoleOwner',
    meta: {title: "设置角色owner"},
    components: {default: resolve => require(['pages/role/setOwner'], resolve)},
  },
  {
    path: '/role/setRoleHasPrivilege/:id', name: 'setRoleHasPrivilege',
    meta: {title: "设置角色拥有权限"},
    components: {default: resolve => require(['pages/role/setRoleHasPrivilege'], resolve)},
  },
  // 角色管理路由   结束

  // 层级管理路由   开始
  {
    path: '/hierarchy/base', name: 'hierarchyBase',
    meta: {title: "层级列表"},
    components: {default: resolve => require(['pages/hierarchy/base'], resolve)},
  }, {
    path: '/hierarchy/add', name: 'hierarchyAdd',
    meta: {title: "添加层级"},
    components: {default: resolve => require(['pages/hierarchy/save'], resolve)},
  },
  {
    path: '/hierarchy/update/:id', name: 'hierarchyUpdate',
    meta: {title: "修改层级"},
    components: {default: resolve => require(['pages/hierarchy/save'], resolve)},
  },
  //层级管理路由   结束

  // 商家管理路由   开始
  {
    path: '/business/base', name: 'businessBase',
    meta: {title: "商家列表"},
    components: {default: resolve => require(['pages/business/base'], resolve)},
  }, {
    path: '/business/add', name: 'businessAdd',
    meta: {title: "添加商家"},
    components: {default: resolve => require(['pages/business/save'], resolve)},
  },
  {
    path: '/business/update/:id', name: 'businessUpdate',
    meta: {title: "修改商家"},
    components: {default: resolve => require(['pages/business/save'], resolve)},
  }, {
    path: '/business/businessHasPrivilege/:id', name: 'businessHasPrivilege',
    meta: {title: "设置部门角色"},
    components: {default: resolve => require(['pages/business/businessHasPrivilege'], resolve)},
  },
  // 商家管理路由   结束

  // 部门管理路由   开始
  {
    path: '/group/base', name: 'groupBase',
    meta: {title: "部门列表"},
    components: {default: resolve => require(['pages/group/base'], resolve)},
  }, {
    path: '/group/add', name: 'groupAdd',
    meta: {title: "添加部门"},
    components: {default: resolve => require(['pages/group/save'], resolve)},
  },
  {
    path: '/group/update/:id', name: 'groupUpdate',
    meta: {title: "修改部门"},
    components: {default: resolve => require(['pages/group/save'], resolve)},
  }, {
    path: '/group/groupHasUser/:id', name: 'groupHasUser',
    meta: {title: "设置部门成员"},
    components: {default: resolve => require(['pages/group/groupHasUser'], resolve)},
  }, {
    path: '/group/groupHasLeader/:id', name: 'groupHasLeader',
    meta: {title: "设置部门Leader"},
    components: {default: resolve => require(['pages/group/groupHasLeader'], resolve)},
  }, {
    path: '/group/groupHasRole/:id', name: 'groupHasRole',
    meta: {title: "设置部门角色"},
    components: {default: resolve => require(['pages/group/groupHasRole'], resolve)},
  },
  // 部门管理路由   结束

  // 权限查询路由  开始
  {
    path: '/searchPrivilege/base', name: 'searchPrivilegeBase',
    meta: {title: "基本表格"},
    components: {default: resolve => require(['pages/searchPrivilege/base'], resolve)},
  }, {
    path: '/searchPrivilege/add', name: 'searchPrivilegeAdd',
    meta: {title: "数据修改"},
    components: {default: resolve => require(['pages/searchPrivilege/save'], resolve)},
  },
  // 权限查询路由  结束

  // 审核流程管理路由   开始
  {
    path: '/process/base', name: 'processBase',
    meta: {title: "审核流程列表"},
    components: {default: resolve => require(['pages/process/base'], resolve)},
  }, {
    path: '/process/add', name: 'processAdd',
    meta: {title: "添加审核流程"},
    components: {default: resolve => require(['pages/process/save'], resolve)},
  },
  {
    path: '/process/update/:id', name: 'processUpdate',
    meta: {title: "修改审核流程"},
    components: {default: resolve => require(['pages/process/save'], resolve)},
  },
  // 审核流程管理路由   结束

  // 审核管理路由   开始
  {
    path: '/approval/base', name: 'approvalBase',
    meta: {title: "审核列表"},
    components: {default: resolve => require(['pages/approval/base'], resolve)},
  },{
    path: '/approval/agentEdit', name: 'agentEdit',
    meta: {title: "设置代签"},
    components: {default: resolve => require(['pages/approval/agentEdit'], resolve)},
  },{
    path: '/approval/changeSign/:id', name: 'changeSign',
    meta: {title: "转签"},
    components: {default: resolve => require(['pages/approval/changeSign'], resolve)},
  },{
    path: '/approval/addSign/:id', name: 'addSign',
    meta: {title: "加签"},
    components: {default: resolve => require(['pages/approval/addSign'], resolve)},
  },
  // 审核管理路由   结束

  // 用户角色授权管理路由   开始
  {
    path: '/userAndRole/base', name: 'userAndRoleBase',
    meta: {title: "用户角色授权列表"},
    components: {default: resolve => require(['pages/userAndRole/base'], resolve)},
  },{
    path: '/userAndRole/userRoleEdit/:id', name: 'userRoleEdit',
    meta: {title: "用户角色授权"},
    components: {default: resolve => require(['pages/userAndRole/userRoleEdit'], resolve)},
  },
  // 用户角色授权管理路由   结束

  {
    path: '/table/base', name: 'tableBase',
    meta: {title: "基本表格"},
    components: {default: resolve => require(['pages/table/base'], resolve)},
  }, {
    path: '/table/update/:id', name: 'tableUpdate',
    meta: {title: "数据修改"},
    components: {default: resolve => require(['pages/table/save'], resolve)},
  }, {
    path: '/table/add', name: 'tableAdd',
    meta: {title: "添加数据"},
    components: {default: resolve => require(['pages/table/save'], resolve)},
  }, {
    path: '/500', name: '500',
    components: {fullView: resolve => require(['pages/error/500'], resolve)}
  }, {
    path: '*', name: 'notPage',
    components: {fullView: resolve => require(['pages/error/404'], resolve)}
  },

]



const router = new VueRouter({
  routes,
  mode: 'hash', //default: hash ,history
  scrollBehavior (to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return {x: 0, y: 0}
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
      setUserInfo({login: true, userInfo: responseData.data})
      next();
    }
  }, (error) => {
    next("/500");
  })
}
export default router
