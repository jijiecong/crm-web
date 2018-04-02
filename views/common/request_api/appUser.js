/**
 *
 * @Date: 2017/3/24 16:46
 *
 */

//数据列表
exports.list = "/member/appUser/list"
//折线统计注册用户
exports.registerStatistics = "/member/appUser/registerStatistics"
//饼状统计注册用户
exports.registerToPieStatistics = "/member/appUser/registerToPieStatistics"
//饼状根据projectName统计注册用户
exports.registerByProjectNameStatistics = "/member/appUser/registerByProjectNameStatistics"
//获取所有projects
exports.getProjects = "/member/appUser/getProjects"
//根据id添加或取消黑名单
exports.createBlackListUserById = "/member/appUser/createBlackListUserById"
//查询黑名单列表
exports.blackList = "/member/appUser/blackList"
//根据id删除用户
exports.deleteUserById = "/member/appUser/deleteUserById"
//批量删除用户
exports.deleteUserByIdsBatch = "/member/appUser/deleteUserByIdsBatch"
//批量拉黑或解黑用户
exports.createBlackListBatch = "/member/appUser/createBlackListBatch"
//导出数据到表格
exports.exportLine = "/member/appUser/exportLine"
//黑名单权限用户
exports.getAuthByToken = "/member/appUser/getAuthByToken"

//导入数据到表格
exports.importExcel = "/member/appUser/importExcel"
//马甲账号
exports.addWaistcoatUser = "/member/appUser/addWaistcoatUser"
exports.delWaistcoatUser = "/member/appUser/delWaistcoatUser"
exports.editWaistcoatUser = "/member/appUser/editWaistcoatUser"
exports.waistcoatList = "/member/appUser/waistcoatList"
exports.addWaistcoatUserBatch = "/member/appUser/addWaistcoatUserBatch"
exports.getWaistcoatUser = "/member/appUser/getWaistcoatUser"
//测试账号
exports.sendSmsVerifyCode = "/member/betaUser/sendSmsVerifyCode"
exports.getSmsVerifyCode = "/member/betaUser/getSmsVerifyCode"
exports.doRegisterByPhone = "/member/betaUser/doRegisterByPhone"
exports.writeRegisterUserInfo = "/member/betaUser/writeRegisterUserInfo"
exports.doLogin = "/member/betaUser/doLogin"
exports.getUserInfo = "/member/betaUser/getUserInfo"
exports.updateUserInfo = "/member/betaUser/updateUserInfo"
exports.changePassword = "/member/betaUser/changePassword"
exports.forgetPassWord = "/member/betaUser/forgetPassWord"
exports.bindPhone = "/member/betaUser/bindPhone"
exports.partnerDoLogin = "/member/betaUser/partnerDoLogin"
exports.refreshToken = "/member/betaUser/refreshToken"
exports.checkLoginNameExists = "/member/betaUser/checkLoginNameExists"
exports.doRegisterByQuestion = "/member/betaUser/doRegisterByQuestion"
exports.doLoginByQuestion = "/member/betaUser/doLoginByQuestion"
exports.changeBindPhone = "/member/betaUser/changeBindPhone"
exports.checkSmsVerifyCode = "/member/betaUser/checkSmsVerifyCode"
exports.detectionPartner = "/member/betaUser/detectionPartner"
//获取所有appName
exports.getAppName = "/member/betaUser/getAppName"


