package com.meiren.web.member;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiren.common.constants.VueConstants;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.member.entity.PageEO;
import com.meiren.member.entity.QueryParamEO;
import com.meiren.member.entity.StatisticsReturnEO;
import com.meiren.member.entity.UserInfoStatisticsEO;
import com.meiren.member.service.MemberService;
import com.meiren.member.service.UserStatisticsService;
import com.meiren.utils.RequestUtil;
import com.meiren.web.acl.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
            rMap.put("totalCount", pageEO.getTotalCount());
            rMap.put("data", pageEO.getData());
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
        System.out.println(JSONObject.toJSON(apiResult));
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
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
        ArrayList<StatisticsReturnEO> arrayList = new ArrayList<StatisticsReturnEO>();
        if (apiResult.getData() != null) {
            arrayList = (ArrayList<StatisticsReturnEO>) apiResult.getData();
        }
        return new VueResult(arrayList);
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
    public VueResult deleteUserById(HttpServletRequest request){
        Long userId = RequestUtil.getLong(request, "userId");
        ApiResult apiResult = memberService.delAccountByUserId(userId);
        return new VueResult(apiResult);
    }

    //批量删除用户
    @RequestMapping("/deleteUserByIdsBatch")
    public VueResult deleteUserByIdsBatch(HttpServletRequest request){
        List<String> userIdList = RequestUtil.getArray(request, "userIds");
        List<Long> userIds =new ArrayList<>();
        try{
            for (String id : userIdList) {
                userIds.add(Long.parseLong(id));
            }
        }catch(Exception  e){
            e.printStackTrace();
        }
        ApiResult apiResult = memberService.delAccountByUserIdsBatch(userIds);
        return new VueResult(apiResult);
    }

    //批量添加或解除黑名单
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