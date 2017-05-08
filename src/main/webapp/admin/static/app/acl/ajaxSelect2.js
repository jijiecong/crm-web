//防止 搜索框无法选中
$.fn.modal.Constructor.prototype.enforceFocus = function () {
};
function isArray(arr) {
    return arr instanceof Array;
}
/**
 * 初始化接口 :url + '/init?id=**'
 * 搜索接口接口 :url + '/query?q=**'
 * @param dom
 * @param url
 * @param overrideParam
 */
var ajaxSelect2 = function (dom, url, overrideParam) {
    var pThis = this;
    this.data = $.extend({
        textName: 'name',
        baseUrl: url,
        queryUrl: url + '/query',
        initUrl: url + '/init',
        multiple: false
    }, overrideParam);

    var param = $.extend({
        placeholder: "请输入",
        minimumInputLength: 1,
        multiple: false,
        separator: ",",                  // 分隔符
        maximumSelectionSize: 200,       // 限制数量
        // 初始化时设置默认值
//        initSelection: function (element, callback) {
//            pThis.initData($(element).val());
//            //console.log(1);
//            //var id = $(element).val();
//            //if (id !== "") {
//            //    $.ajax(pThis.data.initUrl + '?id=' + id, {
//            //        dataType: "json"
//            //    }).done(function (data) {
//            //        var list = pThis.formatData(data.data, element);
//            //        callback(list);
//            //    });
//            //}
//        },
        // 选择结果中的显示
        formatSelection: function (item) {
            return item.name;
        },
        // 搜索列表中的显示
        formatResult: function (item) {
            return item.name;
        },
        ajax: {
            url: pThis.data.queryUrl,
            dataType: "json",
            data: function (term, page) {  // 请求参数（GET）
                return {q: term};
            },
            // 构造返回结果
            results: function (data, page) {
                var list = pThis.formatData(data.data);
                return {results: list};
            },
            // 字符转义处理
            escapeMarkup: function (m) {
                return m;
            }
        },
        formatNoMatches: function () {
            return "没有找到结果"
        },
        formatInputTooShort: function (a, b) {
            var c = b - a.length;
            return "请在输入 " + c + " 个字符";
        },
        formatInputTooLong: function (a, b) {
            var c = a.length - b;
            return "输入字符过长";
        },
        formatSelectionTooBig: function (a) {
            return "只能选择 " + a + "个";
        },
        formatLoadMore: function () {
            return "加载更多"
        },
        formatSearching: function () {
            return "搜索中..."
        }
    }, overrideParam);

    this.dom = 'input' + dom;
    this.param = param;
};

ajaxSelect2.prototype = {
    enable: true,
    initData: function (initId,dom) {
        var pThis = this;
        if (initId !== "") {
            $.ajax(pThis.data.initUrl + '?id=' + initId, {
                dataType: "json"
            }).done(function (data) {
                var list = pThis.formatData(data.data);
                $(dom).select2("data", list);
            });
        }
    },
    formatData: function (data) {
        var pThis = this;
        if (data == null) {
            return [];
        }
        var list = [];
        if (isArray(data)) {
            $.each(data, function (key, val) {
                list.push({
                    id: val.id,
                    name: val[pThis.data.textName]
                })
            });
        } else {
            list = {
                id: data.id,
                name: data[pThis.data.textName]
            }
        }
        return list;
    },
    clear: function () {
        $(this.dom).select2('data', '');
        $(this.dom).select2('val', '');
    },
    init: function (data) {
        var pThis = this;
        $(this.dom).select2("destroy");
        $(this.dom).select2(this.param);
        $.each($(this.dom), function (key, val) {
            if (data != undefined) {
                pThis.initData(data,val);
            } else if ($(val).val() != "") {
                pThis.initData($(val).val(),val);
            }
        });
        var callback = this.changeFunc;
        if (callback && typeof callback == 'function') {
            $(this.dom).on("change", function (e) {
                callback(e);
            });
        }
    },
    setData: function (data) {
        $(this.dom).select2("data", data);
    },
    getVal: function () {
        return $(this.dom).select2("val")
    },
    getData: function () {
        return $(this.dom).select2("data");
    },
    disabled: function (bool) {
        $(this.dom).prop("disabled", !bool);
    },
    setChangeFunc: function (callback) {
        this.changeFunc = callback;
    }
};
