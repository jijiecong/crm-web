/**
 * support
 * 新建动态表单布局
 */
define(function (require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('collection');


    var RuleTypeMap = {};     // Map map = new HashMap();
    RuleTypeMap[1] = "SPONSOR"; // map.put(key, value);
    RuleTypeMap[2] = "PARTICIPATOR";
    RuleTypeMap[3] = "SPONSORAWARD";
    RuleTypeMap[4] = "PARTICIPATORAWARD";
    RuleTypeMap[5] = "SHAREAWARD";


    //提取表单数据
    var submitFormFields = function () {
        //获取直接子元素
        var fieldListObj = $("#fieldRegion").children("li");

        var fieldList = new Array();
        fieldListObj.each(function (i) {
            var fieldNode = parseFieldNode($(this));
            fieldList.push(fieldNode);
        });

        var basicId = $("#saveForm").val();

        //ajax提交表单数据
        $.ajax({
            type: "POST",
            dataType: "json",
            data: {
                id: basicId,
                fieldList: JSON.stringify(fieldList)
            },
            url: "/form/formSubmit",
            success: function (result) {
                if (result.success) {
                    window.parent.closeIframe(); //执行关闭自身操作
                } else {
                    alert(result.error);
                }

            }
        });

    };


    var parseFieldNode = function (fieldLiNodeObj) {
        var liNodeMap = {};
        var sort = fieldLiNodeObj.find('.module-menu h4').html();
        var title = fieldLiNodeObj.find('.ui-drag-area div span').html();
        var subNodeContainer = fieldLiNodeObj.find('.cq-items-content ul');
        var typeNumber = subNodeContainer.attr("data-checktype");
        var optionName = subNodeContainer.attr("data-namestr");
        liNodeMap["title"] = title;
        liNodeMap["sort"] = sort;
        liNodeMap["type"] = typeNumber;
        var subNodeList = new Array();
        if (typeNumber != 1 && typeNumber != 2) {
            var optionMap = {};
            optionMap["sort"] = "0";
            optionMap["labelName"] = title;
            optionMap["logicName"] = optionName;
            subNodeList.push(optionMap);
            liNodeMap["subNode"] = subNodeList;
            return liNodeMap;
        }


        subNodeContainer.children("li").each(function (index) {
            var subOptionValue = $(this).find("label input").attr("value");
            var subLabelName = $(this).find("div").html();
            var optionMap = {};
            optionMap["sort"] = index;
            optionMap["labelName"] = subLabelName;
            optionMap["logicName"] = optionName;
            optionMap["value"] = subOptionValue;
            subNodeList.push(optionMap);
            index++;
        });
        liNodeMap["subNode"] = subNodeList;
        return liNodeMap;
    }


    return {

        init: function () {


            //点击保存表单按钮----任务保存
            $("#saveForm").on("click", function () {
                var url;
                var save=false;
                var filterId = $("#textarea_filter_id").val();
                if (filterId == null || filterId == "" || filterId == -1) {
                    url = "/mission/filter/create";
                    save=true;
                } else {
                    url = "/mission/filter/modify";
                }
                var ruleName = $("#textarea_filter_name").val();
                var ruleList = new Array();
                var cursor = 1;
                $(".tab-content .tab-pane").each(function () {
                    var tempRuleList = new Array();
                    $(this).find(".ruleComponent").each(function () {
                        var ruleKey = $(this).find("#textarea_filter_param_key").val();
                        var ruleValue = $(this).find("#textarea_filter_param_value").val();
                        var operationEnum = $(this).find("#textarea_filter_param_operator").find("option:selected").val();
                        var tempData = {
                            ruleKey: ruleKey,
                            ruleValue: ruleValue,
                            operationEnum: operationEnum
                        }
                        tempRuleList.push(tempData);
                    })

                    var tempAwardList = new Array();
                    $(this).find(".awardComponent").each(function () {
                        var awardType = $(this).find("#textarea_filter_award_type").attr("data-awardType");
                        var amount = $(this).find("#textarea_filter_award_amount").val();
                        if (awardType != null) {
                            var couponCode = (awardType == 1 ? $(this).find("#textarea_filter_award_code").val() : null);
                            var tempData = {
                                awardType: awardType,
                                amount: amount,
                                couponCode: couponCode
                            }
                            tempAwardList.push(tempData);
                        }
                    })
                    var isAuto = $(this).find("#textarea_filter_is_auto").prop("checked");
                    var formId = $(this).find("#textarea_filter_form_id").val();
                    var sponsor = {
                        ruleTypeEnum: RuleTypeMap[cursor],
                        filterRuleDetailList: tempRuleList,
                        actionVOList: tempAwardList,
                        isAuto: isAuto,
                        formId: formId
                    };
                    ruleList.push(sponsor);
                    cursor = cursor + 1;
                })

                //发送修改请求
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    data: {
                        id: filterId,
                        filterRuleVOList: JSON.stringify(ruleList),
                        ruleName: ruleName
                    },
                    url: url,
                    success: function (result) {
                        if (result.success) {
                            if(save){
                                window.parent.document.getElementById('textarea_mission_filter_id').value=result.data;
                                window.document.getElementById("textarea_filter_id").value=result.data;
                            }
                        } else {
                            alert(result.error);
                        }

                    }
                });
            });

            $("#closeFormPage").on("click", function () {
                window.parent.closeIframe(); //执行关闭自身操作
            });

            // $(".delComponentBtn").on("click", function () {
            //     $(this).closest('.delComponent').remove();
            // });

            $(document).on("click",".delComponentBtn", function () {
                $(this).closest('.delComponent').remove();
            });
        }
    }

});
