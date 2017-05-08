define(function (require, exports, module) {
	var processBase = function (dom) {
        this.param.dom = dom;
    };
    processBase.prototype = {
        list: [],
        data: [
            'processId',
            'approvalLevel',
            'approvalCondition',
            'hierarchyId',
        ],
        param: {
            dom: null,
            pDom: '#process-',
            cDom: '#checkbox-',
        },
        makeHtml: function (data) {
            var html = '<p id="process-' + data.id + '" style="border-bottom: 1px dashed #000;">'
                + '<input type="checkbox" style="margin: auto" id="checkbox-' + data.id + '" value="' + data.id + '"/>&nbsp;' + data.name
                + '<input type="hidden" name="processId-' + data.id + '" value="' + data.id + '">'
                + '<input type="hidden" name="approvalLevel-' + data.id + '" value="' + data.approvalLevel + '">'
                + '<br>&nbsp;通过标准:&nbsp;'
                + '<label style="display: initial"><input type="radio" style="margin: auto" name="approvalCondition-' + data.id + '" value="AND" checked/>&nbsp;and&nbsp;</label>'
                + '<label style="display: initial"><input type="radio" style="margin: auto" name="approvalCondition-' + data.id + '" value="OR"/>&nbsp;or&nbsp;</label>'
                + '<br>最高审核等级:<input type="text" class="hierarchy-select m-wrap large" name="hierarchyId-' + data.id + '" value="' + data.hierarchyId + '" style="margin-bottom: 5px;"/>'
                + '</p>'
            return html;
        },
        init: function (data) {
            var pThis = this;
            $(this.param.dom).html('');
            var html = '';
            $.each(data.all, function (key, val) {
                html += pThis.makeHtml(val);
            });
            $(this.param.dom).html(html);
            this.setData(data.have);
        },
        setData: function (list) {
            var pThis = this;
            var pData = this.data;
            $.each(list, function (key, val) {
                var id = val.processId;
                $(pThis.param.cDom + id).attr('checked', true);
                $.each(pData, function (idx, key) {
                    var valDom = $(pThis.param.pDom + id).find('input[name=' + key + '-' + id + ']');
                    if (key == 'approvalCondition') {
                        $.each(valDom, function (a, b) {
                            var cVal = $(b).val();
                            if (cVal == val[key]) {
                                $(b).attr('checked', true)
                            } else {
                                $(b).attr('checked', false)
                            }
                        })
                    } else {
                        $(valDom).val(val[key]);
                    }
                });
            });
        },
        getData: function () {
            var list = [];
            var pThis = this;
            var pData = this.data;
            var checkbox = $(this.param.dom).find('input[type=checkbox]:checked');
            checkbox.each(function (a, b) {
                var id = $(b).val();
                var data = {};
                $.each(pData, function (idx, key) {
                    var valDom;
                    if (key == 'approvalCondition') {
                        valDom = $(pThis.param.pDom + id).find('input[name=' + key + '-' + id + ']:checked');
                    } else {
                        valDom = $(pThis.param.pDom + id).find('input[name=' + key + '-' + id + ']');
                    }
                    data[key] = $(valDom).val()
                });
                list.push(data)
            });
            return list;

        }
    };
    
    var Basic = require('../basic.js');
    var process = new Basic('acl/process');

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
    
    var ajax2 = function (url, data, callback, postType) {
        var jsonParam = {
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data),
        };
        var postParam = {
            data: data,
        };
        var param = $.extend({
            type: "POST",
            dataType: "json",
            url: url,
            success: function (result) {
                if (result.success) {
                    callback(result);
                } else {
                    alert(result.error);
                }
            }
        }, postType == 'json' ? jsonParam : postParam);
        $.ajax(param);
    };

    return {
        init: function () {

            var hierarchy2 = new ajaxSelect2('.hierarchy-select', "/acl/search/hierarchy", {
                textName: 'hierarchyName'
            });
            var process2 = new processBase('#process-param');
            
            var type="添加失败";
            var uid = $("#form-submit").data('uid');
            console.log(uid);
            if(uid!=""&&uid!=undefined){
            	type="修改失败";
            	ajax2('/acl/process/setModel/init?riskLevel=' + riskLevel, [], function (data) {
                    process2.init(data.data);
                    hierarchy2.init();
                }, 'json');
            }else{
            	hierarchy.init();
            }    
            
            $("#close-form").on("click", function () {
            	window.location.href = '/acl/process/index';
            });
            
            $("#form-submit").on("click", function () {
            	process.submit2($(this).data('uid'),function(data){
		        	if(data.success){
		        		window.location.href = '/acl/process/index';
		        	}else{
		        		alert(type);
		        	}
		        });
            });
            
            $(".set-lowprocess").on("click", function () {
                var riskLevel = $(this).data('level');
                ajax2('/acl/process/setModel/init?riskLevel=' + riskLevel, [], function (data) {
                    process2.init(data.data);
                    hierarchy2.init();
                }, 'json');
                $("#process-submit").data('riskLevel', riskLevel);
                $(".operation2-name").html('编辑低风险审核流程模板');
            });
            
            $(".set-middleprocess").on("click", function () {
                var riskLevel = $(this).data('level');
                ajax2('/acl/process/setModel/init?riskLevel=' + riskLevel, [], function (data) {
                    process2.init(data.data);
                    hierarchy2.init();
                }, 'json');
                $("#process-submit").data('riskLevel', riskLevel);
                $(".operation2-name").html('编辑中风险审核流程模板');
            });
            
            $(".set-highprocess").on("click", function () {
                var riskLevel = $(this).data('level');
                ajax2('/acl/process/setModel/init?riskLevel=' + riskLevel, [], function (data) {
                	process2.init(data.data);
                	hierarchy2.init();
                }, 'json');
                $("#process-submit").data('riskLevel', riskLevel);
                $(".operation2-name").html('编辑高风险审核流程模板');
            });

            $("#process-submit").on("click", function () {
                var riskLevel = $(this).data('riskLevel');
                var list = process2.getData();
                ajax2('/acl/process/setModel/post?riskLevel=' + riskLevel, list, function (data) {
                    console.log(data);
                }, 'json');
            });
        }
    }
});
