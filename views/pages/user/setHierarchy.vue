<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="选择层级:" prop="hierarchyId">
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
  import {mapGetters} from 'vuex'
  import {result_code,request_user,request_hierarchy} from 'common/request_api'
  import {tools_verify} from 'common/tools'

  export default{
    data(){
      return {
        select_url: request_hierarchy.search,
        form: {
          userId: null,
          hierarchyId: null,
        },
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
        rules: {
          hierarchyId: [{
            type: 'number',
            required: true,
            message: '请选择要设置的层级',
            trigger: 'change'
          }]
        }
      }
    },
    created(){
      this.form.userId = this.route_id
      if(this.$route.params.hierarchyId !== 0){
        this.form.hierarchyId = this.$route.params.hierarchyId
      }
    },
    computed: {
      ...mapGetters(['getUserInfo']),
    },
    methods: {
      //提交
      on_submit_form(){
        this.$refs.form.validate((valid) => {
          if (!valid) return false
          this.on_submit_loading = true
          let param = this.$qs.stringify(this.form)
          this.axios.post(request_user.setHierarchy, param)
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
