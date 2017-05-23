<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" ></panel-title >
    <div class="panel-body"
      v-loading="load_data"
      element-loading-text="拼命加载中" >
      <el-row >
        <el-col >
          <el-form ref="form" :model="form" :rules="rules" label-width="100px" >
            <el-form-item label="权限名称:" prop="name" >
              <el-input v-model="form.name" placeholder="请输入内容" ></el-input >
            </el-form-item >
            <el-form-item label="权限说明:" prop="description" >
              <el-input v-model="form.description" placeholder="请输入内容" ></el-input >
            </el-form-item >
            <el-form-item label="token:" prop="token" >
              <el-input v-model="form.token" placeholder="请输入内容" ></el-input >
            </el-form-item >
            <el-form-item label="权限owner:" prop="ownerId" >
              <search-select
                multiple
                :selectUrl="select_user_url"
                v-model="form.ownerId"
                placeholder="请输入内容"
                :initId="initId" ></search-select >
            </el-form-item >
            <el-form-item >
              <p style="" >* 命名规范:<br >
                * 功能和菜单级别:meiren.acl.{appName}.{serviceName}.{funtionName}<br >
                * 字段级别:meiren.acl.{appName}.{serviceName}.{funtionName}.{fieldName}</p >
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
  import { request_searchPrivilege, request_user, result_code } from 'common/request_api'
  import { tools_verify } from 'common/tools'

  export default{
    data(){
      return {
        select_user_url: request_user.search,
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
          description: null,
          token: null,
          ownerId: []
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
          description: [{
            required: true,
            message: '请输入权限说明',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
          token: {
            required: true,
            message: '请输入token',
            trigger: 'blur'
          },
        }
      }
    },
    created(){
      this.initId = this.getUserInfo.id
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
          paramtmp.ownerId = paramtmp.ownerId.join(",")
          let param = this.$qs.stringify(paramtmp)
          this.$http.post(request_searchPrivilege.save, param)
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
