define(function (require, exports, module) {
    var Basic = require('../basic.js');
    var privilege = new Basic('acl/privilege');
    return {
        init: function () {
            var type = "添加失败";
            var uid = $("#form-submit").data('uid');
            if (uid != "" && uid != undefined) {
                type = "修改失败";
                privilege.getById(uid);
            }

            $("#close-form").on("click", function () {
                window.location.href = '/acl/privilege/index';
            });

            $("#form-submit").on("click", function () {
            	if($('#riskLevel option:selected').val() == undefined || $('#riskLevel option:selected').val() == ""){
            		alert("请选择风险等级");
            		return;
            	}
                privilege.submit2($(this).data('uid'), function (data) {
                    if (data.success) {
                        window.location.href = '/acl/privilege/index';
                    } else {
                        alert(type);
                    }
                });
            });
        }
    }
});
