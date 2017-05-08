define(function (require, exports, module) {
    /**
     * 简单列表页面基本方法
     *      1.表单提交
     *      2.多选删除,多选数据
     * @param name
     * @param param
     * @constructor
     */
    function Basic(name, param) {
        this.route = {
            add: "/" + name + "/add",             //添加
            modify: "/" + name + "/modify",    //修改
            find: "/" + name + "/find",    //查询
            del: "/" + name + "/delete",   //删除
            delBatch: "/" + name + "/deleteBatch"  //批量删除
        };
        this.param = $.extend({
            formDom: '#form',
            checkboxDom: '.list-checkbox',
            allCheckboxDom: '#all-checkbox'
        }, param);
        this.init();
        this.initCheckbox();
    }

    Basic.prototype = {
        initCheckbox: function () {
            var pThis = this;
            var checkbox = $(this.param.checkboxDom);
            var allCheckbox = $(this.param.allCheckboxDom);
            allCheckbox.on("click", function () {
                var isChecked = $(this).is(':checked');
                if (isChecked) {
                    checkbox.attr("checked", "checked");
                } else {
                    checkbox.removeAttr("checked");
                }
                $.uniform.update();
            });
            checkbox.on("click", function () {
                var isChecked = $(this).is(':checked');
                var isCheckedAll = allCheckbox.is(':checked');
                if (!isChecked && isCheckedAll) {
                    allCheckbox.removeAttr("checked");
                }
                $.uniform.update();
            });
        },
        init: function () {
            var pThis = this;
            ////打开弹出层进行设置
            //$(".add").on("click", function () {
            //    $(".operation-name").html('添加');
            //    $("#form input").val('');
            //    $("#modal-form-submit").data('uid', '');
            //});
            ////打开弹出层修改页面
            //$(".edit").on("click", function () {
            //    var uid = $(this).data('uid');
            //    pThis.getById(uid);
            //    $("#modal-form-submit").data('uid', uid);
            //    $(".operation-name").html('编辑');
            //});
            //
            //$("#modal-form-submit").on("click", function () {
            //    pThis.submit($(this).data('uid'));
            //});
            //
            //$(".del").on("click", function () {
            //    var uid = $(this).data('uid');
            //    $("#affirm-del").data('uid', uid);
            //});
            //
            //$("#affirm-del").on("click", function () {
            //    var formId = $(this).data('uid');
            //    pThis.del(formId);
            //});

            //$(".batch-del").on("click", function () {
            //});
            //
            //$("#batch-del-submit").on("click", function () {
            //    pThis.batchDel();
            //});
        },

        clearData: function () {
            $(this.param.formDom + " input:text").val('');
            $(this.param.formDom + " select").prop('selectedIndex', 0);
            $(this.param.formDom + " textarea").html('');
        },
        setData: function (data) {
            var pThis = this;
            if (data==null){
                return;
            }
            $.each(data, function (key, value) {
                var input = $(pThis.param.formDom + ' input[name=' + key + ']:text');
                if (input.length > 0) {
                    input.val(value);
                } else {
                    var select = $(pThis.param.formDom + ' select[name=' + key + ']');
                    if (select.length > 0) {
                        select.val(value);
                    }else{
                        var textarea = $(pThis.param.formDom + ' textarea[name=' + key + ']');
                        if (textarea.length > 0) {
                            textarea.html(value);
                        }
                    }
                }
            });
        },

        batchDel: function () {
            var ids = this.getChecked();
            if (ids.length > 0) {
                this._requestReload(this.route.delBatch, {ids: ids})
            }
        },

        submit: function (uid) {
            var url = uid == '' ? this.route.add : this.route.modify;
            var data = this.getData();
            if (uid != undefined && uid != '') {
                url += '?id=' + uid;
            }
            this._requestReload(url, data);
        },
        
        submit2: function (uid,callback) {
            var url = uid == '' ? this.route.add : this.route.modify;
            var data = this.getData();
            if (uid != undefined && uid != '') {
                url += '?id=' + uid;
            }
            this._request(url, data,callback);
        },

        /**
         * 删除
         */
        del: function (uid) {
            this._requestReload(this.route.del, {id: uid});
        },
        getChecked: function () {
            var checkData = [];
            var pThis = this;
            var checkedList = $(pThis.param.checkboxDom + ':checked');
            $.each(checkedList, function (key, val) {
                checkData.push($(val).val());
            });
            return checkData;
        },

        getData: function () {
            var data = {};
            var pThis = this;
            $.each($(this.param.formDom + ' input'), function (a, b) {
                var key = $(b).prop("name");
                if (key) {
                    data[key] = $(b).val();
                }
            });
            $.each($(this.param.formDom + ' select'), function (a, b) {
                var key = $(b).prop("name");
                if (key) {
                    data[key] = $('#'+key+' option:selected').val();
                }
            });
            return data;
        },

        getById: function (id, callback) {
            var pThis = this;
            this._request(this.route.find, {id: id}, function (result) {
                pThis.setData(result.data);
                if (callback && typeof callback == 'function') {
                    callback(result.data)
                }
            });
        },

        _request: function (url, data, callback) {
            var call = callback || function (data) {
                    console.log(data);
                };
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                success: function (result) {
                    if (result.success) {
                        call(result);
                    } else {
                        alert(result.error);
                    }
                },
                error: this._error
            });
        },

        _requestReload: function (url, data, callback) {
            var call = callback || function (data) {
                    console.log(data);
                };
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                success: function (result) {
                    if (result.success) {
                        call(result);
                        location.reload();
                    } else {
                        alert(result.error);
                    }
                },
                error: this._error
            });
        },

        _success: function (result) {
            if (result.success) {
                location.reload();
            } else {
                alert(result.error);
            }
        },

        _error: function (error) {
            var msg = JSON.parse(error.responseText).data.status.msg;
            msg = msg ? msg : '网络异常!';
            //oTips.init(msg);
            alert(msg);
        },

        ajax: function (url, data, callback) {
            this._request(url, data, callback)
        },

        ajaxAndReload: function (url, data, callback) {
            this._requestReload(url, data, callback)
        }

    };

    return Basic;
});
