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
              <!--这里ownerId会作为value属性传入searchSelect组件, 组件更新数据时通过$emit('input', value)传给ownerId-->
              <search-select
                multiple
                :selectUrl="select_user_url"
                v-model="ownerId"
                placeholder="请输入内容"
                :initId="initId">
              </search-select>
            </el-form-item>
            <el-form-item label="风险等级:" prop="riskLevel" >
              <el-select v-model="form.riskLevel" placeholder="请选择" >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value" >
                </el-option >
              </el-select >
            </el-form-item >
            <el-form-item class="width_100" >
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
  import { mapGetters } from 'vuex'
  import { panelTitle, simpleSelect,searchSelect } from 'components'
  import { request_privilege, result_code, request_user } from 'common/request_api'
  import { tools_verify } from 'common/tools'

  export default{
    data(){
      return {
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
          riskLevel: 1
        },
        ownerId: [],
        route_id: this.$route.params.id,
        select_user_url: request_user.search,
        initId: [],
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
          }
        }
      }
    },
    created(){
      if(!this.route_id){
        this.initId.push(this.getUserInfo.id)
      }else {
          this.get_form_data();
          this.get_owner();
      }
    },
    watch: {},
    computed: {
      ...mapGetters(['getUserInfo']),
      // 仅读取，值只须为函数
      aDouble() {
        return this.a * 2
      },
    },
    methods: {
      //获取数据
      get_form_data(){
        this.load_data = true
        this.$http.get(request_privilege.find, {
          params: {
            id: this.route_id
          }
        })
          .then(({ data: responseData }) => {
            this.form = responseData
            this.load_data = false
          })
          .catch(() => {
            this.load_data = false
          })
      },
      get_owner(){
        this.load_data = true
        this.$http.get(request_privilege.getOwner, {
          params: {
            id: this.route_id
          }
        })
          .then(({ data: responseData }) => {
            let selected = responseData.selected;
            let data = [];
            selected.forEach(function (element) {
              data.push(
                element.id
              )
            });
            this.initId = data;
            this.load_data = false
          })
          .catch(() => {
            this.load_data = false
          })
      },
      //提交
      on_submit_form(){
        this.$refs.form.validate((valid) => {
          if (!valid) return false
          this.on_submit_loading = true
          this.form.ownerId = this.ownerId
          let param = this.$qs.stringify(this.form)
          this.$http.post(request_privilege.save, param)
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
      simpleSelect,
      searchSelect
    }
  }
</script >
