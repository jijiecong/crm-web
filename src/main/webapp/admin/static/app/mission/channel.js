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

			  $("#modal-form-edit").hide();
		  });
		  //打开弹出层修改页面
		  $(".edit").on("click", function(){
              $("#myModalLabel1").html("修改	");
			  $("#modal-form-submit").hide();
              $("input[name=uid]").val($(this).attr("data-uid"));

			  $("#modal-form-edit").show();
		  });
		  //提交新的通道
		  $("#modal-form-submit").on("click", function(){
			  var channelName=$("input[name=channelName]").val();
			  var appName=$("input[name=appName]").val();
			  var appToken=$("input[name=appToken]").val();
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  channelName:channelName,
					  appName: appName,
					  appToken:appToken,
				  },
				  url :  "/mission/missionChannelCreate",
				  success : function(result){
					  if (result.success) {
						 //关闭对话框并且刷新当前页面
						  location.reload();
						  // alert("创建成功")
					  }else{
                          window.alertGlobalErrorCall(result);
					  }

				  }
			  });

		  });
		  //修改类型
		  $("#modal-form-edit").on("click", function(){
			  var uid=$("input[name=uid]").val();
			  var channelName=$("input[name=channelName]").val();
			  var appName=$("input[name=appName]").val()
			  var appToken=$("input[name=appToken]").val();
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  id:uid,
					  channelName:channelName,
					  appName: appName,
					  appToken:appToken,
				  },
				  url :  "/mission/missionChannelModify",
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
                  url :  "/mission/missionChannelDelete",
                  success : function(result){
                      if (result.success) {
                          location.reload();
                      }else{
                          window.alertGlobalErrorCall(result);
                      }

                  }
              });

		  });

		  //===禁用
          $("#disableChannelId").on("click", function(){
              var formId = $(this).data('uid');
              //禁用一个通道
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data : {
                      id:formId,
					  isValid:false
                  },
                  url :  "/mission/missionChannelValid",
                  success : function(result){
                      if (result.success) {
                          location.reload();
                      }else{
                          window.alertGlobalErrorCall(result);
                      }

                  }
              });

          });

          //===启用
          $("#enableChannelId").on("click", function(){
              var formId = $(this).data('uid');
              //启用一个通道
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data : {
                      id:formId,
                      isValid:true
                  },
                  url :  "/mission/missionChannelValid",
                  success : function(result){
                      if (result.success) {
                          location.reload();
                      }else{
                          window.alertGlobalErrorCall(result);
                      }

                  }
              });

          });

		  $('#tableId tbody').on( 'click', 'tr', function () {
			  if ( $(this).hasClass('selected') ) {
				  var channelId =$("#tableId tr.selected").find("td").eq(0).text();
				  var channelName=$("#tableId tr.selected").find("td").eq(1).text();
				  var appName=$("#tableId tr.selected").find("td").eq(2).text();
				  var appToken=$("#tableId tr.selected").find("td").eq(3).text();
				  $("input[name=uid]").val(channelId);
				  $("input[name=channelName]").val(channelName)
				  $("input[name=appName]").val(appName);
				  $("input[name=appToken]").val(appToken);

			  }
			  else {
				  $('#tableId tr.selected').removeClass('selected');
				  $(this).addClass('selected');
				  var channelId =$("#tableId tr.selected").find("td").eq(0).text();
				  var channelName=$("#tableId tr.selected").find("td").eq(1).text();
				  var appName=$("#tableId tr.selected").find("td").eq(2).text();
				  var appToken=$("#tableId tr.selected").find("td").eq(3).text();
				  $("input[name=uid]").val(channelId);
				  $("input[name=channelName]").val(channelName)
				  $("input[name=appName]").val(appName);
				  $("input[name=appToken]").val(appToken);

			  }
		  } );

        }
    }

});
