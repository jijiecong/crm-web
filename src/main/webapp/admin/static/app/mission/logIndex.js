/**
 * Created by admin on 2017/4/13.
 */
define(function(require, exports, module) {

});

$(document).ready(function () {
    $('#operation_type_select').SumoSelect({
        placeholder:'操作类型',
        csvDispCount:1,
        captionFormat:'',
        captionFormatAllSelected:'',
    });

});

/**
 * 在这里没有效果。。。
 */
// $(document).ready(function (){
//     var inputs=$(".hiddenInput");//所有的隐藏输入框
//     inputs.each(function (index,input) {
//         var selectId=$(this).attr("selectId");//this==input
//         var value=input.value;
//         var hashtag="#";
//         //遍历当前select的option，如果option的value在上次选择的value中，则勾选当前option
//         $(hashtag+selectId+" option").each(function (optionIndex,option){
//             if(value.indexOf($(option).val())!=-1){
//                 // $(option).attr('selected',true);
//                 $(hashtag+selectId).get(0)[optionIndex].selectedIndex=1;
//             }
//         });
//     });
// })


/**保存下拉列表选组的值*/
$(function () {
    $("select.SumoSelect").on("change",function () {
        var inputId=$(this).attr("inputId");
        var values=$(this).val();
        $("#"+inputId).val(values);
    });
})

/**
 * 绑定查询按钮点击事件 验证id输入是否是数字 如果验证不通过 取消表单提交
 */
$("#search_button").on("click",function () {
    var id=$("#missionIdSearch").val();
    if (id!=null&&id!=''&&!/^\d+$/.test(id)){
        $.notify("输入的ID必须是数字","warn");
        //取消表单的提交
        return false;
    }
});


//        将日志描述截取固定长度赋值给td
var logDescs=$(".logDesc_span_hidden");
var descLength=180;
for(i=0;i<logDescs.length;i++){
    var originalDesc=logDescs[i].textContent;
    if (originalDesc.length>descLength){
        originalDesc=originalDesc.substr(0,descLength)+"...";
    }
    $(".logDesc_td")[i].textContent=originalDesc;
}

//点击日志描述弹出框显示
$(".logDesc_td").on("click",function (event) {
//            var currentDesc=$(this).html();
    var id=$(this).attr("data-uid");
    var fullDesc=$("#"+id).html();
    $("#logDesc_div").html(fullDesc);

    var height1=$("#logDesc_div").css("height");//80px
    var heightNum=parseInt(height1)
    var curTop=$(event.target).offset().top+$(event.target).outerHeight();
    //        顶部距离是0和curTop-height1/2的最大值\
    var topNum=parseInt(curTop);
    //        var top=topNum-heightNum/2;
    var top=Math.max(topNum-heightNum/2,0);
    $("#logDesc_div").css("top",top+"px");
    var divTop=$("#logDesc_div").css("top");

//            if(currentDesc.indexOf("...")!=-1){//如果没有三个点 不用弹出
    if(fullDesc.length>120){
        $("#logDesc_bg_div").css("display","block");
        $("#logDesc_div").css("display","block");
    }else {
        $("#logDesc_div").css("display","none");
        $("#logDesc_bg_div").css("display","none");
    }
});

$("#logDesc_bg_div").on("click",function () {
    $("#logDesc_bg_div").css("display","none");
    $("#logDesc_div").css("display","none");
});


/**
 *初始化时间选择控件
 */
var start = {
    elem: '#missionCreateStartTimeSearch',
    format: 'YYYY-MM-DD hh:mm:ss',
    max: '2099-06-16 23:59:59', //最大日期
    istime: true,
    istoday: true,
    choose: function(datas){
        end.min = datas; //开始日选好后，重置结束日的最小日期
        end.start = datas //将结束日的初始值设定为开始日
    }
};
var end = {
    elem: '#missionCreateEndTimeSearch',
    format: 'YYYY-MM-DD hh:mm:ss',
    max: '2099-06-16 23:59:59',
    istime: true,
    istoday: false,
    choose: function(datas){
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start);
laydate(end);