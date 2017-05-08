/*define(function (require, exports, module) {
    require('../ajaxSelect2.js');
    var Basic = require('../basic.js');
    var userRole = new Basic('acl/userRole');

    return {
        init: function () {
            var user = new ajaxSelect2('#user-select2', '/acl/search/user', {
                multiple: true,
                textName: 'nickname',
                queryUrl: '/acl/search/user/notUsed'

            });
            var role = new ajaxSelect2('#role-select2', '/acl/userRole/searchRole', {
                multiple: true
            });

            $(".authorize").on("click", function () {
                user.clear();
                role.clear();
                user.init();
                role.init();
                user.disabled(true);
            });
            $(".authorize-edit").on("click", function () {
                var userid = $(this).data('userid');
                user.init(userid);
                role.init(userid);
                user.disabled(false);

            });
            $("#authorize-submit").on("click", function () {
                var uid = $(this).data('uid');
                var userIds = user.getVal();
                var roleIds = role.getVal();
                userRole.ajaxAndReload('/acl/userRole/authorize', {userIds: userIds, roleIds: roleIds});
            });
        }
    }
});*/
