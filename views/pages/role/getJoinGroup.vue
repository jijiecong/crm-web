<template>
  <div class="panel">
    <panel-title :title="$route.meta.title">
    </panel-title>
    <div class="panel-title-down">
      <el-row>
        <el-col :span="1" >
          <div >
            <el-button type="primary" size="small" @click="$router.back()">返回</el-button>
          </div>
        </el-col>
        <el-col :span="4" :offset="19">
          <div class="fr">
            <el-button type="danger" icon="el-icon-delete" size="small" @click="delete_all()">一键删除</el-button>
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
        style="width: 100%;">
        <!--<el-table-column-->
          <!--type="selection"-->
          <!--width="42">-->
        <!--</el-table-column>-->
        <el-table-column
          prop="id"
          label="id"
          width="80">
        </el-table-column>
        <el-table-column
          prop="groupId"
          label="部门Id"
          width="80">
        </el-table-column>
        <el-table-column
          prop="groupName"
          label="部门名称">
        </el-table-column>
        <el-table-column
          prop="businessName"
          label="部门所属商家">
        </el-table-column>
        <el-table-column
          label="操作"
          width="100">
          <template scope="props">
            <el-row class="operation-row">
              <el-col class="operation-col">
                <el-button type="warning" size="small" icon="delete" @click="delete_data(props.row)">删除</el-button>
              </el-col>
            </el-row>
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
  import {panelTitle, bottomToolBar} from 'components'
  import {request_role as request_uri} from 'common/request_api'

  export default{
    data(){
      return {
        route_id: this.$route.params.id,
        table_data: null,
        //数据总条目
        total_count: 0,
        //每页显示多少条数据
        currentPage: 1,
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
      this.route_id && this.get_table_data()
    },
    methods: {

      //查询
      on_search(){
        this.get_table_data()
      },
      //刷新
      on_refresh(){
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_uri.getJoinGroupList, {
          params: {
            page: this.currentPage,
            rows: this.rows,
            roleId: this.route_id
          }
        }).then(({data}) => {

          this.table_data = data.data
          this.total_count = data.totalCount
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //单个删除
      delete_data(item){
        const h = this.$createElement;
        this.$msgbox({
          title: '消息',
          type: 'warning',
          message: h('p', null, [
            h('span', null, '此操作将删除该部门和当前角色的所有关联, 是否继续?'),
          ]),
          showCancelButton: true,
        }).then((action) => {
          if (action === 'cancel') {
            return
          }
          let param = this.$qs.stringify({roleId: this.route_id, groupId: item.groupId})
          this.$http.post(request_uri.delGroupHasRole, param)
            .then(({data: responseData}) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
        })
      },
      //一键删除
      delete_all(){
        const h = this.$createElement;
        this.$msgbox({
          title: '消息',
          type: 'warning',
          message: h('p', null, [
            h('span', null, '此操作将删除当前角色和所有部门的关联, 是否继续?'),
          ]),
          showCancelButton: true,
        }).then((action) => {
          if (action === 'cancel') {
            return
          }
          let param = this.$qs.stringify({roleId: this.route_id})
          this.$http.post(request_uri.delGroupHasRole, param)
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
    }
  }
</script>
