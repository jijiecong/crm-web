define(function (require, exports, module) {
    require('../user/ajaxSelect2.js');
    var Basic = require('./basic.js');
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
        $("#batch-del-submit").on("click", function () {
            pThis.batchDel();
        });
    };

    var monitor = new Basic('notify/notify');
    return {
        init: function () {

        }
    }

});
