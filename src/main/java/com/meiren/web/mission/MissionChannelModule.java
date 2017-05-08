package com.meiren.web.mission;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.constant.OssConstant;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.service.*;
import com.meiren.mission.service.entity.MissionChannelDefineEntity;
import com.meiren.mission.service.entity.MissionLogEntity;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/3/13.
 */
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.mission.channel.index"})
@Controller
@RequestMapping("/mission")
public class MissionChannelModule {
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
     * 获取渠道列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/channelIndex")
    public ModelAndView channelIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        String channelNameSearch = RequestUtil.getStringTrans(request, "channelNameSearch");
        String channelAppNameSearch = RequestUtil.getStringTrans(request, "channelAppNameSearch");
        Long channelIdSearch = RequestUtil.getLong(request, "channelIdSearch");
        Map<String, Object> searchParamMap = new HashedMap();
        searchParamMap.put("id", channelIdSearch);
        searchParamMap.put("channelName", channelNameSearch);
        searchParamMap.put("appName", channelAppNameSearch);
        ApiResult apiResult = missionChannelDefineService.searchMissionChannelDefine(searchParamMap, pageNum, pageSize);
        if (!apiResult.isSuccess()) {
            modelAndView.addObject("message", apiResult.getError());
            return modelAndView;
        }
        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }
        if (resultMap.get("data") != null) {
            List<MissionChannelDefineEntity> resultList = (List<MissionChannelDefineEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);
        modelAndView.addObject("channelNameSearch", channelNameSearch);
        modelAndView.addObject("channelAppNameSearch", channelAppNameSearch);
        modelAndView.addObject("channelIdSearch", channelIdSearch);
        modelAndView.setViewName("/mission/channelIndex");
        return modelAndView;
    }

