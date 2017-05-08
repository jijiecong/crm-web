define(function (require, exports, module) {

    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    var userRole = new Basic('acl/userRole');

    return {
        init: function () {
        	var user = new ajaxSelect2('#user-select2', '/acl/search/user', {
                multiple: true,
                textName: 'nickname',
                queryUrl: '/acl/search/user/notUsed'

            });
            var role = new ajaxSelect2('#role-select2', '/acl/userRole/searchRole', {
                multiple: true
            });   
            
            var type="添加失败";
            var uid = $("#form-submit").data('uid');
            if(uid!=""&&uid!=undefined){
            	type="修改失败";
                user.init(uid);
                role.init(uid);
                user.disabled(false);
            }else{
            	user.clear();
                role.clear();
                user.init();
                role.init();
                user.disabled(true);
            }    
            
            $("#close-form").on("click", function () {
            	window.location.href = '/acl/userRole/index';
            });
            
            $("#form-submit").on("click", function () {
            	var uid = $(this).data('uid');
                var userIds = user.getVal();
                var roleIds = role.getVal();
                userRole.ajax('/acl/userRole/authorize', {userIds: userIds, roleIds: roleIds},function(data){
		        	if(data.success){
		        		window.location.href = '/acl/userRole/index';
		        	}else{
		        		alert(type);
		        	}
                });
            });
        }
    }
});
