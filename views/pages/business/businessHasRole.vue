<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="商家:">
              <el-input v-model="group_data.name" :readonly="true"></el-input>
            </el-form-item>
            <el-form-item label="选择角色:" prop="roleId">
              <simple-select :selectUrl="select_url" v-model="search_data.businessId" title="商家"
                             size="small"></simple-select>
            </el-form-item>
            <el-form-item>
              <el-button @click="$router.back()">取消</el-button>
              <el-button type="primary" @click="on_submit_form" :loading="on_submit_loading">立即提交</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script type="text/javascript">
  import {panelTitle,simpleSelect} from 'components'
  import {request_role, result_code,request_business} from 'common/request_api'
  import {tools_verify} from 'common/tools'

  export default{
    data(){

      return {
        select_url: request_role.search,
        search_data: {
          businessId: ''
        },
        form: {
          businessId: null,
          fromGroupId: null,
        },
        group_data: {
          id: null,
          name: '',
          description: null,
          businessId: null,
          status: null
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          toGroupId: [{
            type: 'number',
            required: true,
            message: '请选择',
            trigger: 'blur'
          }]
        }
      }
    },
    created(){
      this.route_id && this.get_form_data()
      this.form.userId = this.route_id
    },
/*    watch: {
      group_data: (val, oldVal) => {
        this.form.fromGroupId = val.id
      }
    },*/
    methods: {
      //获取数据
      get_form_data(){
        this.load_data = true
        this.axios.get(request_group.findByUserId, {
          params: {
            userId: this.route_id
          }
        }).then(({data: responseData}) => {
          if(responseData !== null){
            this.group_data = responseData
            this.form.fromGroupId = this.group_data.id
          }
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //提交
      on_submit_form(){
        this.$refs.form.validate((valid) => {
          if (!valid) return false
          this.on_submit_loading = true
          let param = this.$qs.stringify(this.form)
          this.axios.post(request_user.changeGroup, param)
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
      simpleSelect
    }
  }
</script>
