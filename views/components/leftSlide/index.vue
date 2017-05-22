<template>
  <div class="left-side">
    <div class="left-side-inner">
      <router-link to="/" class="logo block">
        <div style="height: 40px">
          <img src="./images/logo.png" alt="">
        </div>
      </router-link>
      <el-menu
        class="menu-box"
        theme="dark"
        router
        :default-active="$route.path">
        <div
          v-for="(item, index) in nav_menu_data"
          :key="index">
          <el-menu-item
            class="menu-list"
            v-if="typeof item.child === 'undefined'"
            :index="item.path">
            <i class="icon fa" :class="item.icon"></i>
            <span v-text="item.title" class="text"></span>
          </el-menu-item>
          <el-submenu
            :index="item.path"
            v-else>
            <template slot="title">
              <i class="icon fa" :class="item.icon"></i>
              <span v-text="item.title" class="text"></span>
            </template>
            <el-menu-item
              class="menu-list"
              v-for="(sub_item, sub_index) in item.child"
              :index="sub_item.path"
              :key="sub_index">
              <!--<i class="icon fa" :class="sub_item.icon"></i>-->
              <span v-text="sub_item.title" class="text"></span>
            </el-menu-item>
          </el-submenu>
        </div>
      </el-menu>
    </div>
  </div>
</template>
<script type="text/javascript">

  export default{
    name: 'slide',
    data(){
      return {
        nav_menu_data: [{
          title: "主页",
          path: "/",
          icon: "fa-home"
        }, {
          title: "用户列表",
          path: "/user/base",
        }, {
          title: "权限列表",
          path: "/privilege/base",
        }, {
          title: "层级列表",
          path: "/hierarchy/base",
        }, {
          title: "部门列表",
          path: "/group/base",
        },{
          title: "角色列表",
          path: "/role/base",
        },{
          title: "用户角色列表",
          path: "/userAndRole/base",
        },{
          title: "商家列表",
          path: "/business/base",
        }, {
          title: "审核流程列表",
          path: "/process/base",
        },{
          title: "审核列表",
          path: "/approval/base",
        },
//          {
//            title: "表格管理",
//            path: "/table",
//            icon: "fa-table",
//            child: [{
//              title: "基本表格",
//              path: "/table/base"
//            }]
//          }
        ]
      }
    },
    components: {}
  }
</script>
