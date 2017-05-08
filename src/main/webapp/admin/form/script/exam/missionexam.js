var missionExam={

	//初始化
	init:function(){ 
		//拖拽
		this.dragFn();
		//拖拽排序
		// this.sortFn();
		//题目菜单滚动固定顶部
		this.fixFn();
		//题目菜单折叠/展开
		this.menuFn();
		//标题编辑
		this.titleEditFn();
		//题操作事件初始化
		this.listAllCtrlFn('.ui-questions-content-list','.ui-up-btn','.ui-down-btn','.ui-clone-btn','.ui-del-btn');
		//批量添加事件初始化
		this.topicACtrlFn('.ui-questions-content-list','.ui-add-item-btn','.ui-batch-item-btn','.ui-add-answer-btn');
		//
		this.moveTispFn('.ui-up-btn,.ui-down-btn,.ui-clone-btn,.ui-del-btn');
		this.moveTispFn('.ui-add-item-btn,.ui-batch-item-btn,.ui-add-answer-btn'); 

		//常量初始化
		this.stateEnum();
		this.missionTypeEnum();
        this.missionAwardEnum();

		this.isTopEnum();
		this.isTimelyEnum();
		// this.awardEnum();
		this.renderForm();


	},
	 
	
	initTypeMap:function(){
		var typeMap = {};     // Map map = new HashMap();
		typeMap["TITLE"] = "标题"; // map.put(key, value);
		typeMap["SUBTITLE"] = "子标题";
		typeMap["PIC"] = "图片";
		typeMap["FORM"] = "表单";
		typeMap["TEXT"] = "文本";
		typeMap["TOPPIC"] = "顶部图片";
		return typeMap;
	},
	// 任务状态
	stateEnum:function () {
		$("<option></option>").val("PENDING").text("待审核").appendTo($("#textarea_state"));
		$("<option></option>").val("PASS").text("审核通过").appendTo($("#textarea_state"));
		$("<option></option>").val("PUBLISH").text("发布").appendTo($("#textarea_state"));
		$("<option></option>").val("REFUSE").text("否决").appendTo($("#textarea_state"));
		$("<option></option>").val("OFFLINE").text("下架").appendTo($("#textarea_state"));


	},
	//是否置顶
	isTopEnum:function () {
		$("<option></option>").val("1").text("是").appendTo($("#textarea_mission_isTop"));
		$("<option></option>").val("0").text("否").appendTo($("#textarea_mission_isTop"));
	},
	
	//是否实时
	isTimelyEnum:function () {
		$("<option></option>").val("1").text("是").appendTo($("#textarea_mission_isTimely"));
		$("<option></option>").val("0").text("否").appendTo($("#textarea_mission_isTimely"));		
	},

	awardEnum:function () {
		$("<option></option>").val("COUPON").text("优惠券").appendTo($("#textarea_award"));
		$("<option></option>").val("SCORE").text("积分").appendTo($("#textarea_award"));
		$("<option></option>").val("CASH").text("现金").appendTo($("#textarea_award"));

		$('#textarea_award').change(function(){
			var index=$(this).children('option:selected').index();
			if(index==0){
				$("#textarea_award_coupCode_span").show();
			} else {
				$("#textarea_award_coupCode_span").hide();
			}


		});
	},

	missionTypeEnum:function () {

	/*	$.ajax({
                    type: 'POST',
                    url: '/mission/getAllMissionType',
            		async: false,
                    data: {
                    },
                    success: function (result) {
                        if(result.success == true){

                            jQuery.each(result.data, function(i,item){
                            	if(i==0){
                                    $("#textarea_mission_type_desc").html(item.description);
								}

                                $("<option></option>").attr("desc",item.description).val(item.typeName).text(item.typeNameDesc).appendTo($("#textarea_mission_type"));
								// $("<span style='color: grey;margin-left: 3px'></span>").text(item.description).append($("#textarea_mission_type"))
                            });
                        }else{
                            alert(result.error);
                        }

                    },
                });*/

      /*  $("<option></option>").val("FORM").text("表单任务").appendTo($("#textarea_mission_type"));
		$("<option></option>").val("DISCOVER").text("体验任务").appendTo($("#textarea_mission_type"));
		$("<option></option>").val("NEWER").text("拉新任务").appendTo($("#textarea_mission_type"));
		$("<option></option>").val("WELFARE").text("福利任务").appendTo($("#textarea_mission_type"));
		$("<option></option>").val("VIDEO").text("视频广告任务").appendTo($("#textarea_mission_type"));
		$("<option></option>").val("EXPAND").text("拓展活动互动活动").appendTo($("#textarea_mission_type"));*/




	},
    missionAwardEnum:function () {
        $("<option></option>").val("SCORE").text("积分").appendTo($("#textarea_mission_award"));
        $("<option></option>").val("CASH").text("现金").appendTo($("#textarea_mission_award"));
        $("<option></option>").val("BOTH").text("积分+现金").appendTo($("#textarea_mission_award"));

    },


    renderForm:function(){
		var _this=this;
		var typeMap = _this.initTypeMap();
		//弹出新窗口
		$("#missionFilterButton").click(function(){
			$(".exam-nav").hide();
			//添加iframe
			var if_w = 1200;
			var if_h = 700;
			//allowTransparency='true' 设置背景透明
			$("<iframe width='" + if_w + "' height='" + if_h + "' id='YuFrame2' name='YuFrame2' style='position:absolute;z-index:5;'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>").prependTo('body');
			var st=document.documentElement.scrollTop|| document.body.scrollTop;//滚动条距顶部的距离
			var sl=document.documentElement.scrollLeft|| document.body.scrollLeft;//滚动条距左边的距离
			var sl=document.documentElement.scrollLeft|| document.body.scrollLeft;//滚动条距左边的距离
			var ch=document.documentElement.clientHeight;//屏幕的高度
			var cw=document.documentElement.clientWidth;//屏幕的宽度
			var objH=$("#YuFrame2").height();//浮动对象的高度
			var objW=$("#YuFrame2").width();//浮动对象的宽度
			var objT=Number(st)+(Number(ch)-Number(objH))/2;
			var objL=Number(sl)+(Number(cw)-Number(objW))/2;
			$("#YuFrame2").css('left',objL);
			$("#YuFrame2").css('top',objT);

			$("#YuFrame2").attr("src", "/mission/filter/build");

			//添加背景遮罩
			$("<div id='YuFrame2Bg' style='background-color: Gray;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
			var bgWidth = Math.max($("body").width(),cw);
			var bgHeight = Math.max($("body").height(),ch);
			$("#YuFrame2Bg").css({width:bgWidth,height:bgHeight});
			$("#YuFrame2Bg").click(function() {
				$(".exam-nav").show();
				$("#YuFrame2").remove();
				$("#YuFrame2Bg").remove();
			});
        });
        $("#missionChannelButton").click(function(){
            $(".exam-nav").hide();
            //添加iframe
            var if_w = 1200;
			var if_h = 700;
			//allowTransparency='true' 设置背景透明
			$("<iframe width='" + if_w + "' height='" + if_h + "' id='YuFrame3' name='YuFrame3' style='position:absolute;z-index:5;'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>").prependTo('body');
			var st=document.documentElement.scrollTop|| document.body.scrollTop;//滚动条距顶部的距离
			var sl=document.documentElement.scrollLeft|| document.body.scrollLeft;//滚动条距左边的距离
			var ch=document.documentElement.clientHeight;//屏幕的高度
			var cw=document.documentElement.clientWidth;//屏幕的宽度
			var objH=$("#YuFrame3").height();//浮动对象的高度
			var objW=$("#YuFrame3").width();//浮动对象的宽度
			var objT=Number(st)+(Number(ch)-Number(objH))/2;
			var objL=Number(sl)+(Number(cw)-Number(objW))/2;
			$("#YuFrame3").css('left',objL);
			$("#YuFrame3").css('top',objT);

			$("#YuFrame3").attr("src", "/mission/missionChannel");

			//添加背景遮罩
			$("<div id='YuFrame3Bg' style='background-color: Gray;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
			var bgWidth = Math.max($("body").width(),cw);
			var bgHeight = Math.max($("body").height(),ch);
			$("#YuFrame3Bg").css({width:bgWidth,height:bgHeight});
			$("#YuFrame3Bg").click(function() {
				$(".exam-nav").show();
				$("#YuFrame3").remove();
				$("#YuFrame3Bg").remove();
			});
		});
	/*    var formObj = window.loadFormStructure();
        if(formObj==null){
            $("#fieldMissionUserName").hide();
            $("#fieldMissionUserId").hide();
       	   return;
        }*/

	   //	var fieldArray = formObj;
		// $("#textarea_mission_id").val(fieldArray.id);
		// $("#textarea_user_id").val(fieldArray.userId);
		// $("#textarea_user_name").val(fieldArray.userName);
		// $("#textarea_mission_name").val(fieldArray.missionName);
		// //状态匹配
		// $("#textarea_state").get(0).options[fieldArray.state-1].selected = true;
		// //任务总数
		// $("#textarea_mission_total_snapshot").val(fieldArray.totalSnapshot);
		// $("#textarea_mission_receive_limit").val(fieldArray.receiveLimit);
		// $("#textarea_mission_filter_id").val(fieldArray.missionFilterId);
		// //任务时间
		// $("#textarea_mission_begin_time").val(moment(fieldArray.beginTime).format('YYYY-MM-DD HH:mm'));
		// $("#textarea_mission_end_time").val(moment(fieldArray.endTime).format('YYYY-MM-DD HH:mm'));
       //
		// //任务类型
		// $("#textarea_mission_type").val(fieldArray.missionType);
		// //任务通道
		// $("#textarea_mission_channelIdList").val(fieldArray.channelIds);
       //
		// //H5页面
		// $("#textarea_mission_html_id").val(fieldArray.html);
       //
		// //是否置顶匹配
		// $("#textarea_mission_isTop").val(fieldArray.isTop);
		//
		// //是否实时匹配
		// $("#textarea_mission_isTimely").val(fieldArray.isTimely);
       //
		// //内容初始化话
		// var content = jQuery.parseJSON(fieldArray.content);
		// var cursor=0;
		// $.each(content,function(key,value){
		// 	_this.renderField(key,value,cursor);
		// 	cursor=cursor+1;
		// });
    },


	renderField:function(key,value,cursor){
 		var _this=this;
 		//控件名
		var pluginName =  key;

		//不区分是否含有子类
		var typeMap=_this.initTypeMap();
		var desc=typeMap[key];
		var type;
		if(key=="TEXT"){
			type=4;
		} else {
			type=3;
		}
		data={
			type:type,
			name:key,
			desc:desc,
		    value:value,
		    //随机数模拟
		    itmetid:"A"+(10045+parseInt(100*Math.random()))
		}
		$('.ui-questions-content-list').append(template("drag_choice",data));
		
		var selector = ".ui-module:eq("+cursor+")";
		//渲染选项名
		$(selector).each(function(v){
			var ulItem = $(this).find('.cq-unset-list');
			$(this).find('.ui-drag-area div span').html(desc);
		});
		
		
        _this.orderFn($('.ui-questions-content-list'));
        // _this.sortFn();
	},
	
	renderSelection:function(ulModel,fieldSubNode){
		ulModel.children('li').each(function(i){
			var labelName = fieldSubNode[i].labelName;
			$(this).find('.cq-answer-content').html(labelName); 
		}); 
	},
	 
	
	//点击生成控件
	dragFn:function(){
		var _this=this;
		var typeMap = _this.initTypeMap();
		
		$( "#ui_sortable_exam li" ).on('click',function(e){ 
			var type_value = $(this).children('a').attr('data-checkType');//3是input，4是text
			//控件名

			var name=$(this).attr('data-uid');
			  
			data={
			    type:type_value,//1为单选，2为多选
			    name:name,
				desc:typeMap[name],
				value:"",
			    //随机数模拟
			    itmetid:"A"+(10045+parseInt(100*Math.random()))
			}
			$('.ui-questions-content-list').append(template("drag_choice",data));

			_this.orderFn($('.ui-questions-content-list'));
			// _this.sortFn();
		});
	},
	//拖拽排序
	sortFn:function(){
		var _this=this;
		$('.ui-questions-content-list').sortable({
			handle:'.ui-drag-area',
			items:'>li',
			containment:'#pageContentId',
			opacity:0.7,
			placeholder: 'ui-state-highlight',
			start:function(event) {
		    	missionExam.titleDelFn();
		    },
			stop:function() {
			    _this.orderFn($(this));
			    },
		revert:'invalid'
		});
	},
	//标题序列号
	orderFn:function(obj){
		obj.find('li.items-questions').each(function(i){
			      	 $(this).find('.module-menu h4').html("Con-"+(i+1));
			      });
	},
	//题目菜单滚动固定顶部
	fixFn:function(){
		var _this=this;
		$('#desktop_scroll').scroll(function(){
			_this.titleDelFn();
		var parentLeft=$('.exam-nav').parent().offset().left;
			if($('.exam-nav').offset().top+20+$('.conditionItems').outerHeight()+$('.title').outerHeight()<=$(this).scrollTop()){
				$('.exam-nav').css({'position':'fixed','top':0+'px','left':parentLeft+'px'});
				$('.exam-nav').addClass('scrollCurr');
			}else{
				$('.exam-nav').removeAttr('style');
				$('.exam-nav').removeClass('scrollCurr');
			}
			
		});
	},
	//题目菜单折叠/展开
	menuFn:function(){
		$('.exam-item-title').on('click',function(){
			if($(this).hasClass('curr')){
				$(this).removeClass('curr');
				$(this).find('i').removeClass('icon-collapse').addClass('icon-expand');
				$(this).next('ul.exam-nav-list').stop().slideDown();
			}else{
				$(this).addClass('curr');
				$(this).find('i').removeClass('icon-expand').addClass('icon-collapse');
				$(this).next('ul.exam-nav-list').stop().slideUp();
			}
		});
	},
	//标题编辑
	titleEditFn:function(){
		$(document).on('click','.T_edit',function(event){
			$('.cq-into-edit').remove();
			var data={
				title:''
			}
			if(!$('.cq-into-edit').size()){
				$('body').append(template('drag_T_edit',data));
				$('.cq-into-edit').attr('data-gettid',$(this).attr('data-tid'));
			}
		//	if($(this).hasClass('T_plugins')){
			//	$('.cq-into-edit').append(template('T_edit_plugins',{}));
			//}
			$('.cq-into-edit').css({
				'top':($(this).offset().top-1)+'px',
				'left':($(this).offset().left)+'px',
				'width':$(this).outerWidth()+'px',
			});
			if($(this).hasClass('T-center')){
				$('.cq-into-edit .cq-edit-title').css({
					'text-align':'center'
				});
			}else{
				$('.cq-into-edit .cq-edit-title').css({
					'text-align':'left'
				});
			}
			if($(this).attr('data-font')){
				$('.cq-into-edit .cq-edit-title').css({
					'font-size':$(this).attr('data-font')+'px'
				});
			}else{
				$('.cq-into-edit .cq-edit-title').css({
					'font-size':''
				});
			}
			$('.cq-into-edit .cq-edit-title').css({
				'min-height':$(this).height()+'px',
				'padding-top':($(this).outerHeight()-$(this).height())/2+'px',
				'padding-bottom':($(this).outerHeight()-$(this).height())/2+'px'
			}).html($(this).html()).focus();
			
			$(document).one('click',function(){
					$('.cq-into-edit').remove();
				});
			$(document).on('click','.cq-into-edit',function(e){
			      e.stopPropagation();
			});
			event.stopPropagation();
		});
		$(document).on('blur','.cq-into-edit .cq-edit-title',function(){
					$('.T_edit[data-tid='+$('.cq-into-edit').attr('data-gettid')+']').html($('.cq-into-edit .cq-edit-title').html());
				});
	},
	//关闭标题编辑
	titleDelFn:function(){
		if($('.cq-into-edit').size()){
			$('.T_edit[data-tid='+$('.cq-into-edit').attr('data-gettid')+']').html($('.cq-into-edit .cq-edit-title').html());
			$('.cq-into-edit').hide();
		}
		
	},
	//鼠标移动上显示
	moveTispFn:function(obj){
		$(document).on('mousemove',obj,function(e){
			var strTx=$(this).attr('data-tisp');
			var str=$('<div class="move-tisp-box"></div>');
			str.html(strTx);
			if(!$('.move-tisp-box').size()){
				str.appendTo('body');
			}
			$('.move-tisp-box').css({top:(e.pageY+15)+'px',left:(e.pageX+15)+'px'});
		});
		$(document).on('mouseout',obj,function(e){
			$('.move-tisp-box').remove();
		});
	},
	//控制操作：上移，下移，复制，删除
	listAllCtrlFn:function(parentObj,upObj,downObj,cloneObj,delObj){
		var _this=this;
		//上移
		$(document).on('click',parentObj+' '+upObj,function(e){
			var $parentItems=$(this).closest('li.ui-module');
			if($parentItems.prev('li.ui-module').size()){
				$parentItems.insertBefore($parentItems.prev('li.ui-module'));
				_this.orderFn($(parentObj));
				_this.titleDelFn();
			}else{
				layer.msg('已经是第一个了！');
			}
		});
		//下移
		$(document).on('click',parentObj+' '+downObj,function(e){
			var $parentItems=$(this).closest('li.ui-module');
			if($parentItems.next('li.ui-module').size()){
				$parentItems.insertAfter($parentItems.next('li.ui-module'));
				_this.orderFn($(parentObj));
				_this.titleDelFn();
			}else{
				layer.msg('已经是最后一个了！');
			}
		});
		//复制/克隆
		$(document).on('click',parentObj+' '+cloneObj,function(e){
		//	var $parentItems=$(this).closest('li.ui-module');
		//	$parentItems.clone(true).insertAfter($parentItems);
		//	_this.orderFn($(parentObj));
		//	_this.titleDelFn();
		});
		//删除
		$(document).on('click',parentObj+' '+delObj,function(e){
			var $parentItems=$(this).closest('li.ui-module');
			$parentItems.remove();
			layer.msg('已删除！');
			$('.move-tisp-box').remove();
			_this.orderFn($(parentObj));
			_this.titleDelFn();
		});
	},
	//单题添加，批量添加
	topicACtrlFn:function(parentObj,addObj,batchAddObj,addAnswerObj){
		//添加选项栏
		var $tid=100135;
		$(document).on('click',parentObj+' '+addObj,function(e){
			var $parentItems=$(this).closest('li.ui-module').find('.cq-unset-list');
			var $name=$.trim($parentItems.attr('data-nameStr'));
			$tid++;
			var indexValue = $parentItems.children('li:last').index()+1;
			var data={
				 type:parseInt($parentItems.attr('data-checktype')),
				 name:$name,
				 index:indexValue,
				 items:[{value:indexValue,tid:$tid}]
			}
			$parentItems.append(template('ui_additem_content',data));
			
		});
        $("#textarea_mission_award").change(function (){
        	console.log($(this).val())
            if($(this).val()=='BOTH'){
                $("#span_mission_award_cash").empty();
                $("#span_mission_award_score").empty();
                $("#span_mission_award_score").append("<input type='text' id='textarea_mission_award_score' class='input-top'style='border:1px solid;border-color:rgba(82,168,236,.8);width: 200px;height: 30px;margin-left: 5px'/> 分 <span class='score_desc'>（请输入0-100,000之间的整数）</span>");

                $("#span_mission_award_cash").append("<input type='text' id='textarea_mission_award_cash' class='input-top'style='border:1px solid;border-color:rgba(82,168,236,.8);width: 200px;height: 30px;margin-left: 5px'/> 元  <span class='cash_desc'>（请输入不小于1的整数）</span>");
			}else if($(this).val()=='CASH'){
                $("#span_mission_award_score").empty();
                $("#span_mission_award_cash").empty();
                $("#span_mission_award_cash").append("<input type='text' id='textarea_mission_award_cash' class='input-top'style='border:1px solid;border-color:rgba(82,168,236,.8);width: 200px;height: 30px;margin-left: 5px'/> 元  <span class='cash_desc'>（请输入不小于1的整数）</span>");
            }else if($(this).val()=='SCORE'){
                $("#span_mission_award_cash").empty();
                $("#span_mission_award_score").empty();
                $("#span_mission_award_score").append("<input type='text' id='textarea_mission_award_score' class='input-top'style='border:1px solid;border-color:rgba(82,168,236,.8);width: 200px;height: 30px;margin-left: 5px'/> 分 <span class='score_desc'>（请输入0-100,000之间的整数）</span>");
            }
        });


    }
}
