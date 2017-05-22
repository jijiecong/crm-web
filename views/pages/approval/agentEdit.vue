<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form>
            <el-form-item style="margin-left: 150px" prop="userId">
              <search-select :selectUrl="select_url" v-model="form.userId"></search-select>
            </el-form-item>
            <el-form-item style="margin-left: 150px">
              <el-button @click="$router.back()">取消</el-button>
              <el-button @click="sure()" type="primary">确定</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

    </div>
  </div>
</template>
<script>
  import {panelTitle, searchSelect} from 'components'
  import {request_user} from 'common/request_api'

  export default {
    data() {
      return {
        select_url: request_user.search,
        form: {
          userId: null
        },
      };
    },
    created(){
    },
    methods: {

    },
    components: {
      panelTitle,
      searchSelect
    }
  };
</script>
