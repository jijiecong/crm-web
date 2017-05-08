define(function (require, exports, module) {
    var ajax = function (url, data, callback) {
        $.ajax({
            type: "POST",
            dataType: "json",
            data: data,
            url: url,
            success: function (result) {
                if (result.success) {
                    callback(result);
                } else {
                    alert(result.error);
                }
            }
        });
    };

    return {
        init: function () {
            var searchGroupId = new ajaxSelect2('#searchGroupId', "/acl/search/group", {});
        }
    }
});
