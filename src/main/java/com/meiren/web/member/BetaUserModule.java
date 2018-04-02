package com.meiren.web.member;

import com.alibaba.fastjson.JSONObject;
import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.common.constants.VueConstants;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.member.enums.KidsRegisterTypeEnum;
import com.meiren.member.product.entity.*;
import com.meiren.member.product.service.LoginProductService;
import com.meiren.member.service.UserStatisticsService;
import com.meiren.message.entity.MessageSmsLogEO;
import com.meiren.message.service.MessageSmsAllManageService;
import com.meiren.message.vo.BootstrapTablePage;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.SessionUserVO;
import com.meiren.web.acl.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jijc
 * @ClassName: AppUserModule
 * @Description: ${todo}
 * @date 2017/10/26 18:45
 */
@Controller
@RequestMapping("{uuid}/member/betaUser")
@ResponseBody
public class BetaUserModule extends BaseController {

    @Resource LoginProductService loginProductService;
    @Resource AclPrivilegeService aclPrivilegeService;
    @Resource UserStatisticsService userStatisticsService;
    @Resource MessageSmsAllManageService messageSmsAllManageService;

    /**
     * 权限查询
     * @param request
     * @return
     */
    @RequestMapping("/getAuthByToken")
    public VueResult getAuthByToken(HttpServletRequest request) {
        SessionUserVO sessionUser = RequestUtil.getSessionUser(request);
        Long userId = sessionUser.getId();
        Boolean blackBoolean = aclPrivilegeService.hasPrivilege(userId, VueConstants.BLACKLIST_AUTH);
        Boolean removeBoolean = aclPrivilegeService.hasPrivilege(userId, VueConstants.MEMBER_REMOVE_AUTH);
        Map map = new HashMap();
        map.put("blackBoolean",blackBoolean);
        map.put("removeBoolean",removeBoolean);
        return new VueResult(map);
    }


   /* //添加马甲账号
    @RequestMapping("/addBetaMethod")
    public VueResult addBetaMethod(HttpServletRequest request){
        MemberBetaEO memberBetaEO = new MemberBetaEO();
        memberBetaEO.setMethodName(RequestUtil.getString(request, "methodName"));
        memberBetaEO.setMethodDescribe(RequestUtil.getString(request, "methodDescribe"));
        memberBetaEO.setParam(RequestUtil.getString(request, "param"));
        ApiResult apiResult = memberBetaService.insertMemberBeta(memberBetaEO);
        return new VueResult(apiResult);
    }

    //根据id删除用户
    @RequestMapping("/deleteBetaMethod")
    //@AuthorityToken(needToken = {VueConstants.MEMBER_REMOVE_AUTH})
    public VueResult deleteBetaMethod(HttpServletRequest request){
        Long id = RequestUtil.getLong(request, "id");
        ApiResult apiResult = memberBetaService.deleteMemberBeta(id);
        return new VueResult(apiResult);
    }

    //修改马甲账号
    @RequestMapping("/editBetaMethod")
    public VueResult editBetaMethod(HttpServletRequest request){
        MemberBetaEO memberBetaEO = new MemberBetaEO();
        memberBetaEO.setId(RequestUtil.getLong(request, "id"));
        memberBetaEO.setMethodName(RequestUtil.getString(request, "methodName"));
        memberBetaEO.setMethodDescribe(RequestUtil.getString(request, "methodDescribe"));
        memberBetaEO.setParam(RequestUtil.getString(request, "param"));
        ApiResult apiResult = memberBetaService.updateMemberBeta(memberBetaEO);
        return new VueResult(apiResult);
    }

    //查询马甲账号列表
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request){
        MemberBetaEO memberBetaEO = new MemberBetaEO();
        memberBetaEO.setMethodName(RequestUtil.getString(request, "keyword"));
        int pageSize = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        ApiResult apiResult = memberBetaService.getMemberBetaListByCondition(memberBetaEO,pageNum,pageSize);
        return new VueResult(apiResult);
    }*/

