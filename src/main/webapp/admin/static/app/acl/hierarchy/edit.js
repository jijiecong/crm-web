define(function (require, exports, module) {
    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    var hierarchy = new Basic('acl/hierarchy');
    return {
        init: function () {
            var type = "添加失败";
            var uid = $("#form-submit").data('uid');
            if (uid != "" && uid != undefined) {
                type = "修改失败";
                hierarchy.getById(uid);
            }

            $("#close-form").on("click", function () {
                window.location.href = '/acl/hierarchy/index';
            });

            $("#form-submit").on("click", function () {
                hierarchy.submit2($(this).data('uid'), function (data) {
                    if (data.success) {
                        window.location.href = '/acl/hierarchy/index';
                    } else {
                        alert(type);
                    }
                });
            });
        }
    }
});
