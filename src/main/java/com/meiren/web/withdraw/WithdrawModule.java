package com.meiren.web.withdraw;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.constant.OssConstant;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.DateUtils;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.mission.enums.ParamTypeEnum;
import com.meiren.mission.result.ParamResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.result.WalletOrderResult;
import com.meiren.mission.service.MissionParamService;
import com.meiren.mission.service.ProducerLogService;
import com.meiren.mission.service.WalletOrderService;
import com.meiren.mission.service.entity.MissionLogEntity;
import com.meiren.mission.service.vo.*;
import com.meiren.oss.StsInfo;
import com.meiren.oss.sts.OssUtils;
import com.meiren.sso.web.SsoHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/2/8.
 * 提现管理
 */

@Controller
@RequestMapping("/withdraw")
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.withdraw.index"})
public class WithdrawModule {

    @Autowired
    private WalletOrderService walletOrderService;
    @Autowired
    MissionParamService missionParamService;

    @Autowired
    private ProducerLogService producerLogService;
    @Autowired
    private OssConstant ossConstant;

    private String timePatternWithSec="yyyy-MM-dd HH:mm:ss";

    @Autowired
    private SsoHelper ssoHelper;

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");

        Long userId = RequestUtil.getLong(request, "userId");
        String userName = RequestUtil.getStringTrans(request, "userName");
        String withdrawAccount=RequestUtil.getStringTrans(request,"withdrawAccount");
        String auditUserName=RequestUtil.getStringTrans(request,"auditUserName");
        String process = RequestUtil.getString(request, "hasProcess");//default=0
        String status=RequestUtil.getString(request,"status");
        Integer sort=RequestUtil.getInteger(request,"sort");//default=1
        String applyMinTime = RequestUtil.getStringTrans(request, "applyMinTime");
        String applyMaxTime = RequestUtil.getStringTrans(request, "applyMaxTime");
        String auditMinTime = RequestUtil.getStringTrans(request, "auditMinTime");
        String auditMaxTime = RequestUtil.getStringTrans(request, "auditMaxTime");


        List<String> statusList=string2StringList(status);
        List<Integer> processList=string2IntegerList(process);

        modelAndView.addObject("userId", userId);
        modelAndView.addObject("userName", userName);
        modelAndView.addObject("withdrawAccount", withdrawAccount);
        modelAndView.addObject("auditUserName", auditUserName);
        modelAndView.addObject("hasProcess", process);
        modelAndView.addObject("status",status);
        modelAndView.addObject("sort", sort);
        modelAndView.addObject("applyMinTime", applyMinTime);
        modelAndView.addObject("applyMaxTime", applyMaxTime);
        modelAndView.addObject("auditMinTime", auditMinTime);
        modelAndView.addObject("auditMaxTime", auditMaxTime);
        modelAndView.setViewName("/withdraw/withdrawIndex");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        WalletOrderQueryVO walletOrderQueryVO = new WalletOrderQueryVO();
        walletOrderQueryVO.setPage(pageNum);

        if (userId!=null){
            walletOrderQueryVO.setUserId(userId);
        }
        if (StringUtils.isNotEmpty(userName)){
            walletOrderQueryVO.setUserName("%"+userName+"%");
        }
        if (StringUtils.isNotEmpty(withdrawAccount)){
            walletOrderQueryVO.setWithdrawAccount("%"+withdrawAccount+"%");
        }

        if(StringUtils.isNotEmpty(auditUserName)){
            walletOrderQueryVO.setAuditUserName("%"+auditUserName+"%");
        }
        if(StringUtils.isNotEmpty(applyMinTime)) {
            walletOrderQueryVO.setApplyStartTime(DateUtils.parseDate(applyMinTime, timePatternWithSec));
        }
        if(StringUtils.isNotEmpty(applyMaxTime)) {
            walletOrderQueryVO.setApplyEndTime(DateUtils.parseDate(applyMaxTime, timePatternWithSec));
        }
        if(StringUtils.isNotEmpty(auditMinTime)) {
            walletOrderQueryVO.setAuditStartTime(DateUtils.parseDate(auditMinTime, timePatternWithSec));
        }
        if(StringUtils.isNotEmpty(applyMaxTime)) {
            walletOrderQueryVO.setAuditEndTime(DateUtils.parseDate(auditMaxTime, timePatternWithSec));
        }
//        if(hasProcess==null && hasProcess>=0) {
        if(!CollectionUtils.isEmpty(processList)) {
            walletOrderQueryVO.setHasProcessList(processList);
        }
        if (!CollectionUtils.isEmpty(statusList)){
            walletOrderQueryVO.setStatus(statusList);
        }
        if (sort==null){
            walletOrderQueryVO.setSort(1);
        }else {
            walletOrderQueryVO.setSort(sort);
        }




