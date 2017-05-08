define(function (require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('js/arale/calendar/0.9.0/calendar.css');

    function AnswerBasic() {
        this.init();
    }

    return {
        init: function () {
            $(".showAnswerDetail").on("click", function () {
                var answerId = $(this).data('uid');
                var boxName = ".answerDetail" + answerId;
                if ($(boxName).is(":hidden")) {
                    if ($(boxName).children().size() == 0) {
                        $.ajax({
                            type: "POST",
                            dataType: "json",
                            data: {
                                answerId: answerId
                            },
                            url: "/answer/detail",
                            success: function (result) {
                                if (result.success) {
                                    var html = "<td colspan=\"5\"><table class=\"table table-condensed table-hover\">" +
                                        "<thead><tr><th>编号</th>" +
                                        "<th>题目</th>" +
                                        "<th>选项</th>" +
                                        "<th>选项值</th></thead><tbody>";
                                    if (typeof(result.data.answerDetailsMap) != 'undefined') {
                                        $.each(result.data.answerDetailsMap, function (i, item) {
                                            $.each(item,function (j,answer) {
                                                html += "<tr><td>" + answer.answerDetailId + "</td>" +
                                                    "<td>" + answer.topicTitle + "</td>" +
                                                    "<td>" + answer.optionLabelName + "</td>" +
                                                    "<td>" + answer.optionValue + "</td></tr>";
                                            });
                                        });
                                    }
                                    html += "</tbody></table></td>";
                                    $(boxName).append(html);

                                } else {
                                    alert(result.error);
                                }
                            }
                        });
                    }
                    $(this).html("收起");
                    $(boxName).slideDown("slow");
                } else {
                    $(this).html("查看");
                    $(boxName).slideUp("slow");
                }
            });
        }
    }

});
