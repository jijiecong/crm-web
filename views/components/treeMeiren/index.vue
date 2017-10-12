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
      :data=this.value
      :show-checkbox ="showCheckbox"
      node-key="id"
      :default-expand-all="false"
      :filter-node-method="filterNode">
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
      size: {
        type: String,
        default: null
      },
      value: {
        type: Array,
        default: []
      }
    },
    data() {
      return {
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
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      }
    }
  };
</script>
