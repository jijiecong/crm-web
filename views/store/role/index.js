/**
 * 用户信息存储
 */
import { cookieStorage } from 'common/storage'
import * as types from './types'

// 默认状态 state
const state = {
  role_search_data: null,
  role_current_page: 1
}

// getters
const getters = {
  getRoleSearchData: state => {
    return state.role_search_data
  },
  getRoleCurrentPage: state => {
    return state.role_current_page
  }
}

// actions
const actions = {
  setRoleSearchData({ commit }, data){
    commit(types.SET_ROLE_SEARCH_DATA, data)
  },
  setRoleCurrentPage({ commit }, data){
    commit(types.SET_ROLE_CURRENT_PAGE, data)
  }
}

// mutations
const mutations = {
  [types.SET_ROLE_SEARCH_DATA](state, data){
    state.role_search_data = data || null
  },
  [types.SET_ROLE_CURRENT_PAGE](state, data){
    state.role_current_page = data || null
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
