<template>
  <div class="panel">
    <panel-title :title="$route.meta.title" >
    </panel-title >
    <div class="panel-title-down" >
      <el-row >
        <el-col :span="20" >
          <form>
            <el-row :gutter="10" >
              <el-col :span="4" >
                <template>
                  <el-select v-model="selectValueApps1" multiple  placeholder="请选择APP名称">
                    <el-option
                      v-for="item in optionsApp"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </template>
              </el-col >
              <el-col :span="4" >
                <template>
                  <el-row >
                    <el-col :span="8">
                      <label style="line-height: 36px;font-size: 14px;">统计单位：</label >
                    </el-col >
                    <el-col :span="16">
                      <el-select v-model="selectValueType1" placeholder="请选择统计单位">
                        <el-option
                          v-for="item in optionsType"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value">
                        </el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                </template>
              </el-col >

              <el-col :span="4" >
                <template >
                  <el-date-picker
                    v-model="timeStart1"
                    :type="selectValueType1"
                    placeholder="选择起始时间">
                  </el-date-picker>
                </template>
              </el-col >
              <el-col :span="1">
                <span style="line-height: 36px;">——</span>
              </el-col>
              <el-col :span="4" >
                <template >
                  <el-date-picker
                    v-model="timeEnd1"
                    :type="selectValueType1"
                    placeholder="选择结束时间">
                  </el-date-picker>
                </template>
              </el-col >

              <el-col :span="2" >
                <el-button type="primary" size="middle  " native-type="submit" @click="search1()">查询</el-button >
              </el-col >
            </el-row >
          </form >
        </el-col >
        <!--<el-col :span="4" >-->
          <!--<div class="fr" >-->
            <!--<el-button size="middle" >-->
              <!--<i class="fa fa-refresh" ></i >-->
            <!--</el-button >-->
          <!--</div >-->
        <!--</el-col >-->
      </el-row >
    </div >
    <div class="panel-body">
      <div >
        <chart-meiren :option="optionRegister" :styleObject="styleObject" :chartId="chartIdRegister"></chart-meiren>
      </div>
    </div>
    <div class="panel-title-down" >
    </div>
    <div class="panel-title-down" >
      <el-row >
        <el-col :span="20" >
          <form>
            <el-row :gutter="10" >
              <el-col :span="4" >
                <template>
                  <el-select v-model="selectValueApp2" placeholder="请选择APP名称">
                    <el-option
                      v-for="item in optionsApp"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </template>
              </el-col >

              <el-col :span="4" >
                <template >
                  <el-date-picker
                    v-model="timeStart2"
                    type="datetime"
                    placeholder="选择起始时间">
                  </el-date-picker>
                </template>
              </el-col >
              <el-col :span="1">
                <span style="line-height: 36px;">——</span>
              </el-col>
              <el-col :span="4" >
                <template >
                  <el-date-picker
                    v-model="timeEnd2"
                    type="datetime"
                    placeholder="选择结束时间">
                  </el-date-picker>
                </template>
              </el-col >

              <el-col :span="2" >
                <el-button type="primary" size="middle  " native-type="submit" @click="search2()">查询</el-button >
              </el-col >
            </el-row >
          </form >
        </el-col >
        <!--<el-col :span="4" >-->
          <!--<div class="fr" >-->
            <!--<el-button size="middle" >-->
              <!--<i class="fa fa-refresh" ></i >-->
            <!--</el-button >-->
          <!--</div >-->
        <!--</el-col >-->
      </el-row >
    </div >

    <div class="panel-body">
      <div style="height: 480px;">
        <div class="chart-wrapper">
          <chart-meiren :option="optionRegisterWay" :chartId="chartIdRegisterWay"></chart-meiren>
        </div>
        <div class="chart-wrapper">
          <chart-meiren :option="optionUserSex" :chartId="chartIdUserSex"></chart-meiren>
        </div>
        <div class="chart-wrapper">
          <chart-meiren :option="optionUserAge" :chartId="chartIdUserAge"></chart-meiren>
        </div>
      </div>
    </div>

    <div class="panel-title-down" >
    </div>
    <div class="panel-body">
      <div >
        <chart-meiren :option="optionRegisterFrom" :styleObject="styleObject" :chartId="chartIdRegisterFrom"></chart-meiren>
      </div>
    </div>
  </div>
