/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports, module) { 
    var $ = require('jquery');     
    require('js/component/plugin.ajaxform')($);
    require('collection');

});

$("#closeFormPage").on("click", function(){
    window.parent.closeIframe(); //执行关闭自身操作
});


//弹出订单审批
$(".auditForm").on("click", function(){
    $("#selectedBasicRegion").val($(this).attr("data-uid"));
});

//审批确定
$(".audit_submit").on("click", function(){
    var auditResult=$(this).val();

    var walletOrderId =  $("#selectedBasicRegion").val();
    //审批描述
    var id="aduitDesc"+auditResult;
    var aduitDesc=$("#"+id).val();



    $.ajax({
        type : "POST",
        dataType :"json",
        data : {
            id: walletOrderId,
            auditResult:auditResult,
            auditDesc:aduitDesc
        },
        url :  "/withdraw/auditOrder",
        success : function(result){
            if (result.success) {
                location.reload();
            }else{
                alertGlobalErrorCall(result);
            }

        }
    });



});

//提现规则查看
$(".withdrawRule").on("click", function(){
    var withdrawRule=null;
    $.ajax({
        type : "POST",
        async:false,
        dataType :"json",
        data : {

        },
        url :  "/withdraw/getWithdrawRule",
        success : function(result){
            if (result.success) {
                withdrawRule=result.data;
                //渲染
                $("#maxWithdrawCount").val(withdrawRule.maxValue==null?"":withdrawRule.maxValue);
                $("#minWithdrawCount").val(withdrawRule.minValue==null?"":withdrawRule.minValue);
                $("#limitTimesWithdraw").val(withdrawRule.withdrawTimes==null?"":withdrawRule.withdrawTimes);
                $(".withdrawRuleClass").prop('checked', false);
                $(".withdrawRuleClass").parents("span").removeClass("checked");
                if(withdrawRule.withdrawDays!=null && withdrawRule.withdrawDays.length>0){
                    for (var i = 0; i < withdrawRule.withdrawDays.length; i++) {
                        var id="#dayWithdraw"+withdrawRule.withdrawDays[i];
                        $(id).prop('checked', true);
                        $(id).parents('span').addClass("checked");
                    }

                }
            }else{
                // alert(result.error);
                alertGlobalErrorCall(result);
            }

        }
    });


});

//提现规则修改
$("#modal-form-submit-rule").on("click", function(){
    var maxValue= $("#maxWithdrawCount").val();
    var minValue=$("#minWithdrawCount").val();
    var withdrawTimes= $("#limitTimesWithdraw").val();
    var listDays = new Array();
    $(".withdrawRuleClass").each(function () {
        if($(this).prop("checked")){
            listDays.push(parseInt($(this).attr("data-uid")));
        }

    })

    $.ajax({
        type : "POST",
        dataType :"json",
        data : {
            maxValue: maxValue,
            minValue:minValue,
            withdrawTimes:withdrawTimes,
            withdrawDays:JSON.stringify(listDays)
        },
        url :  "/withdraw/withdrawRuleUpdate",
        success : function(result){
            if (result.success) {
                // $("#modal-form-box-withdraw-rule").hide();
            }else{
                // alert(result.error);
                window.alertErrorCall(result.code)
            }

        }
    });

    //发送ajax请求，然后刷新页面


});
//导出提现订单

$("#modal-form-box-withdraw-export").on("click", function(){
    //导出时可以增加的附加条件：申请时间段 排序方式 用户名称 用户ID 默认是未处理的
    var userId=$("#user_id_input").val();//id
    var userName=$("#user_name_input").val();//name
    var withdrawAccount=$("#withdraw_account_input").val();
    var applyStartTime=$("#apply_min_time_input").val();
    var applyEndTime=$("#apply_max_time_input").val();
    var sort=$("#sort_input").val();
    $.ajax({
        type : "POST",
        dataType :"json",
        data : {
            userId:userId,
            userName:userName,
            withdrawAccount:withdrawAccount,
            applyStartTime:applyStartTime,
            applyEndTime:applyEndTime,
            sort:sort
        },
        url :  "/withdraw/export",
        success : function(result){
            if (result.success) {
                var data= result.data;
                window.open(data,"_self").close();
                // location.reload();
                setTimeout("location.reload()",200)
            }else{
                alertGlobalErrorCall(result)
            }

        }
    });

});
$("#modal-form-submit-confirm").on("click", function(){
    location.reload()
});


function alertGlobalErrorCall(result) {
    var errorCode=result.code;
    var errorMap= $.getGlobalErrorMap();
    if(errorCode==224){
        $.noPrivilege(result.data)
        return;
    }

    if(errorCode==225){
        $.stackTrace(result)
        return;
    }
    if(errorMap[errorCode]!=null){
        $.notify(errorMap[errorCode],"warn");
    } else {
        $.notify("系统异常","warn");
    }
}


