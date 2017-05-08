/**
 * support
 * 新建动态表单布局
 */
define(function (require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('collection');
    require('')
    var awardMapEnum = function () {
        var awardMap = {};
        awardMap[1] = "优惠券";
        awardMap[2] = "积分";
        awardMap[3] = "现金";
        return awardMap;
    }

    var showIframe = function (url, w, h) {
        //添加iframe
        var if_w = w;
        var if_h = h;
        //allowTransparency='true' 设置背景透明
        $("<iframe width='" + if_w + "' height='" + if_h + "' id='YuFrame1' name='YuFrame1' style='position:absolute;z-index:4;'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>").prependTo('body');
        var st = document.documentElement.scrollTop || document.body.scrollTop;//滚动条距顶部的距离
        var sl = document.documentElement.scrollLeft || document.body.scrollLeft;//滚动条距左边的距离
        var ch = document.documentElement.clientHeight;//屏幕的高度
        var cw = document.documentElement.clientWidth;//屏幕的宽度
        var objH = $("#YuFrame1").height();//浮动对象的高度
        var objW = $("#YuFrame1").width();//浮动对象的宽度
        var objT = Number(st) + (Number(ch) - Number(objH)) / 2;
        var objL = Number(sl) + (Number(cw) - Number(objW)) / 2;
        $("#YuFrame1").css('left', objL);
        $("#YuFrame1").css('top', objT);
        $("#YuFrame1").attr("src", url);
        //添加背景遮罩
        $("<div id='YuFrame1Bg' style='background-color: Gray;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
        var bgWidth = Math.max($("body").width(), cw);
        var bgHeight = Math.max($("body").height(), ch);
        $("#YuFrame1Bg").css({width: bgWidth, height: bgHeight});
        $("#YuFrame1Bg").click(function () {
            $("#YuFrame1").remove();
            $("#YuFrame1Bg").remove();
        });
    }

    return {

        init: function () {

            $("#closeFormPage").on("click", function () {
                window.parent.closeIframe(); //执行关闭自身操作
            });

            //查看详情
            $(".detailForm").on("click", function () {

                // showIframe('/missionTrace/build',800,500);
                var id = $(this).attr("data-uid");
                $("#selectedBasicRegion").val(id);

                //清空
                $("#textarea_trace_id").val("");
                $("#textarea_trace_user_id").val("");
                $("#textarea_trace_user_name").val("");
                $('.ui-questions-content-list li').remove();
                var _this = this;
                var formObj;
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    async: false,
                    data: {
                        id: id
                    },
                    url: "/missionTrace/detail",
                    success: function (result) {
                        if (result.success) {
                            formObj = result.data;
                        } else {
                            alert(result.error);
                        }

                    }
                });
                if (formObj == null) {
                    return;
                }
                var fieldArray = formObj;

                $("#textarea_trace_id").val(fieldArray.id);
                $("#textarea_trace_user_id").val(fieldArray.receiverId);
                $("#textarea_trace_user_name").val(fieldArray.receiverName);

                //奖励渲染
                var actionList = JSON.parse(fieldArray.actionDesc);
                var awardMap = awardMapEnum();
                var templateForm=$("#award_choice").html();
                $.each(actionList, function (index, action) {

                    var awardType = action.awardType;
                    var amount = action.amount;
                    var couponCode = action.couponCode;
                    var couponCodeTrue=false;
                    if(couponCode!=null){
                        couponCodeTrue=true;
                    }
                    var data = {
                        awardType: awardType,
                        awardName: awardMap[awardType],
                        amount: amount,
                        couponCode: couponCode,
                        couponCodeTrue:couponCodeTrue
                    }
                    // $('.ui-questions-content-list').append(template("award_choice", data));
                    $('.ui-questions-content-list').append(Mustache.to_html(templateForm, data));

                })

            });

            //弹出订单审批
            $(".auditForm").on("click", function () {
                $("#selectedBasicRegion").val($(this).attr("data-uid"));
            });

            //奖励补偿审批确定
            $("#modal-form-submit").on("click", function () {
                var traceId = $("#selectedBasicRegion").val();

                $.ajax({
                    type: "POST",
                    dataType: "json",
                    data: {
                        id: traceId
                    },
                    url: "/missionTrace/compensate",
                    success: function (result) {
                        if (result.success) {
                            location.reload();
                        } else {
                            alert(result.error);
                        }

                    }
                });
                //发送ajax请求，然后刷新页面

            });

            //初始化一些数据
            $("<option></option>").val("").text("请选择").appendTo($("#textarea_process"));
            $("<option></option>").val("RUNNING").text("进行中").appendTo($("#textarea_process"));
            $("<option></option>").val("SUCCESS").text("成功").appendTo($("#textarea_process"));
            $("<option></option>").val("FAILED").text("失败").appendTo($("#textarea_process"));
            $("#textarea_process").change(function () {
                var state = $("#textarea_process").find("option:selected").val();
                $("#searchStatus").val(state);
            })
            var state = $("#searchStatus").val();
            if (state != null && state != "") {
                $("#textarea_process option[value=" + state + "]").prop("selected", true);
            }
        }
    }

});
