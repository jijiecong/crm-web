define(function (require, exports, module) {
    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    Basic.prototype.init = function () {
        var pThis = this;
        //打开弹出层进行设置
        $(".add").on("click", function () {
            $(".operation-name").html('添加');
            $("#form input").val('');
            $("#modal-form-submit").data('uid', '');
        });
        //打开弹出层修改页面
        $(".edit").on("click", function () {
            var uid = $(this).data('uid');
            pThis.getById(uid);
            $("#modal-form-submit").data('uid', uid);
            $(".operation-name").html('编辑');
        });

        $("#modal-form-submit").on("click", function () {
            pThis.submit($(this).data('uid'));
        });

        $(".del").on("click", function () {
            var uid = $(this).data('uid');
            $("#affirm-del").data('uid', uid);
        });

        $("#affirm-del").on("click", function () {
            var formId = $(this).data('uid');
            pThis.del(formId);
        });
    };
    var hierarchy = new Basic('acl/hierarchy');

    return {
        init: function () {
            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/business/findByName', {
                multiple: false
            });

            businessSelect2.init($("#select-business").val());
        }
    }
});
