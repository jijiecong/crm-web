<template >
  <div class="main-content ofh" >
    <div class="wrapper" >
        <slot ></slot >
    </div >
  </div >
</template >
<script type="text/javascript" >
  import vFooter from './footer'

  export default{
    name: 'mainContent',
    data(){
      return {}
    },
    components: {
      vFooter,
    }
  }
</script >
