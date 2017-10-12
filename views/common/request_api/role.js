/**
 *
 * @Date: 2017/3/24 16:46
 *
 */

//数据列表
exports.list = "/acl/role/list"
//根据id查询数据
exports.find = "/acl/role/find"
//根据id删除数据
exports.del = "/acl/role/del"
//添加或修改数据
exports.save = "/acl/role/save"
//批量删除
exports.batch_del = "/acl/role/batch/del"
//角色审核流程
exports.setProcess = "/acl/role/process"
//设置owner
exports.setOwner = "/acl/role/setOwner"
//设置角色拥有权限
exports.setRoleHasPrivilege = "/acl/role/setRoleHasPrivilege"
//角色查询select
exports.search = "/acl/search/role"
//角色查询下拉
exports.applyRole = "/acl/search/applyRole"
//角色视图权限
exports.getRoleViewPrivilege = "/acl/role/getRoleViewPrivilege"
//关联用户
exports.getJoinUserList = "/acl/role/getJoinUserList"
//删除角色的关联用户
exports.delUserHasRole = "/acl/role/delUserHasRole"
//关联部门
exports.getJoinGroupList = "/acl/role/getJoinGroupList"
//删除角色的关联部门
exports.delGroupHasRole = "/acl/role/delGroupHasRole"
//角色被申请中记录
exports.getJoinApplyList = "/acl/role/getJoinApplyList"
