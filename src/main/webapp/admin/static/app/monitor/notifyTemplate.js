define(function (require, exports, module) {
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

    var monitor = new Basic('monitor/notifyTemplate');
    return {
        init: function () {
            $(".add").on("click", function () {
                $(".operation-name").html('添加');
                $("#modal-form-submit").data('uid', '');
                monitor.clearData();
            });

            $(".edit").on("click", function () {
                var uid = $(this).data('uid');
                $(".operation-name").html('编辑');
                $("#modal-form-submit").data('uid', uid);
                monitor.getById(uid, function (data) {
                    //var paramType = data.paramType.substring(1, data.paramType.length - 1).split("|");
                    //var paramValue = data.paramValue.substring(1, data.paramValue.length - 1).split("|");
                    //var paramtype = $(".paramtype");
                    //$.each(paramtype, function (key, val) {
                    //    var type = $(val);
                    //    var value = $(val).next();
                    //    type.val(paramType[key]);
                    //    value.val(paramValue[key]);
                    //})
                });
            });
        }
    }

});
