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
			  $("#modal-form-submit").show();
			  $("#modal-form-edit").hide();
		  });
		  //打开弹出层修改页面
		  $(".edit").on("click", function(){
			  $("#modal-form-submit").hide();
			  $("#modal-form-edit").show();
		  });


		  //添加
		  $("#modal-form-submit").on("click", function(){
			  // var paramType=$("input[name=paramType]").val()
			  var paramType=$("#param_type_select").val()
			  var paramKey= $("input[name=paramKey]").val();
			  var paramValueType= $("input[name=paramValueType]").val();
			  var desc= $("input[name=desc]").val();
			  var missionParamValue=$("textarea[name=missionParamValue]").val();
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  paramType:paramType,
					  paramKey: paramKey,
					  paramValueType:paramValueType,
					  desc:desc,
					  missionParamValue:missionParamValue
				  },
				  url :  "/param/create",
				  success : function(result){
					  if (result.success) {
						  location.reload();
					  }else{
						  alert(result.error);
					  }

				  }
			  });

		  });
		  //修改提交
		  $("#modal-form-edit").on("click", function(){
			  var uid =$("input[name=uid]").val();
			  var paramType=$("input[name=paramType]").val()
			  var paramKey= $("input[name=paramKey]").val();
			  var paramValueType= $("input[name=paramValueType]").val();
			  var desc= $("input[name=desc]").val();
			  var missionParamValue=$("textarea[name=missionParamValue]").val();
			  
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  id:uid,
					  paramType:paramType,
					  paramKey: paramKey,
					  paramValueType:paramValueType,
					  desc:desc,
					  missionParamValue:missionParamValue
				  },
				  url :  "/param/modify",
				  success : function(result){
					  if (result.success) {
						  //关闭对话框并且刷新当前页面
						  location.reload();
					  }else{
						  alert(result.error);
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

		//删除确认
		  $("#affirm-del").on("click", function(){
			  var uid = $(this).data('uid');
			  
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  id:uid
				  },
				  url :  "/param/delete",
				  success : function(result){
					  if (result.success) {
						  location.reload();
					  }else{
						  alert(result.error);
					  }
				  }
			  });
		  });

		  $('#tableId tbody').on( 'click', 'tr', function () {
			  if ( !$(this).hasClass('selected') ) {
				  $('#tableId tr.selected').removeClass('selected');
				  $(this).addClass('selected');
			  }			  
			  var paramId =$("#tableId tr.selected").find("td").eq(0).text();
			  var paramType=$("#tableId tr.selected").find("td").eq(1).text();
			  var paramKey=$("#tableId tr.selected").find("td").eq(2).text();
			  var paramValueType=$("#tableId tr.selected").find("td").eq(3).text();
			  var desc=$("#tableId tr.selected").find("td").eq(4).text();
			  var missionParamValue=$("#tableId tr.selected").find("td").eq(5).text();
			  $("input[name=uid]").val(paramId);
			  $("input[name=paramType]").val(paramType)
			  $("input[name=paramKey]").val(paramKey);
			  $("input[name=paramValueType]").val(paramValueType);
			  $("input[name=desc]").val(desc);
			  $("textarea[name=missionParamValue]").val(missionParamValue);
		  } );

        }
    }

});
