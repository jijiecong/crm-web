/**
 * MultiSelect扩展
 * init请求:url +'/init'
 * add请求:url +'/add'
 * del请求:url +'/del'
 */
var ajaxMultiSelect = function (overrideParam) {
    var param = $.extend({
        isSearchable: true,
        dom: null,   //对应需要生成multiSelect的div的id
        selectdom: null,  //自动生成的multiSelect的ID
        url: null, //获取数据的接口
        idName: 'uid',
        dataIdName: 'dataId',
        selectedIdName: 'selectedId',
        selectableHeaderTitle: '未分配',
        selectionHeaderTitle: '已拥有'
    }, overrideParam);

    var rand = Math.random() * 10000;
    param.selectdom = '#select-' + Math.round(rand);
    this.param = param;
};
ajaxMultiSelect.prototype = {
    init: function () {
        this.setDisabled(true);
        $(this.param.dom).html(this.MakeSelectHtml());
        var data = {};
        var pThis = this;
        data[this.param.dataIdName] = $(this.param.dom).data(this.param.idName);
        console.log(data);
        this.ajax(this.param.url + '/init', data, function (rData) {
            var selectedIds = [];
            var selected = rData.data.selected;
            var selectData = rData.data.selectData;
            var html = '';
            selected.map(function (value, key) {
                selectedIds.push(value.id.toString());
            });
            selectData.map(function (value, key) {
                html += pThis.MakeOptionHtml(value);
            });
            $(pThis.param.selectdom).html(html);
            pThis.initMultiSelect(selectedIds);
            pThis.setDisabled(false);
        })
    },
    afterSelect: function (values) {
        this.post(values, 'add');
    },
    afterDeselect: function (values) {
        this.post(values, 'del');
    },
    initMultiSelect: function (selectedIds) {
        $(this.param.selectdom).multiSelect(this.initParam());
        $(this.param.selectdom).multiSelect('select', selectedIds);
    },
    setDisabled: function (bool) {
        this.disabled = bool;
    },
    initParam: function () {
        var isSearchable = this.param.isSearchable;
        var pThis = this;
        var param = {
            keepOrder: true,
            selectableHeader: "<div class='custom-header'>" + pThis.param.selectableHeaderTitle + "</div>",
            selectionHeader: "<div class='custom-header'>" + pThis.param.selectionHeaderTitle + "</div>",
            afterSelect: function (values) {
                pThis.afterSelect(values);
            },
            afterDeselect: function (values) {
                pThis.afterDeselect(values);
            }
        };
        if (isSearchable) {
            var searchable = {
                selectableHeader: "<input type='text' class='search-input' autocomplete='off' placeholder='" + pThis.param.selectableHeaderTitle + "'>",
                selectionHeader: "<input type='text' class='search-input' autocomplete='off' placeholder='" + pThis.param.selectionHeaderTitle + "'>",
                afterInit: function (ms) {
                    var that = this,
                        $selectableSearch = that.$selectableUl.prev(),
                        $selectionSearch = that.$selectionUl.prev(),
                        selectableSearchString = '#' + that.$container.attr('id') + ' .ms-elem-selectable:not(.ms-selected)',
                        selectionSearchString = '#' + that.$container.attr('id') + ' .ms-elem-selection.ms-selected';
                    that.qs1 = $selectableSearch.quicksearch(selectableSearchString)
                        .on('keydown', function (e) {
                            if (e.which === 40) {
                                that.$selectableUl.focus();
                                return false;
                            }
                        });

                    that.qs2 = $selectionSearch.quicksearch(selectionSearchString)
                        .on('keydown', function (e) {
                            if (e.which == 40) {
                                that.$selectionUl.focus();
                                return false;
                            }
                        });
                }
            };
            param = $.extend(param, searchable);
            param.afterSelect = function (values) {
                this.qs1.cache();
                this.qs2.cache();
                pThis.afterSelect(values);
            };
            param.afterDeselect = function (values) {
                this.qs1.cache();
                this.qs2.cache();
                pThis.afterDeselect(values);
            };
        }
        return param;
    },
    MakeSelectHtml: function () {
        var dom = this.param.selectdom.substr(1);
        return "<select id='" + dom + "' multiple='multiple'></select>";
    },
    MakeOptionHtml: function (data) {
        return '<option value="' + data.id + '">' + data.name + '</option>';
    },
    disabled: true,
    post: function (values, type) {
        var disabled = this.disabled;
        if (disabled) {
            return;
        }
        if (values == null) {
            return;
        }
        var data = {};
        data[this.param.selectedIdName] = values[0];
        data[this.param.idName] = $(this.param.dom).data(this.param.idName);
        this.ajax(this.param.url + '/' + type, data, function (data) {
            console.log(data.data);
        });
    },
    ajax: function (url, data, callback) {
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
    }
};

