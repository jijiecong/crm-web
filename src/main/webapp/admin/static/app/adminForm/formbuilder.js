/**
 * support
 * 新建动态表单布局
 */
define(function(require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('collection');


    var typeValueMap = {};     // Map map = new HashMap();
    typeValueMap[1] = "option"; // map.put(key, value);
    typeValueMap[2] = "checkbox";
    typeValueMap[3] = "text";
    typeValueMap[4] = "textarea";
    typeValueMap[5] = "bannerFixedPlaceHolder";
    typeValueMap[6] = "datePlaceHolder";
    typeValueMap[7] = "addressPlaceHolder";


    //提取表单数据
    var submitFormFields = function(){
        //获取直接子元素
        var fieldListObj = $("#fieldRegion").children("li");

        var fieldList = new Array();
        fieldListObj.each(function(i){
            var fieldNode = parseFieldNode($(this));
            fieldList.push(fieldNode);
        });

        var basicId =  $("#saveForm").val();

        //ajax提交表单数据
        $.ajax({
            type : "POST",
            dataType :"json",
            data : {
                id: basicId,
                fieldList: JSON.stringify(fieldList)
            },
            url :  "/form/formSubmit",
            success : function(result){
                if (result.success) {
                    window.parent.closeIframe(); //执行关闭自身操作
                }else{
                    alert(result.error);
                }

            }
        });

    };





    var parseFieldNode = function(fieldLiNodeObj){
        var liNodeMap = {};
        var sort = fieldLiNodeObj.find('.module-menu h4').html();
        var title = fieldLiNodeObj.find('.ui-drag-area div span').html();
        var subNodeContainer = fieldLiNodeObj.find('.cq-items-content ul');
        var typeNumber = subNodeContainer.attr("data-checktype");
        var optionName = subNodeContainer.attr("data-namestr");
        liNodeMap["title"]=title;
        liNodeMap["sort"]=sort;
        liNodeMap["type"]=typeNumber;

        var validatorBox = fieldLiNodeObj.find('.validatorBox');
        var validator = {};
        if (typeof(validatorBox)!='undefined') {
            // var validatorId = validatorBox.attr("data-id");
            var basicValidator = validatorBox.find('.basicValidator');
            var minLength = basicValidator.find('.minlength').val();
            var maxLength = basicValidator.find('.maxlength').val();
            var isRequired = 0;
            if (basicValidator.find('.isRequired').is(':checked')) {
                isRequired = 1;
            }
            // validator["id"] = validatorId;
            validator["minLength"] = minLength;
            validator["maxLength"] = maxLength;
            validator["isRequired"] = isRequired;

            var regexBox = validatorBox.find('.regexBox');
            if (typeNumber == 3 || typeNumber == 4) {
                if (regexBox.find('.isCustomerRegex').is(':checked')) {
                    //组装正则表达式
                    var customerRegex = regexBox.find('.customerRegex');
                    var upperCaseHead = customerRegex.find('.regex-upperCaseHead').is(':checked');
                    var underline = customerRegex.find('.regex-underline').is(':checked');
                    var english = customerRegex.find('.regex-english').is(':checked');
                    var upperCase = customerRegex.find('.regex-upperCase').is(':checked');
                    var chinese = customerRegex.find('.regex-chinese').is(':checked');
                    var number = customerRegex.find('.regex-number').is(':checked');
                    var regexRule = "^[";
                    var regexInfo = "";
                    var regexName = customerRegex.find('.regex-name').val();
                    if (typeof(regexName) == 'undefined') {
                        regexName = " ";
                    }
                    if (upperCaseHead) {
                        regexRule += "A-Z][";
                        regexName += "_regex-upperCaseHead";
                        regexInfo += "大写字母开头\n";
                    }
                    if (underline) {
                        regexRule += "_|";
                        regexName += "_regex-underline";
                        regexInfo += "下划线\n";
                    }
                    if (english) {
                        regexRule += "a-z|";
                        regexName += "_regex-english";
                        regexInfo += "可选英文\n";
                    }
                    if (upperCase) {
                        regexRule += "A-Z|";
                        regexName += "_regex-upperCase";
                        regexInfo += "可选大写字母\n";
                    }
                    if (chinese) {
                        regexRule += "\\u4e00-\\u9fa5|";
                        regexName += "_regex-chinese";
                        regexInfo += "可选中文\n";
                    }
                    if (number) {
                        regexRule += "0-9|";
                        regexName += "_regex-number";
                        regexInfo += "可选数字\n";
                    }
                    regexRule = regexRule.substr(0, regexRule.length - 1);
                    regexRule += "]*$";
                    validator["regexName"] = regexName;
                    validator["regexRule"] = regexRule;
                    validator["info"] = regexInfo;
                } else {
                    var selectedRegex = regexBox.find('.preRegex option:selected');
                    var regexId = selectedRegex.attr("data-id");
                    var regexName = selectedRegex.attr("data-name");
                    var regexRule = selectedRegex.attr("data-rule");
                    var info = regexBox.find('.preRegex .' + selectedRegex.html() + 'span').html();
                    validator["regexId"] = regexId;
                    validator["regexName"] = regexName;
                    validator["regexRule"] = regexRule;
                    validator["info"] = info;
                }
            }
        }
        liNodeMap["validator"] = validator;

        var subNodeList = new Array();
        if(typeNumber!=1&&typeNumber!=2){
            var optionMap = {};
            optionMap["sort"]="0";
            optionMap["labelName"]=title;
            optionMap["logicName"]=optionName;
            subNodeList.push(optionMap);
            liNodeMap["subNode"]=subNodeList;
            return liNodeMap;
        }else {
            subNodeContainer.children("li").each(function(index){
                var subOptionValue =   $(this).find("label input").attr("value");
                var subLabelName = $(this).find("div").html();
                var optionMap = {};
                optionMap["sort"]=index;
                optionMap["labelName"]=subLabelName;
                optionMap["logicName"]=optionName;
                optionMap["value"]=subOptionValue;
                subNodeList.push(optionMap);
                index++;
            });
            liNodeMap["subNode"]=subNodeList;
        }


        return liNodeMap;
    };







    return {

        init: function() {


            //点击保存表单按钮
            $("#saveForm").on("click", function(){
                submitFormFields();
            });

            $("#closeFormPage").on("click", function(){
                window.parent.closeIframe(); //执行关闭自身操作
            });

            //获取父类的基础信息
            var basicInfo =   window.parent.getBasicInfo();
            var basicInfoArray = basicInfo.split(":");
            $("#saveForm").val(basicInfoArray[0]);
            $("#formTitle").html(basicInfoArray[1]);

            var xOffset=100;
            var yOffset=0;
            $(".dc_select").find("li").hover(function(e){
                $("#preview")
                $('.'+$(this).html()).css("top",(e.pageY + yOffset) + "px")
                    .css("left",(e.pageX + xOffset) + "px")
                    .fadeIn("slow");
            },function(){
                $('.'+$(this).html()).hide();
            });

        }
    }

});
