<template>
  <div class="panel">
    <panel-title :title="$route.meta.title">
    </panel-title>
    <div class="panel-title-down">
      <el-row>
        <el-col :span="20">
          <form @submit.prevent="on_refresh">
            <el-row :gutter="10">
              <el-col :span="6">
                <el-input size="small" placeholder="名称" v-model="search_data.name"></el-input>
              </el-col>
              <el-col :span="1">
                <el-button type="primary" size="small" native-type="submit">查询</el-button>
              </el-col>
            </el-row>
          </form>
        </el-col>
        <el-col :span="4">
          <div class="fr">
            <el-button @click.stop="on_refresh" size="small">
              <i class="fa fa-refresh"></i>
            </el-button>
            <router-link :to="{name: 'hierarchyAdd'}" tag="span">
              <el-button type="primary" icon="plus" size="small">添加数据</el-button>
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
          prop="hierarchyName"
          label="名称">
        </el-table-column>
        <el-table-column
          prop="sort"
          label="排序">
        </el-table-column>
        <el-table-column
          label="操作"
          width="180">
          <template scope="props">
            <el-row>
              <el-col :span="11">
                <el-button type="danger" size="small" icon="delete" @click="delete_data(props.row)">删除</el-button>
              </el-col>
              <el-col :span="13">
                <el-button split-button type="info" icon="edit" size="small" @click="to_router('hierarchyUpdate',props.row)">修改</el-button>
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
  import {panelTitle, bottomToolBar,simpleSelect} from 'components'
  import {request_hierarchy,request_business} from 'common/request_api'
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
        },
      }
    },
    components: {
      panelTitle,
      bottomToolBar,
      simpleSelect
    },
    created(){
      this.get_table_data()
    },
    computed: {
      ...mapGetters(['getUserInfo'])
    },
    methods: {
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
        this.$http.get(request_hierarchy.list, {
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
      //单个删除
      delete_data(item){
        const h = this.$createElement;
        this.$msgbox({
          title: '消息',
          type: 'warning',
          message: h('p', null, [
            h('span', null, '此操作将删除选择数据, 是否继续?'),
            h('br'),
            h('br'),
            h('span', null, 'ps:1、当有用户是该层级时，2、当有权限或角色的审核流程使用到该层级时，需要先取消关联才能删除，否则删除失败！'),
          ]),
          showCancelButton: true,
        }).then((action) => {
          if (action === 'cancel') {
            return
          }
          let param = this.$qs.stringify({id: item.id})
          this.$http.post(request_hierarchy.del, param)
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
      //批量删除
/*      on_batch_del(){
        this.$confirm('此操作将批量删除选择数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http.post(request_uri.batch_del, this.batch_select)
            .then(({data: responseData}) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
        })
      }*/
    }
  }
</script>