//	/**
//	 * 任务通道列表----unused
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/missionChannel")
//	public ModelAndView missionChannel(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView modelAndView = new ModelAndView();
//		String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
//		int pageNum = Integer.valueOf(page);
//		if (pageNum <= 0) {
//			pageNum = 1;
//		}
//		int pageSize = DEFAULT_ROWS;
//		Map<String, Object> searchParamMap = new HashedMap();
//
//		ApiResult apiResult = missionChannelDefineService.searchMissionChannelDefine(searchParamMap, pageNum, pageSize);
//		if (!apiResult.isSuccess()) {
//			modelAndView.addObject("message", apiResult.getError());
//			return modelAndView;
//		}
//		Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();
//
//		if (resultMap.get("totalCount") != null) {
//			modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
//		}
//		if (resultMap.get("data") != null) {
//			List<MissionChannelDefineEntity> resultList = (List<MissionChannelDefineEntity>) resultMap.get("data");
//			modelAndView.addObject("basicVOList", resultList);
//		}
//		modelAndView.addObject("curPage", pageNum);
//		modelAndView.addObject("pageSize", DEFAULT_ROWS);
//		modelAndView.setViewName("/mission/missionChannel");
//		return modelAndView;
//	}

    /**
     * 创建任务渠道
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/missionChannelCreate")
    @ResponseBody
    public ApiResult missionChannelCreate(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        String appName = RequestUtil.getString(request, "appName");
        ApiResult byAppName =missionChannelDefineService.getByAppName(appName);
        if (byAppName.getData() != null) {
            apiResult.setError(ResultCode.MISSION_CHANNEL_ALREADY_EXISTS.getCode(),ResultCode.MISSION_CHANNEL_ALREADY_EXISTS.getMessage());
            return apiResult;
        }
        String channelName = RequestUtil.getString(request, "channelName");
        String appToken = RequestUtil.getString(request, "appToken");
        MissionChannelDefineEntity missionChannelDefineEntity = new MissionChannelDefineEntity();
        missionChannelDefineEntity.setChannelName(channelName);
        missionChannelDefineEntity.setAppName(appName);
        missionChannelDefineEntity.setAppToken(appToken);
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        missionChannelDefineEntity.setUserId(user.getId());
        missionChannelDefineEntity.setUserName(user.getUserName());
        missionChannelDefineEntity.setValid(Boolean.TRUE);
        apiResult = missionChannelDefineService.createMissionChannelDefine(missionChannelDefineEntity);

        //记录任务条件操作日志
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ChannelOperate.name());
            missionLogDO.setLogDesc(JSON.toJSONString(missionChannelDefineEntity));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }

    /**
     * 任务通道修改
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/missionChannelModify")
    @ResponseBody
    public ApiResult missionChannelModify(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id == null) {
            apiResult.setError(ResultCode.MISSION_CHANNELIDS_NOT_EMPTY.getCode(),ResultCode.MISSION_CHANNELIDS_NOT_EMPTY.getMessage());
            return apiResult;
        }
        String channelName = RequestUtil.getString(request, "channelName");
        String appName = RequestUtil.getString(request, "appName");
        String appToken = RequestUtil.getString(request, "appToken");
        if (StringUtils.isNotBlank(appName)) {
            ApiResult byAppName = missionChannelDefineService.getByAppName(appName);
            if (byAppName.getData() != null) {
                MissionChannelDefineEntity missionChannelDefineEntity = (MissionChannelDefineEntity) byAppName.getData();
                if (missionChannelDefineEntity.getId() != id) {
                    apiResult.setError(ResultCode.MISSION_CHANNEL_ALREADY_EXISTS.getCode(),ResultCode.MISSION_CHANNEL_ALREADY_EXISTS.getMessage());
                    return apiResult;
                }
            }
        }
        Map<String, Object> objectMap = new HashedMap();
        objectMap.put("channelName", channelName);
        objectMap.put("appName", appName);
        objectMap.put("appToken", appToken);
        apiResult = missionChannelDefineService.updateMissionChannelDefine(id, objectMap);
        //是否需要记录

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ChannelOperate.name());
            objectMap.put("id",id);
            objectMap.put("operate","任务渠道修改");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }

    @RequestMapping("/missionChannelValid")
    @ResponseBody
    public ApiResult missionChannelValid(@RequestParam("id") Long id, @RequestParam("isValid") Boolean isValid, HttpServletRequest request) {
        ApiResult apiResult = new ApiResult();
        ApiResult missionChannelDefine = missionChannelDefineService.findMissionChannelDefine(id);
        if (!missionChannelDefine.isSuccess() || missionChannelDefine.getData() == null) {
            apiResult.setError(ResultCode.MISSION_CHANNEL_NOT_FOUND.getCode(),ResultCode.MISSION_CHANNEL_NOT_FOUND.getMessage());
            return apiResult;
        }
        //更新
        missionChannelDefineService.validMissionChannel(id, isValid);

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ChannelOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("isValid", isValid);
            objectMap.put("id",id);
            objectMap.put("operate","任务渠道启用/禁用");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }

    @RequestMapping("/missionChannelDelete")
    @ResponseBody
    public ApiResult missionChannelDelete(@RequestParam("id") Long id,HttpServletRequest request) {
        ApiResult apiResult = new ApiResult();
        ApiResult missionChannelDefine = missionChannelDefineService.findMissionChannelDefine(id);
        if (!missionChannelDefine.isSuccess() || missionChannelDefine.getData() == null) {
            apiResult.setError(ResultCode.MISSION_CHANNEL_NOT_FOUND.getCode(),ResultCode.MISSION_CHANNEL_NOT_FOUND.getMessage());
            return apiResult;
        }

        //TODO　首先查看有没有被引用的任务通道
        Integer missionCountByChannelId = missionChannelDefineService.getMissionCountByChannelId(id);
        if(missionCountByChannelId>0){
            apiResult.setError(ResultCode.MISSION_CHANNEL_NOT_ALLOWED_DELETE.getCode(),ResultCode.MISSION_CHANNEL_NOT_ALLOWED_DELETE.getMessage());
            return apiResult;
        }

        //更新
        missionChannelDefineService.deleteMissionChannel(id);

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.ChannelOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("id",id);
            objectMap.put("operate","任务渠道删除");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }
}
