<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" ></panel-title >
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中" >
      <el-row >
        <el-col >
          <el-form ref="form" :model="form" :rules="rules" label-width="100px" >
            <el-form-item label="选择角色:" prop="roleId" >
              <search-select
                multiple
                :selectUrl="select_role_url"
                v-model="form.roleId"
                placeholder="请输入内容"
                ></search-select >
            </el-form-item >
            <el-form-item >
              <el-button @click="$router.back()" >取消</el-button >
              <el-button type="primary" @click="on_submit_form" :loading="on_submit_loading" >立即提交</el-button >
            </el-form-item >
          </el-form >
        </el-col >
      </el-row >
    </div >
  </div >
</template >
<script type="text/javascript" >
  import {mapGetters} from 'vuex'
  import { panelTitle, searchSelect } from 'components'
  import { request_business, request_role, request_user, result_code } from 'common/request_api'
  import { tools_verify } from 'common/tools'

  export default{
    data(){
      return {
        select_role_url: request_role.search,
        initId: null,
        options: [{
          value: 1,
          label: '低风险'
        }, {
          value: 2,
          label: '中风险'
        }, {
          value: 3,
          label: '高风险'
        }],
        form: {
          name: null,
          roleId: []
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          name: [{
            required: true,
            message: '请输入权限名称',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
        }
      }
    },
    watch: {},
    computed: {
      ...mapGetters(['getUserInfo']),
    },
    methods: {
      //提交
      on_submit_form(){
        this.$refs.form.validate((valid) => {
          if (!valid) return false
          this.on_submit_loading = true
        let paramtmp = this.form;
        paramtmp.roleId = paramtmp.roleId.join(",")
          let param = this.$qs.stringify(this.form)
          this.$http.post(request_business.setBusinessHasRole, param)
            .then(({ data: responseData }) => {
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
  }
</script >
