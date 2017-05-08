package com.meiren.web.mission;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.utils.RequestUtil;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.mission.enums.MissionInstanceStatusEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.service.*;
import com.meiren.mission.service.entity.MissionLogEntity;
import com.meiren.mission.service.vo.MissionInstanceFilterVO;
import com.meiren.mission.service.vo.MissionVO;
import com.meiren.tech.form.service.QuestionaireAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2016/12/28.
 */
@Controller
@RequestMapping("/mission/instance")
public class MissionInstanceModule {
    @Autowired
    private MissionActionDefineService missionActionDefineService;

    @Autowired
    private MissionDefineService missionDefineService;

    @Autowired
    private MissionInstanceService missionInstanceService;

    @Autowired
    private QuestionaireAnswerService questionaireAnswerService;

    @Autowired
    private MissionInstanceTraceService missionInstanceTraceService;

    @Autowired
    private ProducerLogService producerLogService;

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        Integer page = RequestUtil.getInteger(request,"page");
        if (page==null || page <= 0) {
            page = 1;
        }
        int pageSize = DEFAULT_ROWS;
        modelAndView.setViewName("/mission/instanceIndex");
        String channelIdsSearch = RequestUtil.getStringTrans(request, "channelIdsSearch");
        String missionNameSearch = RequestUtil.getStringTrans(request, "missionNameSearch");
        modelAndView.addObject("channelIdsSearch",channelIdsSearch);
        modelAndView.addObject("missionNameSearch",missionNameSearch);
        MissionInstanceFilterVO missionInstanceFilterVO=new MissionInstanceFilterVO();
        missionInstanceFilterVO.setPage(page);
        missionInstanceFilterVO.setSize(pageSize);
        missionInstanceFilterVO.setMissionName(missionNameSearch);
        missionInstanceFilterVO.setChannelIds(channelIdsSearch);
        ApiResult apiResult = missionInstanceService.getMissionInstancePageBoss(missionInstanceFilterVO);
        if (!apiResult.isSuccess()) {
            modelAndView.addObject("message", apiResult.getError());
            return modelAndView;
        }
        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }
        if (resultMap.get("data") != null) {
            List<MissionVO> resultList = (List<MissionVO>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", page);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);
        return modelAndView;
    }


    @RequestMapping("/build")
    public ModelAndView build(HttpServletRequest request,
                              HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/mission/missionInstanceDetail");
        return modelAndView;
    }

    /**
     * 根据id编号获取任务详情
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
        apiResult = missionInstanceService.getMissionInstanceDetailBoss(id);
        return apiResult;
    }


    @RequestMapping("/form")
    public ModelAndView form(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/mission/missionform");
        return modelAndView;
    }

    @RequestMapping(value = "/loadFormStructure",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult loadFormStructure(@NotNull Long missionInstanceId){
       ApiResult apiResult=new ApiResult();
        //读取值
        com.meiren.common.result.ApiResult apiResultAnswer = questionaireAnswerService.findQuestionaireAnswerVOByMissionInstanceId(missionInstanceId);
        Object data = apiResultAnswer.getData();
        apiResult.setData(data);
        return apiResult;
    }


    @RequestMapping(value = "/audit",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult auditMission(Long missionInstanceId,String status,HttpServletRequest request){
        ApiResult apiResult=new ApiResult();
        //读取值
        MissionInstanceStatusEnum missionInstanceStatusEnum = MissionInstanceStatusEnum.valueOf(status);
        if(missionInstanceStatusEnum!=MissionInstanceStatusEnum.SUCCESS && missionInstanceStatusEnum!=MissionInstanceStatusEnum.FAIL){
            apiResult.setError(ResultCode.MISSION_INSTANCE_MUST_SUCCESS_FAIL.getCode(),ResultCode.MISSION_INSTANCE_MUST_SUCCESS_FAIL.getMessage());
            return apiResult;
        }
        apiResult = missionInstanceTraceService.auditMissionInstance(missionInstanceId, missionInstanceStatusEnum);
        try {
            MissionLogEntity missionLogDO=new MissionLogEntity();
            //TODO 取出用户的id以及用户的姓名
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setUserId(user.getId());
            missionLogDO.setOperationType(LogTypeEnum.MissionAudit.name());
            missionLogDO.setLogDesc(JSON.toJSONString(missionInstanceId));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }
}
