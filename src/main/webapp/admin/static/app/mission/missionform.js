/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports, module) { 
    var $ = require('jquery');     
    require('js/component/plugin.ajaxform')($);
    require('collection');
    var auditMission = function(missionInstanceId,status){
        //发送请求
        $.ajax({
            type : "POST",
            dataType :"json",
            data : {
                missionInstanceId:missionInstanceId,
                status: status,
            },
            url :  "/mission/instance/audit",
            success : function(result){
                if (result.success) {
                    //关闭对话框并且刷新当前页面

                    window.parent.parent.location.reload();
                    window.parent.closeIframe(); //执行关闭自身操作
                }else{
                    alert(result.error);
                }

            }
        });

    }
    return {
    	
	  init: function() { 
		  
		   
		  //点击保存表单按钮
		  $("#passForm").on("click", function(){
			  //通过
             var missionInstanceId= $("#selectedBasicRegion").val();
             var status="SUCCESS";
              auditMission(missionInstanceId,status);

          });
		  $("#failForm").on("click", function(){
			  //拒绝
              var missionInstanceId= $("#selectedBasicRegion").val();
              var status="FAIL";
             auditMission(missionInstanceId,status);

		  });
		  $("#closeFormPage").on("click", function(){ 
				 window.parent.closeIframe(); //执行关闭自身操作
          }); 
		  


          
		  
		  
		   

        }
    }

});
