<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="名称:" prop="name">
              <el-input v-model="form.name" placeholder="请输入内容"></el-input>
            </el-form-item>
            <!--<el-form-item label="类型:" prop="type">-->
              <!--<el-select v-model="form.type" placeholder="请选择">-->
                <!--<el-option-->
                  <!--v-for="item in option"-->
                  <!--:key="item.type"-->
                  <!--:label="item.label"-->
                  <!--:value="item.type">-->
                <!--</el-option>-->
              <!--</el-select>-->
            <!--</el-form-item>-->
            <el-form-item label="类型:" prop="type">
              <el-radio class="radio" v-model="form.type" label="0">http</el-radio>
              <el-radio class="radio" v-model="form.type" label="1">dubbo</el-radio>
            </el-form-item>
            <!--<el-form-item label="间隔时间:" prop="timeValue">-->
              <!--<el-select v-model="form.timeValue" placeholder="请选择">-->
                <!--<el-option-->
                  <!--v-for="item in options"-->
                  <!--:key="item.timeValue"-->
                  <!--:label="item.label"-->
                  <!--:value="item.timeValue">-->
                <!--</el-option>-->
              <!--</el-select>-->
            <!--</el-form-item>-->
            <el-form-item label="间隔时间:" prop="timeValue">
              <el-radio class="radio" v-model="form.timeValue" label="30">30秒</el-radio>
              <el-radio class="radio" v-model="form.timeValue" label="60">60秒</el-radio>
              <el-radio class="radio" v-model="form.timeValue" label="300">5分钟</el-radio>
            </el-form-item>
            <el-form-item label="ip或域名:" prop="domain">
              <el-input v-model="form.domain" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="路由:" prop="router">
              <el-input v-model="form.router" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="方法:" prop="method">
              <el-input v-model="form.method" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="参数1:" prop="" >
              <el-input v-model="paramTypes[0]" placeholder="name" style="width: 198px"></el-input> <el-input style="width: 198px" v-model="paramValues[0]" placeholder="value"></el-input>
            </el-form-item>
            <el-form-item label="参数2:" prop="" >
              <el-input v-model="paramTypes[1]" placeholder="name" style="width: 198px"></el-input> <el-input style="width: 198px" v-model="paramValues[1]" placeholder="value"></el-input>
            </el-form-item>
            <el-form-item label="参数3:" prop="" >
              <el-input v-model="paramTypes[2]" placeholder="name" style="width: 198px"></el-input> <el-input style="width: 198px" v-model="paramValues[2]" placeholder="value"></el-input>
            </el-form-item>
            <el-form-item label="通知触发类型:" prop="notifyType">
              <el-radio class="radio" v-model="form.notifyType" label="0">出错</el-radio>
              <el-radio class="radio" v-model="form.notifyType" label="1">其他</el-radio>
            </el-form-item>
            <el-form-item label="通知触发值:" prop="triggerValue">
              <el-input v-model="form.triggerValue" placeholder="请输入内容"></el-input>
            </el-form-item>
            <el-form-item label="通知人:" prop="userId">
              <simple-select :selectUrl="select_url" multiple v-model="form.userIds"></simple-select>
            </el-form-item>

            <el-form-item label="是否启用:" prop="isUsed">
            <el-radio class="radio" v-model="form.isUsed" :label="0">是</el-radio>
            <el-radio class="radio" v-model="form.isUsed" :label="1">否</el-radio>
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
  import {request_monitor, result_code, request_user} from 'common/request_api'
  import {tools_verify} from 'common/tools'

  export default{
    data(){
      return {
        select_url: request_user.search,
        option: [{
          type: '1',
          label: 'http'
        }, {
          type: '2',
          label: 'dubbo'
        }],
        type: '',
        options: [{
          timeValue: '30',
          label: '30s'
        }, {
          timeValue: '60',
          label: '60s'
        },{
          timeValue: '300',
          label: '5分钟'
        }],
        timeValue: '',

        form: {
          name: null,
          type: null,
          domain:null,
          param:null,
          timeValue:null,
          triggerValue:null,
          notifyType:null,
          isUsed:null,
          userIds:[]
        },
        paramTypes:[],
        paramValues:[],
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          name: [{
            required: true,
            message: '请输入名称',
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
      this.paramTypes.push('','','')
      this.paramValues.push('','','')
    },
    methods: {
      //获取数据
      get_form_data(){
        this.load_data = true
        this.$http.get(request_monitor.find, {
          params: {
            id: this.route_id
          }
        })
          .then(({data: responseData}) => {
            this.form = responseData
            let list_type = responseData.paramTypeList;
            let list_value = responseData.paramValueList;
            for(let i = 0 ; i < list_type.length ; i++){
              this.paramTypes[i] = list_type[i]
            }
            for(let j = 0 ; j < list_value.length ; j++){
              this.paramValues[j] = list_value[j]
            }
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
          this.form.paramTypes = '|'+this.paramTypes.join('|')+'|';
          this.form.paramValues = '|'+this.paramValues.join('|')+'|';
          let param = this.$qs.stringify(this.form)
          this.$http.post(request_monitor.save, param)
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
