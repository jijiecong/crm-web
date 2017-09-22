/**
 * 用户信息存储
 */
import { cookieStorage } from 'common/storage'
import * as types from './types'

// 默认状态 state
const state = {
  privilege_search_data: null,
  privilege_current_page: 1
}

// getters
const getters = {
  getPrivilegeSearchData: state => {
    return state.privilege_search_data
  },
  getPrivilegeCurrentPage: state => {
    return state.privilege_current_page
  }
}

// actions
const actions = {
  setPrivilegeSearchData({ commit }, data){
    commit(types.SET_PRIVILEGE_SEARCH_DATA, data)
  },
  setPrivilegeCurrentPage({ commit }, data){
    commit(types.SET_PRIVILEGE_CURRENT_PAGE, data)
  }
}

// mutations
const mutations = {
  [types.SET_PRIVILEGE_SEARCH_DATA](state, data){
    state.privilege_search_data = data || null
  },
  [types.SET_PRIVILEGE_CURRENT_PAGE](state, data){
    state.privilege_current_page = data || null
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
