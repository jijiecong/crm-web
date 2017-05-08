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
		  //数据传递到父页面
		  $("#saveForm").on("click", function(){
		  	var channelIdList="";
			  $(".selChannel").each(function () {
				  if($(this).prop("checked")==true){
				  	channelIdList=channelIdList+($(this).attr("data-uid"))+",";
				  }
			  })
			  if(channelIdList!=""){
				  //获取选中的channel
				  parent.document.getElementById("textarea_mission_channelIdList").value=channelIdList;
			  }
			  window.parent.closeIframeChannel(); //执行关闭自身操作
		  });

		  $("#closeFormPage").on("click", function(){
			  window.parent.closeIframeChannel(); //执行关闭自身操作
          });

        }
    }

});
