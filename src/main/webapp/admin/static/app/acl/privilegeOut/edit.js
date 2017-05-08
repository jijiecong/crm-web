define(function (require, exports, module) {
    require('../ajaxSelect2.js');
    var Basic = require('../basic.js');
    var privilegeOut = new Basic('acl/privilegeOut');
    Basic.prototype.init = function () {
        var pThis = this;
        $("#form-submit").on("click", function () {
            pThis.submit($(this).data('uid'));
        });
    };

    return {
        init: function () {
            var select2 = new ajaxSelect2('#select-owner', "/acl/search/user", {
                multiple: true,
                textName: 'nickname',
                initUrl: "/acl/search/user/init"
            });

        	var type="添加失败";
            select2.init($("#selfUserId").val());
            // var uid = $("#form-submit").data('uid');

            $("#close-form").on("click", function () {
            	window.location.href = '/acl/privilegeOut/index';
            });

		    $("#form-submit").on("click", function () {
                privilegeOut.submit2($(this).data('uid'),function(data){
		        	if(data.success){
		        		window.location.href = '/acl/privilegeOut/index';
		        	}else{
		        		alert(type);
		        	}
		        });
		    });
        }
    }

});
