define(function (require, exports, module) {
    var Basic = require('../basic.js');
    var biz = new Basic('acl/biz');
    return {
        init: function () {
            var select2 = new ajaxSelect2('#select-owner', "/acl/search/user", {
                multiple: true,
                textName: 'nickname',
                initUrl: "/acl/search/user/initBizOwner"
            });

            var type = "添加失败";
            var uid = $("#form-submit").data('uid');
            if (uid != "" && uid != undefined) {
                type = "修改失败";
                biz.getById(uid);
                select2.init(uid);
            }else{
            	select2.init(uid);
            }

            $("#close-form").on("click", function () {
                window.location.href = '/acl/biz/index';
            });

            $("#form-submit").on("click", function () {
                biz.submit2($(this).data('uid'), function (data) {
                    if (data.success) {
                        window.location.href = '/acl/biz/index';
                    } else {
                        alert(type);
                    }
                });
            });
        }
    }
});
