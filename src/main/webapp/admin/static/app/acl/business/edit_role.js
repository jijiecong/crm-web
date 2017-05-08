define(function (require, exports, module) {
    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    var userRole = new Basic('acl/business');

    return {
        init: function () {
            var select2 = new ajaxSelect2('#select-business', "/acl/business/searchRole", {
                multiple: false,
                textName: 'name',
                initUrl: "/acl/business/searchRole"
            });
            var role = new ajaxSelect2('#role-select2', '/acl/business/searchRole', {

                multiple: true
            });   
            
            var type="添加失败";
            var uid = $("#form-submit").data('uid');
            if(uid!=""&&uid!=undefined){
            	type="修改失败";
                role.init(uid);
            }else{
                select2.clear();
                role.clear();
                role.init();
                select2.init();
            }
            
            $("#close-form").on("click", function () {
            	window.location.href = '/acl/business/index';
            });
            
            $("#form-submit").on("click", function () {
            	var uid = $(this).data('uid');
            	var businessId = $(this).data('bid');
                var roleIds = role.getVal();
                userRole.ajax('/acl/business/addPrivilege', {businessId:businessId,roleIds: roleIds},function(data){
		        	if(data.success){
		        		window.location.href = '/acl/business/index';
		        	}else{
		        		alert(type);
		        	}
                });
            });
        }
    }
});
