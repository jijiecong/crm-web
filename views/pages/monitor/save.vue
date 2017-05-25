<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col :span="8">
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="名称:" prop="name">
              <el-input v-model="form.name" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="类型:" prop="type">
              <el-select v-model="type" placeholder="请选择">
                <el-option
                  v-for="item in option"
                  :key="item.type"
                  :label="item.label"
                  :value="item.type">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="间隔时间:" prop="time">
              <el-select v-model="time" placeholder="请选择">
                <el-option
                  v-for="item in options"
                  :key="item.time"
                  :label="item.label"
                  :value="item.time">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="ip或域名:" prop="token">
              <el-input v-model="form.token" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="路由:" prop="token">
              <el-input v-model="form.token" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="方法:" prop="token">
              <el-input v-model="form.token" placeholder="请输入内容"></el-input>
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
  import {simpleSelect, panelTitle , bottomToolBar} from 'components'
  import {request_business, result_code} from 'common/request_api'
  import {tools_verify} from 'common/tools'

  export default{
    data(){
      return {
        option: [{
          type: 'http',
          label: 'http'
        }, {
          type: 'dubbos',
          label: 'dubbo'
        }],
        type: '',

        options: [{
          time: '30',
          label: '30s'
        }, {
          time: '60',
          label: '60s'
        },{
          time: '5',
          label: '5分钟'
        }],
        time: '',

        form: {
          name: null,
          description: null,
          token: null
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
          description: {
            required: true,
            message: '请输入商家描述',
            trigger: 'blur'
          },
          token: {
            required: true,
            message: '请输入商家token',
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
        this.$http.get(request_business.find, {
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
          this.$http.post(request_business.save, param)
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
