<template>
  <section class="body-warp">
    <transition name="fade" mode="out-in">
      <router-view name="fullView" style="z-index: 200"></router-view>
    </transition>
    <template v-if="is_show_all">
      <left-slide style="z-index: 151"></left-slide>
      <header-section style="z-index: 150"></header-section>
    </template>
    <main-content style="z-index: 100" :class="{'no-show-all':!is_show_all}">
      <transition name="fade" mode="out-in">
        <router-view></router-view>
      </transition>
    </main-content>
  </section>
</template>

<script type="text/javascript">
  import {mainContent,headerSection,leftSlide} from 'components'
  import {request_login, result_code} from 'common/request_api'

  export default {
    name: 'app',
    data(){
      return {
        is_show_all: process.env.NODE_ENV !== 'production'
      }
    },
    mounted () {
      this.$NProgress.done()
    },
    created () {
      this.$NProgress.start()
    },
    components: {
      mainContent,leftSlide,headerSection
    }
  }
</script>

<style lang="scss" type="text/scss" rel="stylesheet/scss">
  @import '~assets/scss/main';

  .fade-enter-active,
  .fade-leave-active {
    transition: all .2s ease;
  }

  .fade-enter,
  .fade-leave-active {
    opacity: 0;
  }
</style>
