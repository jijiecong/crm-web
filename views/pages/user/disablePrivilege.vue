<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form>
            <el-form-item style="width: 600px">
              <transfer-meiren ref="transferMeiren" :initUrl="data_url" :initId="route_id" :titles="['可用的权限', '禁用的权限']"></transfer-meiren>
            </el-form-item>
            <el-form-item style="margin-left: 150px">
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
  import {request_user} from 'common/request_api'

  export default {
    data() {
      return {
        data_url: request_user.privilegeControl,
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
