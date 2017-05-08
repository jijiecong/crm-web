define(function (require, exports, module) {

    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    var process = new Basic('acl/process');

    return {
        init: function () {
        	var hierarchy = new ajaxSelect2('#hierarchyId', "/acl/search/hierarchy", {
                textName:'hierarchyName'
            }); 
            
            var type="添加失败";
            var uid = $("#form-submit").data('uid');
            if(uid!=""&&uid!=undefined){
            	type="修改失败";
            	process.getById(uid, function () {
                    hierarchy.init();
                });
            }else{
            	hierarchy.init();
            }    
            
            $("#close-form").on("click", function () {
            	window.location.href = '/acl/process/index';
            });
            
            $("#form-submit").on("click", function () {
            	process.submit2($(this).data('uid'),function(data){
		        	if(data.success){
		        		window.location.href = '/acl/process/index';
		        	}else{
		        		alert(type);
		        	}
		        });
            });
        }
    }
});
