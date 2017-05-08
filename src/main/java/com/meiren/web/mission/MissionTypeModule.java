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
import com.meiren.mission.result.MissionConditionListResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.service.*;
import com.meiren.mission.service.entity.MissionLogEntity;
import com.meiren.mission.service.entity.MissionTypeEntity;
import com.meiren.mission.service.param.MissionTypeParam;
import com.meiren.param.MissionTypeCreateParam;
import com.meiren.param.MissionTypeModifyParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/3/13.
 */
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.mission.type.index"})
@Controller
@RequestMapping("/mission")
public class MissionTypeModule {

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
     * 获取类型列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/typeIndex")
    public ModelAndView typeIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        String typeNameSearch = RequestUtil.getStringTrans(request, "typeNameSearch");
        Long typeIdSearch = RequestUtil.getLong(request, "typeIdSearch");

        String createTimeBeginSearch = RequestUtil.getStringTrans(request, "createTimeBeginSearch");
        String createTimeEndSearch = RequestUtil.getStringTrans(request, "createTimeEndSearch");
        MissionTypeParam missionTypeParam = new MissionTypeParam();
        missionTypeParam.setPage(pageNum);
        missionTypeParam.setSize(pageSize);
        if (StringUtils.isNotBlank(createTimeBeginSearch)) {
            missionTypeParam.setCreateTimeBegin(DateUtils.parseDate(createTimeBeginSearch, timePatternWithSec));
        }
        if (StringUtils.isNotBlank(createTimeEndSearch)) {
            missionTypeParam.setCreateTimeEnd(DateUtils.parseDate(createTimeEndSearch, timePatternWithSec));
        }
        missionTypeParam.setTypeId(typeIdSearch);
        missionTypeParam.setTypeNameDesc(typeNameSearch);
        ApiResult apiResult = missionTypeService.getMissionTypePage(missionTypeParam);
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
        modelAndView.addObject("typeNameSearch", typeNameSearch);
        modelAndView.addObject("typeIdSearch", typeIdSearch);
        modelAndView.addObject("createTimeBeginSearch", createTimeBeginSearch);
        modelAndView.addObject("createTimeEndSearch", createTimeEndSearch);
        modelAndView.setViewName("/mission/typeIndex");
        return modelAndView;
    }


    @RequestMapping("/missionTypeCreate")
    @ResponseBody
    public ApiResult missionTypeCreate(HttpServletRequest request, MissionTypeCreateParam missionTypeCreateParam) {
        ApiResult apiResult = new ApiResult();
        if (StringUtils.isBlank(missionTypeCreateParam.getTypeName())) {
            apiResult.setError(ResultCode.MISSION_TYPE_NAME_EMPTY.getCode(),ResultCode.MISSION_TYPE_NAME_EMPTY.getMessage());
            return apiResult;
        }
        if (StringUtils.isBlank(missionTypeCreateParam.getTypeNameDesc())) {
            apiResult.setError(ResultCode.MISSION_TYPE_NAME_DESC_EMPTY.getCode(),ResultCode.MISSION_TYPE_NAME_DESC_EMPTY.getMessage());
            return apiResult;
        }


        //查看是否存在相同的任务类型Name
        ApiResult key = missionTypeService.getByTypeName(missionTypeCreateParam.getTypeName());
        if (key.isSuccess() && key.getData() != null) {
            apiResult.setError(ResultCode.MISSION_TYPE_ALREADY_EXIST.getCode(),ResultCode.MISSION_TYPE_ALREADY_EXIST.getMessage());
            return apiResult;
        }
        String associateRuleDesc = "";
        String associateRule = missionTypeCreateParam.getAssociateRule();
        String[] split = associateRule.split(",");
        List<Long> idList = new ArrayList<>();
        for (String id : split) {
            if (StringUtils.isNotBlank(id)) {
                idList.add(Long.parseLong(id));
            }
        }
        if (idList.size() > 0) {
            ApiResult conditionIdListResult = missionConditionService.getByConditionIdList(idList);
            List<MissionConditionListResult> missionConditionListResultList = (List<MissionConditionListResult>) conditionIdListResult.getData();
            if (missionConditionListResultList.size() > 0) {
                for (MissionConditionListResult missionConditionListResult : missionConditionListResultList) {
                    associateRuleDesc= associateRuleDesc.concat(missionConditionListResult.getDesc()).concat(",");
                }
                associateRuleDesc= associateRuleDesc.substring(0, associateRuleDesc.length() - 1);
            }

        }
        MissionTypeEntity missionTypeEntity = new MissionTypeEntity();
        missionTypeEntity.setTypeNameDesc(missionTypeCreateParam.getTypeNameDesc());
        missionTypeEntity.setTypeName(missionTypeCreateParam.getTypeName());
        missionTypeEntity.setAssociateRule(",".concat(associateRule).concat(","));
        missionTypeEntity.setAssociateRuleDesc(associateRuleDesc);
        missionTypeEntity.setDescription(missionTypeCreateParam.getDescription());
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        missionTypeEntity.setUserId(user.getId());
        missionTypeEntity.setUserName(user.getUserName());
        missionTypeService.createMissionType(missionTypeEntity);

        //任务类型日志--创建
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionTypeOperate.name());
            missionLogDO.setLogDesc(JSON.toJSONString(missionTypeEntity));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    /**
     * 任务类型修改
     *
     * @param request
     * @param missionTypeModifyParam
     * @return
     */
    @RequestMapping("/missionTypeModify")
    @ResponseBody
    public ApiResult missionTypeModify(HttpServletRequest request, MissionTypeModifyParam missionTypeModifyParam) {
        ApiResult apiResult = new ApiResult();

        //关联的任务条件
        String associateRuleDesc = "";
        String associateRule = missionTypeModifyParam.getAssociateRule();
        String[] split = associateRule.split(",");
        List<Long> idList = new ArrayList<>();
        for (String id : split) {
            if (StringUtils.isNotBlank(id)) {
                idList.add(Long.parseLong(id));
            }
        }
        if (idList.size() > 0) {
            ApiResult conditionIdListResult = missionConditionService.getByConditionIdList(idList);
            List<MissionConditionListResult> missionConditionListResultList = (List<MissionConditionListResult>) conditionIdListResult.getData();
            if (missionConditionListResultList.size() > 0) {
                for (MissionConditionListResult missionConditionListResult : missionConditionListResultList) {
                    associateRuleDesc = associateRuleDesc.concat(missionConditionListResult.getDesc()).concat(",");
                }
                associateRuleDesc= associateRuleDesc.substring(0, associateRuleDesc.length() - 1);
            }

        }

        Map<String, Object> objectMap = new HashedMap();
        objectMap.put("associateRule", ",".concat(missionTypeModifyParam.getAssociateRule()).concat(","));
        objectMap.put("associateRuleDesc", associateRuleDesc);
        objectMap.put("typeNameDesc", missionTypeModifyParam.getTypeNameDesc());
        objectMap.put("description", missionTypeModifyParam.getDescription());
        apiResult = missionTypeService.updateMissionType(missionTypeModifyParam.getId(), objectMap);
        //是否需要记录

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionTypeOperate.name());
            objectMap.put("id",missionTypeModifyParam.getId());
            objectMap.put("operate","任务类型修改");
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
    @RequestMapping("/missionTypeDelete")
    @ResponseBody
    public ApiResult missionTypeDelete(HttpServletRequest request,@RequestParam("id") Long id) {
        ApiResult apiResult = new ApiResult();

        //TODO 查看有没有对应的类型被引用
        Integer missionCountByMissionType = missionTypeService.getMissionCountByMissionType(id);
        if(missionCountByMissionType>0){
            apiResult.setError(ResultCode.MISSION_TYPE_NOT_ALLOWED_DELETE.getCode(),ResultCode.MISSION_TYPE_NOT_ALLOWED_DELETE.getMessage());
            return apiResult;
        }
        //删除
        missionTypeService.deleteById(id);
        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionTypeOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("id",id);
            objectMap.put("operate","任务类型删除");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }


    @RequestMapping("/missionTypeDetail")
    @ResponseBody
    public ApiResult missionTypeDetail(@RequestParam("id") Long id) {
        return missionTypeService.getMissionTypeDetailById(id);
    }
}
