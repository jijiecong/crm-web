package com.meiren.web.mission;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.utils.RequestUtil;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.MissionFilterResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.service.MissionFilterRulesService;
import com.meiren.mission.service.MissionParamService;
import com.meiren.mission.service.ProducerLogService;
import com.meiren.mission.service.entity.MissionFilterRulesEntity;
import com.meiren.mission.service.entity.MissionLogEntity;
import com.meiren.mission.service.vo.MissionFilterFilterVO;
import com.meiren.vo.RuleForm;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/1/2.
 */
@Controller
@RequestMapping("/mission/filter")
public class MissionFilterModule {

    @Autowired
    private MissionFilterRulesService missionFilterRulesService;
    @Autowired
    private MissionParamService missionParamService;
    @Autowired
    private ProducerLogService producerLogService;
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");

        String filterNameSearch = RequestUtil.getStringTrans(request, "filterNameSearch");
        Long missionIdSearch = RequestUtil.getLong(request, "missionIdSearch");
        modelAndView.addObject("filterNameSearch",filterNameSearch);
        modelAndView.addObject("missionIdSearch",missionIdSearch);


        modelAndView.setViewName("/mission/filterIndex");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        MissionFilterFilterVO missionFilterFilterVO=new MissionFilterFilterVO();
        missionFilterFilterVO.setPage(pageNum);
        missionFilterFilterVO.setSize(pageSize);
        missionFilterFilterVO.setRuleName(filterNameSearch);
        missionFilterFilterVO.setMissionId(missionIdSearch);
        ApiResult apiResult = missionFilterRulesService.getMissionFilterPage(missionFilterFilterVO);
        if (!apiResult.isSuccess()) {
            modelAndView.addObject("message", apiResult.getError());
            return modelAndView;
        }
        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }
        if (resultMap.get("data") != null) {
            List<MissionFilterResult> resultList = (List<MissionFilterResult>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);
        return modelAndView;
    }





//    @RequestMapping("/build")
//    public ModelAndView build(HttpServletRequest request,
//                              HttpServletResponse response) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/mission/filterbuilder");
//        //将规则相关参数放到View中
//        ApiResult paramByType = missionParamService.getParamByType(ParamTypeEnum.RULE);
//        List<ParamResult> paramResultList = (List<ParamResult>) paramByType.getData();
//        modelAndView.addObject("basicVOList",paramResultList);
//        return modelAndView;
//    }




    /**
     * 根据id编号获取规则详情
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/detail",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getDetail(HttpServletRequest request, HttpServletResponse response){
        ApiResult apiResult=new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        if(id==null){
            apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
            return apiResult;
        }
        apiResult = missionFilterRulesService.getMissionFilterDetail(id);
        return apiResult;
    }

    /**
     * 获取规则选项
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getRuleConfig",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getRuleConfig(HttpServletRequest request, HttpServletResponse response){

        return null;
    }


    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult createFilter(RuleForm ruleForm,HttpServletRequest request){
        ApiResult apiResult=new ApiResult();
        MissionFilterRulesEntity missionFilterRulesEntity=new MissionFilterRulesEntity();
        missionFilterRulesEntity.setRuleName(ruleForm.getRuleName());
        missionFilterRulesEntity.setRuleContent(ruleForm.getFilterRuleVOList());
        apiResult = missionFilterRulesService.createMissionFilterRules(missionFilterRulesEntity);
        //创建规则日志
        try {
            MissionLogEntity missionLogDO=new MissionLogEntity();
            //TODO 取出用户信息
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setUserId(user.getId());
            missionLogDO.setOperationType(LogTypeEnum.FilterCreate.name());
            missionLogDO.setLogDesc(JSON.toJSONString(ruleForm));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }

    @RequestMapping(value = "/modify",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult modifyFilter( RuleForm ruleForm,HttpServletRequest request){
        ApiResult apiResult=new ApiResult();
        String filterRuleVOList = ruleForm.getFilterRuleVOList();
//        List<FilterRuleVO> filterRuleVOs = JSON.parseArray(filterRuleVOList, FilterRuleVO.class);
        Map<String, Object> paramMap=new HashedMap();
        paramMap.put("ruleName",ruleForm.getRuleName());
        paramMap.put("ruleContent",ruleForm.getFilterRuleVOList());
        apiResult= missionFilterRulesService.updateMissionFilterRules(ruleForm.getId(), paramMap);
        //创建规则日志
        try {
            MissionLogEntity missionLogDO=new MissionLogEntity();
            //TODO 取出用户信息
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setUserId(user.getId());
            missionLogDO.setOperationType(LogTypeEnum.FilterModify.name());
            missionLogDO.setLogDesc(JSON.toJSONString(ruleForm));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }
}
