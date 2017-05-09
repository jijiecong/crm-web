define(function (require, exports, module) {

    require('../ajaxSelect2');
    var Basic = require('../basic.js');
    var role = new Basic('acl/role');

    return {
        init: function () {
            var businessSelect2 = new ajaxSelect2('#select-business', '/acl/business/findByName', {
                multiple: false
            });

            var type="添加失败";
            var uid = $("#form-submit").data('uid');
            if(uid!=""&&uid!=undefined){
            	type="修改失败";
            	role.getById(uid);
            }else{
                businessSelect2.init($("#form-submit").data('bid'));
            }

            $("#close-form").on("click", function () {
            	window.location.href = '/acl/role/index';
            });
            
            $("#form-submit").on("click", function () {
            	if($('#riskLevel option:selected').val() == undefined || $('#riskLevel option:selected').val() == ""){
            		alert("请选择风险等级");
            		return;
            	}
            	role.submit2($(this).data('uid'),function(data){
		        	if(data.success){
		        		window.location.href = '/acl/role/index';
		        	}else{
		        		alert(type);
		        	}
		        });
            });
        }
    }
});
