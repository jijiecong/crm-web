define(function (require, exports, module) {
	require('../ajaxSelect2.js');
    var Basic = require('../basic.js');
    Basic.prototype.init = function () {
        var pThis = this;
        $("#modal-form-submit").on("click", function () {
            pThis.submit($(this).data('uid'));
        });
    };

    var privilegeOut = new Basic('acl/privilegeOut');
    return {
        init: function () {
        	var select2 = new ajaxSelect2('#select-owner', "/acl/search/user", {
                multiple: true,
                textName: 'nickname',
                initUrl: "/acl/search/user/init"
            });
        	
        	$(".add").on("click", function () {
                $(".operation-name").html('添加');
                $("#modal-form-submit").data('uid', '');
                privilegeOut.clearData();
                select2.init($("#selfUserId").val());
            });
        	
        }
    }
});
