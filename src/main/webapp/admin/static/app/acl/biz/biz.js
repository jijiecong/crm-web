define(function (require, exports, module) {
    require('../ajaxSelect2.js');
    require('../ajaxMultiSelect');
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
    var biz = new Basic('acl/biz');
    return {
        init: function () {
            var select2 = new ajaxSelect2('#select-owner', "/acl/search/user", {
                multiple: true,
                textName: 'nickname',
                initUrl: "/acl/search/user/initBizOwner"
            });
            var privilegeSelect2 = new ajaxSelect2('#select-privilege', "/acl/biz/setBizPrivilege");

            //查询应用权限start
            var selectPrivilege = new ajaxMultiSelect({
                dom: '#select-privilege',
                url: '/acl/biz/setBizPrivilege',
                selectableHeaderTitle: '未拥有权限',
                selectionHeaderTitle: '已拥有权限'
            });

            $(".set-privilege").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-privilege").data('uid', uid);
                selectPrivilege.init();
            });

            $(".add").on("click", function () {
                $(".operation-name").html('添加');
                $("#modal-form-submit").data('uid', '');
                biz.clearData();
                privilegeSelect2.init();
            }); //查询应用权限end

            //打开弹出层修改页面
            $(".edit").on("click", function () {
                var uid = $(this).data('uid');
                $(".operation-name").html('编辑');
                $("#modal-form-submit").data('uid', uid);
                biz.getById(uid, function (data) {
                    select2.init(uid);
                });
            });

        }
    }

});
