define(function (require, exports, module) {
    require('../ajaxSelect2.js');
    var Basic = require('../basic.js');
    var userRole = new Basic('acl/userRole');

    return {
        init: function () {

            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/search/business/findByName', {
                multiple: false
            });

            businessSelect2.init($("#select-business").val());
        }
    }
});
