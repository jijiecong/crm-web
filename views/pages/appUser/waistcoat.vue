<template >
  <div class="panel" >
    <panel-title :title="$route.meta.title" >
    </panel-title >
    <div class="panel-title-down" >
      <el-row>
        <el-col :span="20" >
          <form @submit.prevent="on_search" >
            <el-row :gutter="10" >
              <el-col :span="2" >
                <template>
                  <el-select v-model="getSelectValue" placeholder="请选择">
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
                <el-input size="middle" placeholder="请输入关键字" v-model="getSearchData" ></el-input >
              </el-col >
              <el-col :span="2" >
                <el-button type="primary" size="middle" native-type="submit" >查询</el-button >
              </el-col >
            </el-row >
          </form >
        </el-col >
       <!-- <el-col :span="2" >
          <input id="upload" type="file" @change="importfxx(this)"  accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" />
        </el-col>-->
          <el-col :span="4" >
          <div class="fr" >
            <el-button type="primary" size="middle" @click="dialogAddUserForm = true">
              添加
            </el-button >
            <el-button type="danger" size="middle" @click="removeUserBatch()">
              批量删除
            </el-button >
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
          prop="nickname"
          width="100"
          label="头像" >
          <template scope="scope">
            <img :src="scope.row.userIcon" width="40" height="40"/>
          </template>
        </el-table-column >
        <el-table-column
          prop="sex"
          width="60"
          label="性别" >
        </el-table-column >
        <el-table-column
          prop="regTime"
          label="注册时间" >
          <template scope="props">
            <label v-text="to_date(props.row.regTime)" ></label>
          </template>
        </el-table-column >
        <el-table-column
          label="操作"
          width="130" >
          <template scope="scope" >
            <el-row class="operation-row" >
              <el-button type="warning" size="small" @click="updateUserDialog(scope.row)">修改</el-button>
              <el-button type="danger" size="small" @click="removeUser(scope.row)">删除</el-button>
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

    <el-dialog title="添加马甲账号" :visible.sync="dialogAddUserForm" :modal-append-to-body="false">
      <el-form :model="addForm" :rules="rules" ref="addForm" >
        <el-form-item label="昵称" prop="nickname" :label-width="formLabelWidth">
          <el-input v-model="addForm.nickname" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="pwd" :label-width="formLabelWidth">
          <el-input v-model="addForm.pwd" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="头像" prop="userIcon" :label-width="formLabelWidth">
          <SingleImageUpload action="/upload/headUpload" :auto-upload="true" @uploadResponse="uploadResponse" :imageUrlInit="imgUrl"></SingleImageUpload>
          <input type="hidden" v-model="addForm.userIcon"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth">
            <el-button @click="dialogAddUserForm = false">取 消</el-button>
            <el-button type="primary" @click="submitForm('addForm')">确 定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-dialog title="修改马甲账号" :visible.sync="dialogEditUserForm" :modal-append-to-body="false">
      <el-form :model="editForm" :rules="rules" ref="editForm" >
        <input type="hidden" v-model="editForm.userId" />
        <el-form-item label="昵称" prop="nickname" :label-width="formLabelWidth">
          <el-input v-model="editForm.nickname" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="头像" :label-width="formLabelWidth">
          <SingleImageUpload action="/upload/headUpload" :auto-upload="true" @uploadResponse="uploadResponse" :imageUrlInit="imgUrl">
          </SingleImageUpload>
          <input type="hidden" v-model="editForm.userIcon"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth">
          <el-button @click="dialogEditUserForm = false">取 消</el-button>
          <el-button type="primary" @click="updateUser('editForm')">确 定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div >
