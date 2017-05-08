/**
 * Created by caoguoshun on 2016/12/21.
 */

 var preSelect = function () {
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
}
var initSumoselect=function () {
    $('#state_select').SumoSelect({
        placeholder: '任务状态',
        csvDispCount: 1,
        captionFormat: '',
        captionFormatAllSelected: '',
    });
    $('#type_select').SumoSelect({
        placeholder: '任务类型',
        csvDispCount: 1,
        captionFormat: '',
        captionFormatAllSelected: '',
    });
    $('#channel_select').SumoSelect({
        placeholder: '投放渠道',
        csvDispCount: 1,
        captionFormat: '',
        captionFormatAllSelected: '',
    });
}

var handleEmoji=function () {
    $(".emoji").each(function () {
        var shortName=$(this).html();
        var emoji=emojione.shortnameToImage(shortName);
        $(this).html(emoji);
    })
}


var initCreateUsers=function () {
    var checkedUsers=$("#create_user_input_hidden").val();
    $.ajax({
        type : "get",
        dataType :"json",
        data : {

        },
        url :  "/mission/allCreateUsers",
        success : function(result){
            if (result.success) {
                //关闭对话框并且刷新当前页面
                var users=result.data;
                var createUserSelect=$("#create_user_select");
                for(i=0;i<users.length;i++){
                    var checked=checkedUsers.indexOf(users[i].id)!=-1;
                    if (checked){
                        $('<option></option>',{value:users[i].id,selected:'selected',text:users[i].userName}).appendTo(createUserSelect);
                    }else {
                        $('<option></option>',{value:users[i].id,text:users[i].userName}).appendTo(createUserSelect);
                    }
                }
                /*sumoselect初始化select一定要在select加载完成以后进行才有效果*/
                $('#create_user_select').SumoSelect({
                    placeholder:'创建人',
                    csvDispCount:1,
                    captionFormat:'',
                    captionFormatAllSelected:'',
                });
            }else{
                alertGlobalErrorCall(result);
            }
        }
    });
}

/**保存下拉列表选组的值*/
$(function () {
    $("select.SumoSelect").on("change",function () {
        var inputId=$(this).attr("inputId");
        var values=$(this).val();
        $("#"+inputId).val(values);
    });
})

function closeIframe() {
    $("#YuFrame1").remove();
    $("#YuFrame1Bg").remove();
}

/*验证ID是否为数字*/
$("#mission_index_submit_button").on("click",function () {
    var id=$("#missionIdSearch").val();
    if (id!=null&&id!=''&&!/^\d+$/.test(id)){
        $.notify("输入的ID必须是数字","warn");
        //取消表单的提交
        return false;
    }
});

var id;
$("td").on("click",function () {

    id=$(this).attr("data-uid");

});
/**
 * 上下架任务
 * @returns {*}
 */
$("#mission_online_submit").on("click",function () {
    $.ajax({
        type : "POST",
        dataType :"json",
        data : {
            id:id,
            state:3
        },
        url :  "/mission/modify",
        success : function(result){
            if (result.success) {
                //关闭对话框并且刷新当前页面
                location.reload();
            }else{
                alertGlobalErrorCall(result);
            }

        },
    });

});

$("#mission_offline_submit").on("click",function () {


    $.ajax({
        type : "POST",
        dataType :"json",
        data : {
            id:id,
            state:6
        },
        url :  "/mission/modify",
        success : function(result){
            if (result.success) {
                //关闭对话框并且刷新当前页面
                location.reload();
            }else{
//                    alert(result.error);
                alertGlobalErrorCall(result);
            }

        }
    });

});



/**
 * 跳转任务详情页面
 */
function missionQRCode(href) {
    var id=$(href).attr("data-uid")
    $.ajax({
        type : "get",
        dataType :"json",
        data : {
            id:id
        },
        url :  "/mission/missionQRCode",
        success : function(result){
            if (result.success) {
                toDetail(result.data)
            }else{
                alertGlobalErrorCall(result);
            }

        }
    });
}

function toDetail(data) {
    var form=$('<form  style="display: none" method="post" action="/mission/toDetail"></form>');
    var input1=$('<input  name="missionDetailUrl"/>')
    input1.val(data.missionDetailUrl);
    var input2=$('<input name="missionHtmlUrl"/>');
    input2.val(data.missionHtmlUrl);
    form.append(input1).append(input2);
    $("body").append(form);
    form.submit();
}


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


