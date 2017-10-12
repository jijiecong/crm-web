<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body" >
      <!--<div style='width:360px;' v-if='ztreeDataSource.length>0'>-->
        <!--<tree :list.sync='ztreeDataSource' :func='nodeClick' :is-open='false'></tree>-->
      <!--</div>-->
      <tree-meiren :showCheckbox='false' :value="ztreeDataSource" size="small"></tree-meiren>
      <div style="height: 15px;"></div>
      <el-button @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>
<script type="text/javascript">
  import {panelTitle, tree, treeMeiren} from 'components'
  import {request_role, result_code} from 'common/request_api'
  export default {
      data() {
          return {
            route_id: this.$route.params.id,
            ztreeDataSource:[]
          }
      },
      created() {
        this.route_id && this.initData();
      },
      methods: {
        // 点击节点
        nodeClick:function(m){
        },
        initData() {
          this.axios.get(request_role.getRoleViewPrivilege, {
            params: {
              roleId: this.route_id
            }
          }).then(({data: responseData}) => {
            if(responseData !== null){
              this.ztreeDataSource = responseData
            }
          }).catch(() => {
          })
        }
      },
      components: {
        panelTitle,
        tree,
        treeMeiren
      }
  }
</script>
<style>
  body {font-family: Helvetica, sans-serif;}

  .iconClassRoot {
    width:15px;
    height:15px;
    display: inline-block;
    background: url("../../assets/images/ztree/root.png") no-repeat center/100% auto;
  }
  .iconClassNode {
    width:15px;
    height:15px;
    display: inline-block;
    background: url("../../assets/images/ztree/node.png") no-repeat center/100% auto;
  }
  .operate{
    display: flex;
  }
  .operate ul>li{
    float:left;
    margin:10px 10px;
    list-style-type: none;
  }
</style>
