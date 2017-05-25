<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" ></panel-title >
    <div class="panel-body"
      v-loading="load_data"
      element-loading-text="拼命加载中" >
      <el-row >
        <el-col >
          <el-form ref="form" :model="form" :rules="rules" label-width="100px" >
            <el-form-item label="部门名称:" prop="name" >
              <el-input v-model="form.name" placeholder="请输入内容" ></el-input >
            </el-form-item >
            <el-form-item label="部门描述:" prop="description" >
              <el-input v-model="form.description" placeholder="请输入内容" ></el-input >
            </el-form-item >
            <el-form-item label="上级部门:" prop="pid" >
              <simple-select :selectUrl="select_group_url" v-model="form.pid" ></simple-select >
            </el-form-item >
            <el-form-item label="选择商家:" prop="businessId" v-if="this.form.id === null && getUserInfo.inSide" >
              <simple-select :selectUrl="select_url" v-model="form.businessId" ></simple-select >
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
  import { panelTitle, simpleSelect } from 'components'
  import { mapGetters } from 'vuex'
  import { request_user, result_code, request_business, request_group } from 'common/request_api'
  import { tools_verify } from 'common/tools'

  export default{
    data(){

      let isEdit = !!this.$route.params.id
      let checkPassword = (rule, value, callback) => {
        if (!isEdit && (value === null || value === '')) {
          return callback(new Error('密码不能为空'))
        }
        callback()
      }
      return {
        select_group_url: request_group.search,
        select_url: request_business.search,
        form: {
          id: null,
          name: null,
          description: null,
          pid: null,
          businessId: null
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          name: [{
            required: true,
            message: '请输入商家名称',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
        }
      }
    },
    created(){
      this.route_id && this.get_form_data()
      if (!this.route_id) {
        this.form.businessId = this.getUserInfo.businessId
      }
    },
    computed: {
      ...mapGetters(['getUserInfo']),
    },
    methods: {
      //获取数据
      get_form_data(){
        this.load_data = true
        this.axios.get(request_group.find, {
          params: {
            id: this.route_id
          }
        }).then(({ data: responseData }) => {
          if (responseData.pid === 0) {
            responseData.pid = null
          }
          this.form = responseData
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
          this.axios.post(request_group.save, param)
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
      simpleSelect
    }
  }
</script >
