/**
 * support
 * 新建动态表单布局
 */
define(function (require, exports, module) {
    var $ = require('jquery');
    require('js/component/plugin.ajaxform')($);
    require('collection');
    var notify= require('js/component/notify2.js')

    var errorMap = {};     // Map map = new HashMap();

    errorMap[104] = "渠道编号为空";
    errorMap[115] = "渠道名已经存在";
    errorMap[119] = "任务类型Key重复";
    errorMap[147] = "渠道不存在";
    errorMap[165] = "获取阿里云密钥失败";
    errorMap[168] = "系统繁忙";
    errorMap[205] = "阿里云上传失败";
    errorMap[207] = "图片不存在";
    errorMap[208] = "图片大小超过规定";
    errorMap[209] = "图片长或宽超过规定";
    errorMap[210] = "H5模板不存在";
    errorMap[211] = "H5模板名称不能为空";
    errorMap[212] = "H5模板内容不能为空";
    errorMap[213] = "H5模板名称已经存在";
    errorMap[214] = "H5模板编号不为空";
    errorMap[215] = "任务类型不存在";
    errorMap[216] = "任务类型Key为空";
    errorMap[217] = "任务类型名称为空";
    errorMap[218] = "H5模板正在被使用";
    errorMap[221] = "任务渠道正在被使用，不能删除";
    errorMap[222] = "任务类型正在被使用，不能删除";
    errorMap[223] = "任务条件正在被使用，不能删除";
    errorMap[229] = "该记录的奖励已经发放完毕";
    errorMap[101] = "返回结果为空";
    errorMap[500] = "H5标题不能为空";
    errorMap[499] = "请填写页面元素";


    return {

        // alertError: function (errorCode) {
        //
        //     if(errorMap[errorCode]!=null){
        //         notify.Notify(errorMap[errorCode]);
        //     } else {
        //         notify.Notify("系统异常");
        //     }
        //
        //
        // }
        getErrorMap:function () {
            return errorMap;
        }

    }

});
