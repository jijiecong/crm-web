define(function (require, exports, module) {
    var Basic = require('../basic.js');
    require('../ajaxMultiSelect.js');
    Basic.prototype.init = function () {
        var pThis = this;
        //打开弹出层进行设置
/*        $(".add").on("click", function () {
            $(".operation-name").html('添加');
            $("#form input").val('');
            $("#modal-form-submit").data('uid', '');
        });*/
        //打开弹出层修改页面
/*        $(".edit").on("click", function () {
            var uid = $(this).data('uid');
            pThis.getById(uid);
            $("#modal-form-submit").data('uid', uid);
            $(".operation-name").html('编辑');
        });*/

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
            user.batchDel();
        });
    };

    var user = new Basic('acl/user');
    return {
        init: function () {
            var multiSelect = new ajaxMultiSelect({
                isSearchable: true,
                dom: '#select-privilege-control',
                url: '/acl/user//privilege/control',
                selectableHeaderTitle: '拥有的权限',
                selectionHeaderTitle: '被禁用的权限'
            });
            
            var multiSelect2 = new ajaxMultiSelect({
                isSearchable: true,
                dom: '#select-role-control',
                url: '/acl/user/role/control',
                selectableHeaderTitle: '拥有的角色',
                selectionHeaderTitle: '被禁用的角色'
            });

            var hierarchy = new ajaxSelect2('#hierarchy-select2', "/acl/search/hierarchy", {
                textName: 'hierarchyName'
            });

            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/search/business/findByName', {
                multiple: false
            });

            businessSelect2.init($("#select-business").val());
            
            //用户禁用判断
            $(".disableTemplateId").on("click", function () {
            	var status = $(this).data("status");
            	var that = $(this);
            	var updateStatus ;
            	if(status == "NORMAL") {
            		updateStatus = "DISABLE";
            	} else {
            		updateStatus = "NORMAL";
            	}
            	 
            	var uid = $(this).data('uid');
            	if (uid != null && uid != "") {
            		user.ajax('/acl/user/disable', {id: uid,status: updateStatus}, function (data) {
            			that.data("status",updateStatus);
            			if(that.data("status") == "NORMAL") {
                            that.html("用户禁用");
            			} else {
            				that.html("用户启用");
            			}
            		})
            	}
            });

            $(".privilege-control").on("click", function () {
                var uid = $(this).data('uid');
                $(multiSelect.param.dom).data('uid', uid);
                multiSelect.init();
            });
        
            $(".role-control").on("click", function () {
                var uid = $(this).data('uid');
                $(multiSelect2.param.dom).data('uid', uid);
                multiSelect2.init();
            });
            
            $(".resign").on("click", function () {
                var uid = $(this).data("uid");
                $("#resign-submit").data('uid', uid);
            });

            $("#resign-submit").on("click", function () {
                var uid = $(this).data("uid");
                console.log(uid);
                if (uid != null && uid != "") {
                    user.ajax('/acl/user/resign', {id: uid}, function (data) {
                        console.log(data);
                        location.reload();
                    })
                }
            });

            $(".set-hierarchy").on("click", function () {
                var uid = $(this).data("uid");
                var hid = $(this).data("hid");
                $("#set-hierarchy-submit").data('uid', uid);
                hierarchy.init(hid);
            });

            $("#set-hierarchy-submit").on("click", function () {
                var uid = $(this).data("uid");
                var hierarchyId = $("#hierarchy-select2").val();
                if (hierarchyId != null && hierarchyId != "") {
                    user.ajax('/acl/user/hierarchy/set', {id: uid, hierarchyId: hierarchyId}, function (data) {
                        console.log(data);
                        location.reload();
                    })
                }
            });

            var groupSelect2 = new ajaxSelect2('#group-select2', "/acl/search/group", {});
            $(".set-group").on("click", function () {
                var uid = $(this).data("uid");
                user.ajax('/acl/search/group/loadByUserId', {userId: uid}, function (data) {
                    var group = data.data[0] || {name: '没有部门', id: undefined}
                    $("#oldGroup").val(group.name);
                    $("#oldGroup").data("groupId", group.id);
                })
                $("#group-select2").val('');
                $("#set-group-submit").data('uid', uid);
                groupSelect2.init();
            });

            $("#set-group-submit").on("click", function () {
                var uid = $(this).data("uid");
                var fromGroupId = $("#oldGroup").data("groupId");
                var toGroupId = $("#group-select2").val();
                if (toGroupId != undefined && toGroupId != "" ) {
                    user.ajax('/acl/user/group/changeGroup', {
                        userId: uid,
                        fromGroupId: fromGroupId,
                        toGroupId: toGroupId
                    }, function (data) {
                        console.log(data)
                    })
                }else{
                	alert("请选择转入部门！");
                }
            });

        }
    }
});
