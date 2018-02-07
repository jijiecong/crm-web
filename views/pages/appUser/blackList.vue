<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" >
    </panel-title >
    <div class="panel-title-down" >
      <el-row >
        <el-col :span="20" >
          <form @submit.prevent="on_search" >
            <el-row :gutter="10" >
              <el-col :span="6" >
                <el-input size="middle" placeholder="用户id/手机号/昵称" v-model="getSearchData" ></el-input >
              </el-col >
              <el-col :span="4" >
                <template>
                  <el-select v-model="getSelectValue" placeholder="请选择APP名称">
                    <el-option
                      v-for="item in options"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </template>
              </el-col>
              <el-col :span="2" >
                <el-button type="primary" size="medium" native-type="submit" ><i class="el-icon-search"></i > 查询</el-button >
              </el-col >
            </el-row >
          </form >
        </el-col >
        <el-col :span="4" >
          <div class="fr" >
           <el-row :gutter="10">
              <el-col :span="16">
                <el-button v-if="blackAuth" type="warning" size="middle" @click="createBlackListBatch()">
                  批量解黑
                </el-button >
              </el-col>
             <el-col :span="6">
              <el-button @click.stop="on_refresh" size="middle" >
                <i class="fa fa-refresh" ></i >
              </el-button >
             </el-col>
           </el-row>
          </div >
        </el-col >
      </el-row >
    </div >
    <div class="panel-body" >
      <el-table
        :data="table_data"
        v-loading="load_data"
        element-loading-text="拼命加载中"
        border
        @selection-change="tableSelectionChange"
        style="width: 100%;" >
        <el-table-column
          type="selection"
          width="42" >
        </el-table-column >
        <el-table-column
          prop="userId"
          label="id"
          width="120" >
        </el-table-column >
        <el-table-column
          prop="mobile"
          label="手机号" >
        </el-table-column >
        <el-table-column
          prop="nickname"
          label="昵称" >
        </el-table-column >
        <el-table-column
          prop="registerProjectName"
          label="注册来源" >
        </el-table-column >
        <el-table-column
          prop="loginedProjectName"
          label="登录过的APP" >
        </el-table-column >
        <el-table-column
          prop="registerSource"
          label="注册方式" >
        </el-table-column >
        <el-table-column
          prop="regTime"
          label="注册时间" >
          <template scope="props">
            <label v-text="to_date(props.row.regTime)" ></label>
          </template>
        </el-table-column >
        <el-table-column
          v-if="blackAuth"
          label="操作"
          width="130" >
          <template scope="props" >
            <el-row class="operation-row" >
              <el-button type="warning" size="small" @click="createBlackList(props.row.userId)">移除黑名单</el-button>
            </el-row >
          </template >
        </el-table-column >
      </el-table >
      <bottom-tool-bar >
        <div slot="page" >
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page="getCurrentPage"
            :page-size="10"
            layout="total, prev, pager, next"
            :total="total_count" >
          </el-pagination >
        </div >
      </bottom-tool-bar >
    </div >
  </div >
