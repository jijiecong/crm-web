var missionformexam = {

    //初始化
    init: function () {
        this.renderForm();
    },


    initTypeMap: function () {
        var typeMap = {};     // Map map = new HashMap();
        typeMap[1] = "option"; // map.put(key, value);
        typeMap[2] = "checkbox";
        typeMap[3] = "text";
        typeMap[4] = "textarea";
        typeMap[5] = "bannerFixedPlaceHolder";
        typeMap[6] = "datePlaceHolder";
        typeMap[7] = "addressPlaceHolder";
        return typeMap;
    },


    renderForm: function () {
        var _this = this;
        var typeMap = _this.initTypeMap();
        var formObj = window.parent.loadFormStructure();
        var fieldArray = formObj.data;
        if (fieldArray == null) {
            return;
        }
        var formSnapshot = jQuery.parseJSON(fieldArray.formSnapshot);
        var answerDetailsMap = fieldArray.answerDetailsMap;
        var missionInstanceId=fieldArray.missionInstanceId;
        $("#selectedBasicRegion").val(missionInstanceId);
        for (var i = 0; i < formSnapshot.length; i++) {
            _this.renderField(formSnapshot[i], i, answerDetailsMap);
        }
    },


    renderField: function (fieldModel, cursor, answerDetailsMap) {
        var _this = this;
        var typeMap = _this.initTypeMap();
        //控件名
        var pluginName = typeMap[fieldModel.type];
        var topicId = fieldModel.topicId;
        var answer = answerDetailsMap[topicId];

        var optionData = fieldModel.subNode;
        //不区分是否含有子类
        var logicName = fieldModel.subNode[0].logicName;

        var itemArray = [];
        //如果含有子类，需要进行子类的渲染
        if (fieldModel.type == 1 || fieldModel.type == 2) {
            for (var i = 0; i < fieldModel.subNode.length; i++) {
                itemArray[i] = {value: i, tid: 100132 + parseInt(100 * Math.random())};
            }
        }

        data = {
            type: fieldModel.type,//1为单选，2为多选
            name: logicName,
            //随机数模拟
            itmetid: "A" + (10045 + parseInt(100 * Math.random())),
            items: itemArray
        }
        $('.ui-questions-content-list').append(template("drag_choice", data));

        var selector = ".ui-module:eq(" + cursor + ")";
        //渲染选项名
        $(selector).each(function (v) {
            var ulItem = $(this).find('.cq-unset-list');
            $(this).find('.ui-drag-area div span').html(fieldModel.title);
            if (ulItem.attr('data-checktype') == "1" || ulItem.attr('data-checktype') == "2") {
                _this.renderSelection(ulItem, fieldModel.subNode);
            }
        });

        //设置选项值
        if (answer != null) {
            for (var i = 0; i < answer.length; i++) {
                var answerDetail=answer[i];
                var topicType=answerDetail.topicType;
                var optionValue=answerDetail.optionValue;
                if (topicType == 1) {
                    //radio
                    $("input[name='" + logicName + "'][value='" + optionValue + "']").attr("checked",true);
                }else if (topicType == 2) {
                    //checkbox
                    $("input[name='" + logicName + "'][value='" + optionValue + "']").attr("checked",true);
                }else if (topicType == 3) {
                    var selectorName='input_'+logicName;
                    $("input[name='" + selectorName + "']").val(optionValue)
                }else if (topicType == 4) {
                    var selectorName='textarea_'+logicName;
                    $("textarea[name='" + selectorName + "']").val(optionValue);
                }


            }
        }
    },

    renderSelection: function (ulModel, fieldSubNode) {
        ulModel.children('li').each(function (i) {
            var labelName = fieldSubNode[i].labelName;
            $(this).find('.cq-answer-content').html(labelName);
        });
    }
}
