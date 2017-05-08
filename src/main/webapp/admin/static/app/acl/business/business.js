define(function (require, exports, module) {
    var Basic = require('../basic.js');
    Basic.prototype.init = function () {
        var pThis = this;

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
    var business = new Basic('acl/business');

    return {
        init: function () {

        }
    }
});
