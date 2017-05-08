/**
 * support
 * 新建表单基础信息
 */
define(function(require, exports, module) {

    var showDetail= function(ratio){
        //根据和窗口的比例来设置详情页面的大小
        var document_width=$(document).width();
        // alert("document_width="+document_width)
        var document_height=$(document).height();
        // alert("document_height="+document_height)

        var detail_width=ratio*document_width;
        var detail_height=ratio*document_height;

        //定位div
        var deatail_left=(document_width-detail_width)/2;
        var detail_top=(document_height-detail_height)/2;
        //allowTransparency='true' 设置背景透明 false的时候也是透明的。。。。
        $("<div width='" + detail_width + "' height='" + detail_height + "' id='account_detail_div' name='account_detail-div' style='position:fixed;z-index:4;background-color: #f4f4f4;margin-left: auto;margin-top: auto'  frameborder='no' marginheight='0' marginwidth='0' allowTransparency='false'></div>").prependTo('body');

        //将div的定位参照页面定位
        $("#account_detail_div").css({
            "left":deatail_left,
            "top":detail_top
        });

        //添加背景遮罩
        $("<div id='account_detail_divBg' style='background-color: slategrey;display:block;z-index:3;position:absolute;left:0px;top:0px;filter:Alpha(Opacity=30);/* IE */-moz-opacity:0.4;/* Moz + FF */opacity: 0.4; '/>").prependTo('body');
        var bgWidth = $("body").width();
        var bgHeight =$("body").height();
        $("#account_detail_divBg").css({width:bgWidth,height:bgHeight});
        $("#account_detail_divBg").click(function() {
            $("#account_detail_div").remove();
            $("#account_detail_divBg").remove();
        });

    }

    return {

        init: function() {
            //详情页面和屏幕的比例
            var ratio=0.6;

            //打开详情页面
            $(".account_detail").on("click", function(e) {

                var trObj = $(this).parent().parent().parent();
                var account_id = trObj.find('.account_id').html();
                var account_name = trObj.find('.account_name').html();
                var account_IDnum = trObj.find('.account_IDnum').html();
                var account_status = trObj.find('.account_status').html();
                var account_remark = trObj.find('.account_remark').html();
                var account_gmt_create = trObj.find('.account_gmt_create').html();
                var account_gmt_modify = trObj.find('.account_gmt_modify').html();
                var account_img1 = trObj.find('.account_img1').html();
                var account_img2 = trObj.find('.account_img2').html();

                var data = {
                    account_id: account_id,
                    account_name: account_name,
                    account_IDnum: account_IDnum,
                    account_status: account_status,
                    account_remark: account_remark,
                    account_gmt_create: account_gmt_create,
                    account_gmt_modify: account_gmt_modify,
                    account_img1: account_img1,
                    account_img2: account_img2
                };

                //详情页面
                var template=$('#show_account_detail').html();
                showDetail(ratio);
                $("#account_detail_div").append(Mustache.to_html(template, data));
            });

            $(".approve_account").on("click", function(e) {
                var trObj = $(this).parent().parent().parent();
                var account_id = trObj.find('.account_id').html();
                var account_name = trObj.find('.account_name').html();
                var account_IDnum = trObj.find('.account_IDnum').html();
                var account_status = trObj.find('.account_status').html();
                var account_remark = trObj.find('.account_remark').html();
                var account_gmt_create = trObj.find('.account_gmt_create').html();
                var account_gmt_modify = trObj.find('.account_gmt_modify').html();
                var account_img1 = trObj.find('.account_img1').html();
                var account_img2 = trObj.find('.account_img2').html();

                var data = {
                    account_id: account_id,
                    account_name: account_name,
                    account_IDnum: account_IDnum,
                    account_status: account_status,
                    account_remark: account_remark,
                    account_gmt_create: account_gmt_create,
                    account_gmt_modify: account_gmt_modify,
                    account_img1: account_img1,
                    account_img2: account_img2
                };
                //打开详情页面
                var show_account_detail=$('#show_account_detail').html();

                //审核页面
                var approve_account=$('#approve_account').html();
                // 调用这个模板方法，自己定义一
                showDetail(ratio);
                $("#account_detail_div").append(Mustache.to_html(show_account_detail, data));

                $("#account_detail_div").append(approve_account);

            });

        }
    }

});
