define(function(require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    var Calendar = require('js/arale/calendar/0.9.0/calendar');
    require('js/arale/calendar/0.9.0/calendar.css');

    function Regex(){
        this.data = {
            id:null,
            name: null,
            rule: null,
            info: null
        };
        this.init();
    }

    return {

        init: function() {
            $('.actions .add').on("click",function () {
                $('#newRegex').slideToggle();
            });
            $('.addRegex').on("click",function () {
                var regexName = $('#regexName').val();
                var regexRule = $('#regexRule').val();
                var regexInfo = $('#regexInfo').val();
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        name : regexName,
                        rule : regexRule,
                        info : regexInfo
                    },
                    async: false,
                    url :  "/regex/add",
                    success : function(result){
                        if (result.success) {
                            window.location.reload();
                        }else{
                            alert(result.error);
                        }

                    }
                });

            });
            $('.showUpdateRegex').on("click",function () {
              var id=$(this).attr("data-uid");
                $('.u_'+id).toggle();
                $('.update_'+id).toggle();
            });
            $('.updateRegex').on("click",function () {
                var regexId=$(this).attr("data-uid");
                var updateBox=$('.update_'+regexId);
                var regexName = updateBox.find('.regexName').val();
                var regexRule = "";
                var regexInfo = updateBox.find('.regexInfo').val();
                if (updateBox.find('.isCustomerRule').is(':checked')) {
                    regexRule = updateBox.find('.regexRule').val();
                } else {
                    //组装正则表达式
                    var customerRegex = updateBox.find('.customerRegex');
                    var upperCaseHead = customerRegex.find('.regex-upperCaseHead').is(':checked');
                    var underline = customerRegex.find('.regex-underline').is(':checked');
                    var english = customerRegex.find('.regex-english').is(':checked');
                    var upperCase = customerRegex.find('.regex-upperCase').is(':checked');
                    var chinese = customerRegex.find('.regex-chinese').is(':checked');
                    var number = customerRegex.find('.regex-number').is(':checked');
                    regexRule = "^[";
                    if (upperCaseHead) {
                        regexRule+="A-Z][";
                        regexName+="_regex-upperCaseHead";
                    }
                    if (underline) {
                        regexRule += "_|";
                        regexName+="_regex-underline";
                    }
                    if (english) {
                        regexRule += "a-z|";
                        regexName+="_regex-english";
                    }
                    if (upperCase) {
                        regexRule += "A-Z|";
                        regexName+="_regex-upperCase";
                    }
                    if (chinese) {
                        regexRule += "\\u4e00-\\u9fa5|";
                        regexName+="_regex-chinese";
                    }
                    if (number) {
                        regexRule += "0-9|";
                        regexName+="_regex-number";
                    }
                    regexRule=regexRule.substr(0, regexRule.length-1);
                    regexRule += "]*$";
                }

                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        id : regexId,
                        name : regexName,
                        rule : regexRule,
                        info : regexInfo
                    },
                    async: false,
                    url :  "/regex/update",
                    success : function(result){
                        if (result.success) {
                            window.location.reload();
                        }else{
                            alert(result.error);
                        }

                    }
                });
            });
            $('.saveRegex').on("click",function () {
                var regexName = $('#regexName').val();
                var regexRule = "";
                var regexInfo = $('#regexInfo').val();
                if ($('#isCustomerRule').is(':checked')) {
                    regexRule = $('#regexRule').val();
                } else {
                    //组装正则表达式
                    var customerRegex = $('#customerRegex');
                    var upperCaseHead = customerRegex.find('#regex-upperCaseHead').is(':checked');
                    var underline = customerRegex.find('#regex-underline').is(':checked');
                    var english = customerRegex.find('#regex-english').is(':checked');
                    var upperCase = customerRegex.find('#regex-upperCase').is(':checked');
                    var chinese = customerRegex.find('#regex-chinese').is(':checked');
                    var number = customerRegex.find('#regex-number').is(':checked');
                    regexRule = "^[";
                    if (upperCaseHead) {
                        regexRule+="A-Z][";
                        regexName+="_regex-upperCaseHead";
                    }
                    if (underline) {
                        regexRule += "_|";
                        regexName+="_regex-underline";
                    }
                    if (english) {
                        regexRule += "a-z|";
                        regexName+="_regex-english";
                    }
                    if (upperCase) {
                        regexRule += "A-Z|";
                        regexName+="_regex-upperCase";
                    }
                    if (chinese) {
                        regexRule += "\\u4e00-\\u9fa5|";
                        regexName+="_regex-chinese";
                    }
                    if (number) {
                        regexRule += "0-9|";
                        regexName+="_regex-number";
                    }
                    regexRule=regexRule.substr(0, regexRule.length-1);
                    regexRule += "]*$";
                }
                $.ajax({
                    type : "POST",
                    dataType :"json",
                    data : {
                        name : regexName,
                        rule : regexRule,
                        info : regexInfo
                    },
                    async: false,
                    url :  "/regex/add",
                    success : function(result){
                        if (result.success) {
                            window.location.reload();
                        }else{
                            alert(result.error);
                        }

                    }
                });

            });
            $('#isCustomerRule').on("change",function () {
                $("#customerRegex").toggle();
                $("#regexRule").toggle();
            });
            $('.isCustomerRule').on("change",function () {
                var id=$(this).attr("data-uid");
                $('.update_'+id).find('.customerRegex').toggle();
                $('.update_'+id).find('.regexRule').toggle();
            });
        }
    }

});
