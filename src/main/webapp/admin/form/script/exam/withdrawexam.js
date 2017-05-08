var withdrawExam={

	//初始化
	init:function(){

		this.renderForm();


	},


	initTypeMap:function(){
		var typeMap = {};     // Map map = new HashMap();
		typeMap["RUNNING"] = "待审核"; // map.put(key, value);
		typeMap["SUCCESS"] = "审批通过";
		typeMap["FAILED"] = "审批拒绝";
		return typeMap;
	},


    initWithDrawMap:function(){
        var typeMap = {};     // Map map = new HashMap();
        typeMap["ALIPAY"] = "支付宝"; // map.put(key, value);
        typeMap["WEICAHTPAY"] = "微信";
        typeMap["TENAPY"] = "财付通";
        return typeMap;
    },

	renderForm:function(){
		var _this=this;
		var typeMap = _this.initTypeMap();
        var withDrawMap = _this.initWithDrawMap();
	    var formObj = window.parent.loadFormStructure();
        if(formObj==null){
       	   return;
        }

	   	var fieldArray = formObj;
		$("#textarea_withdraw_id").val(fieldArray.id);
		$("#textarea_withdraw_user_id").val(fieldArray.userId);
		$("#textarea_withdraw_user_name").val(fieldArray.userName);
		$("#textarea_withdraw_phone").val(fieldArray.phone);

        $("#textarea_withdraw_money").val(fieldArray.withdrawMoney);
        var status=typeMap[fieldArray.status];
        $("#textarea_withdraw_status").val(status);
        $("#textarea_withdraw_apply_time").val(moment(fieldArray.applyTime).format('YYYY-MM-DD HH:mm:ss'));
        var withdrawType=withDrawMap[fieldArray.withdrawType];
        $("#textarea_withdraw_type").val(withdrawType);

        //提现姓名
        $("#textarea_withdraw_withdraw_user_name").val(fieldArray.withdrawUserName);
        //提现账号
        $("#textarea_withdraw_account").val(fieldArray.withdrawAccount);
        //所属渠道
        $("#textarea_withdraw_channel_name").val(fieldArray.channelName);

        //审核人
        $("#textarea_withdraw_audit_user_name").val(fieldArray.auditUserName);
        //审核描述
        $("#textarea_withdraw_audit_desc").val(fieldArray.auditDesc);
		//审核时间
        if(fieldArray.auditTime!=null) {
            $("#textarea_withdraw_audit_time").val(moment(fieldArray.auditTime).format('YYYY-MM-DD HH:mm:ss'));
        }
	}
}
