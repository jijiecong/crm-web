/**
 *
 * @Date: 2017/3/24 16:46
 *
 */

//数据列表
exports.list = "/acl/privilege/list"
//根据id查询数据
exports.find = "/acl/privilege/find"
//根据id删除数据
exports.del = "/acl/privilege/del"
//添加或修改数据
exports.save = "/acl/privilege/save"
//批量删除
exports.batch_del = "/acl/privilege/batch/del"
//权限审核流程
exports.setProcess = "/acl/privilege/process"
//设置owner
exports.setOwner = "/acl/privilege/setOwner"
//查询权限
exports.search = "/acl/search/privilege"
//设置owner
exports.getOwner = "/acl/privilege/getOwner"
//关联用户
exports.getJoinUserList = "/acl/privilege/getJoinUserList"
//删除权限的关联用户
exports.delUserHasPrivilege = "/acl/privilege/delUserHasPrivilege"
//关联角色
exports.getJoinRoleList = "/acl/privilege/getJoinRoleList"
//删除权限的关联角色
exports.delRoleHasPrivilege = "/acl/privilege/delRoleHasPrivilege"
//权限被申请中记录
exports.getJoinApplyList = "/acl/privilege/getJoinApplyList"
