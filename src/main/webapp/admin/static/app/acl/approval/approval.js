define(function (require, exports, module) {
    var Basic = require('../basic.js');
    Basic.prototype.getData = function () {
        console.log($(this.param.formDom));
        return $(this.param.formDom).serialize();
    };
    var agent = new Basic('acl/approval/agent',{
        formDom:'#agent-form'
    });

    return {
        init: function () {
            var userSelect2 = new ajaxSelect2('#change-user', "/acl/search/user", {
                textName: 'nickname'
            });
            var userSelect = new ajaxSelect2('#add-user', "/acl/search/user", {
                textName: 'nickname'
            });
            var agentUser = new ajaxSelect2('#agent-user', "/acl/search/user", {
                textName: 'nickname'
            });

            $(".set-agent").on("click", function () {
                agent.clearData();
                agent.getById(null, function (data) {
                    agentUser.init();
                });

            });

            $("#modal-agent-submit").on("click", function () {
                var uid = $(this).data('uid');
                var data = agent.getData();
                agent.ajaxAndReload('/acl/approval/agent/edit',data, function (data) {
                    console.log(data);
                });
            });

            $(".set-change").on("click", function () {
                var uid = $(this).data('uid');
                userSelect2.init();
                $("#modal-change-submit").data('uid', uid);
            });

            $("#modal-change-submit").on("click", function () {
                var uid = $(this).data('uid');
                var userId = $("#change-user").val();
                console.log(uid);
                if (userId == null || userId == '') {
                    return;
                }
                agent.ajaxAndReload('/acl/approval/signed/change', {id: uid, toUserId: userId}, function (data) {
                    console.log(data);
                });
            });


            $(".set-add").on("click", function () {
                var uid = $(this).data('uid');
                userSelect.init();
                $("#modal-add-submit").data('uid', uid);
            });

            $("#modal-add-submit").on("click", function () {
                var uid = $(this).data('uid');
                var userId = $("#add-user").val();
                if (userId == null || userId == '') {
                    return;
                }
                agent.ajaxAndReload('/acl/approval/signed/add', {id: uid, toUserId: userId}, function (data) {
                    console.log(data);
                });
            });

            $(".set-pass").on("click", function () {
                var uid = $(this).data('uid');
                agent.ajaxAndReload('/acl/approval/result/pass', {id: uid}, function (data) {
                    console.log(data);
                });
            });
            $(".set-no-pass").on("click", function () {
                var uid = $(this).data('uid');
                agent.ajaxAndReload('/acl/approval/result/notpass', {id: uid}, function (data) {
                    console.log(data);
                });
            });
        }
    }
});
