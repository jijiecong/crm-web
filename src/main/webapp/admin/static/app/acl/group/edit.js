define(function (require, exports, module) {
	require('../ajaxSelect2');
    var Basic = require('../basic.js');
    var group = new Basic('acl/group');
    return {
        init: function () {
        	var parent = new ajaxSelect2('#test', "/acl/search/group", {});
            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/search/business/findByName', {
                multiple: false
            });

            businessSelect2.init($("#select-business").val());
            var type = "添加失败";
            var uid = $("#form-submit").data('uid');
            if (uid != "" && uid != undefined) {
                type = "修改失败";
                group.getById(uid, function () {
                    parent.init();
                });
            }else{
            	parent.init();
            }

            $("#close-form").on("click", function () {
                window.location.href = '/acl/group/index';
            });

            $("#form-submit").on("click", function () {
                group.submit2($(this).data('uid'), function (data) {
                    if (data.success) {
                        window.location.href = '/acl/group/index';
                    } else {
                        alert(type);
                    }
                });
            });
        }
    }
});
