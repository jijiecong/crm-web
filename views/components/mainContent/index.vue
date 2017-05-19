<template>
  <div class="main-content ofh">
    <el-row>
      <el-col :span="24">
        <div class="wrapper">
          <div class="pageContent">
            <slot></slot>
          </div>
        </div>
        <!--<v-footer></v-footer>-->
      </el-col>
    </el-row>
  </div>
</template>
<script type="text/javascript">
  import vFooter from './footer'

  export default{
    name: 'content',
    data(){
      return {}
    },
    components: {
      vFooter,
    }
  }
</script>
