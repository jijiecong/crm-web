/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports,module) {
    var $ = require('jquery');     
    require('js/component/plugin.ajaxform')($);
    require('collection');

    var getTemplateData=function () {
        var templateName=$("input[name=templateName]").val();
        var description=$("input[name=templateDesc]").val();
        var templateContent=$("textarea[name=templateContent]").val();
        var templateConfigVO=new Object();
        //标题配置
        if($("#titleConfig").prop("checked")){
            templateConfigVO.title=true;

            if($("#titleSize").val()==null ){
                alert("标题限制字数不能为空");
                return null;
            }
            if(!$.isNumeric($("#titleSize").val())){
                alert("标题限制字数必须为数字");
                return null;
            }
            templateConfigVO.titleSize=$("#titleSize").val();
        } else {

            templateConfigVO.title=false;
        }
        //滑动的图片配置
        if($("#picConfig").prop("checked")){
            templateConfigVO.picList=true;

            if($("#picListSize").val()==null ){
                alert("图片限制张数不能为空");
                return null;
            }
            if(!$.isNumeric($("#picListSize").val())){
                alert("图片限制张数必须为数字");
                return null;
            }
            templateConfigVO.picListSize=$("#picListSize").val();
        } else {
            templateConfigVO.picList=false;
        }

        //是否有底部Banner配置
        if($("#bannerConfig").prop("checked")){
            templateConfigVO.banner=true;
        } else {
            templateConfigVO.banner=false;
        }
        //底部文字描述
        if($("#bannerDescConfig").prop("checked")){
            templateConfigVO.bannerDesc=true;
        } else {
            templateConfigVO.bannerDesc=false;
        }
        var data=new Object();
        data.templateName=templateName;
        data.description=description;
        data.templateContent=templateContent;
        data.templateConfigVO=templateConfigVO;
        return data;
        }

    return {
    	
	  init: function() {
		  var pThis = this;
		  $(".add").on("click", function(){
		      //设置默认值
              $("#myModalLabel1").html("添加");
			  $("#modal-form-submit").show();
              $("input[name=templateName]").val("");
              $("input[name=templateDesc]").val("");
              $("textarea[name=templateContent]").val("");
              $("#titleConfig").prop("checked",false);
              $("#picConfig").prop("checked",false);
              $("#bannerConfig").prop("checked",false);
              $("#bannerDescConfig").prop("checked",false);
			  $("#modal-form-edit").hide();
		  });
		  //打开弹出层修改页面
		  $(".edit").on("click", function(){
              $("#myModalLabel1").html("修改");
			  $("#modal-form-submit").hide();
              var id=$(this).attr("data-uid");
              $("input[name=uid]").val(id);
              //获取详情并进行数据填充
              var detail;
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data :{
                      id:id
                  },
                  url :  "/template/templateDetail",
                  success : function(result){
                      if (result.success) {
                          //关闭对话框并且刷新当前页面
                          detail=result.data;
                          $("input[name=templateName]").val(detail.templateName);
                          $("input[name=templateDesc]").val(detail.description);
                          $("textarea[name=templateContent]").val(detail.templateContent);
                          var templateConfigVO=JSON.parse(detail.config);
                          if(templateConfigVO.title==null ||!templateConfigVO.title){
                              $("#titleConfig").prop("checked",false);
                              $("#titleConfig").parents('span').removeClass("checked");
                              $("#titleSize").hide();
                          } else {
                              $("#titleConfig").prop("checked",true);
                              $("#titleConfig").parents('span').addClass("checked");
                              $("#titleSize").show();
                              $("#titleSize").val(templateConfigVO.titleSize);
                          }

                          if(templateConfigVO.picList==null ||!templateConfigVO.picList){
                              $("#picConfig").prop("checked",false);
                              $("#picConfig").parents('span').removeClass("checked");
                              $("#picListSize").hide();
                          } else {
                              $("#picConfig").prop("checked",true);
                              $("#picConfig").parents('span').addClass("checked");
                              $("#picListSize").show();
                              $("#picListSize").val(templateConfigVO.picListSize);
                          }
                          if(templateConfigVO.banner==null ||!templateConfigVO.banner){
                              $("#bannerConfig").prop("checked",false);
                              $("#bannerConfig").parents('span').removeClass("checked");
                          } else {
                              $("#bannerConfig").prop("checked",true);
                              $("#bannerConfig").parents('span').addClass("checked");
                          }
                          if(templateConfigVO.bannerDesc==null ||!templateConfigVO.bannerDesc){
                              $("#bannerDescConfig").prop("checked",false);
                              $("#bannerDescConfig").parents('span').removeClass("checked");
                          } else {
                              $("#bannerDescConfig").prop("checked",true);
                              $("#bannerDescConfig").parents('span').addClass("checked");
                          }
                      }else{
                          window.alertGlobalErrorCall(result);
                      }

                  }
              });

			  $("#modal-form-edit").show();
		  });
		  //提交新的模板
		  $("#modal-form-submit").on("click", function(){
             var data= getTemplateData();
             if(data==null){
                 return;
             }
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data :{
                      templateName:data.templateName,
                      description:data.description,
                      templateContent:data.templateContent,
                      templateConfigVO:JSON.stringify(data.templateConfigVO)
                  },
				  url :  "/template/missionTemplateCreate",
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
		  //修改模板
		  $("#modal-form-edit").on("click", function(){
              var data= getTemplateData();
              if(data==null){
                  return;
              }
			  var uid=$("input[name=uid]").val();
              data.id=uid;
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data :{
				      id:uid,
                      templateName:data.templateName,
                      description:data.description,
                      templateContent:data.templateContent,
                      templateConfigVO:JSON.stringify(data.templateConfigVO)
                  },
				  url :  "/template/missionTemplateModify",
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
                  url :  "/template/missionTemplateDelete",
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
          $("#disableTemplateId").on("click", function(){
              var formId = $(this).data('uid');
              //禁用一个通道
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data : {
                      id:formId,
					  isValid:false
                  },
                  url :  "/template/missionTemplateValid",
                  success : function(result){
                      if (result.success) {
                          location.reload();
                      }else{
                          alert(result.error);
                      }

                  }
              });

          });

          //===启用
          $("#enableTemplateId").on("click", function(){
              var formId = $(this).data('uid');
              //启用一个通道
              $.ajax({
                  type : "POST",
                  dataType :"json",
                  data : {
                      id:formId,
                      isValid:true
                  },
                  url :  "/template/missionTemplateValid",
                  success : function(result){
                      if (result.success) {
                          location.reload();
                      }else{
                          alert(result.error);
                      }

                  }
              });

          });


          $('#titleConfig').change(function() {
              if (this.checked) {
                $("#titleSize").show();
                $("#titleSize").parents('span').addClass("checked");
              } else {
                  $("#titleSize").hide();
                  $("#titleSize").parents('span').removeClass("checked");
              }
          });
          $('#picConfig').change(function() {
              if (this.checked) {
                  $("#picListSize").show();
                  $("#picListSize").parents('span').addClass("checked");
              } else {
                  $("#picListSize").hide();
                  $("#picListSize").parents('span').removeClass("checked");
              }
          });
        },



    }

});
