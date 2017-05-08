define(function (require, exports, module) {
    var Basic = require('./basic.js');
    var monitor = new Basic('monitor/result');
    return {
        init: function () {
            $(".content").on("click", function () {
                var uid = $(this).data('uid');
                monitor.getById(uid, function (data) {
                	console.log(data.content)
                	$("#contents").html(data.content);
                });
            });
        }
    }
});
