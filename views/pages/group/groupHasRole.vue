<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form>
            <el-form-item style="width: 900px">
              <transfer-meiren ref="transferMeiren" :initUrl="data_url" :initId="route_id" :titles="['可拥有的角色', '已拥有的角色']"></transfer-meiren>
            </el-form-item>
            <el-form-item style="margin-left: 310px">
              <el-button @click="$router.back()">取消</el-button>
              <el-button @click="sure_role()" type="primary">确定</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

    </div>
  </div>
</template>
<script>
  import {panelTitle, transferMeiren} from 'components'
  import {request_role, request_group} from 'common/request_api'

  export default {
    data() {
      return {
        data_url: request_group.groupHasRole,
        route_id: this.$route.params.id,
        load_data: false
      };
    },
    created(){
    },
    methods: {
      sure_role(){
        this.$refs.transferMeiren.sure()
      }
    },
    components: {
      panelTitle,
      transferMeiren
    }
  };
</script>
