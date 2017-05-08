define(function (require, exports, module) {
    var processBase = function (dom) {
        this.param.dom = dom;
    };
    processBase.prototype = {
        list: [],
        data: [
            'processId',
            'approvalLevel',
            'processVersion',
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
                + '<input type="hidden" name="processVersion-' + data.id + '" value="' + data.version + '">'
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

    Basic.prototype.init = function () {
        var pThis = this;
        //打开弹出层进行设置
        $(".add").on("click", function () {
            $(".operation-name").html('添加');
            $("#form input").val('');
            $("#modal-form-submit").data('uid', '');
            pThis.clearData();
        });
        //打开弹出层修改页面
        $(".edit").on("click", function () {
            var uid = $(this).data('uid');
            pThis.getById(uid);
            $("#modal-form-submit").data('uid', uid);
            $(".operation-name").html('编辑');
        });

        $("#modal-form-submit").on("click", function () {
            pThis.submit($(this).data('uid'));
        });

        $(".del").on("click", function () {
            var uid = $(this).data('uid');
            $("#affirm-del").data('uid', uid);
        });

        $("#affirm-del").on("click", function () {
            var formId = $(this).data('uid');
            pThis.del(formId);
        });
    };

    var ajax = function (url, data, callback, postType) {
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

    var privilege = new Basic('acl/privilege');
    return {
        init: function () {
            var select = new ajaxMultiSelect({
                isSearchable:true,
                dom: '#select-owner',
                url: '/acl/privilege/setOwner',
                selectableHeaderTitle: '成员',
                selectionHeaderTitle: 'owner'
            });
            var process = new processBase('#process-param');

            var hierarchy = new ajaxSelect2('.hierarchy-select', "/acl/search/hierarchy", {
                textName: 'hierarchyName'
            });

            $(".set-owner").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-owner").data('uid', uid);
                select.init();
            });

            $(".set-process").on("click", function () {
                var uid = $(this).data('uid');
                ajax('/acl/privilege/process/init?id=' + uid, [], function (data) {
                    process.init(data.data);
                    hierarchy.init();
                }, 'json');
                $("#process-submit").data('uid', uid);
            });

            $("#process-submit").on("click", function () {
                var uid = $(this).data('uid');
                var list = process.getData();
                ajax('/acl/privilege/process/post?id=' + uid, list, function (data) {
                    console.log(data);
                }, 'json');
            });
        }
    }
});
