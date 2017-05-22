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
              <transfer-meiren ref="transferMeiren" :initUrl="data_url" :initId="route_id" :titles="['所有用户', 'owner的用户']"></transfer-meiren>
            </el-form-item>
            <el-form-item style="margin-left: 150px">
              <el-button @click="$router.back()">取消</el-button>
              <el-button @click="sure_owner()" type="primary">确定</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

    </div>
  </div>
</template>
<script>
  import {panelTitle, transferMeiren} from 'components'
  import {request_role} from 'common/request_api'

  export default {
    data() {
      return {
        data_url: request_role.setOwner,
        route_id: this.$route.params.id,
        load_data: false
      };
    },
    created(){
    },
    methods: {
      sure_owner(){
        this.$refs.transferMeiren.sure()
      }
    },
    components: {
      panelTitle,
      transferMeiren
    }
  };
</script>
