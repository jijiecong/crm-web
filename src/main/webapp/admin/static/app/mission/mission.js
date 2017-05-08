/**
 * Created by caoguoshun on 2016/12/21.
 */
define(function(require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    var Calendar = require('js/arale/calendar/0.9.0/calendar');
    require('js/arale/calendar/0.9.0/calendar.css');
    require('js/component/jquery.mCustomScrollbar.concat.min.js')
    function MissionBasic(){
        this.data = {
            id:null,
            userId: null,
            userName: null,
            missionName: null,
            beginTime: null,
            endTime: null,
            channelIds: null,
            parentId: null,
            awardType: null,
            totalSnapshot:null,
            content:null,
            state:null,
            missionTypeId:null,
            isAuto:null,
            actionDesc:null,
            singleInvokeCount:null

        };
        this.route = {
            add: "/mission/add",
            modify: "/mission/modify",
            edit: "/mission/edit",
            delMission: "/mission/delete",
            detail:"/mission/detail",
            // loadContentEnum:"/mission/loadContentEnum",
            topMission: "/mission/top",
            cancelTopMission: "/mission/cancelTop",
        };
        this.requestUrl = null;
        this.init();
    }

    MissionBasic.prototype = {
        init: function(){
            var pThis = this
            //打开弹出层进行设置
            $(".add").on("click", function(){
                $("#modal-form-submit").show();
                $("#modal-form-edit").hide();
                $("input[name=title]").val("");
                $("#summary").val("");
            });

            //提交新建表单
            $("#modal-form-submit").on("click", function(){
                var uid = $("input[name=uid]").val();
                var userName = $("input[name=userName]").val();
                var title = $("input[name=title]").val();
                var summary = $("#summary").val();
                pThis.add(uid,userName,title,summary);
            });
            //初始化变量
            $("<option></option>").val("NULL").text("请选择").appendTo($("#textarea_state"));
            $("<option></option>").val("PENDING").text("待审核").appendTo($("#textarea_state"));
            $("<option></option>").val("PASS").text("审核通过").appendTo($("#textarea_state"));
            $("<option></option>").val("PUBLISH").text("发布").appendTo($("#textarea_state"));
            $("<option></option>").val("REFUSE").text("否决").appendTo($("#textarea_state"));
            $("<option></option>").val("OFFLINE").text("下架").appendTo($("#textarea_state"));

            $("#textarea_state").change(function () {
                var state=$("#textarea_state").find("option:selected").index();
                $("#searchState").val(state);
            })
           var state= $("#searchState").val();
            if(state!=null && state!="") {
                $("#textarea_state").get(0).options[state].selected = true;
            }

            //打开详情页面
            $(".edit").on("click", function(){
                var formId = $(this).data('uid');
                var missionVO;
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        id: formId
                    },
                    url :  pThis.route.detail,
                    success : function(result){
                        if (result.success) {
                           missionVO= result.data;
                           $("input[name=userId]").val(missionVO.userId);
                           $("input[name=userName]").val(missionVO.userName);
                            $("#fieldContent li").remove();
                           //初始化content
                           var content = jQuery.parseJSON(missionVO.content);
                            $.each(content,function(key,value){
                                //动态添加
                                var desc= mapContentEnum[key];
                                $("#fieldContent").append('<li>'+desc +'<input type="text" name='+key +' value='+value +' class="m-wrap large" style="margin-bottom: 5px;"/></li>');
                            });
                        }else{
                            alert(result.error);
                        }

                    }
                });

                $("#modal-form-edit").data('uid', formId);
                $("#modal-form-submit").hide();
                $("#modal-form-edit").show();
            });

            //content动态添加
            $("#addContent").on("click",function () {
                var value= $("#contentSelect").find("option:selected").val();
                var text=$("#contentSelect").find("option:selected").text();
                $("#fieldContent").append('<li>'+text +'<input type="text" name='+value +'value='+text +'class="m-wrap large" style="margin-bottom: 5px;"></li>');
            })

            //提交修改
            $("#modal-form-edit").on("click", function(){
                var userId = $("input[name=userId]").val();
                var userName = $("input[name=userName]").val();

            });


            $(".del").on("click", function(){
                var uid = $(this).data('uid');
                $("#affirm-del").data('uid', uid);
            });


            $("#affirm-del").on("click", function(){
                var formId = $(this).data('uid');
                pThis.delMission(formId);
            });
            
            $("span[name=top]").on("click", function(){
                var formId = $(this).parent().data('uid');
                pThis.topMission(formId);
            });
            
            $("span[name=cancelTop]").on("click", function(){
                var formId = $(this).parent().data('uid');
                pThis.cancelTopMission(formId);
            });

        },

        param: {
            formDom: '#form',
            checkboxDom: '.list-checkbox',
            allCheckboxDom: '#all-checkbox'
        },
        initCheckbox: function () {
            var pThis = this;
            var checkbox = $(this.param.checkboxDom);
            var allCheckbox = $(this.param.allCheckboxDom);
            allCheckbox.on("click", function () {
                var isChecked = $(this).is(':checked');
                if (isChecked) {
                    checkbox.attr("checked", "checked");
                } else {
                    checkbox.removeAttr("checked");
                }
                $.uniform.update();
            });
            checkbox.on("click", function () {
                var isChecked = $(this).is(':checked');
                var isCheckedAll = allCheckbox.is(':checked');
                if (!isChecked && isCheckedAll) {
                    allCheckbox.removeAttr("checked");
                }
                $.uniform.update();
            });
        },

        /**
         * 添加表单
         */
        add: function(uid,userName,title, summary){
            this.set(this.route.add,'', uid,userName,title, summary);
            this._request();
        },


        set: function(url,id,uid,userName,title, summary){
            this.requestUrl = url;
            this.data.id  = id;
            this.data.uid = uid;
            this.data.userName = userName;
            this.data.title = title;
            this.data.summary = summary;

        },


        /**
         * deleteForm
         * @param cb
         * @param form_id
         */
        delMission: function(form_id){
            this.requestUrl = this.route.delMission;
            this.data.id  = form_id;
            this._request();
        },

        _request: function(url){
            var _requestUrl = url ? url : this.requestUrl;
            $.ajax({
                type: "POST",
                url: _requestUrl,
                data:this.data,
                success: this._success,
                error: this._error
            });
        },

        _success: function(result){
            if (result.success) {
                location.reload();
            }else{
                alert(result.error);
            }
        },



        /**
         * 修改
         * @param cb
         * @param form_id
         * @param nick_name
         * @param email
         */
        modify: function(id,uid,userName,title,summary){
            this.set(this.route.modify, id, uid, userName, title,summary);
            this._success = function(result){
                if(result.success){
                    location.reload();
                }else{
                    alert(result.error);
                }
            };
            this._request();
        },



        _error: function(error){
            var msg = JSON.parse(error.responseText).data.status.msg;
            var msg = msg ? msg : '网络异常!';
            oTips.init(msg);
        }
    }

    MissionBasic = new MissionBasic();

    var contentList={};
    var mapContentEnum={};
    var loadEnum = function () {
        $.ajax({
            type : "POST",
            dataType :"json",
            data : {

            },
            url :  MissionBasic.route.loadContentEnum,
            success : function(result){
                if (result.success) {
                  contentList= result.data;
                    $.each(contentList,function (index, value) {
                        $("<option></option>").val(value.name).text(value.desc).appendTo($("#contentSelect"));
                        mapContentEnum[value.name]=value.desc;
                    })
                }else{
                    alert(result.error);
                }

            }
        });

    }
    var stateEnum=function () {
        // $("<option></option>").val(value.name).text(value.desc).appendTo($("#stateSelect"));
    }
    var showIframe= function(url,w,h){
        //添加iframe
        var if_w = w;
        var if_h = h;
        //allowTransparency='true' 设置背景透明
        $("<iframe width='" + if_w + "' height='" + if_h + "' id='YuFrame1' name='YuFrame1' style='position:absolute;z-index:4;'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>").prependTo('body');
        var st=document.documentElement.scrollTop|| document.body.scrollTop;//滚动条距顶部的距离
        var sl=document.documentElement.scrollLeft|| document.body.scrollLeft;//滚动条距左边的距离
        var ch=document.documentElement.clientHeight;//屏幕的高度
        var cw=document.documentElement.clientWidth;//屏幕的宽度
        var objH=$("#YuFrame1").height();//浮动对象的高度
        var objW=$("#YuFrame1").width();//浮动对象的宽度
        var objT=Number(st)+(Number(ch)-Number(objH))/2;
        var objL=Number(sl)+(Number(cw)-Number(objW))/2;
        $("#YuFrame1").css('left',objL);
        $("#YuFrame1").css('top',objT);

        $("#YuFrame1").attr("src", url);

        //添加背景遮罩
        $("<div id='YuFrame1Bg' style='background-color: Gray;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
        var bgWidth = Math.max($("body").width(),cw);
        var bgHeight = Math.max($("body").height(),ch);
        $("#YuFrame1Bg").css({width:bgWidth,height:bgHeight});
        $("#YuFrame1Bg").click(function() {
            $("#YuFrame1").remove();
            $("#YuFrame1Bg").remove();
        });
    }
    return {

        init: function() {
            loadEnum();
            stateEnum();
            $(".setForm").on("click", function(){
                $("#selectedBasicRegion").val($(this).attr("data-uid"));
                showIframe('/mission/build',1300,800);
            });

            $(".addForm").on("click", function(){
                $("#selectedBasicRegion").val(-1);
                showIframe('/mission/build',1300,800);
            });

            // $(".mCustomScrollbar").mCustomScrollbar({ axis: "y" });
        }
    }

});