    //发送短信验证码
    @RequestMapping("/sendSmsVerifyCode")
    public VueResult sendSmsVerifyCode(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        MobileVerifyCodeParamsEO mobileVerifyCodeParamsEO = JSONObject.parseObject(param, MobileVerifyCodeParamsEO.class);
        System.out.println(JSONObject.toJSONString(mobileVerifyCodeParamsEO));
        ApiResult apiResult = loginProductService.sendSmsVerifyCode(mobileVerifyCodeParamsEO);
        return new VueResult(apiResult);
    }

    //获取短信验证码
    @RequestMapping("/getSmsVerifyCode")
    public VueResult getSmsVerifyCode(HttpServletRequest request){
        String phone = RequestUtil.getString(request, "phone");
        String zoneNum = RequestUtil.getString(request, "zoneNum");
        String check = zoneNum+"+"+phone;
        String type = RequestUtil.getString(request, "type");
        ApiResult result = this.messageSmsAllManageService.listMessageSmsLogForPage(new MessageSmsLogEO(), 0, 10);
        BootstrapTablePage data = (BootstrapTablePage) result.getData();
        List<MessageSmsLogEO> rows = data.getRows();
        for (MessageSmsLogEO eo : rows) {
            if(check.equals(eo.getPhone())){
                if(eo.getContent().contains(type)){
                    return new VueResult(getNumberByStr(eo.getContent()));
                }else if(eo.getContent().contains(type)){
                    return new VueResult(getNumberByStr(eo.getContent()));
                }else if(eo.getContent().contains(type)){
                    return new VueResult(getNumberByStr(eo.getContent()));
                }
            }
        }
        return new VueResult();
    }



