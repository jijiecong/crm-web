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
  // 权限管理路由   结束

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
  // 层级管理路由   结束
  //
  // // 商家管理路由   开始
  // {
  //   path: '/business/base', name: 'businessBase',
  //   meta: {title: "商家列表"},
  //   components: {default: resolve => require(['pages/business/base'], resolve)},
  // }, {
  //   path: '/business/add', name: 'businessAdd',
  //   meta: {title: "添加商家"},
  //   components: {default: resolve => require(['pages/business/save'], resolve)},
  // },
  // {
  //   path: '/business/update/:id', name: 'businessUpdate',
  //   meta: {title: "修改商家"},
  //   components: {default: resolve => require(['pages/business/save'], resolve)},
  // },
  // // 商家管理路由   结束

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
