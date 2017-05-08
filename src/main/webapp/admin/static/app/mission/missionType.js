/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('collection');

    return {

        init: function() {
            var pThis = this;
            $(".add").on("click", function(){
                $("#myModalLabel1").html("添加");
                $("#modal-form-submit").show();
                //重置数据
                $("input[name=typeNameDesc]").val("");
                $("input[name=typeName]").val("");
                $("#typeNameUl").show();
                $("input[name=typeName]").show();
                $("input[name=description]").val("");
                //初始化数据
                $('#assocaite-rule-select option').each(function(){
                    this.selected=false;
                });
                window.refreshSelect();
                $("#modal-form-edit").hide();
            });
            //打开弹出层修改页面
            $(".edit").on("click", function(){
                $("#myModalLabel1").html("修改");
                $("#modal-form-submit").hide();
                $("input[name=typeName]").hide();
                $("#typeNameUl").hide();
                var id=$(this).attr("data-uid");
                $("input[name=uid]").val(id);
                //多选框初始化
                //根据任务类型Id获取当前的关联的规则
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        id:id
                    },
                    url :  "/mission/missionTypeDetail",
                    success : function(result){
                        if (result.success) {
                            $("input[name=typeNameDesc]").val(result.data.typeNameDesc);
                            $("input[name=description]").val(result.data.description);
                            var sceneIdList = result.data.associateRule;
                            sceneIdArr = sceneIdList.split(",");
                            $('#assocaite-rule-select option').each(function(i,content){
                                if($.inArray($.trim(content.value),sceneIdArr)>=0){
                                    this.selected=true;
                                } else {
                                    this.selected=false;
                                }
                            });
                            //设置选中值后，需要刷新select控件
                            window.refreshSelect();
                        }else{
                            window.alertGlobalErrorCall(result);
                        }

                    }
                });

                $("#modal-form-edit").show();
            });
            //提交新的条件
            $("#modal-form-submit").on("click", function(){
                var typeNameDesc=$("input[name=typeNameDesc]").val();
                var typeName=$("input[name=typeName]").val();
                var description=$("input[name=description]").val();
                // var selected = $("#assocaite-rule-select").multipleSelect("getSelects");
                var selected =window.getSelect();
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        typeNameDesc:typeNameDesc,
                        typeName: typeName,
                        description:description,
                        associateRule:selected
                    },
                    url :  "/mission/missionTypeCreate",
                    success : function(result){
                        if (result.success) {
                            //关闭对话框并且刷新当前页面
                            location.reload();
                        }else{
                            window.alertGlobalErrorCall(result);
                        }

                    }
                });

            });
            //修改条件===只允许改名字
            $("#modal-form-edit").on("click", function(){
                var uid=$("input[name=uid]").val();
                var typeNameDesc=$("input[name=typeNameDesc]").val();
                var description=$("input[name=description]").val();
                // var selected = $("#assocaite-rule-select").multipleSelect("getSelects");
                var selected =window.getSelect();
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        id:uid,
                        typeNameDesc:typeNameDesc,
                        description:description,
                        associateRule:selected
                    },
                    url :  "/mission/missionTypeModify",
                    success : function(result){
                        if (result.success) {
                            //关闭对话框并且刷新当前页面
                            location.reload();
                        }else{
                            window.alertGlobalErrorCall(result);
                        }

                    }
                });

            });


            $("#closeFormPage").on("click", function(){
                window.parent.closeIframeChannel(); //执行关闭自身操作
            });

            $(".del").on("click", function(){
                var uid = $(this).data('uid');
                $("#affirm-del").data('uid', uid);
            });


            $("#affirm-del").on("click", function(){
                var formId = $(this).data('uid');
                //删除一个通道
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        id:formId
                    },
                    url :  "/mission/missionTypeDelete",
                    success : function(result){
                        if (result.success) {
                            location.reload();
                        }else{
                            window.alertGlobalErrorCall(result);
                        }

                    }
                });

            });
			$(document).ready(function () {

            })
            // //===禁用
            // $("#disableConditionId").on("click", function(){
            //     var formId = $(this).data('uid');
            //     //禁用一个通道
            //     $.ajax({
            //         type : "POST",
            //         dataType :"json",
            //         data : {
            //             id:formId,
            //             isValid:false
            //         },
            //         url :  "/mission/missionConditionValid",
            //         success : function(result){
            //             if (result.success) {
            //                 location.reload();
            //             }else{
            //                 alert(result.error);
            //             }
            //
            //         }
            //     });
            //
            // });
            //
            // //===启用
            // $("#enableConditionId").on("click", function(){
            //     var formId = $(this).data('uid');
            //     //启用一个通道
            //     $.ajax({
            //         type : "POST",
            //         dataType :"json",
            //         data : {
            //             id:formId,
            //             isValid:true
            //         },
            //         url :  "/mission/missionConditionValid",
            //         success : function(result){
            //             if (result.success) {
            //                 location.reload();
            //             }else{
            //                 alert(result.error);
            //             }
            //
            //         }
            //     });
            //
            // });

        }
    }

});
