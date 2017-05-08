define(function (require, exports, module) {
    var Basic = require('../basic.js');
    Basic.prototype.getData = function () {
        console.log($(this.param.formDom).serialize());
        return $(this.param.formDom).serialize();
    };
    var apply = new Basic('acl/apply');
    var apply1 = new Basic('acl/apply',{formDom:"#form1"});
    return {
        init: function () {
            //查询权限
            var select2 = new ajaxSelect2('#select-privilege', "/acl/search/privilege");
            $(".add-apply").on("click", function () {
                var uid = $(this).data('uid');
                select2.init();
                $("#modal-change-submit").data('uid', uid);
            });

            //查询角色
            var select2Role = new ajaxSelect2('#select-role', "/acl/search/role");
            $(".apply-role").on("click", function () {
                var uid = $(this).data('uid');
                select2Role.init();
                $("#modal-role-submit").data('uid', uid);
            });

            //申请角色
            $("#modal-role-submit").on("click", function () {
                var uid = $(this).data('uid');
                var data = apply1.getData();
                apply1.ajaxAndReload('/acl/apply/apply/addRole', data, function (data) {
                    console.log(data);
                });
            });

            //申请权限
            $("#modal-change-submit").on("click", function () {
                var uid = $(this).data('uid');
                var data = apply.getData();
                apply.ajaxAndReload('/acl/apply/apply/add', data, function (data) {
                    console.log(data);
                });
            });
           
        }
    }
});
