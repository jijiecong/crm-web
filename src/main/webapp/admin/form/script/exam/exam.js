var exam={

    //初始化
    init:function(){
        //拖拽
        this.dragFn();
        //拖拽排序
        this.sortFn();
        //题目菜单滚动固定顶部
        this.fixFn();
        //题目菜单折叠/展开
        this.menuFn();
        //标题编辑
        this.titleEditFn();
        //题操作事件初始化
        this.listAllCtrlFn('.ui-questions-content-list','.ui-up-btn','.ui-down-btn','.ui-clone-btn','.ui-del-btn','.num-up','.num-down');
        //批量添加事件初始化
        this.topicACtrlFn('.ui-questions-content-list','.ui-add-item-btn','.ui-batch-item-btn','.ui-add-answer-btn');
        //
        this.moveTispFn('.ui-up-btn,.ui-down-btn,.ui-clone-btn,.ui-del-btn');
        this.moveTispFn('.ui-add-item-btn,.ui-batch-item-btn,.ui-add-answer-btn');
        this.renderForm();

        //数字加减
        this.numCtrlFn('.num-up','.num-down');
    },




    initTypeMap:function(){
        var typeMap = {};     // Map map = new HashMap();
        typeMap[1] = "option"; // map.put(key, value);
        typeMap[2] = "checkbox";
        typeMap[3] = "text";
        typeMap[4] = "textarea";
        typeMap[5] = "bannerFixedPlaceHolder";
        typeMap[6] = "datePlaceHolder";
        typeMap[7] = "addressPlaceHolder";
        return typeMap;
    },


    renderForm:function(){
        var _this=this;
        var typeMap = _this.initTypeMap();
        var formObj = window.loadFormStructure();
        if(formObj==null){
            return;
        }
        if(formObj.data=="init"){
            return;
        }

        var fieldArray = formObj.data;

        //载入正则列表
        var regex=null;
        $.ajax({
            type : "POST",
            dataType :"json",
            async: false,
            url :  "/regex/list",
            success : function(result){
                if (result.success) {
                    regex=result.data.data;
                }else{
                    alert(result.error);
                }

            }
        });
        for(var i=0;i<fieldArray.length;i++){
            _this.renderField(fieldArray[i],i,regex);
        }
    },


    renderField:function(fieldModel,cursor,regex){
        var _this=this;
        var typeMap = _this.initTypeMap();
        //控件名
        var pluginName =  typeMap[fieldModel.type];

        var optionData = fieldModel.subNode;
        //不区分是否含有子类
        var logicName = fieldModel.subNode[0].logicName;

        var itemArray = [];
        //如果含有子类，需要进行子类的渲染
        if(fieldModel.type==1||fieldModel.type==2){
            for(var i = 0 ;i<fieldModel.subNode.length;i++){
                itemArray[i] = {value:i,tid:100132+parseInt(100*Math.random())};
            }
        }

        data={
            type:fieldModel.type,//1为单选，2为多选
            name:logicName,
            //随机数模拟
            itmetid:"A"+(10045+parseInt(100*Math.random())),
            items:itemArray
        }
        $('.ui-questions-content-list').append(template("drag_choice",data));

        var selector = ".ui-module:eq("+cursor+")";
        //渲染选项名
        $(selector).each(function(v){
            var ulItem = $(this).find('.cq-unset-list');
            $(this).find('.ui-drag-area div span').html(fieldModel.title);
            if(ulItem.attr('data-checkType')=="1"||ulItem.attr('data-checkType')=="2"){
                _this.renderSelection(ulItem,fieldModel.subNode);
            }


            var validator = fieldModel.validator;

            if (typeof(validator)!='undefined') {
                //填入验证器
                var validatorBox = $(this).find('.validatorBox');
                validatorBox.attr('data-id',validator.id);
                var basicValidator = validatorBox.find('.basicValidator');
                basicValidator.find('.minLength').val(validator.minLength);
                basicValidator.find('.maxLength').val(validator.maxLength);
                if (validator.isRequired=="1") {
                    basicValidator.find('.isRequired').attr("checked",true);
                }

                var regexBox=validatorBox.find('.regexBox');
                $.each(regexBox.find(".regex option"),function (i,item){
                    if (validator.regexId==$(item).attr("data-id")) {
                        $(item).attr("selected",true);
                    }
                });
                var customerRegex=regexBox.find('.customerRegex');
                if ((validator.regexId==null||validator.regexId==0)&&typeof(validator.regexName)!='undefined') {
                    regexBox.find('.preRegex').toggle();
                    customerRegex.toggle();
                    regexBox.find('.isCustomerRegex').attr("checked",true);
                    var arr=validator.regexName.split("_");
                    if (arr.length>0) {
                        customerRegex.find(".regex-name").val(arr[0]);
                        arr.splice(0,1);
                        $.each(arr,function (i,item) {
                            customerRegex.find('.'+item).attr("checked",true);
                        });
                    }
                }
            }
        });

        _this.orderFn($('.ui-questions-content-list'));
        _this.sortFn();
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
            var type_value = $(this).children('a').attr('data-checkType');//1为单选，2为多选,3为单行文本，4为多行文本，5为固态文本，6为日期控件占位符，7为地址控件占位符
            //控件名
            var pluginName =  typeMap[type_value];

            data={
                type:type_value,//1为单选，2为多选
                name:pluginName+"_"+parseInt(100*Math.random()),
                //随机数模拟
                itmetid:"A"+(10045+parseInt(100*Math.random())),
                items:[{
                    value:'0',
                    //随机数模拟
                    tid:100132+parseInt(100*Math.random())
                },{
                    value:'1',
                    //随机数模拟
                    tid:100152+parseInt(100*Math.random())
                }]
            }
            $('.ui-questions-content-list').append(template($(this).attr('data-tempId'),data));
            _this.orderFn($('.ui-questions-content-list'));
            _this.sortFn();

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
                exam.titleDelFn();
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
            $(this).find('.module-menu h4').html("Q"+(i+1));
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
                type:parseInt($parentItems.attr('data-checkType')),
                name:$name,
                index:indexValue,
                items:[{value:indexValue,tid:$tid}]
            }
            $parentItems.append(template('ui_additem_content',data));

        });


    },
    numCtrlFn:function(numUp,numDown){
        var _this=this;
        //加
        $(document).on('click',numUp,function(e){
            var numInput = $(this).prev();
            if (numInput.val()+0<=10000) {
                numInput.val(numInput.val()-0+1)
            }
        });
        //减
        $(document).on('click',numDown,function(e){
            var numInput = $(this).prev().prev();
            if (numInput.val()+0>0) {
                numInput.val(numInput.val()-1)
            }
        });
    }
}