    private String getNumberByStr(String str) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }




    //手机注册
    @RequestMapping("/doRegisterByPhone")
    public VueResult doRegisterByPhone(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        MobileRegisterParamsEO mobileRegisterParamsEO = JSONObject.parseObject(param, MobileRegisterParamsEO.class);
        System.out.println(JSONObject.toJSONString(mobileRegisterParamsEO));
        ApiResult apiResult = loginProductService.doRegisterByPhone(mobileRegisterParamsEO);
        return new VueResult(apiResult);
    }

    //填写注册信息
    @RequestMapping("/writeRegisterUserInfo")
    public VueResult writeRegisterUserInfo(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        WriteRegisterUserInfoEO eo = JSONObject.parseObject(param, WriteRegisterUserInfoEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.writeRegisterUserInfo(eo);
        return new VueResult(apiResult);
    }

    //手机登录
    @RequestMapping("/doLogin")
    public VueResult doLogin(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        LoginParamsEO loginParamsEO = JSONObject.parseObject(param, LoginParamsEO.class);
        ApiResult apiResult = loginProductService.doLogin(loginParamsEO);
        return new VueResult(apiResult);
    }

    //获取用户信息
    @RequestMapping("/getUserInfo")
    public VueResult getUserInfo(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        GetUserInfoEO getUserInfoEO = JSONObject.parseObject(param, GetUserInfoEO.class);
        ApiResult apiResult = loginProductService.getUserInfo(getUserInfoEO);
        return new VueResult(apiResult);
    }

    //更新用户信息
    @RequestMapping("/updateUserInfo")
    public VueResult updateUserInfo(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        UpdateUserInfoEO updateUserInfoEO = JSONObject.parseObject(param, UpdateUserInfoEO.class);
        System.out.println(JSONObject.toJSONString(updateUserInfoEO));
        ApiResult apiResult = loginProductService.updateUserInfo(updateUserInfoEO);
        return new VueResult(apiResult);
    }

    //忘记密码
    @RequestMapping("/forgetPassWord")
    public VueResult forgetPassWord(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        ForgetPasswordEO eo = JSONObject.parseObject(param, ForgetPasswordEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.forgetPassWord(eo);
        return new VueResult(apiResult);
    }

    //修改密码
    @RequestMapping("/changePassword")
    public VueResult changePassword(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        ChangePasswordEO eo = JSONObject.parseObject(param, ChangePasswordEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.changePassword(eo);
        return new VueResult(apiResult);
    }

    //绑定手机号
    @RequestMapping("/bindPhone")
    public VueResult bindPhone(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        BindPhoneEO eo = JSONObject.parseObject(param, BindPhoneEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.bindPhone(eo);
        return new VueResult(apiResult);
    }

    //第三方登录
    @RequestMapping("/partnerDoLogin")
    public VueResult partnerDoLogin(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        PartnerLoginParamsEO eo = JSONObject.parseObject(param, PartnerLoginParamsEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.partnerDoLogin(eo);
        return new VueResult(apiResult);
    }

    //刷新token
    @RequestMapping("/refreshToken")
    public VueResult refreshToken(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        RefreshTokenEO eo = JSONObject.parseObject(param, RefreshTokenEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.refreshToken(eo);
        return new VueResult(apiResult);
    }

    //校验用户是否存在
    @RequestMapping("/checkLoginNameExists")
    public VueResult checkLoginNameExists(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        CheckLoginNameEO eo = JSONObject.parseObject(param, CheckLoginNameEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.checkLoginNameExists(eo);
        return new VueResult(apiResult);
    }

    //密保注册
    @RequestMapping("/doRegisterByQuestion")
    public VueResult doRegisterByQuestion(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        String kidsRegisterTypeEnum = RequestUtil.getString(request, "kidsRegisterType");
        MobileRegisterParamsEO eo = JSONObject.parseObject(param, MobileRegisterParamsEO.class);
        for (KidsRegisterTypeEnum registerTypeEnum : KidsRegisterTypeEnum.values()) {
            if(registerTypeEnum.getMessage().equals(kidsRegisterTypeEnum)){
                MobileRegisterAccessParamsEO eoParam = eo.getParam();
                eoParam.setKidsRegisterTypeEnum(registerTypeEnum);
                eo.setParam(eoParam);
                break;
            }
        }
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.doRegisterByQuestion(eo);
        return new VueResult(apiResult);
    }

    //用户密保登录
    @RequestMapping("/doLoginByQuestion")
    public VueResult doLoginByQuestion(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        QuestionLoginParamsEO eo = JSONObject.parseObject(param, QuestionLoginParamsEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.doLoginByQuestion(eo);
        return new VueResult(apiResult);
    }

    //更新用户用于登录的手机号-简客
    @RequestMapping("/changeBindPhone")
    public VueResult changeBindPhone(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        ChangeBindPhoneEO eo = JSONObject.parseObject(param, ChangeBindPhoneEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.changeBindPhone(eo);
        return new VueResult(apiResult);
    }

    //校验手机验证码
    @RequestMapping("/checkSmsVerifyCode")
    public VueResult checkSmsVerifyCode(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        MobileVerifyCodeParamsEO eo = JSONObject.parseObject(param, MobileVerifyCodeParamsEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.checkSmsVerifyCode(eo);
        return new VueResult(apiResult);
    }

    //检测第三方账号
    @RequestMapping("/detectionPartner")
    public VueResult detectionPartner(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        DetectionPartnerEO eo = JSONObject.parseObject(param, DetectionPartnerEO.class);
        System.out.println(JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.detectionPartner(eo);
        return new VueResult(apiResult);
    }



   /* //获取app名称
    @RequestMapping("/getAppName")
    public VueResult getAppName(HttpServletRequest request){
        ApiResult apiResult = userStatisticsService.getAllAppName();
        List<String> result = (List<String>) apiResult.getData();
        System.out.println(JSONObject.toJSONString(result));
        return new VueResult(result);
    }
*/



}