</template >
<script type="text/javascript" >
  import { simpleSelect, panelTitle, bottomToolBar,SingleImageUpload } from 'components'
  import { request_appUser } from 'common/request_api'
  import { mapGetters, mapActions } from 'vuex'

  export default{
    data(){
      return {
        imgUrl:"http://avatar.adnonstop.com/sns/default_icon.png",
        table_data: null,
        //数据总条目
        total_count: 0,
        //每页显示多少条数据
        rows: 10,
        //请求时的loading效果
        load_data: true,
        //批量选择数组
        batch_select: [],
        dialogAddUserForm: false,
        dialogEditUserForm: false,
        formLabelWidth: '120px',
        options: [{
          value: 'userId',
          label: '用户ID'
        }, {
          value: 'nickname',
          label: '昵称'
        }, {
          value: 'mobile',
          label: '手机号'
        }],
        getSelectValue:'userId',
        addForm: {
          nickname: '',
          userIcon: 'http://avatar.adnonstop.com/sns/default_icon.png',
          pwd: '12345678',
        },
        editForm: {
          nickname: '',
          userIcon: '',
          userId: '',
        },
        rules: {
          nickname: [
            { required: true, message: '请输入昵称', trigger: 'blur' },
            { min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur' }
          ],
          pwd: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
          ]
        },
      }
    },
    watch: {},
    components: {
      simpleSelect,
      panelTitle,
      bottomToolBar,
      SingleImageUpload
    },
    created(){
      this.get_table_data()
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
     /* //导入数据
      importfxx(obj) {
        let _this = this;
        let inputDOM = this.$refs.inputer;
        // 通过DOM取文件数据

        this.file = event.currentTarget.files[0];

        var rABS = false; //是否将文件读取为二进制字符串
        var f = this.file;

        var reader = new FileReader();
        //if (!FileReader.prototype.readAsBinaryString) {
        FileReader.prototype.readAsBinaryString = function(f) {
          var binary = "";
          var rABS = false; //是否将文件读取为二进制字符串
          var pt = this;
          var wb; //读取完成的数据
          var outdata;
          var reader = new FileReader();
          reader.onload = function(e) {
            var bytes = new Uint8Array(reader.result);
            var length = bytes.byteLength;
            for(var i = 0; i < length; i++) {
              binary += String.fromCharCode(bytes[i]);
            }
            var XLSX = require('xlsx');
            if(rABS) {
              wb = XLSX.read(btoa(fixdata(binary)), { //手动转化
                type: 'base64'
              });
            } else {
              wb = XLSX.read(binary, {
                type: 'binary'
              });
            }
            outdata = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]);//outdata就是你想要的东西<img alt="羡慕" src="http://static.blog.csdn.net/xheditor/xheditor_emot/default/envy.gif">
            let param = this.$qs.stringify(outdata)
            console.log(JSON.stringify(param))
            console.log("param:"+JSON.stringify(param))
            this.$http.post(request_appUser.addWaistcoatUserBatch, param)
              .then(({data}) => {
                console.log(data)
              })
              .catch(() => {
              })

          }
          reader.readAsArrayBuffer(f);
        }
        if(rABS) {
          reader.readAsArrayBuffer(f);
        } else {
          reader.readAsBinaryString(f);
        }
      },*/
      //查询
      on_search(){
        this.get_table_data()
      },
      //刷新
      on_refresh(){
        this.getCurrentPage = 1
        this.getSearchData = ''
        this.getSelectValue = 'userId'
        this.get_table_data()
      },
      //获取数据
      get_table_data(){
        this.load_data = true
        this.$http.get(request_appUser.waistcoatList, {
          params: {
            page: this.getCurrentPage,
            rows: this.rows,
            queryType: this.getSelectValue,
            queryKeyword: this.getSearchData
          }
        }).then(({ data }) => {
          if(!data.success){
            this.$message.error(data.error)
          }
          this.table_data = data.data.data
          this.total_count = data.data.totalCount
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //页码选择
      handleCurrentChange(val) {
        this.getCurrentPage = val
        this.get_table_data()
      },
      //上传图片回调
      uploadResponse(res) {
        this.addForm.userIcon = res
        this.editForm.userIcon = res
      },
      //添加马甲账号
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.load_data = true
            let param = this.$qs.stringify(this.addForm)
            this.$http.post(request_appUser.addWaistcoatUser, param)
              .then(({data}) => {
                if(data.success){
                  var doc = this
                  this.$message.success('添加成功！')
                  setTimeout(function () {
                    doc.get_table_data()
                  }, 1000);
                }else{
                  this.$message.error('添加失败,错误代码：'+data.code+',原因：'+data.error)
                }
                this.load_data = false
              })
              .catch(() => {
                this.$message.error('添加失败！')
                this.load_data = false
              })
            this.dialogAddUserForm = false;
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      //修改马甲账号页面
      updateUserDialog(row) {
        let id = row.userId;
        this.imgUrl = row.userIcon
        this.editForm.userId = id
        this.editForm.userIcon = row.userIcon
        this.editForm.nickname = row.nickname
        this.dialogEditUserForm = true
       /* this.$http.get(request_appUser.getWaistcoatUser, {
          params: {userId:id}
        }).then(({ data }) => {
          if(data.success){
            this.imgUrl = data.data.userIcon
            this.editForm.nickname = data.data.nickname
            console.log("this.imgUrl:"+this.imgUrl)
          }else{
            this.$message.error(data.message)
          }
        }).catch(() => {
          this.$message.error('系统错误')
        })*/

      },
      //修改马甲账号
      updateUser(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.load_data = true
            let param = this.$qs.stringify(this.editForm)
            this.$http.post(request_appUser.editWaistcoatUser, param)
              .then(({data}) => {
                if(data.success){
                  var doc = this
                  this.$message.success('修改成功！')
                  setTimeout(function () {
                    doc.get_table_data()
                  }, 800);
                }else{
                  this.$message.error('修改失败,错误代码：'+data.code+',原因：'+data.error)
                }
                this.load_data = false
              })
              .catch(() => {
                this.$message.error('修改失败！')
                this.load_data = false
              })
            this.dialogEditUserForm = false;
          } else {
            console.log('error submit!!');
            return false;
          }
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
