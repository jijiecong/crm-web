var missionTraceExam={

	//初始化
	init:function(){

		this.renderForm();


	},

    awardMapEnum:function () {
        var awardMap={};
        awardMap[1]="优惠券";
        awardMap[2]="积分";
        awardMap[3]="现金";
        return awardMap;
    },

	renderForm:function(){
		var _this=this;
	    var formObj = window.parent.loadFormStructure();
        if(formObj==null){
       	   return;
        }
	   	var fieldArray = formObj;

		$("#textarea_trace_id").val(fieldArray.id);
		$("#textarea_trace_user_id").val(fieldArray.receiverId);
		$("#textarea_trace_user_name").val(fieldArray.receiverName);

		//奖励渲染
        var actionList=JSON.parse(fieldArray.actionDesc);
        var awardMap=_this.awardMapEnum();
        $.each(actionList,function (index,action) {

            var awardType=action.awardType;
            var amount=action.amount;
            var couponCode=action.couponCode;
            var data={
                awardType:awardType,
                awardName:awardMap[awardType],
                amount:amount,
                couponCode:couponCode
            }
            $('.ui-questions-content-list').append(template("award_choice",data));
        })


	}
}
