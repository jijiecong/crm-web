<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" >
    </panel-title >
    <div class="panel-title-down" >
      <el-row >
        <el-col :span="20" >
          <form @submit.prevent="on_refresh" >
            <el-row :gutter="10" >
              <el-col :span="6" v-if="getUserInfo.inSide" >
                <simple-select :selectUrl="select_url" v-model="getBid" title="商家"
                  size="small" ></simple-select >
              </el-col >
              <el-col :span="6" >
                <el-input size="small" placeholder="名称" v-model="search_data.name" ></el-input >
              </el-col >
              <el-col :span="2" >
                <el-button type="primary" size="small" native-type="submit" >查询</el-button >
              </el-col >
            </el-row >
          </form >
        </el-col >
        <el-col :span="4" >
          <div class="fr" >
            <el-button @click.stop="on_refresh" size="small" >
              <i class="fa fa-refresh" ></i >
            </el-button >
            <router-link :to="{name: 'userAdd'}" tag="span" >
              <el-button type="primary" icon="plus" size="small" >添加数据</el-button >
            </router-link >
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
        @selection-change="on_batch_select"
        style="width: 100%;" >
        <el-table-column
          type="selection"
          width="42" >
        </el-table-column >
        <el-table-column
          prop="id"
          label="id"
          width="80" >
        </el-table-column >
        <el-table-column
          prop="nickname"
          label="昵称" >
        </el-table-column >
        <el-table-column
          prop="userName"
          label="用户名" >
        </el-table-column >
        <el-table-column
          prop="email"
          label="邮件" >
        </el-table-column >
        <el-table-column
          label="操作"
          width="280" >
          <template scope="props" >
            <el-row class="operation-row" >
              <el-col class="operation-col" >
                <el-button type="danger" size="small" icon="delete" @click="delete_data(props.row)" >删除</el-button >
              </el-col >
              <el-col class="operation-col" >
                <el-button type="success" size="small" icon="edit" @click="to_router('userRoleEdit',props.row)" >角色授权
                </el-button >
              </el-col >
              <el-col class="operation-col" >
                <el-dropdown
                  trigger="click"
                  split-button type="info" size="small"
                  @click="to_router('userUpdate',props.row)" >
                  修改
                  <el-dropdown-menu slot="dropdown" class="table-dropdown-menu" >
                    <el-dropdown-item >
                      <a @click="to_router('userChangeGroup',props.row)" >
                        <span >转岗</span >
                      </a >
                    </el-dropdown-item >
                    <el-dropdown-item >
                      <a @click="resign_data(props.row)" >
                        <span >离职</span >
                      </a >
                    </el-dropdown-item >
                    <el-dropdown-item >
                      <a @click="disable_data(props.row)" >
                        <span v-text="disable_text(props.row.status)" ></span >
                      </a >
                    </el-dropdown-item >
                    <el-dropdown-item >
                      <a @click="to_router('disableRole',props.row)" >
                        <span >角色禁用</span >
                      </a >
                    </el-dropdown-item >
                    <el-dropdown-item >
                      <a class="dropdown-item-btn" @click="to_router('disablePrivilege',props.row)" >
                        <span >权限禁用</span >
                      </a >
                    </el-dropdown-item >
                    <!--<el-dropdown-item >-->
                      <!--<a @click="to_router_hierarchy('setHierarchy',props.row)" >-->
                        <!--<span >设置层级</span >-->
                      <!--</a >-->
                    <!--</el-dropdown-item >-->
                  </el-dropdown-menu >
                </el-dropdown >
              </el-col >
            </el-row >
          </template >
        </el-table-column >
      </el-table >
      <bottom-tool-bar >
        <!--<el-button-->
        <!--type="danger"-->
        <!--icon="delete"-->
        <!--size="small"-->
        <!--:disabled="batch_select.length === 0"-->
        <!--@click="on_batch_del"-->
        <!--slot="handler">-->
        <!--<span>批量删除</span>-->
        <!--</el-button>-->
        <div slot="page" >
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page="currentPage"
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
  import { request_user, request_business } from 'common/request_api'
  import { mapGetters, mapActions } from 'vuex'

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
        search_data: {},
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
    },
    computed: {
      ...mapGetters(['getUserInfo', 'getBusinessId']),
      getBid: {
        get(){
          return this.getBusinessId
        },
        set(val){
          this.setBusinessId(val)
        }
      },
    },
    methods: {
      ...mapActions(['setBusinessId']),
      disable_text(status){
        let text = '禁用'
        if (status === 'DISABLE') {
          text = '启用'
        }
        return '用户' + text
      },
      to_router(routerName, row){
        this.$router.push({ name: routerName, params: { id: row.id } })
      },
      to_router_hierarchy(routerName, row){
        this.$router.push({ name: routerName, params: { id: row.id, hierarchyId: row.hierarchyId } })
      },
      //刷新
      on_refresh(){
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_user.list, {
          params: {
            page: this.currentPage,
            rows: this.rows,
            businessId: this.getBid,
            ...this.search_data
          }
        }).then(({ data }) => {
          this.table_data = data.data
          this.total_count = data.totalCount
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //离职
      resign_data(item){
        let param = this.$qs.stringify({ userId: item.id })
        this.$confirm('设置离职后所有权限将被收回', '是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http.post(request_user.resign, param)
            .then(({ data: responseData }) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
        })
      },
      //禁用
      disable_data(item){
        let param = this.$qs.stringify({ userId: item.id, status: item.status })
        let text = this.disable_text(item.status)
        this.$confirm('设置' + text, '是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http.post(request_user.disable, param)
            .then(({ data: responseData }) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
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
            h('span', null, 'ps:1、当用户在任一部门下时，2、当用户为任一角色或权限的owner时，3、当用户有需要审核的记录时，需要先取消关联才能删除，否则删除失败！'),
          ]),
          showCancelButton: true,
        }).then((action) => {
          if (action === 'cancel') {
            return
          }
          let param = this.$qs.stringify({id: item.id})
          this.$http.post(request_user.del, param)
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
            .then(({ data: responseData }) => {
              this.get_table_data()
              this.$message.success("操作成功")
            })
        })
      }
    }
  }
</script >
