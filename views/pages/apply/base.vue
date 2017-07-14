<template>
  <div class="panel">
    <panel-title :title="$route.meta.title">
    </panel-title>
    <div class="panel-title-down">
      <el-row>
        <el-col :span="6" :offset="18">
          <div class="fr">
            <el-button @click.stop="on_refresh" size="small">
              <i class="fa fa-refresh"></i>
            </el-button>
            <router-link :to="{name: 'applyAdd'}" tag="span">
              <el-button type="primary" icon="plus" size="small">申请权限</el-button>
            </router-link>
            <router-link :to="{name: 'applyRoleAdd'}" tag="span">
              <el-button type="primary" icon="plus" size="small">申请角色</el-button>
            </router-link>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="panel-body">
      <el-table
        :data="table_data"
        v-loading="load_data"
        element-loading-text="拼命加载中"
        border
        @selection-change="on_batch_select"
        style="width: 100%;">
        <el-table-column
          type="selection"
          width="42">
        </el-table-column>
        <el-table-column
          prop="id"
          label="id"
          width="80">
        </el-table-column>
        <el-table-column
          prop="userName"
          label="申请人">
        </el-table-column>
        <el-table-column
          prop="applyContent"
          label="申请描述">
        </el-table-column>
        <el-table-column
          prop="wantName"
          label="被申请权限/角色">
        </el-table-column>
        <el-table-column
          prop="approvalState"
          label="审核状态">
        </el-table-column>
        <el-table-column
          prop="applyTime"
          width="200"
          label="申请时间">
          <template scope="props">
            <label v-text="to_date(props.row.applyTime)" ></label>
          </template>
        </el-table-column>

      </el-table>
      <bottom-tool-bar>

        <div slot="page">
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-size="10"
            layout="total, prev, pager, next"
            :total="total_count">
          </el-pagination>
        </div>
      </bottom-tool-bar>
    </div>
  </div>
</template>
<script type="text/javascript">
  import {simpleSelect, panelTitle, bottomToolBar} from 'components'
  import {request_user, request_apply} from 'common/request_api'
  import {mapGetters} from 'vuex'

  export default{
    data(){
      return {
        table_data: null,
        //当前页码
        currentPage: 1,
        //数据总条目
        total_count: 0,
        //每页显示多少条数据
        rows: 10,
        //请求时的loading效果
        load_data: true,
        //批量选择数组
        batch_select: [],
        search_data: {
          businessId: ''
        },
      }
    },
    components: {
      simpleSelect,
      panelTitle,
      bottomToolBar
    },
    created(){
      this.get_table_data()
    },
    computed: {
      ...mapGetters(['getUserInfo'])
    },
    methods: {
      to_date(time){
        return this.$dateFormat(time,'yyyy-MM-dd hh:mm')
      },
      //刷新
      on_refresh(){
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_apply.list, {
          params: {
            page: this.currentPage,
            rows: this.rows,
            ...this.search_data
          }
        }).then(({data}) => {
          this.table_data = data.data
          this.total_count = data.totalCount
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //页码选择
      handleCurrentChange(val) {
        this.currentPage = val
        this.get_table_data()
      },
      //批量选择
      on_batch_select(val){
        this.batch_select = val
      },
    }
  }
</script>