</template>

<script type="text/javascript">

  import {chartMeiren, panelTitle} from 'components'
  import { request_appUser } from 'common/request_api'

  export default {
    data() {
      return {
        dataxAxis: [],
        styleObject: null,
        optionRegister: null,
        optionRegisterWay: null,
        optionUserSex: null,
        optionUserAge: null,
        chartIdRegister: 'chartIdRegister',
        chartIdRegisterWay: 'chartIdRegisterWay',
        chartIdUserSex: 'chartIdUserSex',
        chartIdUserAge: 'chartIdUserAge',
        optionsApp: [],
        optionsType: [{
          value: 'datetime',
          label: '小时'
        }, {
          value: 'date',
          label: '日'
        }, {
          value: 'month',
          label: '月'
        }, {
          value: 'year',
          label: '年'
        }],
        optionRegisterFrom: null,
        chartIdRegisterFrom: 'chartIdRegisterFrom',
        selectValueApps1: [],
        selectValueApp2: 'jianpin',
        selectValueType1: '',
        timeStart1: '',
        timeEnd1: '',
        timeStart2: '',
        timeEnd2: ''
      }
    },
    watch: {
      selectValueType1(value, old) {
        if(value !== old) {
          this.timeStart1 = ''
          this.timeEnd1= ''
        }
      }
    },
    created() {
      //初始化自动生成一些测试数据
      for (var i = 0; i < 20; i++) {
        this.dataxAxis.push(i);
      }

      this.styleObject = {
        width: '95%',
        height: '400px'
      }
      this.getRegisterStatistics()
      this.getRegisterToPieStatistics()
      this.getRegisterByProjectNameStatistics()
      this.getProjects()
    },
    components: {
      chartMeiren,
      panelTitle
    },
    methods: {
      search1() {
        this.getRegisterStatistics()
      },
      search2() {
        this.getRegisterToPieStatistics()
      },
      checkTime(start, end, type){
        if(type == '') {
            return true
        }else {
          if(start == '' && end == '') {
            return true
          }
          if(start == '' || end == '') {
            return false
          }
          let temp = end.getTime() - start.getTime()
          if(type == 'datetime') {//小时
              if(temp > (3600 * 1000 * 24)){
                  return false
              }
          }else if(type == 'date'){//天
            if(temp > (3600 * 1000 * 24 * 31)){
              return false
            }
          }else if(type == 'month'){//月
            if(temp > (3600 * 1000 * 24 * 30 * 12)){
              return false
            }
          }
        }
        return true
      },
      getProjects(){
        this.$http.get(request_appUser.getProjects, {
          params: {
          }
        }).then(({ data }) => {
          let projects = [];
          for(var key in data ){
            projects.push({
              value:key,
              label:data[key]
            })
          }
          this.optionsApp = projects
        }).catch(() => {
        })
      },
      getRegisterStatistics(){
        let timeStart = null
        let timeEnd = null
        if(!this.checkTime(this.timeStart1, this.timeEnd1, this.selectValueType1)){
          this.$message({
            message: '请选择正确的时间区间：开始结束时间不能为空，按小时查询最大区间为24小时，按日查询最大区间为31日，按月查询最大区间为12个月',
            type: 'warning'
          });
          return;
        }
        if(this.timeStart1 != '' && this.timeEnd1 != '') {
          timeStart = this.timeStart1.getTime()
          timeEnd = this.timeEnd1.getTime()
        }
        this.$http.get(request_appUser.registerStatistics, {
          params: {
            projectNames: this.selectValueApps1,
            dateFormat: this.selectValueType1,
            timeStart: timeStart,
            timeEnd: timeEnd
          }
        }).then(({ data }) => {
          alert(data)
          console.log("--"+data);
          let legendData = []
          let series = []
          let xAxisData = []
          let flag = true
          for(var key in data){
            legendData.push(key)
            let seriesData = []
            data[key].forEach(function (value, index) {
              seriesData.push(value.counts)
              if(flag) {
                xAxisData.push(value.commonField)
              }
            })
            flag = false
            series.push({name:key,
              type:'line',
              stack: '总量',
              data:seriesData})
          }
        this.optionRegister = {
            title: {
              text: '用户注册统计图',
              left: 'center',
              top: 20
            },
            tooltip: {
              trigger: 'axis'
            },
            legend: {
                left: 'left',
              data:legendData
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            toolbox: {

            },
            xAxis: {
              type: 'category',
              name : '日期',
              boundaryGap: false,
              data: xAxisData
            },
            yAxis: {
              type: 'value',
              name : '注册人数'
            },
            series: series
          }
        }).catch(() => {
        })
      },
      getRegisterToPieStatistics(){
        let timeStart = null
        let timeEnd = null
        if(this.timeStart2 != '' && this.timeEnd2 != '') {
          timeStart = this.timeStart2.getTime()
          timeEnd = this.timeEnd2.getTime()
        }
        this.$http.get(request_appUser.registerToPieStatistics, {
          params: {
            projectName: this.selectValueApp2,
            timeStart: timeStart,
            timeEnd: timeEnd
          }
        }).then(({ data }) => {
          //注册渠道统计
          let registerSource = data["registerSource"]
          let seriesDataRegisterSource = []
          let legendDataRegisterSource = []
          registerSource.forEach(function (value, index) {
            seriesDataRegisterSource.push({value:value.counts, name:value.commonField})
            legendDataRegisterSource.push(value.commonField)
          })

          //注册年龄
          let age = data["age"]
          let seriesDataAge = []
          let legendDataAge = []
          age.forEach(function (value, index) {
            seriesDataAge.push({value:value.counts, name:value.commonField})
            legendDataAge.push(value.commonField)
          })

          //注册性别
          let sex = data["sex"]
          let seriesDataSex = []
          let legendDataSex = []
          sex.forEach(function (value, index) {
            seriesDataSex.push({value:value.counts, name:value.commonField})
            legendDataSex.push(value.commonField)
          })
          this.optionRegisterWay = {
            title : {
              text: '注册渠道统计',
              subtext: '实时数据',
              x:'center'
            },
            tooltip : {
              trigger: 'item',
              formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              data: legendDataRegisterSource
            },
            series : [
              {
                name: '注册渠道',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:seriesDataRegisterSource,
                itemStyle: {
                  emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              }
            ]
          }
          this.optionUserSex = {
            title : {
              text: '用户性别',
              subtext: '实时数据',
              x:'center'
            },
            tooltip : {
              trigger: 'item',
              formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              data: legendDataSex
            },
            series : [
              {
                name: '用户性别',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data: seriesDataSex,
                itemStyle: {
                  emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              }
            ]
          }
          this.optionUserAge = {
            title : {
              text: '年龄',
              subtext: '实时数据',
              x:'center'
            },
            tooltip : {
              trigger: 'item',
              formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              data: legendDataAge
            },
            series : [
              {
                name: '年龄',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data: seriesDataAge,
                itemStyle: {
                  emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              }
            ]
          }
        }).catch(() => {
        })
      },
      getRegisterByProjectNameStatistics(){
        this.$http.get(request_appUser.registerByProjectNameStatistics, {
          params: {
          }
        }).then(({ data }) => {
          let seriesData = []
          let xAxisData = []
          data.forEach(function (value, index) {
            xAxisData.push(value.commonField)
            seriesData.push(value.counts)
          })
          this.optionRegisterFrom = {
            title: {
              text: '注册来源统计图',
              left: 'center',
              top: 10
            },
            color: ['#3398DB'],
            tooltip : {
              trigger: 'axis',
              axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
              }
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis : [
              {
                type : 'category',
                data : xAxisData,
                name : 'APP名称',
                axisTick: {
                  alignWithLabel: true
                }
              }
            ],
            yAxis : [
              {
                type : 'value',
                name : '注册人数'
              }
            ],
            series : [
              {
                name:'注册人数',
                type:'bar',
                barWidth: '30%',
                data:seriesData
              }
            ]
          }
        }).catch(() => {
        })
      }
    }
  }
</script>

<style lang="scss" type="text/scss" rel="stylesheet/scss">
  .chart-wrapper{
    padding-top: 50px;
    padding-right: 30px;
    padding-bottom: 30px;
    float:left;
  }

</style>
