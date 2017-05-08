package com.meiren.web.mission;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.constant.OssConstant;
import com.meiren.common.utils.DateUtils;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.service.*;
import com.meiren.mission.service.entity.MissionConditionEntity;
import com.meiren.mission.service.entity.MissionLogEntity;
import com.meiren.mission.service.param.MissionConditionParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/3/13.
 */
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.mission.condition.index"})
@Controller
@RequestMapping("/mission")
public class MissionConditionModule {

    @Autowired
    private MissionActionDefineService missionActionDefineService;

    @Autowired
    private MissionDefineService missionDefineService;

    @Autowired
    private MissionInstanceService missionInstanceService;

    @Autowired
    private MissionInstanceTraceService missionInstanceTraceService;

    @Autowired
    private ProducerLogService producerLogService;

    @Autowired
    private MissionChannelDefineService missionChannelDefineService;

    @Autowired
    private OssConstant ossConstant;

    @Autowired
    private MissionConditionService missionConditionService;

    @Autowired
    private MissionFilterRulesService missionFilterRulesService;

    @Autowired
    private MissionTypeService missionTypeService;

    @Autowired
    private MissionTemplateService missionTemplateService;

    private String timePatternWithSec ="yyyy-MM-dd HH:mm:ss";



    /**
     * 获取条件列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/conditionIndex")
    public ModelAndView conditionIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        String conditionNameSearch = RequestUtil.getStringTrans(request, "conditionNameSearch");
        Long conditionIdSearch = RequestUtil.getLong(request, "conditionIdSearch");

        String createTimeBeginSearch = RequestUtil.getStringTrans(request, "createTimeBeginSearch");
        String createTimeEndSearch = RequestUtil.getStringTrans(request, "createTimeEndSearch");
        MissionConditionParam missionConditionParam = new MissionConditionParam();
        missionConditionParam.setPage(pageNum);
        missionConditionParam.setSize(pageSize);
        if (StringUtils.isNotBlank(createTimeBeginSearch)) {
            missionConditionParam.setCreateTimeBegin(DateUtils.parseDate(createTimeBeginSearch, timePatternWithSec));
        }
        if (StringUtils.isNotBlank(createTimeEndSearch)) {
            missionConditionParam.setCreateTimeEnd(DateUtils.parseDate(createTimeEndSearch, timePatternWithSec));
        }
        missionConditionParam.setRuleId(conditionIdSearch);
        missionConditionParam.setRuleName(conditionNameSearch);
        ApiResult apiResult = missionConditionService.getMissionConditionPage(missionConditionParam);
        if (!apiResult.isSuccess()) {
            modelAndView.addObject("message", apiResult.getError());
            return modelAndView;
        }
        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }
        if (resultMap.get("data") != null) {
            modelAndView.addObject("basicVOList", resultMap.get("data"));
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);
        modelAndView.addObject("conditionNameSearch", conditionNameSearch);
        modelAndView.addObject("conditionIdSearch", conditionIdSearch);
        modelAndView.addObject("createTimeBeginSearch", createTimeBeginSearch);
        modelAndView.addObject("createTimeEndSearch", createTimeEndSearch);
        modelAndView.setViewName("/mission/conditionIndex");
        return modelAndView;
    }


    @RequestMapping("/missionConditionCreate")
    @ResponseBody
    public ApiResult missionConditionCreate(HttpServletRequest request, @RequestParam("conditionName") String conditionName, @RequestParam("conditionKey") String conditionKey) {
        ApiResult apiResult = new ApiResult();
        ApiResult key = missionConditionService.getByConditionKey(conditionKey);
        if (key.isSuccess() && key.getData() != null) {
            apiResult.setError(ResultCode.MISSION_CONDITION_KEY_EXIST.getCode(),ResultCode.MISSION_CONDITION_KEY_EXIST.getMessage());
            return apiResult;
        }
        MissionConditionEntity missionChannelDefineEntity = new MissionConditionEntity();
        missionChannelDefineEntity.setDesc(conditionName);
        missionChannelDefineEntity.setParamKey(conditionKey);
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        missionChannelDefineEntity.setUserId(user.getId());
        missionChannelDefineEntity.setUserName(user.getUserName());
        missionChannelDefineEntity.setIsValid(Boolean.TRUE);
        missionConditionService.createMissionCondition(missionChannelDefineEntity);

        //记录任务条件操作日志
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ConditionOperate.name());
            missionLogDO.setLogDesc(JSON.toJSONString(missionChannelDefineEntity));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }


    @RequestMapping("/missionConditionModify")
    @ResponseBody
    public ApiResult missionConditionModify(HttpServletRequest request, @RequestParam("conditionName") String conditionName, @RequestParam("id") Long id) {
        ApiResult apiResult = new ApiResult();
        Map<String, Object> objectMap = new HashedMap();
        objectMap.put("desc", conditionName);
        apiResult = missionConditionService.updateMissionCondition(id, objectMap);

        //记录任务条件操作日志---任务条件修改
        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ConditionOperate.name());
            objectMap.put("id",id);
            objectMap.put("operate","任务条件修改");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    /**
     * 启用或者禁用一个任务条件
     *
     * @param id
     * @param isValid
     * @return
     */
    @RequestMapping("/missionConditionValid")
    @ResponseBody
    public ApiResult missionConditionValid(HttpServletRequest request,@RequestParam("id") Long id, @RequestParam("isValid") Boolean isValid) {
        ApiResult apiResult = new ApiResult();
        //更新
        missionConditionService.validMissionCondition(id, isValid);

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ConditionOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("isValid", isValid);
            objectMap.put("id",id);
            objectMap.put("operate","任务条件启用/禁用");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }

    /**
     * 删除一个任务条件
     *
     * @param id
     * @return
     */
    @RequestMapping("/missionConditionDelete")
    @ResponseBody
    public ApiResult missionConditionDelete(HttpServletRequest request,@RequestParam("id") Long id) {
        ApiResult apiResult = new ApiResult();

        //TODO 首先查看有没有被引用的任务条件
        Integer referenceCountById = missionConditionService.getReferenceCountById(id);
        if(referenceCountById>0){
            apiResult.setError(ResultCode.MISSION_CONDITION_NOT_ALLOWED_DELETE.getCode(),ResultCode.MISSION_CONDITION_NOT_ALLOWED_DELETE.getMessage());
            return apiResult;
        }

        //删除
        missionConditionService.deleteMissionCondition(id);
        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ConditionOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("id",id);
            objectMap.put("operate","任务条件删除");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }
}
