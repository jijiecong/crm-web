/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports, module) { 
    var $ = require('jquery');     
    require('js/component/plugin.ajaxform')($);
    require('collection');

    var showIframe= function(url,w,h){
        //添加iframe
        var if_w = w;
        var if_h = h;
        //allowTransparency='true' 设置背景透明
        $("<iframe width='" + if_w + "' height='" + if_h + "' id='YuFrame1' name='YuFrame1' style='position:absolute;z-index:4;'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='true'></iframe>").prependTo('body');
        var st=document.documentElement.scrollTop|| document.body.scrollTop;//滚动条距顶部的距离
        var sl=document.documentElement.scrollLeft|| document.body.scrollLeft;//滚动条距左边的距离
        var ch=document.documentElement.clientHeight;//屏幕的高度
        var cw=document.documentElement.clientWidth;//屏幕的宽度
        var objH=$("#YuFrame1").height();//浮动对象的高度
        var objW=$("#YuFrame1").width();//浮动对象的宽度
        var objT=Number(st)+(Number(ch)-Number(objH))/2;
        var objL=Number(sl)+(Number(cw)-Number(objW))/2;
        $("#YuFrame1").css('left',objL);
        $("#YuFrame1").css('top',objT);

        $("#YuFrame1").attr("src", url);

        //添加背景遮罩
        $("<div id='YuFrame1Bg' style='background-color: Gray;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
        var bgWidth = Math.max($("body").width(),cw);
        var bgHeight = Math.max($("body").height(),ch);
        $("#YuFrame1Bg").css({width:bgWidth,height:bgHeight});
        $("#YuFrame1Bg").click(function() {
            $("#YuFrame1").remove();
            $("#YuFrame1Bg").remove();
        });
    }
    	 
    return {
    	
	  init: function() {

		  //点击保存表单按钮----任务保存
		  $("#saveForm").on("click", function(){
			  window.parent.closeIframe(); //执行关闭自身操作
		  });
		  
		  $("#closeFormPage").on("click", function(){ 
				 window.parent.closeIframe(); //执行关闭自身操作
          });

          $(".setForm").on("click", function(){
              $("#selectedBasicRegion").val($(this).attr("data-uid"));
              showIframe('/mission/instance/build',1300,800);
          });



      }
    }

});
