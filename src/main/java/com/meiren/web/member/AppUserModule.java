package com.meiren.web.member;

import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.constants.VueConstants;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.member.entity.*;
import com.meiren.member.service.LocationInfoService;
import com.meiren.member.service.MemberService;
import com.meiren.member.service.UserStatisticsService;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.SessionUserVO;
import com.meiren.vo.UserInfoVO;
import com.meiren.web.acl.BaseController;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jijc
 * @ClassName: AppUserModule
 * @Description: ${todo}
 * @date 2017/10/26 18:45
 */
@Controller
@RequestMapping("{uuid}/member/appUser")
@ResponseBody
public class AppUserModule extends BaseController {

    @Resource UserStatisticsService userStatisticsService;
    @Resource MemberService memberService;
    @Resource LocationInfoService locationInfoService;
    @Resource AclPrivilegeService aclPrivilegeService;

    /**
     * 列表
     * @param request
     * @return
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        QueryParamEO queryParamEO = new QueryParamEO();
        queryParamEO.setCommonFile(RequestUtil.getStringTrans(request, "commonFile"));
        String projectName = RequestUtil.getStringTrans(request, "projectName");
        if(!"all".equals(projectName)){
            queryParamEO.setRegisterProjectName(projectName);
        }
        queryParamEO.setPageNum(pageNum);
        queryParamEO.setPageSize(rowsNum);
        ApiResult apiResult = userStatisticsService.getUserInfoByPage(queryParamEO);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.isSuccess() && apiResult.getData() != null ) {
            PageEO<UserInfoStatisticsEO> pageEO = (PageEO<UserInfoStatisticsEO>) apiResult.getData();
            List<UserInfoStatisticsEO> userInfoStatisticsEOList = pageEO.getData();
            List<UserInfoVO> userInfoVOList = new ArrayList<>();
            for (UserInfoStatisticsEO userInfoStatisticsEO : userInfoStatisticsEOList) {
                UserInfoVO userInfoVO = new UserInfoVO();
                BeanUtils.copyProperties(userInfoStatisticsEO, userInfoVO);
                if(userInfoStatisticsEO.getLocationId() != 0){
                    ApiResult topLocationByLocation = locationInfoService.getTopLocationByLocationId(userInfoStatisticsEO.getLocationId(), null);
                    if(topLocationByLocation.isSuccess()){
                        String locationInfo = (String) topLocationByLocation.getData();
                        userInfoVO.setLocationInfo(locationInfo);
                    }
                }
                if(userInfoStatisticsEO.getBirthdayYear() !=0 ){
                    userInfoVO
                        .setBirthday(userInfoStatisticsEO.getBirthdayYear()+"年"+userInfoStatisticsEO.getBirthdayMonth()+"月"+userInfoStatisticsEO.getBirthdayDay()+"日");
                }
                userInfoVOList.add(userInfoVO);
            }
            rMap.put("totalCount", pageEO.getTotalCount());
            rMap.put("data", userInfoVOList);
        }
        return new VueResult(rMap);
    }

    //查询黑名单列表
    @RequestMapping("/blackList")
    public VueResult blackList(HttpServletRequest request){
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索
        QueryParamEO queryParamEO = new QueryParamEO();
        queryParamEO.setCommonFile(RequestUtil.getStringTrans(request, "commonFile"));
        String projectName = RequestUtil.getStringTrans(request, "projectName");
        if(!"all".equals(projectName)){
            queryParamEO.setRegisterProjectName(projectName);
        }
        queryParamEO.setPageNum(pageNum);
        queryParamEO.setPageSize(rowsNum);
        ApiResult apiResult = userStatisticsService.getUserBlacklistByPage(queryParamEO);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.isSuccess() && apiResult.getData() != null ) {
            PageEO<UserInfoStatisticsEO> pageEO = (PageEO<UserInfoStatisticsEO>) apiResult.getData();
            rMap.put("totalCount", pageEO.getTotalCount());
            rMap.put("data", pageEO.getData());
        }
        return new VueResult(rMap);
    }

    /**
     * 权限查询
     * @param request
     * @return
     */
    @RequestMapping("/getAuthByToken")
    public VueResult getAuthByToken(HttpServletRequest request) {
//        String token = RequestUtil.getStringTrans(request, "token");
        String token1 = "meiren.acl.mbc.member.user.createBlackList";
        String token2 = "meiren.acl.mbc.member.user.remove";
        SessionUserVO sessionUser = RequestUtil.getSessionUser(request);
        Long userId = sessionUser.getId();
        Boolean blackBoolean = aclPrivilegeService.hasPrivilege(userId, token1);
        Boolean removeBoolean = aclPrivilegeService.hasPrivilege(userId, token2);
        Map map = new HashMap();
        map.put("blackBoolean",blackBoolean);
        map.put("removeBoolean",removeBoolean);
        return new VueResult(map);
    }

