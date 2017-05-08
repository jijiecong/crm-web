define(function (require, exports, module) {
    var Basic = require('../basic.js');
    require('../ajaxSelect2');
    var user = new Basic('acl/user');

    return {
        init: function () {
            var select2 = new ajaxSelect2('#select-business', "/acl/business/findByName", {
                multiple: false,
                textName: 'name',
                initUrl: "/acl/business/findByName"
            });
            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/business/findByName', {
                multiple: false
            });

            var type="添加失败";
            var uid = $("#form-submit").data('uid');
            if(uid!=""&&uid!=undefined){
                type="修改失败";
                user.getById(uid);
                // select2.init(uid);
                businessSelect2.init(uid);
            }else{
                select2.clear();
                businessSelect2.clear();
                select2.init();
                businessSelect2.init();
            }
            // var type="添加失败";
            // var uid = $("#form-submit").data('uid');
            // if(uid!=""&&uid!=undefined){
            // 	type="修改失败";
            //     user.getById(uid);
            // }

            $("#close-form").on("click", function () {
            	window.location.href = '/acl/user/index';
            });

		    $("#form-submit").on("click", function () {
		    	user.submit2($(this).data('uid'),function(data){
		        	if(data.success){
		        		window.location.href = '/acl/user/index';
		        	}else{
		        		alert(type);
		        	}
		        });
		    });
        }
    }
});
