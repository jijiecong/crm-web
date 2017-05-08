define(function (require, exports, module) {	   
    require('../ajaxMultiSelect');
    /*var Basic = require('../basic.js');
    var role = new Basic('acl/role');
   */
    return {
        init: function () {  
            //查询角色权限start
            var selectPrivilege = new ajaxMultiSelect({
                dom: '#select-privilege',
                url: '/acl/role/setPrivilege',
                selectableHeaderTitle: '未拥有权限',
                selectionHeaderTitle: '已拥有权限'
            });
            selectPrivilege.init();
            
            $("#close-form").on("click", function () {
            	window.location.href = '/acl/role/index';
            });
            
            
        }
    }
    
    
});
