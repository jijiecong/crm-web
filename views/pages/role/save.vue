<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="角色名称:" prop="name">
              <el-input v-model="form.name" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="角色说明:" prop="description">
              <el-input v-model="form.description" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="风险等级:" prop="riskLevel">
              <el-select v-model="form.riskLevel" placeholder="请选择">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="商家:" prop="businessId" v-if="this.form.id === null && getUserInfo.inSide">
              <simple-select :selectUrl="select_url" v-model="form.businessId"></simple-select>
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
  import {request_role, result_code, request_business} from 'common/request_api'
  import {tools_verify} from 'common/tools'
  import {mapGetters} from 'vuex'

  export default{
    data(){
      return {
        select_url: request_business.search,
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
          id: null,
          name: null,
          description: null,
          riskLevel: 1,
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          name: [{
            required: true,
            message: '请输入角色名称',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
          description: [{
            required: true,
            message: '请输入角色说明',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }]
        }
      }
    },
    created(){
      this.route_id && this.get_form_data()
      if(!this.route_id){
          this.form.businessId = this.getUserInfo.businessId
      }
    },
    watch:{

    },

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
        this.$http.get(request_role.find, {
          params: {
            id: this.route_id
          }
        })
          .then(({data: responseData}) => {
            this.form = responseData
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
          let param = this.$qs.stringify(this.form)
          this.$http.post(request_role.save, param)
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
