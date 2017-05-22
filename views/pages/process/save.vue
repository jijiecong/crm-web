<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="流程名称:" prop="name">
              <el-input v-model="form.name" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="流程描述:" prop="processDescribe">
              <el-input v-model="form.processDescribe" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="审核级别:" prop="approvalLevel">
              <el-input v-model="form.approvalLevel" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="最高层级:" prop="hierarchyId">
              <simple-select :selectUrl="select_url" v-model="form.hierarchyId"></simple-select>
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
  import {request_process, result_code, request_hierarchy} from 'common/request_api'
  import {tools_verify} from 'common/tools'

  export default{
    data(){
      return {
        select_url: request_hierarchy.search,
        form: {
          name: null,
          processDescribe: null,
          approvalLevel: null,
          hierarchyId: null
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          name: [{
            required: true,
            message: '请输入流程名称',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
          processDescribe: [{
            required: true,
            message: '请输入流程说明',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
          approvalLevel: {
            required: true,
            message: '请输入审核级别',
            trigger: 'blur'
          }
        }
      }
    },
    created(){
      this.route_id && this.get_form_data()
    },
    watch:{

    },
    computed: {
      // 仅读取，值只须为函数
      aDouble() {
        return this.a * 2
      },
    },
    methods: {
      //获取数据
      get_form_data(){
        this.load_data = true
        this.$http.get(request_process.find, {
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
          this.$http.post(request_process.save, param)
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
