<template>
  <el-row>
    <label class="simple-select-label" :class="[size ? 'label--' + size : '']" v-text="titleText" v-if="title"></label>
    <el-select class="select-div" :class="{'no-title':title===null}"
               v-model="value9"
               :clearable="clearable"
               :multiple="multiple"
               :size="size"
               filterable
               remote
               :placeholder="placeholder"
               :remote-method="remoteMethod"
               @input="handleInput">
      <el-option
        v-for="item in options4"
        :key="item.id"
        :label="item.name"
        :value="item.id">
      </el-option>
    </el-select>
  </el-row>
</template>

<script>
  export default {
    props: {
      value: [String, Number],
      selectUrl: String,
      selectData: {
        type: Array,
        default: () => {
          return []
        }
      },
      params: {
        type: Object,
        default: () => {
          return {}
        }
      },
      size: {
        type: String,
        default: null
      },
      multiple: {
        type: Boolean,
        default: false
      },
      placeholder: {
        type: String,
        default: '请选择'
      },
      title: {
        type: String,
        default: null
      },
      clearable: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        options4: [],
        value9: [],
        load_data: false,
        currentValue: this.value,
      }
    },

    computed: {
      titleText(){
        return this.title + '：'
      }
    },
    watch: {
      currentValue(val) {
          console.log(val)
      },
      value(val) {
        this.setCurrentValue(val)
      },
    },
    methods: {
      handleInput(val) {
        const value = val;
        this.$emit('input', value);
        this.setCurrentValue(value);
        this.$emit('change', value);
      },
      setCurrentValue(value) {
        if (value === this.currentValue) return;
        this.currentValue = value;
      },
      remoteMethod(query) {
        if (query !== '') {
          this.loading = false;
          this.$http.get(this.selectUrl, {
            params: {
              query: query
            }
          }).then(({data}) => {
            this.options4 = data
            this.load_data = false
          }).catch(() => {
            this.load_data = false
          });
        } else {
          this.options4 = [];
        }
      }
    }
  }
</script>
<style lang="scss" type="text/scss" rel="stylesheet/scss">
  .select-div {
    width: 78%;
  }

  .select-div.no-title {
    width: 100%;
  }

  .simple-select-label.label--small {
    line-height: 30px;
  }

  .simple-select-label {
    line-height: 35px;
    display: inline;
    text-align: right;
    font-size: 14px;
    float: left;
    color: #48576a;
    box-sizing: border-box;
    width: 22%;
  }
</style>
