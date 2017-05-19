/**
 *  axios配置.
 */

//导入模块
import axios from 'axios'
import store from 'store'
import {request_login, result_code} from 'common/request_api'
import utils from 'common/utils'

//设置用户信息action
const setUserInfo = (data) => {
  store.dispatch('setUser', data)
}

const install = (Vue) => {
  if (install.installed) return
  install.installed = true

  let $vue = Vue.prototype || Vue
  let h = new Vue().$createElement

  const no_privilege_notify = (url) => {
    let val = h('a',
      {
        attrs: {
          href: url
        },
        style: 'color: teal'
      },
      '您没有拥有操作该功能的权限，如果需要申请点击该提示'
    )
    $vue.$notify.warning({
      title: '权限限制',
      message: val
    })
  }
  //设置默认根地址,请求前缀
  axios.defaults.baseURL = ''
  //设置请求超时设置
  axios.defaults.timeout = 2000
  //设置请求时的header
  axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'
  //添加请求拦截器
  axios.interceptors.request.use(
    (config) => {
      //开发模式下,默认基础路由
      const base = process.env.NODE_ENV === 'production' ? '/' : '/vueApi'
      //用户的请求uuid,唯一标识
      const uuid = store.state.user.user_info !== null ? '/' + store.state.user.user_info.uuid : ''
      config.url = base + uuid + config.url
      //在发送请求之前做某事
      $vue.$NProgress.start()
      return config
    },
    (error) => {
      //请求错误时做些事
      return Promise.reject(error)
    }
  )

  /**
   * 添加响应拦截器
   * 判断返回码,做特殊处理
   */
  axios.interceptors.response.use(
    (response) => {
      $vue.$NProgress.done()
      //成功时
      let responseData = response.data

      let dataCode = responseData.code
      let dataError = responseData.error || null
      let dataResult = responseData.data
      if (dataCode === result_code.success) {
        return Promise.resolve(responseData)
      }
      //没有登录的响应拦截,跳转登录页(登录页地址,后端传入)
      if (dataCode === result_code.unlogin) {
        setUserInfo({login: false, userInfo: null})
        console.log(dataResult)
        $vue.$message.warning('您没有登录，请先前往登录')
        setTimeout(() => {
          window.location.href = dataResult
        }, 1000)
        return
      }
      //没有权限的响应拦截,提示没有权限,点击提示可以跳转申请页面
      if (dataCode === result_code.no_privilege) {
        no_privilege_notify(dataResult)
      } else {
        $vue.$message.warning(dataError)
      }
      return Promise.reject(responseData)
    },
    (error) => {
      $vue.$NProgress.done()
      //错误时
      if (error.response) {
        let resError = error.response
        let resCode = resError.status
        let resMsg = error.message
        $vue.$message.error('操作失败！错误原因 ' + resMsg)
        return Promise.reject({code: resCode, msg: resMsg})
      }
    }
  )

  //设置axios到Vue上
  Vue.axios = axios

  Object.defineProperties(Vue.prototype, {
    axios: {
      get() {
        return axios
      }
    },
    $http: {
      get() {
        return axios
      }
    }
  })
}

export default {
  install
}