        ApiResult apiResult = walletOrderService.getWalletOrderPageBoss(walletOrderQueryVO);
        if (!apiResult.isSuccess()) {
            modelAndView.addObject("message", apiResult.getError());
            return modelAndView;
        }
        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }
        if (resultMap.get("data") != null) {
            List<WalletOrderResult> resultList = (List<WalletOrderResult>) resultMap.get("data");
            if (resultList.size()<1 &&pageNum>1){//当前页没有数据，则返回前一页
                pageNum=pageNum-1;
                walletOrderQueryVO.setPage(pageNum);
                apiResult=walletOrderService.getWalletOrderPageBoss(walletOrderQueryVO);
                resultMap=(Map<String, Object>) apiResult.getData();
                resultList=(List<WalletOrderResult>)resultMap.get("data");
            }
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);
        return modelAndView;
    }

    private List<String> string2StringList(String source){
        List<String> stringList=null;
        if (StringUtils.isNotEmpty(source)){
            stringList= Arrays.asList(source.split(","));
        }
        return stringList;
    }

    private List<Integer> string2IntegerList(String source) {
        List<Integer> integerList = null;
        if (StringUtils.isNotEmpty(source)) {
            String[] strs=source.split(",");
            integerList = new ArrayList<Integer>(strs.length);
            for (String str : strs) {
                integerList.add(Integer.parseInt(str));
            }
        }
        return integerList;
    }


    /**
     * 流水详情渲染页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/build")
    public ModelAndView build(HttpServletRequest request,
                              HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/withdraw/withdrawDetail");
        return modelAndView;
    }


    /**
     * 根据id编号获取任务详情
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getDetail(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id == null) {
            apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
            return apiResult;
        }
        apiResult = walletOrderService.getWalletOrderDetail(id);
        return apiResult;
    }


    @RequestMapping(value = "/auditOrder", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getDetail(AuditOrderVO auditOrderVO,HttpServletRequest request) {
        ApiResult apiResult=new ApiResult();
        //获取审核人
        AclUserEntity aclUserEntity=(AclUserEntity)request.getSession().getAttribute("user");
        if (aclUserEntity==null){
            apiResult.setError(ResultCode.USER_NOT_FOUND.getCode(),ResultCode.USER_NOT_FOUND.getMessage());
            return apiResult;
        }
        auditOrderVO.setAuditUserId(aclUserEntity.getId());
        auditOrderVO.setAuditUserName(aclUserEntity.getUserName());
        apiResult = walletOrderService.auditWalletOrder(auditOrderVO);
        //提现订单审核日志 当操作成功的时候才记录日志
        if (StringUtils.isEmpty(apiResult.getError())){
            try {
                MissionLogEntity missionLogDO = new MissionLogEntity();
                missionLogDO.setOperationType(LogTypeEnum.WithdrawOperate.name());
                missionLogDO.setUserId(aclUserEntity.getId());
                missionLogDO.setUserName(aclUserEntity.getUserName());
                missionLogDO.setLogDesc(JSON.toJSONString(auditOrderVO));
                producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return apiResult;
    }


    /**
     * 获取提现规则参数详情
     *
     * @return
     */
    @RequestMapping(value = "/getWithdrawRule", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getWithdrawRule() {
        ApiResult apiResult = new ApiResult();
        ApiResult paramByType = missionParamService.getParamByType(ParamTypeEnum.WITHDRAW);
        List<ParamResult> paramResultList = (List<ParamResult>) paramByType.getData();
        if (paramResultList.size() > 0) {
            ParamResult paramResult = paramResultList.get(0);
            String missionParamValue = paramResult.getMissionParamValue();
            WithdrawRuleVO withdrawRuleVO = JSON.parseObject(missionParamValue, WithdrawRuleVO.class);
            apiResult.setData(withdrawRuleVO);
        }
        return apiResult;
    }


    /**
     * 创建或者更新配置
     *
     * @param withdrawRuleWebVO
     * @return
     */
    @RequestMapping(value = "/withdrawRuleUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult withdrawRuleUpdate(WithdrawRuleWebVO withdrawRuleWebVO) {
        ApiResult apiResult = new ApiResult();
        //参数校验
        if (withdrawRuleWebVO.getMaxValue() != null && withdrawRuleWebVO.getMinValue() != null) {
            if (withdrawRuleWebVO.getMaxValue().compareTo(withdrawRuleWebVO.getMinValue()) == -1) {
                apiResult.setError(ResultCode.PARAM_VALUE_NOT_ALLOWED.getCode(),ResultCode.PARAM_VALUE_NOT_ALLOWED.getMessage());
                return apiResult;
            }
        }
        WithdrawRuleVO withdrawRuleVO = new WithdrawRuleVO();
        BeanUtils.copyProperties(withdrawRuleWebVO, withdrawRuleVO);
        List<Integer> withdrawDays = new ArrayList<>();
        List<String> stringList = JSON.parseArray(withdrawRuleWebVO.getWithdrawDays(), String.class);
        for (int i = 0; i < stringList.size(); i++) {
            withdrawDays.add(Integer.valueOf(stringList.get(i)));
        }
        withdrawRuleVO.setWithdrawDays(withdrawDays);
        ApiResult paramByType = missionParamService.getParamByType(ParamTypeEnum.WITHDRAW);
        List<ParamResult> paramResultList = (List<ParamResult>) paramByType.getData();
        if (paramResultList.size() > 0) {
            //更新
            ParamResult paramResult = paramResultList.get(0);
            apiResult = missionParamService.updateParamValue(paramResult.getId(), JSON.toJSONString(withdrawRuleVO));
        } else {
            //创建
            ParamVO paramVO = new ParamVO();
            paramVO.setParamType(ParamTypeEnum.WITHDRAW.name());
            paramVO.setParamKey(ParamTypeEnum.WITHDRAW.name());
            paramVO.setParamValueType("String");
            paramVO.setDesc("提现参数配置");
            paramVO.setMissionParamValue(JSON.toJSONString(withdrawRuleVO));
            apiResult = missionParamService.createParamAndValue(paramVO);
        }
        return apiResult;
    }


    /**
     * 可能会传时间参数 直接用vo接收400
     * @param
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value = "/export", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult export(WalletOrderQueryVO walletOrderQueryVO,HttpServletRequest request,HttpServletResponse response) {
//        ApiResult apiResult =new ApiResult();
//        AclUserEntity aclUserEntity=(AclUserEntity)request.getSession().getAttribute("user");
//        if (aclUserEntity==null){
//            apiResult.setError("请先登录");
//            return apiResult;
//        }
//        Long auditUserId =aclUserEntity.getId();
//        apiResult = walletOrderService.exportWalletOrder(walletOrderQueryVO, auditUserId);
//        return apiResult;
//    }

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult export(HttpServletRequest request,HttpServletResponse response) {
        ApiResult apiResult =new ApiResult();
        AclUserEntity aclUserEntity=(AclUserEntity)request.getSession().getAttribute("user");
        if (aclUserEntity==null){
            apiResult.setError(ResultCode.USER_NOT_FOUND.getCode(),ResultCode.USER_NOT_FOUND.getMessage());
            return apiResult;
        }
        Long auditUserId =aclUserEntity.getId();

        WalletOrderQueryVO walletOrderQueryVO=new WalletOrderQueryVO();
        walletOrderQueryVO.setAuditUserName(aclUserEntity.getUserName());

        Long userId=RequestUtil.getLong(request,"userId");
        String userName=RequestUtil.getString(request,"userName");
        String withdrawAccount=RequestUtil.getString(request,"withdrawAccount");
        Integer sort=RequestUtil.getInteger(request,"sort");
        String applyStartTime = RequestUtil.getStringTrans(request, "applyStartTime");
        String applyEndTime = RequestUtil.getStringTrans(request, "applyEndTime");
        if (userId!=null){
            walletOrderQueryVO.setUserId(userId);
        }
        if (StringUtils.isNotEmpty(userName)){
            walletOrderQueryVO.setUserName("%"+userName+"%");
        }
        if (StringUtils.isNotEmpty(withdrawAccount)){
            walletOrderQueryVO.setWithdrawAccount("%"+withdrawAccount+"%");
        }
        if (sort!=null){
            walletOrderQueryVO.setSort(sort);
        }

        if (StringUtils.isNotEmpty(applyStartTime)){
            walletOrderQueryVO.setApplyStartTime(DateUtils.parseDate(applyStartTime,timePatternWithSec));
        }
        if (StringUtils.isNotEmpty(applyEndTime)){
            walletOrderQueryVO.setApplyEndTime(DateUtils.parseDate(applyEndTime,timePatternWithSec));
        }

        apiResult = walletOrderService.exportWalletOrder(walletOrderQueryVO, auditUserId);
        return apiResult;
    }

    @ResponseBody
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ApiResult importFromImg(@RequestParam("file") MultipartFile file) {
        ApiResult apiResult = new ApiResult();
        //上传文件到oss

        StsInfo stsInfo = OssUtils.getStsInfo(Long.valueOf(ossConstant.getUserId()), ossConstant.getOssSecretKey(), ossConstant.getOssStsUrl());
        if (stsInfo == null) {
            apiResult.setError(ResultCode.MISSION_GET_OSS_STS_FAILED.getCode(),ResultCode.MISSION_GET_OSS_STS_FAILED.getMessage());
            return apiResult;
        }
        String key = OssUtils.uploadFileReturnKey(stsInfo, file);

        apiResult = walletOrderService.auditWalletOrderByPath(key, stsInfo.getAccessKeyId(), stsInfo.getAccessKeySecret(), stsInfo.getSecurityToken(), stsInfo.getBucketName(), stsInfo.getEndPoint());
        return apiResult;
    }


    public static void main(String[] args) {
        WithdrawRuleVO withdrawRuleVO = new WithdrawRuleVO();
        withdrawRuleVO.setMaxValue(new BigDecimal(100));
        withdrawRuleVO.setMinValue(new BigDecimal(20));
        withdrawRuleVO.setWithdrawTimes(3L);
        List<Integer> withdrawDays = new ArrayList<>();
        withdrawDays.add(1);
        withdrawDays.add(29);
        withdrawRuleVO.setWithdrawDays(withdrawDays);
        System.out.println(JSON.toJSON(withdrawRuleVO));

    }
}
