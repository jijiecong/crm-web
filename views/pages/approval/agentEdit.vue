<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="150px">
            <el-form-item prop="toUserId" label="请选择代签人:">
              <search-select :selectUrl="select_url" v-model="form.toUserId" :initData="init_data"></search-select>
            </el-form-item>
            <el-form-item label="请选择是否启用:" prop="isUsed">
              <el-radio-group size="small" v-model="form.isUsed">
                <el-radio label="1">启用</el-radio>
                <el-radio label="2">不启用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
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
  import {request_user, request_approval} from 'common/request_api'

  export default {
    data() {
      return {
        select_url: request_user.search,
        init_data: [],
        form: {
          toUserId: null,
          isUsed: 1
        },
        rules: {
          toUserId: {
            type:"number",
            required: true,
            message: '请输入要代签的用户',
            trigger: 'blur'
          }
        }
      };
    },
    created(){
      this.get_form_data()
    },
    methods: {
      //获取数据
      get_form_data(){
        this.load_data = true
        this.$http.post(request_approval.agentFind)
          .then(({data: responseData}) => {
            this.form.isUsed = responseData.isUsed
            this.init_data = [{id:responseData.toUserId,name:responseData.toUserName}]
            this.load_data = false
          })
          .catch(() => {
            this.load_data = false
          })
      },
      sure(){
        this.$refs.form.validate((valid) => {
          if (!valid) return false
          this.on_submit_loading = true
          let param = this.$qs.stringify(this.form)
          this.$http.post(request_approval.agent, param)
            .then(({data: responseData}) => {
              this.$message.success("操作成功")
              setTimeout(() => {
                this.$router.back()
              }, 500)
              this.on_submit_loading = false
            })
            .catch(() => {
              this.on_submit_loading = false
            })
        })
      }
    },
    components: {
      panelTitle,
      searchSelect
    }
  };
</script>
