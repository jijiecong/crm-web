<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" >
    </panel-title >
    <div class="panel-title-down" >
      <el-row>
        <el-col :span="20" >
          <form @submit.prevent="on_search" >
            <el-row :gutter="10">
              <el-col :span="4" >
                <el-input size="middle" placeholder="用户id/手机号/昵称" v-model="getSearchData" ></el-input >
              </el-col >
              <el-col :span="3" style="margin-right: 15px">
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
              </el-col >
              <el-col :span="4" >
                <template >
                  <el-date-picker
                    v-model="timeStart"
                    type="datetime"
                    placeholder="选择注册起始时间">
                  </el-date-picker>
                </template>
              </el-col >
              <el-col :span="1"style="margin-right:-15px;margin-left: -15px" >
                <span style="line-height: 36px;">———</span>
              </el-col>
              <el-col :span="4" >
                <template >
                  <el-date-picker
                    v-model="timeEnd"
                    type="datetime"
                    placeholder="选择注册结束时间">
                  </el-date-picker>
                </template>
              </el-col >
              <el-col :span="2" >
                <template>
                  <el-select v-model="sortValue" placeholder="选择排序">
                    <el-option
                      v-for="item in sortOptions"
                      :key="item.sortValue"
                      :label="item.sortName"
                      :value="item.sortValue">
                    </el-option>
                  </el-select>
                </template>
              </el-col >
              <el-col :span="2">
                <el-button type="primary" size="medium" native-type="submit"><i class="el-icon-search"></i > 查询</el-button >
              </el-col>
            </el-row >
          </form >
        </el-col >
        <el-col :span="4" >
          <div class="fr" >
            <el-col :span="10">
              <el-button v-if="blackAuth" type="warning" size="middle" @click="createBlackListBatch()">
                批量拉黑
              </el-button >
            </el-col>
            <el-col :span="10">
              <el-button v-if="removeAuth" type="danger" size="middle" @click="removeUserBatch()">
                批量删除
              </el-button >
            </el-col>
            <el-col :span="4">
              <el-button @click.stop="on_refresh" size="middle" >
                <i class="fa fa-refresh" ></i >
              </el-button >
            </el-col>



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
          prop="userIcon"
          width="100"
          label="头像" >
          <template scope="scope">
              <img :src="scope.row.userIcon"  style="margin-top: 1px;margin-bottom: 1px"/>
          </template>
        </el-table-column >
        <el-table-column
          prop="sex"
          width="60"
          label="性别" >
        </el-table-column >
        <el-table-column
          prop="birthday"
          label="生日" >
        </el-table-column >
        <el-table-column
          prop="locationInfo"
          label="地理位置" >
        </el-table-column >
        <el-table-column
          prop="tags"
          v-if="isShowTag"
          label="用户标签" >
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
          v-if="allAuth"
          label="操作"
          width="130" >
          <template scope="scope" >
            <el-row class="operation-row" >
              <el-button v-if="blackAuth" type="warning" size="small" @click="createBlackList(scope.row)">拉黑</el-button>
              <el-button v-if="removeAuth" type="danger" size="small" @click="removeUser(scope.row)">删除</el-button>
            </el-row >
          </template >
        </el-table-column >
      </el-table >
      <bottom-tool-bar >
        <div slot="page" >
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="getCurrentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="rows"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total_count">
          </el-pagination>
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
        isShowTag:false,
        allAuth:true,
        blackAuth:false,
        removeAuth:false,
        table_data: null,
        timeStart: '',
        timeEnd: '',
        //数据总条目
        total_count: 0,
        //每页显示多少条数据
        rows: 10,
        //请求时的loading效果
        load_data: true,
        //批量选择数组
        batch_select: [],
        //APP名称
        options: [],
        sortOptions: [{
          sortValue: 'userId-desc',
          sortName: 'ID降序'
        },{
          sortValue: 'userId-asc',
          sortName: 'ID升序'
        },{
          sortValue: 'regTime-desc',
          sortName: '时间降序'
        },{
          sortValue: 'regTime-asc',
          sortName: '时间升序'
        }, ],
        sortValue:''
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
      to_date(regTime){
        return this.$dateFormat(regTime,'yyyy-MM-dd hh:mm')
      },
      //查询
      on_search(){
        this.get_table_data()
      },
      //刷新
      on_refresh(){
        this.getCurrentPage = 1
        this.rows = 10
        this.getSearchData = ''
        this.getSelectValue = ''
        this.isShowTag = false
        this.timeStart = ''
        this.timeEnd = ''
        this.sortValue = ''
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_appUser.list, {
          params: {
            page: this.getCurrentPage,
            rows: this.rows,
            projectName: this.getSelectValue,
            sort: this.sortValue,
            commonFile: this.getSearchData,
            timeStart : (this.timeStart == '') ? null : this.timeStart.getTime(),
            timeEnd : (this.timeEnd == '') ? null : this.timeEnd.getTime(),
          }
        }).then(({ data }) => {
          if("kids_camera".indexOf(this.getSelectValue)!=-1){
            this.isShowTag = true
          }
          this.table_data = data.data
          this.total_count = data.totalCount
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
//        this.table_data = [{
//          id: '1',
//          phone: '1235678910',
//          nickName: '小王',
//          appName: '简拼',
//          registrationMode: '手机注册',
//          area: '杭州',
//          registerTime: '2017-05-02',
//          lastLoginTime: '2017-05-02'
//        }, {
//          id: '2',
//          phone: '1235678910',
//          nickName: '聪聪',
//          appName: '美人相机',
//          registrationMode: '手机注册',
//          area: '杭州',
//          registerTime: '2017-05-03',
//          lastLoginTime: '2017-06-02'
//        }]
//        this.total_count = 2
//        this.load_data = false
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
      //查询权限
      getAuth(){
        this.$http.get(request_appUser.getAuthByToken, {
          params: {
          }
        }).then(({ data }) => {
          this.blackAuth = data.blackBoolean;
          this.removeAuth = data.removeBoolean;
          if(!data.blackBoolean && !data.removeBoolean){
            this.allAuth = false
          }
        }).catch(() => {
        })
      },
      //每页条数选择
      handleSizeChange(val) {
        this.rows = val
        this.get_table_data()
      },
      //页码选择
      handleCurrentChange(val) {
        this.getCurrentPage = val
        this.get_table_data()
      },
      //添加黑名单
      createBlackList(row) {
        let id = row.userId;
        this.$confirm('您确定要把userId为' + id + '的用户添加黑名单吗？', '提示', { type: 'warning' })
          .then(() => {
            this.$http.get(request_appUser.createBlackListUserById, {
              params: {
                userId:id,
                blacklistType:1,
                projectName:'all'
              }
            }).then(({ data }) => {
              if(data.success){
                var doc = this
                this.$message.success('添加黑名单成功！')
                setTimeout(function () {
                  doc.get_table_data()
                }, 800);
              }else{
                this.$message.error('添加黑名单失败,错误代码：'+data.code+',原因：'+data.error)
              }
            }).catch(() => {
              this.$message.error('添加黑名单失败！')
            })
          })
          .catch(() => {
            this.$message('已取消操作!');
          });
      },
      //删除用户
      removeUser(row) {
        let id = row.userId;
        this.$confirm('您确定要删除userId为 ' + id + '的用户吗？', '提示', { type: 'warning' })
          .then(() => {
            // 向请求服务端删除
            this.$http.get(request_appUser.deleteUserById, {
              params: {
                userId:id
              }
            }).then(({ data }) => {
              if(data.success){
                var doc = this
                this.$message.success('删除成功！')
                setTimeout(function () {
                  doc.get_table_data()
                }, 1000);
              }else{
                this.$message.error('删除失败,错误代码：'+data.code+',原因：'+data.error)
              }
            }).catch(() => {
              this.$message.error('删除失败！')
            })
          })
          .catch(() => {
            this.$message('已取消操作!');
          });
      },
      //批量拉黑用户
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
                blacklistType:1,
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
      },
      //批量删除用户
      removeUserBatch() {
        var ids = [];
        this.selected.forEach(function (value, index) {
          ids.push(value.userId);
        })
        this.$confirm('您确定要删除userId为'+ids+'的用户吗？', '提示', { type: 'warning' })
          .then(() => {
            this.$http.get(request_appUser.deleteUserByIdsBatch, {
              params: {
                userIds:ids
              }
            }).then(({ data }) => {
              if(data.success && data.data==null){
                var doc = this
                this.$message.success('批量删除成功！')
                setTimeout(function () {
                  doc.get_table_data()
                }, 800);
              } else if(data.data != null){
                this.$message.error('批量删除失败的userId为：'+data.data)
              } else{
                this.$message.error('批量删除失败,错误代码：'+data.code+',原因：'+data.error)
              }
            }).catch(() => {
              this.$message.error('批量删除失败！')
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
    }
  }
</script >
