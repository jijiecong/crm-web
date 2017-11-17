<template>
  <div :id="chartId" :style="styleObject" ref="charts"></div>
</template>

<script type="text/javascript">
  import echarts from 'echarts'

  export default {
    props: {
      styleObject: {
        type: Object,
        default: () => {
          return {
            width: '500px',
            height: '400px'
          }
        }
      },
      option: {
        type: Object,
        default: null
      },
      chartId: {
        type: String,
        default: 'mainChart'
      }
    },
    data() {
      return {
        myChart: {}
      }
    },
    mounted() {
      //避免在派发事件时，获取到的this是chart实例
      let _this = this
      // 基于准备好的dom，初始化echarts实例
      this.myChart = echarts.init(document.getElementById(this.chartId))
      // 绘制图表
      if(this.option != null) {
          console.log(this.option)
        this.myChart.setOption(this.option)
      }
//      this.myChart.dispatchAction(this.action)
//      this.myChart.on('click', function (params) {
//        _this.$emit('select', params)
//      })
    },
    watch: {
        option(value, old) {
          if(value !== old) {
            this.myChart.clear();
            this.myChart.setOption(value)
          }
        },
    }
  }


</script>

<style lang="scss" type="text/scss" rel="stylesheet/scss">

</style>