</template >
<script type="text/javascript" >
  import { simpleSelect, panelTitle, bottomToolBar } from 'components'
  import { request_appUser } from 'common/request_api'
  import { mapGetters, mapActions } from 'vuex'

  export default{
    data(){
      return {
        blackAuth:false,
        table_data: null,
        //数据总条目
        total_count: 0,
        //每页显示多少条数据
        rows: 10,
        //请求时的loading效果
        load_data: true,
        //批量选择数组
        batch_select: [],
        //APP名称
        options: []
      }
    },
    watch: {},
    components: {
      simpleSelect,
      panelTitle,
      bottomToolBar
    },
    created(){
      this.get_table_data()
      this.getProjects()
      this.getAuth()
//      this.table_data = [{
//        id: '1',
//        phone: '1235678910',
//        nickName: '小王',
//        appName: '简拼',
//        registrationMode: '手机注册',
//        area: '杭州',
//        registerTime: '2017-05-02',
//        lastLoginTime: '2017-05-02'
//      }, {
//        id: '2',
//        phone: '1235678910',
//        nickName: '聪聪',
//        appName: '美人相机',
//        registrationMode: '手机注册',
//        area: '杭州',
//        registerTime: '2017-05-03',
//        lastLoginTime: '2017-06-02'
//      }]
//      this.total_count = 2
//      this.load_data = false
    },
    computed: {
      ...mapGetters(['getAppUserSearchData', 'getAppUserCurrentPage', 'getAppUserSelectValue']),
      getSearchData: {
        get(){
          return this.getAppUserSearchData
        },
        set(val){
          this.setAppUserSearchData(val)
        }
      },
      getSelectValue: {
        get(){
          return this.getAppUserSelectValue
        },
        set(val){
          this.setAppUserSelectValue(val)
        }
      },
      getCurrentPage: {
        get(){
          return this.getAppUserCurrentPage
        },
        set(val){
          this.setAppUserCurrentPage(val)
        }
      },
    },
    methods: {
      ...mapActions(['setAppUserSearchData','setAppUserCurrentPage', 'setAppUserSelectValue']),
      to_date(time){
        return this.$dateFormat(time,'yyyy-MM-dd hh:mm')
      },
      //查询权限
      getAuth(){
        this.$http.get(request_appUser.getAuthByToken, {
          params: {
          }
        }).then(({ data }) => {
          console.log("data:"+JSON.stringify(data))
          this.blackAuth = data.blackBoolean;
        }).catch(() => {
        })
      },
      //查询
      on_search(){
        this.get_table_data()
      },
      //刷新
      on_refresh(){
        this.getCurrentPage = 1
        this.getSearchData = ''
        this.getSelectValue = ''
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_appUser.blackList, {
          params: {
            page: this.getCurrentPage,
            rows: this.rows,
            projectName: this.getSelectValue,
            commonFile: this.getSearchData
          }
        }).then(({ data }) => {
          this.table_data = data.data
          this.total_count = data.totalCount
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //查询全部工程名
      getProjects(){
        this.$http.get(request_appUser.getProjects, {
          params: {
          }
        }).then(({ data }) => {
          let projects = [];
          projects.push({value:"all",label:"全部"});
          for(var key in data ){
            projects.push({
              value:key,
              label:data[key]
            })
          }
          this.options = projects
        }).catch(() => {
        })
      },
      //页码选择
      handleCurrentChange(val) {
        this.getCurrentPage = val
        this.get_table_data()
      },
      //移除黑名单
      createBlackList(id) {
        this.$confirm('您确定要把userId为' + id + '的用户移除黑名单吗？', '提示', { type: 'warning' })
          .then(() => {
            this.$http.get(request_appUser.createBlackListUserById, {
              params: {
                userId:id,
                blacklistType:2,
                projectName:'all'
              }
            }).then(({ data }) => {
              if(data.success){
                var doc = this
                this.$message.success('移除黑名单成功！')
                setTimeout(function () {
                  doc.get_table_data()
                }, 800);
              }else{
                this.$message.error('移除黑名单失败,错误代码：'+data.code+',原因：'+data.error)
              }
            }).catch(() => {
              this.$message.error('移除黑名单失败！')
            })
          })
          .catch(() => {
            this.$message('已取消操作!');
          });
      },
      //获取选中
      tableSelectionChange(val) {
        this.selected = val;
      },
      //批量解黑用户
      createBlackListBatch(){
        var ids = [];
        this.selected.forEach(function (value, index) {
          ids.push(value.userId);
        })
        this.$confirm('您确定要把userId为'+ids+'的用户添加黑名单吗？', '提示', { type: 'warning' })
          .then(() => {
            this.$http.get(request_appUser.createBlackListBatch, {
              params: {
                userIds:ids,
                blacklistType:2,
                projectName:'all'
              }
            }).then(({ data }) => {
              if(data.success && data.data==null){
                var doc = this
                this.$message.success('批量添加黑名单成功！')
                setTimeout(function () {
                  doc.get_table_data()
                }, 800);
              } else if(data.data != null){
                this.$message.error('批量添加黑名单失败的userId为：'+data.data)
              } else{
                this.$message.error('批量添加黑名单失败,错误代码：'+data.code+',原因：'+data.error)
              }
            }).catch(() => {
              this.$message.error('批量添加黑名单失败！')
            })
          })
          .catch(() => {
            this.$message('已取消操作!');
          });
      }
    }
  }
</script >
