package com.meiren.web.member;

import com.alibaba.fastjson.JSONObject;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.member.enums.KidsRegisterTypeEnum;
import com.meiren.member.product.entity.*;
import com.meiren.member.product.service.LoginProductService;
import com.meiren.message.entity.MessageSmsLogEO;
import com.meiren.message.service.MessageSmsAllManageService;
import com.meiren.message.vo.BootstrapTablePage;
import com.meiren.utils.RequestUtil;
import com.meiren.utils.StringUtils;
import com.meiren.web.acl.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    @Resource MessageSmsAllManageService messageSmsAllManageService;
    private static final Logger logger = LoggerFactory.getLogger(BetaUserModule.class);

    //发送短信验证码
    @RequestMapping("/sendSmsVerifyCode")
    public VueResult sendSmsVerifyCode(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        MobileVerifyCodeParamsEO mobileVerifyCodeParamsEO = JSONObject.parseObject(param, MobileVerifyCodeParamsEO.class);
        logger.info("method:sendSmsVerifyCode, param="+JSONObject.toJSONString(mobileVerifyCodeParamsEO));
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
        logger.info("method:getSmsVerifyCode, param="+"{check="+check+"type"+type+"}");
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
        MobileRegisterParamsEO eo = JSONObject.parseObject(param, MobileRegisterParamsEO.class);
        String pwd = eo.getParam().getPwd();
        if(StringUtils.isNotBlank(pwd)){
            eo.getParam().setPwd(StringUtils.md5(pwd));
        }
        logger.info("method:doRegisterByPhone, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.doRegisterByPhone(eo);
        return new VueResult(apiResult);
    }


    //填写注册信息
    @RequestMapping("/writeRegisterUserInfo")
    public VueResult writeRegisterUserInfo(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        WriteRegisterUserInfoEO eo = JSONObject.parseObject(param, WriteRegisterUserInfoEO.class);
        String pwd = eo.getParam().getPwd();
        if(StringUtils.isNotBlank(pwd)){
            eo.getParam().setPwd(StringUtils.md5(pwd));
        }
        logger.info("method:writeRegisterUserInfo, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.writeRegisterUserInfo(eo);
        return new VueResult(apiResult);
    }

    //手机登录
    @RequestMapping("/doLogin")
    public VueResult doLogin(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        LoginParamsEO eo = JSONObject.parseObject(param, LoginParamsEO.class);
        String pwd = eo.getParam().getPassword();
        if(StringUtils.isNotBlank(pwd)){
            eo.getParam().setPassword(StringUtils.md5(pwd));
        }
        logger.info("method:doLogin, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.doLogin(eo);
        return new VueResult(apiResult);
    }

    //获取用户信息
    @RequestMapping("/getUserInfo")
    public VueResult getUserInfo(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        GetUserInfoEO eo = JSONObject.parseObject(param, GetUserInfoEO.class);
        logger.info("method:getUserInfo, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.getUserInfo(eo);
        return new VueResult(apiResult);
    }

    //更新用户信息
    @RequestMapping("/updateUserInfo")
    public VueResult updateUserInfo(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        UpdateUserInfoEO eo = JSONObject.parseObject(param, UpdateUserInfoEO.class);
        logger.info("method:updateUserInfo, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.updateUserInfo(eo);
        return new VueResult(apiResult);
    }

    //忘记密码
    @RequestMapping("/forgetPassWord")
    public VueResult forgetPassWord(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        ForgetPasswordEO eo = JSONObject.parseObject(param, ForgetPasswordEO.class);
        String pwd = eo.getParam().getPassword();
        if(StringUtils.isNotBlank(pwd)){
            eo.getParam().setPassword(StringUtils.md5(pwd));
        }
        logger.info("method:forgetPassWord, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.forgetPassWord(eo);
        return new VueResult(apiResult);
    }

    //修改密码
    @RequestMapping("/changePassword")
    public VueResult changePassword(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        ChangePasswordEO eo = JSONObject.parseObject(param, ChangePasswordEO.class);
        String newPwd = eo.getParam().getNewPwd();
        String oldPwd = eo.getParam().getOldPwd();
        if(StringUtils.isNotBlank(newPwd)){
            eo.getParam().setNewPwd(StringUtils.md5(newPwd));
        }
        if(StringUtils.isNotBlank(oldPwd)){
            eo.getParam().setNewPwd(StringUtils.md5(oldPwd));
        }
        logger.info("method:changePassword, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.changePassword(eo);
        return new VueResult(apiResult);
    }

    //绑定手机号
    @RequestMapping("/bindPhone")
    public VueResult bindPhone(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        BindPhoneEO eo = JSONObject.parseObject(param, BindPhoneEO.class);
        ApiResult apiResult = loginProductService.bindPhone(eo);
        logger.info("method:bindPhone, param="+JSONObject.toJSONString(eo));
        return new VueResult(apiResult);
    }

    //第三方登录
    @RequestMapping("/partnerDoLogin")
    public VueResult partnerDoLogin(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        PartnerLoginParamsEO eo = JSONObject.parseObject(param, PartnerLoginParamsEO.class);
        logger.info("method:partnerDoLogin, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.partnerDoLogin(eo);
        return new VueResult(apiResult);
    }

    //刷新token
    @RequestMapping("/refreshToken")
    public VueResult refreshToken(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        RefreshTokenEO eo = JSONObject.parseObject(param, RefreshTokenEO.class);
        logger.info("method:refreshToken, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.refreshToken(eo);
        return new VueResult(apiResult);
    }

    //校验用户是否存在
    @RequestMapping("/checkLoginNameExists")
    public VueResult checkLoginNameExists(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        CheckLoginNameEO eo = JSONObject.parseObject(param, CheckLoginNameEO.class);
        logger.info("method:checkLoginNameExists, param="+JSONObject.toJSONString(eo));
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
        String pwd = eo.getParam().getPwd();
        if(StringUtils.isNotBlank(pwd)){
            eo.getParam().setPwd(StringUtils.md5(pwd));
        }
        logger.info("method:doRegisterByQuestion, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.doRegisterByQuestion(eo);
        return new VueResult(apiResult);
    }

    //用户密保登录
    @RequestMapping("/doLoginByQuestion")
    public VueResult doLoginByQuestion(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        QuestionLoginParamsEO eo = JSONObject.parseObject(param, QuestionLoginParamsEO.class);
        logger.info("method:doLoginByQuestion, param="+JSONObject.toJSONString(eo));
        ApiResult apiResult = loginProductService.doLoginByQuestion(eo);
        return new VueResult(apiResult);
    }

    //更新用户用于登录的手机号-简客
    @RequestMapping("/changeBindPhone")
    public VueResult changeBindPhone(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        ChangeBindPhoneEO eo = JSONObject.parseObject(param, ChangeBindPhoneEO.class);
        ApiResult apiResult = loginProductService.changeBindPhone(eo);
        logger.info("method:changeBindPhone, param="+JSONObject.toJSONString(eo));
        return new VueResult(apiResult);
    }

    //校验手机验证码
    @RequestMapping("/checkSmsVerifyCode")
    public VueResult checkSmsVerifyCode(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        MobileVerifyCodeParamsEO eo = JSONObject.parseObject(param, MobileVerifyCodeParamsEO.class);
        ApiResult apiResult = loginProductService.checkSmsVerifyCode(eo);
        logger.info("method:checkSmsVerifyCode, param="+JSONObject.toJSONString(eo));
        return new VueResult(apiResult);
    }

    //检测第三方账号
    @RequestMapping("/detectionPartner")
    public VueResult detectionPartner(HttpServletRequest request){
        String param = RequestUtil.getString(request, "param");
        DetectionPartnerEO eo = JSONObject.parseObject(param, DetectionPartnerEO.class);
        ApiResult apiResult = loginProductService.detectionPartner(eo);
        logger.info("method:detectionPartner, param="+JSONObject.toJSONString(eo));
        return new VueResult(apiResult);
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
