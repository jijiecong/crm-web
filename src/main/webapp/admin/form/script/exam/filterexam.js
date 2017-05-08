var filterExam={

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
		// this.awardEnum();
		this.renderForm();
	},

	tableMapEnum:function () {

		var tableMap={};
		tableMap["SPONSOR"]="sponsor-rule";
		tableMap["PARTICIPATOR"]="participator-rule";
		tableMap["SPONSORAWARD"]="sponsor-award";
		tableMap["PARTICIPATORAWARD"]="participator-award";
		tableMap["SHAREAWARD"]="share-award";
		return tableMap;
	},
	awardMapEnum:function () {
		var awardMap={};
		awardMap[1]="COUPON";
		awardMap[2]="SCORE";
		awardMap[3]="CASH";
		return awardMap;
	},

	renderForm:function(){
		var _this=this;
	    var formObj = window.parent.loadFilterDetailStructure();
        if(formObj==null){
           $("#textarea_filter_id").val(-1);
       	   return;
        }

	   	var fieldArray = formObj;
		var filterId=fieldArray.filterId;
		var ruleContent=fieldArray.content;
		var ruleName=fieldArray.ruleName;
		//渲染
		$("#textarea_filter_id").val(filterId);
		$("#textarea_filter_name").val(ruleName);
		var content = jQuery.parseJSON(fieldArray.content);
		var tableMap=_this.tableMapEnum();
		var awardMap=_this.awardMapEnum();
			$.each(content,function(key,value){
				_this.renderField(key,value,tableMap,awardMap);
			});
	},

	S4:function() {
	return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
	},
	guid:function() {
		var _this=this;
		return (_this.S4()+_this.S4()+"-"+_this.S4()+"-"+_this.S4()+"-"+_this.S4()+"-"+_this.S4()+_this.S4()+_this.S4());
	},

	//渲染具体的选项
	renderField:function(key,value,tableMap,awardMap) {
			var _this=this;
			var ruleTypeEnum=value.ruleTypeEnum;
		    var tableId=tableMap[ruleTypeEnum];
			var ruleList=value.filterRuleDetailList;
			var actionList=value.actionVOList;
			$.each(ruleList,function (index,rule) {

				var ruleKey=rule.ruleKey;
				var ruleValue=rule.ruleValue;
				var operationEnum=rule.operationEnum;
				var uuid=_this.guid();
				var data={
					paramKey:ruleKey,
					paramValue:ruleValue,
					uuid:uuid
				}
				$("#"+tableId ).find("#ruleRegion").append(template("rule_choice",data))
				$("."+uuid).find("option[value="+operationEnum+"]").attr("selected",true);
			})
            $.each(actionList,function (index,action) {

                var awardType=action.awardType;
                var amount=action.amount;
                var couponCode=action.couponCode;
                var data={
                    awardType:awardType,
                    awardName:awardMap[awardType],
                    amount:amount,
                    couponCode:couponCode
                }
                $("#"+tableId ).find("#awardRegionFix").append(template("award_choice",data))
            })
                //是否自动发放
            $("#"+tableId ).find("#awardRegionFix").find("#textarea_filter_is_auto").prop('checked',value.isAuto==null?false:value.isAuto);
                //表单编号
             $("#"+tableId ).find("#awardRegionFix").find("#textarea_filter_form_id").val(value.formId);
	},

	//点击生成控件
	dragFn:function(){
		var _this=this;

		$( "#ui_sortable_exam li" ).on('click',function(e){
			var uid = $(this).children('a').attr('data-uid');
			var paramType = $(this).children('a').attr('data-paramType');
			var paramKey = $(this).children('a').attr('data-paramKey');
			var paramValueType = $(this).children('a').attr('data-paramValueType');
			var desc = $(this).children('a').attr('data-desc');
			data={
				uid:uid,
				paramType:paramType,
				paramKey:paramKey,
				paramValueType:paramValueType,
				desc:desc
			};

			$('.active #ruleRegion').append(template("rule_choice",data));

			$('.selectpicker').selectpicker("refresh")

		});


		$( "#ui_sortable_award li" ).on('click',function(e){
			var awardType = $(this).children('a').attr('data-checkType');
			var awardName=$(this).attr("data-uid");
			data={
				awardType:awardType,
				awardName:awardName
			};

			$('.active #awardRegionFix').append(template("award_choice",data));



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


	}
}
