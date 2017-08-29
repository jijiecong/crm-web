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
              <transfer-meiren ref="transferMeiren" :initUrl="data_url" :initId="route_id" :titles="['可拥有的权限', '已拥有的权限']"></transfer-meiren>
            </el-form-item>
            <el-form-item style="margin-left: 310px">
              <el-button @click="$router.back()">取消</el-button>
              <el-button @click="sure_privilege()" type="primary">确定</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

    </div>
  </div>
</template>
<script>
  import {panelTitle, transferMeiren} from 'components'
  import {request_business, request_privilege} from 'common/request_api'

  export default {
    data() {
      return {
        data_url: request_business.setBusinessHasPrivilege,
        route_id: this.$route.params.id,
        load_data: false
      };
    },
    created(){
    },
    methods: {
      sure_privilege(){
        this.$refs.transferMeiren.sure()
        setTimeout(() => {
          this.$router.back()
        }, 500)
      }
    },
    components: {
      panelTitle,
      transferMeiren
    }
  };
</script>
