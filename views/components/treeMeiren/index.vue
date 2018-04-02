/**
*
* 树组件
*
* @Date: 2017/9/30
* @Author: jijc
*
*/
<template >
  <div >
    <el-input
      placeholder="输入关键字进行过滤" :size="size"
      v-model="filterText" style="height:30px;width: 360px;">
    </el-input>
    <div style="height: 15px;"></div>
    <el-tree
      class="filter-tree"
      ref="tree"
      :data="treeData"
      :default-checked-keys="defaultCheckedData"
      :show-checkbox ="showCheckbox"
      :check-strictly = "checkStrictly"
      node-key="id"
      :default-expand-all="false"
      :filter-node-method="filterNode"
      @check-change="handleCheckChange">
    </el-tree>
  </div >
</template >

<script>
  export default {
    props: {
      showCheckbox: {
        type: Boolean,
        default: false
      },
      checkStrictly: {
        type: Boolean,
        default: false
      },
      size: {
        type: String,
        default: null
      },
      initUrl: String,
      initId: [String, Number],
      businessId: Number
    },
    created(){
      this.initUrl && this.initId && this.get_form_data()
    },
    data() {
      return {
        treeData:[],
        defaultCheckedData:[],
        filterText: '',
        defaultProps: {
          children: 'children',
          label: 'label'
        }
      };
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },

    methods: {
      get_form_data(){
        this.axios.get(this.initUrl, {
          params: {
            initId: this.initId,
            businessId: this.businessId
          }
        }).then(({data: responseData}) => {
          if(responseData !== null){
            this.treeData = responseData.treeData;
            this.defaultCheckedData = responseData.defaultCheckedData;
          }
        }).catch(() => {
        })
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      getCheckedKeys() {
//        console.log(this.$refs.tree.getCheckedKeys());
      },
      handleCheckChange(data, checked, indeterminate) {
          let settedId = data.id;
          let type;
        if(checked == true){
          type = '/add'
        }else {
          type = '/del'
        }
        let param = this.$qs.stringify({ initId: this.initId, settedId: settedId })
        this.axios.post(this.initUrl + type, param)
          .then(({ data: responseData }) => {
            if (responseData !== null) {
              console.log(responseData)
            }
          }).catch(() => {
        })
      },
    }
  };
</script>
