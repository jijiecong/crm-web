/**
 * 用户信息存储
 */
import { cookieStorage } from 'common/storage'
import * as types from './types'

// 默认状态 state
const state = {
  businessId: null,
  is_login: false,
  user_info: null,
  user_search_data: null,
  user_current_page: 1
}

// getters
const getters = {
  getIsLogin: state => {
    return state.is_login
  },
  getUserInfo: state => {
    return state.user_info
  },
  getBusinessId: state => {
    return state.businessId
  },
  getUserSearchData: state => {
    return state.user_search_data
  },
  getUserCurrentPage: state => {
    return state.user_current_page
  }
}

// actions
const actions = {
  setUser({ commit }, data){
    commit(types.SET_IS_LOGIN, data.login || false)
    commit(types.SET_USER_INFO, data.userInfo || null)
  },
  setUserInfo({ commit }, data){
    commit(types.SET_USER_INFO, data)
  },
  setIsLogin({ commit }, data){
    commit(types.SET_IS_LOGIN, data)
  },
  setBusinessId({ commit }, data){
    commit(types.SET_BUSINESS_ID, data)
  },
  setUserSearchData({ commit }, data){
    commit(types.SET_USER_SEARCH_DATA, data)
  },
  setUserCurrentPage({ commit }, data){
    commit(types.SET_USER_CURRENT_PAGE, data)
  }
}

// mutations
const mutations = {
  [types.SET_BUSINESS_ID](state, data){
    state.businessId = data || null
  },
  [types.SET_IS_LOGIN](state, data){
    state.is_login = data || false
  },
  [types.SET_USER_INFO](state, data){
    state.user_info = data || {}
    state.businessId = data.businessId || null
  },
  [types.SET_USER_SEARCH_DATA](state, data){
    state.user_search_data = data || null
  },
  [types.SET_USER_CURRENT_PAGE](state, data){
    state.user_current_page = data || null
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
