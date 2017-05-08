/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports, module) { 
    var $ = require('jquery');     
    require('js/component/plugin.ajaxform')($);
    require('collection');

    
	var typeValueMap = {};     // Map map = new HashMap();
	typeValueMap[1] = "TITLE"; // map.put(key, value);
	typeValueMap[2] = "SUBTITLE";
	typeValueMap[3] = "PIC";
	typeValueMap[4] = "FORM";
	typeValueMap[5] = "TEXT";
	typeValueMap[6] = "TOPPIC";
    
  
	//提取表单数据
	var submitFormFields = function(){
		//获取直接子元素
		var fieldListObj = $("#fieldRegion").children("li");
		
		var fieldList = new Array();  
		fieldListObj.each(function(i){ 
			 var fieldNode = parseFieldNode($(this));
			 fieldList.push(fieldNode);
	    });
		
		var basicId =  $("#saveForm").val();
		
		//ajax提交表单数据  
		$.ajax({
    		type : "POST",
    		dataType :"json",
    		data : {
    			id: basicId,
    			fieldList: JSON.stringify(fieldList)
    		},
    		url :  "/form/formSubmit",
    		success : function(result){ 
    			   if (result.success) { 
    				   window.parent.closeIframe(); //执行关闭自身操作
    			   }else{ 
                	   alert(result.error);     
                   }
    			
    		}
    	}); 
		
	};
	
	
    	
    	
    	
	var parseFieldNode = function(fieldLiNodeObj){
		 var liNodeMap = {};   
		 var sort = fieldLiNodeObj.find('.module-menu h4').html();
		 var title = fieldLiNodeObj.find('.ui-drag-area div span').html();
		 var subNodeContainer = fieldLiNodeObj.find('.cq-items-content ul');
		 var typeNumber = subNodeContainer.attr("data-checktype");
		 var optionName = subNodeContainer.attr("data-namestr");
		 liNodeMap["title"]=title;
		 liNodeMap["sort"]=sort;
		 liNodeMap["type"]=typeNumber; 
		 var subNodeList = new Array();
		 if(typeNumber!=1&&typeNumber!=2){
			 var optionMap = {};
			 optionMap["sort"]="0";
			 optionMap["labelName"]=title;
			 optionMap["logicName"]=optionName;
 			 subNodeList.push(optionMap);
 			 liNodeMap["subNode"]=subNodeList;
			 return liNodeMap;
		 }
		 
		 
		 subNodeContainer.children("li").each(function(index){ 
			 var subOptionValue =   $(this).find("label input").attr("value");
			 var subLabelName = $(this).find("div").html();
			 var optionMap = {};
			 optionMap["sort"]=index;
			 optionMap["labelName"]=subLabelName;
			 optionMap["logicName"]=optionName;
			 optionMap["value"]=subOptionValue;
 			 subNodeList.push(optionMap); 
			 index++;
		 }); 
		 liNodeMap["subNode"]=subNodeList;
		 return liNodeMap;
	}
	
	
 
	  
 
	 
    	 
    return {
    	
	  init: function() { 
		  

		  //点击保存表单按钮----任务保存
		  $("#saveForm").on("click", function(){
			  var url;
			  var missionId=$("#textarea_mission_id").val();
			  if(missionId==null || missionId==""){
				  url="/mission/missionDefineCreate";
			  } else {
				  url="/mission/missionDefineModify";
			  }
			  var userId=$("#textarea_user_id").val();
			  var userName=$("#textarea_user_name").val();
			  var missionName=$("#textarea_mission_name").val();
			  var state=$("#textarea_state").find("option:selected").index()+1;
			  var totalSnapshot= $("#textarea_mission_total_snapshot").val();
			  var receiveLimit=$("#textarea_mission_receive_limit").val();
			  var missionFilterId=$("#textarea_mission_filter_id").val();

			  // var awardType=$("#textarea_award").find("option:selected").index()+1;
			  // var amount=$("#textarea_award_count").val();
			  // var couponCode=(awardType==1?$("#textarea_award_coupCode").val():null);
			  // var isAuto=$("#textarea_is_auto").prop("checked")==true?1:0;
			  var beginTime=$("#textarea_mission_begin_time").val();
			  var endTime=$("#textarea_mission_end_time").val();
			  var channelIds=$("#textarea_mission_channelIdList").val();
			  var html=$("#textarea_mission_html_id").val();
			  var missionType=$("#textarea_mission_type").find("option:selected").val();
			  var isTop = $("#textarea_mission_isTop").find("option:selected").val();
			  var isTimely = $("#textarea_mission_isTimely").find("option:selected").val();
			  
			  //ActionDesc
			  // var actionVO={awardType:awardType,amount:amount,couponCode:couponCode};
			  // var actionDesc=JSON.stringify(actionVO);
			  //任务内容

			  var content="{";
			  var hasContent=false;
			  $('.ui-questions-content-list .items-questions').each(function () {
				  var temp=$(this).find(".cq-items-content .cq-unset-list");
				  var type= temp.attr("data-checkType");
				  var name=temp.attr("data-nameStr");
				  var value=temp.find(".contentAdd").val();
				  content=content.concat("\"",name,"\":","\"",value,"\"",",");
				  hasContent=true;
			  })
			  content= content.substring(0,content.length-1);
			  content=content+"}";
			  if(hasContent==false){
			  	content="{}";
			  }
			  //发送修改请求
			  $.ajax({
				  type : "POST",
				  dataType :"json",
				  data : {
					  id: missionId,
					  userId:userId,
					  userName:userName,
					  missionName:missionName,
					  state:state,
					  totalSnapshot:totalSnapshot,
					  receiveLimit:receiveLimit,
					  beginTime:beginTime,
					  endTime:endTime,
					  channelIds:channelIds,
					  missionType:missionType,
					  content:content,
					  missionFilterId:missionFilterId,
					  html:html,
					  isTop:isTop,
					  isTimely:isTimely
					  
				  },
				  url :  url,
				  success : function(result){
					  if (result.success) {
						  alert("操作成功");
					  }else{
						  alert(result.error);
					  }

				  }
			  });
		  });
		  
		  $("#closeFormPage").on("click", function(){
			     window.parent.location.reload();
				 window.parent.closeIframe(); //执行关闭自身操作
          });



        }
    }

});
