/**
 * 用户信息存储
 */
import {cookieStorage} from 'common/storage'
import * as types from './types'

const getData = (key) => {
  let data = cookieStorage.get('acl_vue') || {}
  return data[key]
}

const setData = (key, val) => {
  let data = cookieStorage.get('acl_vue') || {}
  data[key] = val;
  cookieStorage.set('acl_vue', data)
}
// 默认状态 state
const state = {
  is_login: false,
  user_info: null
}

// getters
const getters = {
  getIsLogin: state => {
    return state.is_login
  },
  getUserInfo: state => {
    return state.user_info
  }
}

// actions
const actions = {
  setUser({commit}, data){
    commit(types.SET_IS_LOGIN, data.login || false)
    commit(types.SET_USER_INFO, data.userInfo || null)
  },
  setUserInfo({commit}, data){
    commit(types.SET_USER_INFO, data)
  },
  setIsLogin({commit}, data){
    commit(types.SET_IS_LOGIN, data)
  }
}

// mutations
const mutations = {
  [types.SET_IS_LOGIN](state, data){
    state.is_login = data || false
    // setData('is_login', data)
  },
  [types.SET_USER_INFO](state, data){
    state.user_info = data || {}
    // if (data === null) {
    //   setData('is_login', false)
    //   setData('user_info', null)
    // } else {
    //   setData('user_info', data)
    // }
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
