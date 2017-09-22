/**
 * 状态管理器
 *
 */

import Vue from 'vue'
import Vuex from 'vuex'

import user from './user'
import role from './role'
import privilege from './privilege'
import group from './group'
Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    role,
    privilege,
    group,
  }
})
