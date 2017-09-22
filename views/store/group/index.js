/**
 * 用户信息存储
 */
import { cookieStorage } from 'common/storage'
import * as types from './types'

// 默认状态 state
const state = {
  group_search_data: null,
  group_current_page: 1
}

// getters
const getters = {
  getGroupSearchData: state => {
    return state.group_search_data
  },
  getGroupCurrentPage: state => {
    return state.group_current_page
  }
}

// actions
const actions = {
  setGroupSearchData({ commit }, data){
    commit(types.SET_GROUP_SEARCH_DATA, data)
  },
  setGroupCurrentPage({ commit }, data){
    commit(types.SET_GROUP_CURRENT_PAGE, data)
  }
}

// mutations
const mutations = {
  [types.SET_GROUP_SEARCH_DATA](state, data){
    state.group_search_data = data || null
  },
  [types.SET_GROUP_CURRENT_PAGE](state, data){
    state.group_current_page = data || null
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
