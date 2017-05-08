package com.meiren.web.mission;


import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.utils.DateUtils;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.MissionLogResult;
import com.meiren.mission.service.*;
import com.meiren.mission.service.vo.MissionLogQueryVO;
import com.meiren.vo.LogTypeVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;
/**
 * Created by admin on 2017/3/7.
 */
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.mission.logIndex"})
@Controller
@RequestMapping("/mission")
public class MissionLogModule {


    @Autowired
    private MissionLogService logService;


    private String timePatternWithSec ="yyyy-MM-dd HH:mm:ss";


    @RequestMapping("/logIndex")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");

        Long id=RequestUtil.getLong(request,"missionIdSearch");
        Integer sort = RequestUtil.getInteger(request, "sortSearch");
        String operationTypeStr=RequestUtil.getString(request,"operationTypes");
        String userName=RequestUtil.getStringTrans(request,"userNameSearch");
        String operationStartTime=RequestUtil.getString(request,"operationStartTime");
        String operationEndTime=RequestUtil.getString(request,"operationEndTime");
        List<String> operationTypeList=null;
        if (StringUtils.isNotEmpty(operationTypeStr)){
            operationTypeList=Arrays.asList(operationTypeStr.split(","));
        }

        modelAndView.addObject("missionIdSearch",id);
        modelAndView.addObject("sortSearch", sort);
        modelAndView.addObject("operationStartTime",operationStartTime);
        modelAndView.addObject("operationEndTime",operationEndTime);
        modelAndView.addObject("operationTypes",operationTypeStr);
        modelAndView.addObject("userNameSearch",userName);
        modelAndView.setViewName("/mission/logIndex");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        MissionLogQueryVO queryVO=new MissionLogQueryVO();
        queryVO.setPage(pageNum);
        queryVO.setSize(pageSize);
        if (id!=null){
            queryVO.setId(id);
        }
        if (CollectionUtils.isNotEmpty(operationTypeList)){
            queryVO.setOperateTypes(operationTypeList);
        }
        queryVO.setSort(sort == null ? 1 : sort);
        if (StringUtils.isNotEmpty(operationStartTime)){
            queryVO.setOperationStartTime(DateUtils.parseDate(operationStartTime,timePatternWithSec));
        }
        if (StringUtils.isNotEmpty(operationEndTime)){
            queryVO.setOperationEndTime(DateUtils.parse(operationEndTime,timePatternWithSec));
        }

        if (StringUtils.isNotEmpty(userName)){
            queryVO.setUserName("%"+userName+"%");
        }

        ApiResult apiResult=logService.getMissionLogPage(queryVO);
//
        if (!apiResult.isSuccess()) {
            modelAndView.addObject("message", apiResult.getError());
            return modelAndView;
        }

        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }
        if (resultMap.get("data") != null) {
            List<MissionLogResult> resultList = (List<MissionLogResult>) resultMap.get("data");
            formatVO(resultList);
            modelAndView.addObject("basicVOList", resultList);
        }

        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);

        //查询所有的操作类型
        modelAndView.addObject("totalOperationTypes",getOperationTypes());
        return modelAndView;
    }


    private List<LogTypeVO> getOperationTypes(){
        LogTypeEnum[] typeEnums=LogTypeEnum.values();
        List<LogTypeVO> typeVOList=new ArrayList<>(typeEnums.length);
        for (LogTypeEnum typeEnum:typeEnums){
            LogTypeVO typeVO=new LogTypeVO();
            typeVO.setName(typeEnum.name());
            typeVO.setDesc(typeEnum.getDesc());
            typeVOList.add(typeVO);
        }
        return typeVOList;
    }


    /*
    两个功能：
    1.将操作类型转换成中文
    2.将logDesc的内容格式化
     */
    private void formatVO(List<MissionLogResult> resultList){
        Iterator<MissionLogResult> iterator=resultList.iterator();
        while (iterator.hasNext()){
            MissionLogResult logResult=iterator.next();
            String operationType= logResult.getOperationType();
            String desc=LogTypeEnum.valueOf(operationType).getDesc();
            logResult.setOperationType(desc);

            logResult.setLogDesc(logResult.getLogDesc()
                    .replace("<","&lt;")
                    .replace(">","&gt;")
                    .replace("\\t","&nbsp;")
                    .replace("\\n","<br/>")
                    .replace("\\",""));

        }
    }
}