    //折线图统计注册用户 - 第一个图
    @RequestMapping("/registerStatistics")
    public VueResult registerStatistics(HttpServletRequest request) {
        List<String> projectNames = RequestUtil.getArray(request, "projectNames");
        Long timeStart = RequestUtil.getLong(request, "timeStart");
        Long timeEnd = RequestUtil.getLong(request, "timeEnd");
        String dateFormat = RequestUtil.getStringTrans(request, "dateFormat");
        if(dateFormat.equals("datetime")){
            dateFormat = "H";
        }else if (dateFormat.equals("month")){
            dateFormat = "M";
        }else if (dateFormat.equals("year")){
            dateFormat = "y";
        }else {
            dateFormat = "d";
        }
        ApiResult apiResult = userStatisticsService.statisticsByUserRegFromMbc(dateFormat, timeStart,timeEnd, projectNames);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    @RequestMapping("/exportLine")
    public String exportLine(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //TODO
        System.out.println("成功");
        return null;
    }


    //饼状图统计注册用户 - 第二个图
    @RequestMapping("/registerToPieStatistics")
    public VueResult registerToPieStatistics(HttpServletRequest request) {
        String projectName = RequestUtil.getStringTrans(request, "projectName");
        Long timeStart = RequestUtil.getLong(request, "timeStart");
        Long timeEnd = RequestUtil.getLong(request, "timeEnd");
        ApiResult apiResult = userStatisticsService.statisticsByUserRegToPieFromMbc(timeStart, timeEnd, projectName);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    //柱状图根据projectName统计注册用户 - 第三个图
    @RequestMapping("/registerByProjectNameStatistics")
    public VueResult registerByProjectNameStatistics(HttpServletRequest request) {
        ApiResult apiResult = userStatisticsService.statisticsByProjectNameFromMbc();
        List<StatisticsProjectNameReturnEO> result = new ArrayList<>();
        if (apiResult.getData() != null) {
            List<StatisticsProjectNameReturnEO> statisticsProjectNameReturnEOList = (List<StatisticsProjectNameReturnEO>) apiResult.getData();
            for (StatisticsProjectNameReturnEO statisticsProjectNameReturnEO : statisticsProjectNameReturnEOList) {
                StatisticsProjectNameReturnEO statisticsProjectNameReturnEO1 = new StatisticsProjectNameReturnEO();
                if("任务大厅".equals(statisticsProjectNameReturnEO.getProjectName())){
                    continue;
                }else if("素材平台".equals(statisticsProjectNameReturnEO.getProjectName())){
                    continue;
                }else{
                    BeanUtils.copyProperties(statisticsProjectNameReturnEO,statisticsProjectNameReturnEO1);
                    result.add(statisticsProjectNameReturnEO1);
                }
            }
        }
        return new VueResult(result);
    }

    //获取所有projects
    @RequestMapping("/getProjects")
    public VueResult getProjects(){
        ApiResult apiResult = userStatisticsService.getAllProjectName();
        if(!apiResult.isSuccess()){
            return new VueResult();
        }
        Map<String,String> result = (Map<String, String>) apiResult.getData();
        return new VueResult(result);
    }

    //根据id添加或取消黑名单
    @AuthorityToken(needToken = {"meiren.acl.mbc.member.user.createBlackList"})
    @RequestMapping("/createBlackListUserById")
    public VueResult createBlackListUserById(HttpServletRequest request){
        Long userId = RequestUtil.getLong(request, "userId");
        String projectName = RequestUtil.getStringTrans(request, "projectName");
        Integer blacklistType = RequestUtil.getInteger(request, "blacklistType");// 1,"黑名单"      2,"白名单"
        ApiResult apiResult = userStatisticsService.createMemberBlacklist(userId,projectName, VueConstants.BLACKLIST_OPERATOR,blacklistType);
        return new VueResult(apiResult);
    }

    //根据id删除用户
    @RequestMapping("/deleteUserById")
    @AuthorityToken(needToken = {"meiren.acl.mbc.member.user.remove"})
    public VueResult deleteUserById(HttpServletRequest request){
        SessionUserVO sessionUser = RequestUtil.getSessionUser(request);
        MbcUserInfoEO mbcUserInfoEO = new MbcUserInfoEO();
        mbcUserInfoEO.setUserId(RequestUtil.getLong(request, "userId"));
        mbcUserInfoEO.setOperatorId(sessionUser.getId());
        mbcUserInfoEO.setOperatorName(sessionUser.getUserName());
        ApiResult apiResult = memberService.delAccountByMbcUserInfo(mbcUserInfoEO);
        return new VueResult(apiResult);
    }

    //批量删除用户
    @AuthorityToken(needToken = {"meiren.acl.mbc.member.user.remove"})
    @RequestMapping("/deleteUserByIdsBatch")
    public VueResult deleteUserByIdsBatch(HttpServletRequest request){
        SessionUserVO sessionUser = RequestUtil.getSessionUser(request);
        List<String> userIdList = RequestUtil.getArray(request, "userIds");
        List<MbcUserInfoEO> mbcUserInfoEOList = new ArrayList<>();
        try{
            for (String id : userIdList) {
                MbcUserInfoEO mbcUserInfoEO = new MbcUserInfoEO();
                mbcUserInfoEO.setUserId(Long.parseLong(id));
                mbcUserInfoEO.setOperatorId(sessionUser.getId());
                mbcUserInfoEO.setOperatorName(sessionUser.getUserName());
                mbcUserInfoEOList.add(mbcUserInfoEO);
            }
        }catch(Exception  e){
            e.printStackTrace();
        }


        ApiResult apiResult = memberService.delAccountByMbcUserInfoBatch(mbcUserInfoEOList);
        return new VueResult(apiResult);
    }

    //批量添加或解除黑名单
    @AuthorityToken(needToken = {"meiren.acl.mbc.member.user.createBlackList"})
    @RequestMapping("/createBlackListBatch")
    public VueResult createBlackListBatch(HttpServletRequest request){
        String projectName = RequestUtil.getStringTrans(request, "projectName");
        Integer blacklistType = RequestUtil.getInteger(request, "blacklistType");// 1,"黑名单"      2,"白名单"
        List<String> userIdList = RequestUtil.getArray(request, "userIds");
        List<Long> userIds =new ArrayList<>();
        try{
            for (String id : userIdList) {
                userIds.add(Long.parseLong(id));
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        ApiResult apiResult = userStatisticsService.createMemberBlacklistBatch(userIds,projectName, VueConstants.BLACKLIST_OPERATOR,blacklistType);
        return new VueResult(apiResult);
    }

    //UTC世界标准时间转时间戳
    public static Long formatDate(String dateStr){
        if(dateStr.equals("")){
            return null;
        }
        dateStr = dateStr.replace("Z", " UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date==null) return null;
        return (date.getTime());
    }

}
