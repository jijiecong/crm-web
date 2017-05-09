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
    
    require('../ajaxMultiSelect');
    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    Basic.prototype.init = function () {
        var pThis = this;
/*        $("#modal-form-submit").on("click", function () {
            pThis.submit($(this).data('uid'));
        });*/
        $(".del").on("click", function () {
            var uid = $(this).data('uid');
            $("#affirm-del").data('uid', uid);
        });
        $("#affirm-del").on("click", function () {
            var formId = $(this).data('uid');
            pThis.del(formId);
        });
        $("#batch-del-submit").on("click", function () {
            pThis.batchDel();
        });
    };
    Basic.prototype.getData = function () {
        return $(this.param.formDom).serialize();
    };
    var role = new Basic('acl/role');

    var Team = function (dom) {
        this.dom = dom;
    };
    Team.prototype = {
        init: function (data, checkedData) {
            if (data == null) {
                return;
            }
            $(this.dom).html(this.makeTd());
            $("#make-html").html(this.makeHtml(data));
            this.initCheckBox();
            this.setChecked(checkedData || []);
        },
        clear: function () {
            $(this.dom).html('')
        },
        add: function (data) {
            console.log(data);
        },
        del: function (data) {
            console.log(data);
        },
        setChecked: function (data) {
            $.each(data, function (key, val) {
                var checkbox = $('.check-children[value=' + val.id + ']:checkbox');
                checkbox.attr("checked", "checked");
            })
        },
        change: function (val, isChecked) {
            var one = {
                id: $(val).val(),
                name: $(val).data('name')
            };
            var checkbox = $('.check-children[value=' + one.id + ']:checkbox');
            if (isChecked) {
                checkbox.attr("checked", "checked");
                this.add(one)
            } else {
                checkbox.removeAttr("checked");
                this.del(one)
            }
        },
        initCheckBox: function () {
            var pThis = this;
            $('.check-all').unbind("click");
            $('.check-children').unbind("click");

            $('.check-all').on("click", function () {
                var teamid = $(this).val();
                var checkbox = $('.check-children.teamid-' + teamid);
                var isChecked = $(this).is(':checked');
                checkbox.each(function (key, val) {
                    pThis.change(val, isChecked);
                });
            });
            $('.check-children').on("click", function () {
                var teamid = $(this).data('teamid');
                var isChecked = $(this).is(':checked');
                var allCheckbox = $('.check-all.teamid-' + teamid);
                var isCheckedAll = allCheckbox.is(':checked');
                if (!isChecked && isCheckedAll) {
                    allCheckbox.removeAttr("checked");
                }
                pThis.change(this, isChecked);
            });
        },
        makeTd: function () {
            var html = '<td><span>对应权限组：</span></td>'
                + '<td>'
                + '    <div id="make-html"></div>'
                + '</td>';
            return html;
        },
        makeTop: function (data) {
            var html = '';
            $.each(data, function (key, val) {
                var active = key == 0 ? 'active' : '';
                html += '<li class="' + active + ' p-position"><input type="checkbox" class="checkbox-fix check-all teamid-' + val.id + '" value="' + val.id + '"/>'
                    + '<a href="#tab-' + val.id + '" data-toggle="tab"><span class="ml8">' + val.name + '</span></a>'
                    + '</li>';
            });
            return html;
        },
        makeCenter: function (data) {
            var html = '';
            $.each(data, function (key, val) {
                var active = key == 0 ? 'active' : '';
                html += '<div class="tab-pane ' + active + '" id="tab-' + val.id + '">';
                if (val.children != null) {
                    $.each(val.children, function (key2, val2) {
                        html += '<label class="ml2 mr2"><input type="checkbox" class="check-children teamid-' + val.id + '" data-name="' + val2.name + '" data-teamid="' + val.id + '" name="privilegeId" value="' + val2.id + '"/><span class="ml2">' + val2.name + '</span></label>';
                    });
                }
                html += '</div>';
            });
            return html;
        },
        makeHtml: function (data) {
            var html = '<div style="max-width: 400px">'
                + '<ul class="nav nav-tabs">'
                + this.makeTop(data)
                + '</ul>'
                + '<div class="tab-content" style="border-bottom:1px solid #ddd;">'
                + this.makeCenter(data)
                + '</div>'
                + '</div>';
            return html
        }
    };

    //var teamBox = new Team('#team-html');
    //teamBox.add = function (data) {
    //    var repeat = false;
    //    var checkedData = privilegeSelect2.getData();
    //    $.each(checkedData, function (key, val) {
    //        if (val.id == data.id) {
    //            repeat = true;
    //            return false
    //        }
    //    });
    //    if (!repeat) {
    //        checkedData.push(data)
    //    }
    //    privilegeSelect2.setData(checkedData);
    //};
    //teamBox.del = function (data) {
    //    var newData = [];
    //    var checkedData = privilegeSelect2.getData();
    //    $.each(checkedData, function (key, val) {
    //        if (val.id != data.id) {
    //            newData.push(val)
    //        }
    //    });
    //    privilegeSelect2.setData(newData);
    //};

    return {
        init: function () {
         
/*            var bizSelect2 = new ajaxSelect2('#biz-select2', "/acl/role/roleAddBiz");*/
            
            var process = new processBase('#process-param');
            var hierarchy = new ajaxSelect2('.hierarchy-select', "/acl/search/hierarchy", {
                textName: 'hierarchyName'
            });
            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/search/findByName', {
                multiple: false
            });

            businessSelect2.init($("#select-business").val());


            //查询角色owner
            var selectrole = new ajaxMultiSelect({
                dom: '#select-role',
                url: '/acl/role/setOwner',
                selectableHeaderTitle: '成员',
                selectionHeaderTitle: 'owner'
            });
            
            $(".set-role").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-role").data('uid', uid);
                selectrole.init();
            });
            
/*            $(".add").on("click", function () {
                $(".operation-name").html('添加');
                $("#modal-form-submit").data('uid', '');               
                role.clearData();
                bizSelect2.init();
            });*/

/*            //查询角色权限start
            var selectPrivilege = new ajaxMultiSelect({
                dom: '#select-privilege',
                url: '/acl/role/setPrivilege',
                selectableHeaderTitle: '未拥有权限',
                selectionHeaderTitle: '已拥有权限'
            });

            $(".set-privilege").on("click", function () {
                var uid = $(this).data('uid');
                $("#select-privilege").data('uid', uid);
                selectPrivilege.init();
            });*/

          

            //打开弹出层修改页面
/*            $(".edit").on("click", function () {
                var uid = $(this).data('uid');
                $(".operation-name").html('编辑');
                $("#modal-form-submit").data('uid', uid);
                role.getById(uid, function () {
                    bizSelect2.init(uid);
                });
            });*/
            
            $(".set-process").on("click", function () {
                var uid = $(this).data('uid');
                ajax('/acl/role/process/init?id=' + uid, [], function (data) {
                    process.init(data.data);
                    hierarchy.init();
                }, 'json');
                $("#process-submit").data('uid', uid);
            });

            $("#process-submit").on("click", function () {
                var uid = $(this).data('uid');
                var list = process.getData();
                ajax('/acl/role/process/post?id=' + uid, list, function (data) {
                    console.log(data);
                }, 'json');
            });
        }
    }
    
    
});
