<template>
  <div class="panel">
    <panel-title :title="$route.meta.title"></panel-title>
    <div class="panel-body"
         v-loading="load_data"
         element-loading-text="拼命加载中">
      <div v-for="(process, index) in all_process" class="process-body">
        <el-row :key="index" class="process-row">
          <el-col class="process-col">
            <el-row>
              <el-col :span="20">
                <span>{{process.name}}</span>
              </el-col>
              <el-col :span="4">
                <el-switch
                  class="process-switch"
                  v-model="process.checked">
                </el-switch>
              </el-col>
            </el-row>
          </el-col>
          <el-col class="process-col">
            <el-row>
              <el-col :span="8">
                <span>审核条件：</span>
              </el-col>
              <el-col :span="16">
                <el-radio-group size="small" v-model="process.radioVal" class="process-radio">
                  <el-radio label="AND"></el-radio>
                  <el-radio label="OR"></el-radio>
                </el-radio-group>
              </el-col>
            </el-row>
          </el-col>
          <el-col class="process-col">
            <el-row>
              <el-col :span="8">
                <span>最高层级限定：</span>
              </el-col>
              <el-col :span="16">
                <simple-select
                  size="small"
                  :selectData="select_options"
                  v-model="process.hierarchyId"
                ></simple-select>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>
      <el-row class="process-footer">
        <el-col>
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" @click="on_submit_form" :loading="on_submit_loading">立即提交</el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script type="text/javascript">
  import {panelTitle, simpleSelect} from 'components'
  import {request_privilege, request_hierarchy} from 'common/request_api'

  export default{
    data(){
      return {
        select_options: [],
        hierarchy_url: request_hierarchy.search,
        all_process: [],
        route_id: this.$route.params.id,
        load_data: false,
        on_submit_loading: false,
      }
    },
    created(){
      this.hierarchy_url && this.get_select_data()
      this.route_id && this.get_form_data()
    },
    methods: {
      //获取数据
      get_select_data(){
        this.$http.get(this.hierarchy_url).then(({data}) => {
          this.select_options = data
        })
      },
      get_form_data(){
        this.load_data = true
        let param = this.$qs.stringify({id: this.route_id})
        this.$http.post(request_privilege.setProcess + '/init', param)
          .then(({data: responseData}) => {
            let have = responseData.have;
            let all = responseData.all;
            let data = [];
            all.forEach(function (element, index) {
              data.push({
                  checked: false,
                  radioVal: 'AND',
                  id: element.id,
                  name: element.name,
                  hierarchyId: element.hierarchyId
                }
              );
            });
            this.all_process = data;
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
          this.$http.post(request_privilege.save, param)
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
