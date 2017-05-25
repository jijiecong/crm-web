<template>
  <div class="panel">
    <panel-title :title="$route.meta.title">
    </panel-title>
    <div class="panel-title-down">
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
          prop="configId"
          label="配置编号">
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态码">
        </el-table-column>
        <el-table-column
        prop="domain"
        label="域名">
      </el-table-column>
        <el-table-column
          prop="content"
          width="200"
          label="内容">
          <template scope="props">
            <label v-text="to_content(props.row.content)" ></label>
          </template>
        </el-table-column>

        <el-table-column
          prop="resultTime"
          width="200"
          label="时间">
          <template scope="props">
            <label v-text="to_date(props.row.resultTime)" ></label>
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
  import {request_user, request_monitorResult} from 'common/request_api'
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
      //刷新
      on_refresh(){
        this.get_table_data()
      },
      to_date(time){
        return this.$dateFormat(time,'yyyy-MM-dd hh:mm')
      },
      to_content(content){
          if(content.length>15){
             content = content.substring(0,20)+'...'
           }
         return content;
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_monitorResult.list, {
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
      //批量选择
      on_batch_select(val){
        this.batch_select = val
      },
      //页码选择
      handleCurrentChange(val) {
        this.currentPage = val
        this.get_table_data()
      },
    }
  }
</script>
