/**
 * support
 * 新建表单基础信息
 */
define(function(require, exports, module) { 
    var $ = require('jquery');     
    require('js/component/plugin.ajaxform')($);
    var Calendar = require('js/arale/calendar/0.9.0/calendar');
    require('js/arale/calendar/0.9.0/calendar.css'); 
    
    function FormBasic(){
        this.data = {
        	id:null,
            uid: null,
            userName: null,
            title: null,
            summary: null
        };
        this.route = {
            add: "/form/add", 
            modify: "/form/modify",
            edit: "/form/edit",
            delForm: "/form/delete"
        };
        this.requestUrl = null;
        this.init();
    }
     
    FormBasic.prototype = {
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
                //打开弹出层修改页面
                $(".edit").on("click", function(){
                	var formId = $(this).data('uid'); 
                	var title,summary; 
                	$.ajax({
                		type : "POST",
                		dataType :"json",
                		data : {
                			id: formId
                		},
                		url :  pThis.route.edit,
                		success : function(result){ 
                			   if (result.success) { 
                				   title=result.data.title;
                				   summary = result.data.summary;
                				   $("input[name=title]").val(title); 
                                   $("#summary").val(summary); 
                			   }else{ 
                            	   alert(result.error);     
                               }
                			
                		}
                	}); 
                 
                    $("#modal-form-edit").data('uid', formId);
                    $("#modal-form-submit").hide();
                    $("#modal-form-edit").show();  
                });
 
                 
	            //提交修改
	            $("#modal-form-edit").on("click", function(){
	            	 var userId = $("input[name=uid]").val();
	                 var userName = $("input[name=userName]").val();
	                 var title = $("input[name=title]").val();
	                 var summary = $("#summary").val();  
	                 var formId = $(this).data('uid');
	                 pThis.modify(formId,userId,userName,title,summary);
	            });
                 

                $(".del").on("click", function(){
                    var uid = $(this).data('uid');
                    $("#affirm-del").data('uid', uid);
                });
                

                $("#affirm-del").on("click", function(){
                    var formId = $(this).data('uid');
                    pThis.delForm(formId);
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
            delForm: function(form_id){
            	this.requestUrl = this.route.delForm; 
                this.data.id  = form_id;
             //   this.set(this.route.delMission, form_id,'','','','');
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
    
        FormBasic = new FormBasic();
    
    
    
    
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
		  //点击背景遮罩移除iframe和背景
		  $(".setForm").on("click", function(){  
			  $("#selectedBasicRegion").val($(this).attr("data-uid"));
			  showIframe('/form/build',1300,800);
          });
		  KISSY.use('node,gallery/calendar-deprecated/1.0/,gallery/calendar-deprecated/1.0/assets/dpl.css', function(S, Node, Calendar) {
              var S_Date = Calendar.Date; 
      
              //时间控件
              new Calendar('#taskValidityStartTime', {
                  popup : true,
                  showTime : true,
                  triggerType : ['click'],
                  closable : true
                 
               }).on('timeSelect', function(e) {
          
                  S.DOM.val('#taskValidityStartTime', S_Date.format(e.date, 'yyyy-mm-dd HH:MM:ss'));
               });
               new Calendar('#taskValidityEndTime', {
               	popup : true,
               	showTime : true,
               	triggerType : ['click'],
               	closable : true
                
               }).on('timeSelect', function(e) {
               	
               	S.DOM.val('#taskValidityEndTime', S_Date.format(e.date, 'yyyy-mm-dd HH:MM:ss'));
               }); 
           });    

        }
    }

});
