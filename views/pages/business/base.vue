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
              <el-col :span="2">
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
            <router-link :to="{name: 'businessAdd'}" tag="span">
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
          prop="name"
          label="商家名称">
        </el-table-column>
        <el-table-column
          prop="description"
          label="商家描述">
        </el-table-column>
        <el-table-column
          prop="token"
          label="商家token">
        </el-table-column>
        <el-table-column
          label="操作"
          width="180">
          <template scope="props">
            <el-row>
              <el-col :span="11">
                <el-button type="danger" size="small" icon="delete" @click="delete_data(props.row)" :disabled="props.row.token === 'INSIDE'">删除</el-button>
              </el-col>
              <el-col :span="13">
                <el-dropdown
                  trigger="click"
                  split-button type="info" size="small"
                  @click="to_router('businessUpdate',props.row)">
                  修改
                  <el-dropdown-menu slot="dropdown" class="table-dropdown-menu">
                    <el-dropdown-item>
                      <a @click="to_router('setBusinessHasPrivilege',props.row)">
                        <span>设置商家权限</span>
                      </a>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <a @click="to_router('setBusinessHasRole',props.row)">
                        <span>导入角色权限</span>
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
  import {request_user, request_business} from 'common/request_api'
  import {mapGetters} from 'vuex'

  export default{
    data(){
      return {
        select_url: request_business.search,
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
    watch: {},
    components: {
      simpleSelect,
      panelTitle,
      bottomToolBar
    },
    created(){
      if (this.getUserInfo.businessId) {
        this.search_data.businessId = this.getUserInfo.businessId;
      }
      this.get_table_data()
    },
    computed: {
      ...mapGetters(['getUserInfo'])
    },
    methods: {
      to_router(routerName, row){
        this.$router.push({name: routerName, params: {id: row.id}})
      },
      to_router_hierarchy(routerName, row){
        this.$router.push({name: routerName, params: {id: row.id, hierarchyId: row.hierarchyId}})
      },
      //刷新
      on_refresh(){
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_business.list, {
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
            h('span', null, 'ps:当商家下有用户时，需要先取消关联才能删除，否则删除失败！'),
          ]),
          showCancelButton: true,
        }).then((action) => {
          if (action === 'cancel') {
            return
          }
          let param = this.$qs.stringify({id: item.id})
          this.$http.post(request_business.del, param)
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
      on_batch_del(){
        this.$confirm('此操作将批量删除选择数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http.post(request_user.batch_del, this.batch_select)
            .then(({data: responseData}) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
        })
      }
    }
  }
</script>
