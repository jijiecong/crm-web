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
		  
		  $("#closeFormPage").on("click", function(){ 
				 window.parent.closeIframe(); //执行关闭自身操作
          });



        }
    }

});
