/**
 * 用户信息存储
 */
import { cookieStorage } from 'common/storage'
import * as types from './types'

// 默认状态 state
const state = {
  appUser_search_data: null,
  appUser_current_page: 1,
  appUser_select_value: null,
  appUser_select_queryStr: null
}

// getters
const getters = {
  getAppUserSearchData: state => {
    return state.appUser_search_data
  },
  getAppUserCurrentPage: state => {
    return state.appUser_current_page
  },
  getAppUserSelectValue: state => {
    return state.appUser_select_value
  },
  getAppUserSelectQueryStr: state => {
    return state.appUser_select_queryStr
  }
}

// actions
const actions = {
  setAppUserSearchData({ commit }, data){
    commit(types.SET_APPUSER_SEARCH_DATA, data)
  },
  setAppUserCurrentPage({ commit }, data){
    commit(types.SET_APPUSER_CURRENT_PAGE, data)
  },
  setAppUserSelectValue({ commit }, data){
    commit(types.SET_APPUSER_SELECT_VALUE, data)
  },
  setAppUserSelectQueryStr({ commit }, data){
    commit(types.SET_APPUSER_SELECT_QUERYSTR, data)
  }
}

// mutations
const mutations = {
  [types.SET_APPUSER_SEARCH_DATA](state, data){
    state.appUser_search_data = data || null
  },
  [types.SET_APPUSER_CURRENT_PAGE](state, data){
    state.appUser_current_page = data || null
  },
  [types.SET_APPUSER_SELECT_VALUE](state, data){
    state.appUser_select_value = data || null
  },
  [types.SET_APPUSER_SELECT_QUERYSTR](state, data){
    state.appUser_select_queryStr = data || null
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
