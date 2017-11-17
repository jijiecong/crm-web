package com.meiren.web.member;

import com.alibaba.fastjson.JSONObject;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.member.entity.PageEO;
import com.meiren.member.entity.QueryParamEO;
import com.meiren.member.entity.StatisticsReturnEO;
import com.meiren.member.entity.UserInfoStatisticsEO;
import com.meiren.member.service.UserStatisticsService;
import com.meiren.utils.RequestUtil;
import com.meiren.web.acl.BaseController;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired UserStatisticsService userStatisticsService;

    /**
     * 列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索
        QueryParamEO queryParamEO = new QueryParamEO();
        //queryParamEO.setStartTime(1495296000000L);
        //queryParamEO.setEndTime(1506787200000L);
        queryParamEO.setRegisterProjectName(RequestUtil.getStringTrans(request, "projectName"));
        queryParamEO.setPageNum(pageNum);
        queryParamEO.setPageSize(rowsNum);
        ApiResult apiResult = userStatisticsService.getUserInfoByPageFromMbc(queryParamEO);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            PageEO<UserInfoStatisticsEO> pageEO = (PageEO<UserInfoStatisticsEO>) apiResult.getData();
            rMap.put("totalCount", pageEO.getTotalCount());
            rMap.put("data", pageEO.getData());
        }
        return new VueResult(rMap);
    }

    //折线统计注册用户
    @RequestMapping("/registerStatistics")
    public VueResult registerStatistics(HttpServletRequest request) {

        List<String> projectNameList = RequestUtil.getArray(request, "projectNames");
        String[] projectNames = null;
        if(!projectNameList.isEmpty()){
            projectNames = projectNameList.toArray(new String[projectNameList.size()]);
        }
        Long timeStart = RequestUtil.getLong(request, "timeStart");
        Long timeEnd = RequestUtil.getLong(request, "timeEnd");
        String dateFormat = RequestUtil.getStringTrans(request, "dateFormat");
        if(dateFormat.equals("datetime")){
            dateFormat = "h";
        }else if(dateFormat.equals("date")){
            dateFormat = "d";
        }else if (dateFormat.equals("month")){
            dateFormat = "M";
        }
//        System.out.println("++++++"+dateFormatMap.get(dateFormat));
        ApiResult apiResult = userStatisticsService.statisticsByUserRegFromMbc(dateFormat, timeStart, timeEnd, projectNames);
//        System.out.println(JSONObject.toJSONString(apiResult.getData()));
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    //饼状统计注册用户
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

    //饼状根据projectName统计注册用户
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
    public VueResult getProjects(HttpServletRequest request){

        //ApiResult apiResult = userStatisticsService.getUserInfoByPageFromMbc(queryParamEO);
        return new VueResult();
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