$(document).ready(function (){
    var inputs=$(".hiddenInput");//所有的隐藏输入框
    inputs.each(function (index,input) {
        var selectId=$(this).attr("selectId");//this==input
        var value=input.value;
        //遍历当前select的option，如果option的value在上次选择的value中，则勾选当前option
        $("#"+selectId+" option").each(function (optionIndex,option){
            if(value.indexOf($(option).val())!=-1){
                $(this).prop('selected','selected');
            }
        });
    });
})

$(document).ready(function () {
    $('#status_select').SumoSelect({
        placeholder:'审核状态',
        csvDispCount:1,
        captionFormat:'',
        captionFormatAllSelected:'',
    });
    $('#process_select').SumoSelect({
        placeholder:'处理状态',
        csvDispCount:1,
        captionFormat:'',
        captionFormatAllSelected:'',
    });


});

$(document).ready(function () {//解析emoji表情shortname-->image
    $(".emoji").each(function () {
        var shortName=$(this).html();
//            alert(shortName)
        var emoji=emojione.shortnameToImage(shortName);
        $(this).html(emoji);
    })
})

/**保存下拉列表选组的值*/
$(function () {
    $("select.SumoSelect").on("change",function () {
        var inputId=$(this).attr("inputId");
        var values=$(this).val();
        $("#"+inputId).val(values);
    });
})



function closeIframe() {
//        alert("准备关闭详情")
    $("#withdraw_detail_div").remove();
    $("#YuFrame1Bg").remove();
}

/*渲染详情弹出框
 */
$(".withdrawDetail").on("click",function () {
    var id=$(this).attr("data-uid");
//        $("#withdraw_id").html(id);
    $.ajax({
        type:"POST",
        dataType:"json",
        async:false,
        data:{
            id:id
        },
        url:"/withdraw/detail",
        success:function (result) {
            if (result.success){
                var data=result.data;

                //渲染详情页面
                $("#withdraw_id").html(data.id);
                $("#withdraw_userName").html(data.userName);
                $("#withdraw_phone").html(data.phone);
                $("#withdraw_count").html(data.withdrawMoney);
                var status;
                if ("RUNNING"===data.status){
                    status="待审核";
                }else if ("SUCCESS"===data.status){
                    status="通过";
                }else if ("FAILED"===data.status){
                    status="拒绝";
                }
                $("#withdraw_status").html(status);
                $("#withdraw_applyTime").html(data.applyTime);
                var type;
                if("ALIPAY"==data.withdrawType){
                    type="支付宝";
                }else if("WEICAHTPAY"===data.withdrawType){
                    type="微信";
                }else if("TENAPY"===data.withdrawType){
                    type="财付通";
                }
                $("#withdraw_type").html(type);
                $("#withdraw_name").html(data.withdrawUserName);
                $("#withdraw_account").html(data.withdrawAccount);
                $("#withdraw_channel").html(data.channelName);
                $("#withdraw_auditUserName").html(data.auditUserName);
                $("#withdraw_desc").html(data.auditDesc);
                $("#withdraw_auditTime").html(data.auditTime);
            }else{
                // window.alertErrorCall(result.code)
                alertGlobalErrorCall(result)
            }
        }
    });
});


$("#search_button").on("click",function () {
    var id=$("#user_id_input").val();
    if (id!=null&&id!=''&&!/^\d+$/.test(id)){
        $.notify("输入的ID必须是数字","warn");
        //取消表单的提交
        return false;
    }
});

/**
 *文件上传
 */
$(document).ready(function () {
    $('input[type=file]').change(function () {
        $(this).simpleUpload("/withdraw/import", {
            start: function (file) {
                $('#filename').html(file.name);
                $('#progress').html("");
                $('#progressBar').width(0);
                console.log("upload started");
            },
            progress: function (progress) {
                //应该隐藏
                $('input[type=file]').prop("type","hidden");

                //received progress
                $('#progress').html("Progress: " + Math.round(progress) + "%");
                $('#progressBar').width(progress + "%");
                $('#progress').html("Progress: " + JSON.stringify(progress)+"%");
            },
            success: function (data) {//apiResult
                //upload successful
                if(data.error==null){

                    $('#progress').html("Success!");
                }else {
                    $('#progress').html("Failure!<br>Data: " + JSON.stringify(data.error));
                }
            },
            error: function (error) {
                //upload failed
                $('#progress').html("Failure!<br>" + error.name + ": " + error.message);
            }
        });
    });
});


/**
 *时间选择控件初始化
 */
var start = {
    elem: '#apply_min_time_input',
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
    elem: '#apply_max_time_input',
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

var start2 = {
    elem: '#audit_min_time_input',
    format: 'YYYY-MM-DD hh:mm:ss',
    max: '2099-06-16 23:59:59', //最大日期
    istime: true,
    istoday: true,
    choose: function(datas){
        end2.min = datas; //开始日选好后，重置结束日的最小日期
        end2.start = datas //将结束日的初始值设定为开始日
    }
};
var end2 = {
    elem: '#audit_max_time_input',
    format: 'YYYY-MM-DD hh:mm:ss',
    max: '2099-06-16 23:59:59',
    istime: true,
    istoday: false,
    choose: function(datas){
        start2.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(start2);
laydate(end2);
