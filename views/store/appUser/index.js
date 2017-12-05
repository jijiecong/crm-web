/**
 * 用户信息存储
 */
import { cookieStorage } from 'common/storage'
import * as types from './types'

// 默认状态 state
const state = {
  appUser_search_data: null,
  appUser_current_page: 1,
  appUser_select_value: null
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
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
