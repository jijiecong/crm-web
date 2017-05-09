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
        $("#batch-del-submit").on("click", function () {
            pThis.batchDel();
        });
    };

    var group = new Basic('acl/group');

    return {
        init: function () {
            var parent = new ajaxSelect2('#test', "/acl/search/group", {});

            var selectleader = new ajaxMultiSelect({
                dom: '#select-leader',
                url: '/acl/group/setLeader',
                selectableHeaderTitle: '部门成员',
                selectionHeaderTitle: '部门leader'
            });
            var selectUser = new ajaxMultiSelect({
                dom: '#select-user',
                url: '/acl/group/setUser',
                selectableHeaderTitle: '用户列表',
                selectionHeaderTitle: '部门成员'
            });

            //查询角色
            var selectrole = new ajaxMultiSelect({
                dom: '#select-role',
                url: '/acl/group/setRole',
                selectableHeaderTitle: '角色列表',
                selectionHeaderTitle: '部门角色'

            });

            $(".set-role").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-role").data('uid', uid);
                selectrole.init();
            });

            $(".set-user").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-user").data('uid', uid);
                selectUser.init();
            });

            $(".set-leader").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-leader").data('uid', uid);
                selectleader.init();
            });

            $(".add").on("click", function () {
                $(".operation-name").html('添加');
                $("#form input").val('');
                $("#modal-form-submit").data('uid', '');
                parent.init();
            });

            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/search/findByName', {
                multiple: false
            });

            businessSelect2.init($("#select-business").val());
            //打开弹出层修改页面
            $(".edit").on("click", function () {
                var uid = $(this).data('uid');
                group.getById(uid, function () {
                    parent.init();
                });
                $("#modal-form-submit").data('uid', uid);
                $(".operation-name").html('编辑');
            });
        }
    }
});
