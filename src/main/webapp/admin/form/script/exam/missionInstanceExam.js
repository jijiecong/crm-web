var missionInstanceExam = {

    //初始化
    init: function () {
        this.renderForm();
    },

    renderForm: function () {
        var formObj = window.parent.loadFormStructure();
        if (formObj == null) {
            return;
        }
        // var templateForm=   "<tr class=\"u_{{missionInstanceId}}\"> <td>{{missionInstanceId}}</td> <td>{{status}}</td> <td>{{missionName}}</td> <td>{{isAuto}}</td> <td>{{singleInvokeCount}}</td> <td>{{finishCount}}</td> <td>{{missionId}}</td> <td>{{missionParentId}}</td> </tr>";
        var templateForm=$("#drag_choice").html();
        var display=null;
        if(formObj.status=="WAITING" || formObj.status=="FAIL"){
            display="display: in-block";
        } else {
            display="display: none";
        }
        //加载出对应的表单编号
        // var identityType=formObj.identityType;
        // var content = jQuery.parseJSON(formObj.content);
        // var formId=null;
        // $.each(content,function(key,value){
        //     var ruleTypeEnum=value.ruleTypeEnum;
        //     if(ruleTypeEnum=="SPONSORAWARD" && identityType=="SPONSOR"){
        //
        //     }
        //     if(ruleTypeEnum=="PARTICIPATORAWARD" && identityType=="PARTICIPATOR"){
        //
        //
        //     }
        // });

        var data = {
            missionInstanceId: formObj.missionInstanceId,
            status: formObj.status,
            missionName: formObj.missionName,
            missionId: formObj.missionId,
            missionType: formObj.missionType,
            display:display
        };

        $('#tableBody').append(Mustache.to_html(templateForm, data))
        // $('#tableBody').append(template("drag_choice", data));
    }


}
