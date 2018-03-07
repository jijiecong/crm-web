package com.meiren.web.member;

import com.alibaba.fastjson.JSONObject;
import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.constants.VueConstants;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.member.entity.*;
import com.meiren.member.service.*;
import com.meiren.utils.ExcelUtil;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.DateFormatVO;
import com.meiren.vo.SessionUserVO;
import com.meiren.vo.UserInfoVO;
import com.meiren.web.acl.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
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
    @Resource WaistcoatService waistcoatService;
    @Resource UserTagService userTagService;

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
        String sort = RequestUtil.getStringTrans(request, "sort");
        Long timeStart = RequestUtil.getLong(request, "timeStart");
        Long timeEnd = RequestUtil.getLong(request, "timeEnd");
        if(!"all".equals(projectName)){
            queryParamEO.setRegisterProjectName(projectName);
        }
        if(StringUtils.isNotBlank(sort)){
            String[] sortArray = sort.split("-");
            queryParamEO.setSortFile(sortArray[0]);
            queryParamEO.setSort(sortArray[1]);
        }
        queryParamEO.setPageNum(pageNum);
        queryParamEO.setPageSize(rowsNum);
        queryParamEO.setStartTime(timeStart);
        queryParamEO.setEndTime(timeEnd);
        ApiResult apiResult = userStatisticsService.getUserInfoByPage(queryParamEO);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.isSuccess() ) {
            PageEO<UserInfoStatisticsEO> pageEO = (PageEO<UserInfoStatisticsEO>) apiResult.getData();
            if( pageEO.getData() == null){
                rMap.put("totalCount", pageEO.getTotalCount());
                rMap.put("data", null);
                return new VueResult(rMap);
            }
            List<UserInfoStatisticsEO> userInfoStatisticsEOList = pageEO.getData();
            List<UserInfoVO> userInfoVOList = new ArrayList<>();
            for (UserInfoStatisticsEO userInfoStatisticsEO : userInfoStatisticsEOList) {
                UserInfoVO userInfoVO = new UserInfoVO();
                BeanUtils.copyProperties(userInfoStatisticsEO, userInfoVO);
                if(!(userInfoStatisticsEO.getLocationId() == null || userInfoStatisticsEO.getLocationId() == 0)){
                    ApiResult topLocationByLocation = locationInfoService.getTopLocationByLocationId(userInfoStatisticsEO.getLocationId(), null);
                    if(topLocationByLocation.isSuccess()){
                        String locationInfo = (String) topLocationByLocation.getData();
                        userInfoVO.setLocationInfo(locationInfo);
                    }
                }
                if(!(userInfoStatisticsEO.getBirthdayYear() == null || userInfoStatisticsEO.getBirthdayYear() == 0)){
                    userInfoVO.setBirthday(userInfoStatisticsEO.getBirthdayYear()+"年"+userInfoStatisticsEO.getBirthdayMonth()+"月"+userInfoStatisticsEO.getBirthdayDay()+"日");
                }
                if(StringUtils.isNotBlank(userInfoVO.getTags())){
                    ApiResult tagsNameList = userTagService.getTagsNameList(userInfoVO.getTags());
                    if(tagsNameList.isSuccess()){
                        List<String> tagsNames = (List<String>) tagsNameList.getData();
                        userInfoVO.setTags(StringUtils.join(tagsNames.toArray(), ","));
                    }
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
        SessionUserVO sessionUser = RequestUtil.getSessionUser(request);
        Long userId = sessionUser.getId();
        Boolean blackBoolean = aclPrivilegeService.hasPrivilege(userId, VueConstants.BLACKLIST_AUTH);
        Boolean removeBoolean = aclPrivilegeService.hasPrivilege(userId, VueConstants.MEMBER_REMOVE_AUTH);
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
        DateFormatVO dateFormatVO = addCycleByDateFormat(dateFormat, timeEnd);
        ApiResult apiResult = userStatisticsService.statisticsByUserRegFromMbc(dateFormatVO.getDateFormat(), timeStart,dateFormatVO.getTimeEnd(), projectNames);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    private DateFormatVO addCycleByDateFormat(String dateFormat,Long timeEnd){
        DateFormatVO dateFormatVO = new DateFormatVO();
        Calendar c = Calendar.getInstance();
        if(timeEnd != null){
            c.setTimeInMillis(timeEnd);
        }
        if(dateFormat.equals("datetime")){
            c.add(Calendar.HOUR_OF_DAY, 1);
            dateFormatVO.setDateFormat("H");
        }else if (dateFormat.equals("month")){
            c.add(Calendar.MONTH, 1);
            dateFormatVO.setDateFormat("M");
        }else if (dateFormat.equals("year")){
            c.add(Calendar.YEAR, 1);
            dateFormatVO.setDateFormat("y");
        }else {
            c.add(Calendar.DAY_OF_MONTH, 1);
            dateFormatVO.setDateFormat("d");
        }
        dateFormatVO.setTimeEnd(timeEnd==null?null:c.getTimeInMillis()-1);
        return dateFormatVO;
    }

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.DAY_OF_MONTH, 1);
        System.out.println(c.getTimeInMillis());
        System.out.println(c.getTime());
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
        if (apiResult.getData() != null) {
            List<StatisticsProjectNameReturnEO> statisticsProjectNameReturnEOList = (List<StatisticsProjectNameReturnEO>) apiResult.getData();
            return new VueResult(statisticsProjectNameReturnEOList);
        }
        return new VueResult();
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
    @AuthorityToken(needToken = {VueConstants.BLACKLIST_AUTH})
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
    @AuthorityToken(needToken = {VueConstants.MEMBER_REMOVE_AUTH})
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
    @AuthorityToken(needToken = {VueConstants.MEMBER_REMOVE_AUTH})
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
    @AuthorityToken(needToken = {VueConstants.BLACKLIST_AUTH})
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

//------------------------------------- 马甲账号开始 -------------------------------------------------
    //查询马甲账号列表
    @RequestMapping("/waistcoatList")
    public VueResult waistcoatList(HttpServletRequest request){
        String queryType = RequestUtil.getString(request, "queryType");
        String queryKeyword = RequestUtil.getString(request, "queryKeyword");
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        QueryParamEO queryParamEO = new QueryParamEO();
        if(StringUtils.isNoneBlank(queryType,queryKeyword)){
            if(queryType.equals("userId")){
                queryParamEO.setUserId(Long.valueOf(queryKeyword));
            }else if(queryType.equals("mobile")){
                queryParamEO.setMobile(queryKeyword);
            }else{
                queryParamEO.setNickname(queryKeyword);
            }
        }
        queryParamEO.setPageNum(pageNum);
        queryParamEO.setPageSize(rowsNum);
        ApiResult apiResult = waistcoatService.getWaistcoatUserByPage(queryParamEO);
        return new VueResult(apiResult);
    }

    //根据userId获取马甲账号
    @RequestMapping("/getWaistcoatUser")
    public VueResult getWaistcoatUser(HttpServletRequest request){
        ApiResult apiResult = memberService.getUserInfoByUserId(RequestUtil.getLong(request, "userId"),false,false);
        return new VueResult(apiResult);
    }

    //添加马甲账号
    @RequestMapping("/addWaistcoatUser")
    public VueResult addWaistcoatUser(HttpServletRequest request){
        MemberDetailAndIndexEO memberDetailAndIndexEO = new MemberDetailAndIndexEO();
        memberDetailAndIndexEO.setNickname(RequestUtil.getString(request, "nickname"));
        memberDetailAndIndexEO.setUserIcon(RequestUtil.getString(request, "userIcon"));
        memberDetailAndIndexEO.setPwdHash(RequestUtil.getString(request, "pwd"));
        ApiResult apiResult = waistcoatService.addWaistcoatUser(memberDetailAndIndexEO);
        return new VueResult(apiResult);
    }

    //修改马甲账号
    @RequestMapping("/editWaistcoatUser")
    public VueResult editWaistcoatUser(HttpServletRequest request){
        MemberDetailAndIndexEO memberDetailAndIndexEO = new MemberDetailAndIndexEO();
        memberDetailAndIndexEO.setUserId(RequestUtil.getLong(request, "userId"));
        memberDetailAndIndexEO.setNickname(RequestUtil.getString(request, "nickname"));
        String userIcon = RequestUtil.getString(request, "userIcon");
        if(StringUtils.isNotBlank(userIcon)){
            memberDetailAndIndexEO.setUserIcon(userIcon);
        }
        ApiResult apiResult = memberService.updateUserInfoByUserIdSecurity(memberDetailAndIndexEO.getUserId(), true, memberDetailAndIndexEO);
        return new VueResult(apiResult);
    }

    //导入Excel表格
    @RequestMapping(value = "importExcel", method = RequestMethod.POST)
    public VueResult importExcel(HttpServletRequest request,HttpSession session,@RequestParam("file") MultipartFile file) throws Exception {
        VueResult vueResult = new VueResult();
        AclUserEntity user = (AclUserEntity) session.getAttribute("user");
        if(user == null){
            vueResult.setError(100, "exception occur: user not exsits");
            return vueResult;
        }
        InputStream inputStream = file.getInputStream();
        List<List<Object>> lists = ExcelUtil.readExcel(inputStream);
        System.out.println("----:"+ JSONObject.toJSONString(lists));
        /*if(CollectionUtils.isEmpty(lists)){
            apiResult.setError(300, "参数缺失");
            return apiResult;
        }
        Date date = new Date();
        List<FamilySensitiveWordEntity>  sensitiveWordEntityList = new ArrayList<>();
        for(int i=1;i<lists.size();i++){//过滤第一行标题
            FamilySensitiveWordEntity familySensitiveWordEntity = new FamilySensitiveWordEntity();
            SensitiveTypeEnum sensitiveTypeEnum = SensitiveTypeEnum.getEnumByName(String.valueOf(lists.get(i).get(0)));
            familySensitiveWordEntity.setSensitiveType(sensitiveTypeEnum!=null?sensitiveTypeEnum.name():SensitiveTypeEnum.OTHERS.name());
            familySensitiveWordEntity.setSensitiveWord(String.valueOf(lists.get(i).get(1)));
            familySensitiveWordEntity.setBeginDateNum(0);
            familySensitiveWordEntity.setCreatePerson(user.getUserName());
            familySensitiveWordEntity.setCreatePersonId(user.getId());
            familySensitiveWordEntity.setOperatorId(user.getId());
            familySensitiveWordEntity.setOperator(user.getUserName());
            familySensitiveWordEntity.setCreateTime(date);
            familySensitiveWordEntity.setUpdateTime(date);
            familySensitiveWordEntity.setIsMatchCase(1);
            familySensitiveWordEntity.setValidTime(date);
            familySensitiveWordEntity.setStatus(SensitiveWordStatusEnum.ON.getValue());
            sensitiveWordEntityList.add(familySensitiveWordEntity);
        }
        familySensitiveWordDubboService.batchCreateSensitiveWord(sensitiveWordEntityList);
        return new ApiResult();*/
        return null;
    }

//------------------------------------- 马甲账号结束 -------------------------------------------------
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
