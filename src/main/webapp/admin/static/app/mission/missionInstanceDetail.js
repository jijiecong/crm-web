/**
 * support
 * 新建动态表单布局
 */
define(function (require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('collection');

    var showIframe= function(url,w,h){
        //添加iframe
        var if_w = w;
        var if_h = h;
        //allowTransparency='true' 设置背景透明
        $("<iframe width='" + if_w + "' height='" + if_h + "' id='YuFrame2' name='YuFrame2' style='position:absolute;z-index:5;'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>").prependTo('body');
        var st=document.documentElement.scrollTop|| document.body.scrollTop;//滚动条距顶部的距离
        var sl=document.documentElement.scrollLeft|| document.body.scrollLeft;//滚动条距左边的距离
        var ch=document.documentElement.clientHeight;//屏幕的高度
        var cw=document.documentElement.clientWidth;//屏幕的宽度
        var objH=$("#YuFrame2").height();//浮动对象的高度
        var objW=$("#YuFrame2").width();//浮动对象的宽度
        var objT=Number(st)+(Number(ch)-Number(objH));
        var objL=Number(sl)+(Number(cw)-Number(objW))/2;
        $("#YuFrame2").css('left',objL);
        $("#YuFrame2").css('top',objT);

        $("#YuFrame2").attr("src", url);

        //添加背景遮罩
        $("<div id='YuFrame2Bg' style='background-color: Gray;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
        var bgWidth = Math.max($("body").width(),cw);
        var bgHeight = Math.max($("body").height(),ch);
        $("#YuFrame2Bg").css({width:bgWidth,height:bgHeight});
        $("#YuFrame2Bg").click(function() {
            $("#YuFrame2").remove();
            $("#YuFrame2Bg").remove();
        });
    }

    return {

        init: function () {
            $("#closeFormPage").on("click", function () {
                window.parent.closeIframe(); //执行关闭自身操作
            });
            $(".setForm").on('click',function () {
                //暂时审批表
                $("#selectedBasicRegion").val($(this).attr("data-uid"));
                showIframe('/mission/instance/form',1200,700);
            })

                // var formObj = window.parent.loadFormStructure();
                // if (formObj == null) {
                //     return;
                // }
                // var templateForm=   "<tr class=\"u_{{missionInstanceId}}\"> <td>{{missionInstanceId}}</td> <td>{{status}}</td> <td>{{missionName}}</td> <td>{{isAuto}}</td> <td>{{singleInvokeCount}}</td> <td>{{finishCount}}</td> <td>{{missionId}}</td> <td>{{missionParentId}}</td> </tr>"
                // var data = {
                //     missionInstanceId: formObj.missionInstanceId,
                //     status: formObj.status,
                //     missionName: formObj.missionName,
                //     isAuto: formObj.isAuto,
                //     singleInvokeCount: formObj.singleInvokeCount,
                //     finishCount: formObj.finishCount,
                //     missionId: formObj.missionId,
                //     missionParentId: formObj.missionParentId != null ? formObj.missionParentId : 0
                // };
                //
                // $('#tableBody').append(Mustache.to_html(templateForm, data))
                // $('#tableBody').append(template("drag_choice", data));
            }


    }

});
