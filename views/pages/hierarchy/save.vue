<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col :span="8">
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="层级名称:" prop="hierarchyName">
              <el-input v-model="form.hierarchyName" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="层级值:" prop="hierarchyValue">
              <el-input v-model="form.hierarchyValue" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="排序:" prop="sort">
              <el-input v-model="form.sort" placeholder="请输入内容"></el-input>
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
  import {panelTitle} from 'components'
  import {request_hierarchy, result_code} from 'common/request_api'
  import {tools_verify} from 'common/tools'

  export default{
    data(){
      return {
        form: {
          hierarchyName: null,
          hierarchyValue: null,
          sort: null
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          hierarchyName: [{
            required: true,
            message: '请输入层级名称',
            trigger: 'blur'
          }, {
            min: 2,
            max: 25,
            message: '长度在 4 到 25 个字符'
          }],
          hierarchyValue: {
            required: true,
            message: '请输入层级值',
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
        this.$http.get(request_hierarchy.find, {
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
          this.$http.post(request_hierarchy.save, param)
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
      panelTitle
    }
  }
</script>
