<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="150px">
            <el-form-item prop="toUserId" label="请选择转签人：">
              <search-select :selectUrl="select_url" v-model="form.toUserId"></search-select>
            </el-form-item>
            <el-form-item>
              <el-button @click="$router.back()">取消</el-button>
              <el-button @click="sure()" type="primary">确定</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

    </div>
  </div>
</template>
<script>
  import {panelTitle, searchSelect} from 'components'
  import {request_user, request_approval} from 'common/request_api'

  export default {
    data() {
      return {
        select_url: request_user.search,
        form: {
          toUserId: null,
          id: this.$route.params.id
        },
        rules: {
          toUserId: {
            type:"number",
            required: true,
            message: '请输入要加签的用户',
            trigger: 'blur'
          }
        }
      };
    },
    created(){
    },
    watch: {
      form(val){
        console.log(val)
      }
    },
    methods: {
      sure(){
        this.$refs.form.validate((valid) => {
          if (!valid) return false
          this.on_submit_loading = true
          let param = this.$qs.stringify(this.form)
          this.$http.post(request_approval.addSign, param)
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
      searchSelect
    }
  };
</script>
