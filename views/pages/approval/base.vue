<template>
  <div class="panel">
    <panel-title :title="$route.meta.title">
    </panel-title>
    <div class="panel-title-down">
      <el-row>
        <el-col :span="4" :offset="20">
          <div class="fr">
            <el-button @click.stop="on_refresh" size="small">
              <i class="fa fa-refresh"></i>
            </el-button>
            <router-link :to="{name: 'agentEdit'}" tag="span">
              <el-button type="primary" icon="plus" size="small">设置代签</el-button>
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
          prop="id"
          label="id"
          width="80">
        </el-table-column>
        <el-table-column
          prop="approverName"
          width="150"
          label="审核人">
        </el-table-column>
        <el-table-column
          prop="userName"
          width="150"
          label="申请人">
        </el-table-column>
        <el-table-column
          prop="applyContent"
          width="261"
          label="申请描述">
        </el-table-column>
        <el-table-column
          prop="wantName"
          width="200"
          label="被申请角色/权限">
        </el-table-column>
        <el-table-column
          prop="approvalLevelName"
          width="200"
          label="审核级别">
        </el-table-column>
        <el-table-column
          prop="approvalResultName"
          width="150"
          label="审核结果">
        </el-table-column>
        <el-table-column
          prop="applyTime"
          width="150"
          label="申请时间">
          <template scope="props">
            <label v-text="to_date(props.row.applyTime)" ></label>
          </template>
        </el-table-column>
        <el-table-column
          prop="approvalTime"
          width="150"
          label="审核时间">
          <template scope="props">
            <label v-text="to_date(props.row.approvalTime)" ></label>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          fixed="right"
          width="147">
          <template scope="props">
            <el-row>
              <el-col :span="13" v-if="props.row.approvalResult === 'NONE'">
                <el-dropdown trigger="click" >
                  <el-button type="info" size="small">
                    操作<i class="el-icon-caret-bottom el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown" class="table-dropdown-menu">
                    <el-dropdown-item>
                      <a @click="pass(props.row)">
                        <span>审核通过</span>
                      </a>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <a @click="not_pass(props.row)">
                        <span>审核退回</span>
                      </a>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <a @click="to_router('changeSign',props.row)">
                        <span>转签</span>
                      </a>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <a @click="to_router('addSign',props.row)">
                        <span>加签</span>
                      </a>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
      </el-table>
      <bottom-tool-bar>
        <!--<el-button-->
        <!--type="danger"-->
        <!--icon="delete"-->
        <!--size="small"-->
        <!--:disabled="batch_select.length === 0"-->
        <!--@click="on_batch_del"-->
        <!--slot="handler">-->
        <!--<span>批量删除</span>-->
        <!--</el-button>-->
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
  import {panelTitle, bottomToolBar} from 'components'
  import {request_approval} from 'common/request_api'

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
        search_data: {},
      }
    },
    components: {
      panelTitle,
      bottomToolBar
    },
    created(){
      this.get_table_data()
    },
    methods: {
      to_date(time){
         return this.$dateFormat(time,'yyyy-MM-dd hh:mm')
      },
      to_router(routerName, row){
        this.$router.push({name: routerName, params: {id: row.id}})
      },
      //刷新
      on_refresh(){
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_approval.list, {
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
      //审核通过
      pass(item){
        this.$confirm('审核通过, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let param = this.$qs.stringify({id: item.id})
          this.$http.post(request_approval.pass, param)
            .then(({data: responseData}) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
        })
      },
      //审核退回
      not_pass(item){
        this.$confirm('审核退回, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let param = this.$qs.stringify({id: item.id})
          this.$http.post(request_approval.notPass, param)
            .then(({data: responseData}) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
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
