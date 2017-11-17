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
              </el-col >
              <el-col :span="2" >
                <el-button type="primary" size="middle" native-type="submit" >查询</el-button >
              </el-col >
            </el-row >
          </form >
        </el-col >
        <el-col :span="4" >
          <div class="fr" >
            <el-button @click.stop="on_refresh" size="middle" >
              <i class="fa fa-refresh" ></i >
            </el-button >
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
          prop="registerSource"
          label="注册方式" >
        </el-table-column >
        <el-table-column
          prop="locationId"
          label="地区" >
        </el-table-column >
        <el-table-column
          prop="regTime"
          label="注册时间" >
          <template slot-scope="props">
            <label v-text="to_date(props.row.regTime)" ></label>
          </template>
        </el-table-column >
        <el-table-column
          label="操作"
          width="280" >
          <template slot-scope="props" >
            <el-row class="operation-row" >
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
        table_data: null,
        //数据总条目
        total_count: 0,
        //每页显示多少条数据
        rows: 10,
        //请求时的loading效果
        load_data: true,
        //批量选择数组
        batch_select: [],
        options: [{
          value: '',
          label: '全部'
        },{
          value: 'jane_plus',
          label: '简客'
        }, {
          value: 'xmen',
          label: '在一起'
        }, {
          value: 'beauty_camera',
          label: '美人相机'
        }, {
          value: 'jianpin',
          label: '简拼'
        }]
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
        this.$http.get(request_appUser.list, {
          params: {
            page: this.getCurrentPage,
            rows: this.rows,
            projectName: this.getSelectValue,
            name: this.getSearchData
          }
        }).then(({ data }) => {
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
      //页码选择
      handleCurrentChange(val) {
        this.getCurrentPage = val
        this.get_table_data()
      }
    }
  }
</script >
