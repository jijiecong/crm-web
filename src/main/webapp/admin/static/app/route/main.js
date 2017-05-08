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
            name: null,
            path: null,
            method: null,
			interfaze: null, 
			responseDemo:null, 
            items: null
        };
        this.route = {
            add: "/route/add", 
            modify: "/route/modify",
            edit: "/route/edit",
            delForm: "/route/delete"
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
                    $("input[name=name]").val("");
                    $("input[name=path]").val("");
                    $("input[name=method]").val("");
                    $("input[name=interfaze]").val("");
					$("input[name=responseDemo]").val("");
                    $("#paramTypeRegion").html("");
                  });
                                
                
                //提交 
                $("#modal-form-submit").on("click", function(){
                    var path = $("input[name=path]").val();
                    var method = $("input[name=method]").val();
                    var interfaze = $("input[name=interfaze]").val();
					var responseDemo = $("input[name=responseDemo]").val();
                    var items =null;
                    $("#paramTypeRegion").children('li').each(function(i){
                    	if(items==null){
                    		items = $(this).find('.paramType').val();
                    	}else{
                    		items = items+","+$(this).find('.paramType').val();
                    	}
            		});  
                    pThis.add(path,method,interfaze,items,responseDemo);
                });
                //打开弹出层修改页面
                $(".edit").on("click", function(){
                	var routerId = $(this).data('uid'); 
                	 
                	$.ajax({
                		type : "POST",
                		dataType :"json",
                		data : {
                			id: routerId
                		},
                		url :  pThis.route.edit,
                		success : function(result){ 
                			   if (result.success) { 
                				  var path=result.data.path;
								  var method=result.data.method;
								  var interfaze=result.data.interfaze;
                				  var paramTypes = result.data.paramTypes; 
								  var responseDemo = result.data.responseDemo;
                				   $("input[name=path]").val(path); 
								   $("input[name=method]").val(method); 
								   $("input[name=interfaze]").val(interfaze); 
								   for (i=0;i<paramTypes.length ;i++ ) 
								   { 
									 $("#paramTypeItem li input").val(paramTypes[i]);
									 $("#paramTypeRegion").append($("#paramTypeItem").html());
								   }     
                			   }else{ 
                            	   alert(result.error);     
                               }
                			
                		}
                	}); 
                 
                    $("#modal-form-edit").data('uid', routerId);
                    $("#modal-form-submit").hide();
                    $("#modal-form-edit").show();  
                });
 
                 
	            //提交修改
	            $("#modal-form-edit").on("click", function(){
				
					var path = $("input[name=path]").val();
                    var method = $("input[name=method]").val();
                    var interfaze = $("input[name=interfaze]").val();
					var responseDemo = $("input[name=responseDemo]").val();
                    var items =null;
                    $("#paramTypeRegion").children('li').each(function(i){
                    	if(items==null){
                    		items = $(this).find('.paramType').val();
                    	}else{
                    		items = items+","+$(this).find('.paramType').val();
                    	}
            		});   
	                 pThis.modify(routerId,path,method,interfaze,items,responseDemo);
	            });
                 

                $(".del").on("click", function(){
                    var uid = $(this).data('uid');
                    $("#affirm-del").data('uid', uid);
                });
                

                $("#affirm-del").on("click", function(){
                    var routerId = $(this).data('uid');
                    pThis.delForm(routerId);
                });

            },
    		 
            /**
             * 添加表单
             */
            add: function(path,method,interfaze, items,responseDemo){
                this.set(this.route.add,'', path,method,interfaze, items,responseDemo);
                this._request(); 
            },
            
     		set: function(url,id,path,method,interfaze, items,responseDemo){
                this.requestUrl = url; 
                this.data.id  = id;
                this.data.path = path;
                this.data.method = method;
                this.data.interfaze = interfaze;
                this.data.items = items;
				this.data.responseDemo = responseDemo;
				
               
            },
            
            
            /**
             * deleteForm
             * @param cb
             * @param router_id
             */
            delForm: function(router_id){
            	this.requestUrl = this.route.delForm; 
                this.data.id  = router_id;
             //   this.set(this.route.delMission, router_id,'','','','');
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
            modify: function(id,path,method,interfaze,items,responseDemo){ 
                this.set(this.route.modify, id, path, method, interfaze,items,responseDemo);
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
    
    
    
    
   
    
    return {
    	 
	  init: function() { 
		  $("#addParamType").on("click", function(){  
			  $("#paramTypeItem li input").val("");
			  $("#paramTypeRegion").append($("#paramTypeItem").html());
			  $("#paramTypeRegion li .delParamType").on("click", function(){   
				    $(this).parent().remove();
			  });
          });  
        }
    }

});
