define(function (require, exports, module) {
    var Basic = require('../basic.js');
    var business = new Basic('acl/business');
    return {
        init: function () {

            var type = "添加失败";
            var uid = $("#form-submit").data('uid');
            if (uid != "" && uid != undefined) {
                type = "修改失败";
                business.getById(uid);
            }

            $("#close-form").on("click", function () {
                window.location.href = '/acl/business/index';
            });

            $("#form-submit").on("click", function () {
                business.submit2($(this).data('uid'), function (data) {
                    if (data.success) {
                        window.location.href = '/acl/business/index';
                    } else {
                        alert(type);
                    }
                });
            });
        }
    }
});
