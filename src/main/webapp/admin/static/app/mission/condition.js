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
              $("input[name=conditionKey]").val("");
              $("input[name=conditionName]").val("");
              $("input[name=conditionKey]").show();
              $("#conditionKeyUl").show();
			  $("#modal-form-edit").hide();
		  });
		  //打开弹出层修改页面
		  $(".edit").on("click", function(){
              $("#myModalLabel1").html("修改");
			  $("#modal-form-submit").hide();
              $("input[name=uid]").val($(this).attr("data-uid"));
              $("input[name=conditionKey]").hide();
              $("#conditionKeyUl").hide();
			  $("#modal-form-edit").show();
		  });
		  //提交新的条件
		  $("#modal-form-submit").on("click", function(){
			  var conditionName=$("input[name=conditionName]").val();
			  var conditionKey=$("input[name=conditionKey]").val();
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  conditionName:conditionName,
					  conditionKey: conditionKey,
				  },
				  url :  "/mission/missionConditionCreate",
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
			  var conditionName=$("input[name=conditionName]").val();
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  id:uid,
					  conditionName:conditionName,
				  },
				  url :  "/mission/missionConditionModify",
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
                  url :  "/mission/missionConditionDelete",
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
          $("#disableConditionId").on("click", function(){
              var formId = $(this).data('uid');
              //禁用一个通道
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data : {
                      id:formId,
					  isValid:false
                  },
                  url :  "/mission/missionConditionValid",
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
          $("#enableConditionId").on("click", function(){
              var formId = $(this).data('uid');
              //启用一个通道
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data : {
                      id:formId,
                      isValid:true
                  },
                  url :  "/mission/missionConditionValid",
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
                  var conditionId =$("#tableId tr.selected").find("td").eq(0).text();
                  var conditionName=$("#tableId tr.selected").find("td").eq(1).text();
                  var conditionKey=$("#tableId tr.selected").find("td").eq(2).text();
                  $("input[name=uid]").val(conditionId);
                  $("input[name=conditionName]").val(conditionName)
                  $("input[name=conditionKey]").val(conditionKey);

              }
              else {
                  $('#tableId tr.selected').removeClass('selected');
                  $(this).addClass('selected');
                  var conditionId =$("#tableId tr.selected").find("td").eq(0).text();
                  var conditionName=$("#tableId tr.selected").find("td").eq(1).text();
                  var conditionKey=$("#tableId tr.selected").find("td").eq(2).text();
                  $("input[name=uid]").val(conditionId);
                  $("input[name=conditionName]").val(conditionName)
                  $("input[name=conditionKey]").val(conditionKey);

              }
          } );

        }
    }

});
