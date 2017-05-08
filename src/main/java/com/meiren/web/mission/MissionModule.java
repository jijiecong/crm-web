package com.meiren.web.mission;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.api.AclApiService;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.constant.OssConstant;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.DateUtils;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.*;
import com.meiren.mission.result.*;
import com.meiren.mission.result.TemplateQueryResult;
import com.meiren.mission.service.*;
import com.meiren.mission.service.entity.*;
import com.meiren.mission.service.vo.*;
import com.meiren.oss.StsInfo;
import com.meiren.oss.sts.OssUtils;
import com.meiren.utils.MissionUtils;
import com.meiren.vo.FileUploadVO;
import com.meiren.vo.MissionContentTypeVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2016/12/21.
 */
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.mission.manager"})
@Controller
@RequestMapping("/mission")
public class MissionModule {

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



    @Autowired
	private AclApiService aclClient;


	private String timePatternWithSec ="yyyy-MM-dd HH:mm:ss";


    @RequestMapping("/missionIndex")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");

        Long id = RequestUtil.getLong(request, "missionIdSearch");
        String name = RequestUtil.getStringTrans(request, "missionNameSearch");
        String startTime = RequestUtil.getString(request, "missionStartTimeSearch");
        String endTime = RequestUtil.getString(request, "missionEndTimeSearch");
        String createStartTime = RequestUtil.getString(request, "missionCreateStartTimeSearch");
        String createEndTime = RequestUtil.getString(request, "missionCreateEndTimeSearch");
        Integer sort = RequestUtil.getInteger(request, "sortSearch");
        String statesStr=RequestUtil.getString(request,"stateSearch");
        String typeStr= RequestUtil.getString(request,"typeSearch");
        String channelStr=RequestUtil.getString(request,"channelSearch");
        String createUsersStr=RequestUtil.getString(request,"createUserSearch");

        List<Integer> state = string2IntegerList(statesStr);
        List<String> type = string2StringList(typeStr);
        List<String> channel =string2PercentStringList(channelStr);
        List<Long> createUser = string2LongList(createUsersStr);//userId list

        modelAndView.addObject("missionIdSearch", id);
        modelAndView.addObject("missionNameSearch", name);
        modelAndView.addObject("missionStartTimeSearch", startTime);
        modelAndView.addObject("missionEndTimeSearch", endTime);
        modelAndView.addObject("missionCreateStartTimeSearch", createStartTime);
        modelAndView.addObject("missionCreateEndTimeSearch", createEndTime);
        modelAndView.addObject("stateSearch", statesStr);
        modelAndView.addObject("typeSearch", typeStr);
        modelAndView.addObject("channelSearch", channelStr);
        modelAndView.addObject("createUserSearch", createUsersStr);
        modelAndView.addObject("sortSearch", sort);
        modelAndView.setViewName("/mission/missionIndex");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
            int pageSize = DEFAULT_ROWS;
            MissionDefineFilterVO missionDefineFilterVO = new MissionDefineFilterVO();
            missionDefineFilterVO.setPage(pageNum);
            missionDefineFilterVO.setSize(pageSize);
            if (id != null) {
                missionDefineFilterVO.setId(id);
            }
            if (StringUtils.isNotEmpty(name)) {
                missionDefineFilterVO.setMissionName("%"+name+"%");
            }

            if (StringUtils.isNotEmpty(startTime)) {
                missionDefineFilterVO.setStartTime(DateUtils.parseDate(startTime, timePatternWithSec));
            }

            if (StringUtils.isNotEmpty(endTime)) {
            missionDefineFilterVO.setEndTime(DateUtils.parse(endTime, timePatternWithSec));
        }

        if (StringUtils.isNotEmpty(createStartTime)) {
            missionDefineFilterVO.setCreateStartTime(DateUtils.parse(createStartTime, timePatternWithSec));
        }

        if (StringUtils.isNotEmpty(createEndTime)) {
            missionDefineFilterVO.setCreateEndTime(DateUtils.parse(createEndTime, timePatternWithSec));
        }
        missionDefineFilterVO.setSort(sort == null ? 1 : sort);

        if (state != null) {
            missionDefineFilterVO.setState(state);
        }
        if (type != null) {
            missionDefineFilterVO.setType(type);
        }

        if (channel != null) {
            missionDefineFilterVO.setChannel(channel);
        }


        if (createUser != null) {
            missionDefineFilterVO.setCreateUserId(createUser);
        }

        ApiResult apiResult = missionDefineService.getMissionDefinePage(missionDefineFilterVO);
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
            if (resultList.size()<1 && pageNum>1){
                pageNum=pageNum-1;
                missionDefineFilterVO.setPage(pageNum);
                apiResult=missionDefineService.getMissionDefinePage(missionDefineFilterVO);
                resultMap=(Map<String,Object>)apiResult.getData();
                resultList=(List<MissionVO>) resultMap.get("data");
            }
            formatMissionType(resultList);
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);

        //查询所有的任务类型
        ApiResult typeApiResult = missionTypeService.getAllMissionType();
        if (!typeApiResult.isSuccess()) {
            modelAndView.addObject("message", typeApiResult.getError());
            return modelAndView;
        }

        List<MissionTypeListResult> totalTypes = (List<MissionTypeListResult>) typeApiResult.getData();
        modelAndView.addObject("totalTypes", totalTypes);

        //查询所有渠道
        ApiResult channelApiResult = missionChannelDefineService.listMissionChannelDefine();
        if (!channelApiResult.isSuccess()) {
            modelAndView.addObject("message", channelApiResult.getError());
            return modelAndView;
        }
        List<MissionChannelListResult> totalChannels = (List<MissionChannelListResult>) channelApiResult.getData();
        modelAndView.addObject("totalChannels", totalChannels);


        //查询所有具有创建资格的用户 即访问列表的权限
//        com.meiren.common.result.ApiResult userApiResult=aclClient.getAllUserByPrivilege("meiren.acl.mbc.backend.mission.list");
//		if(StringUtils.isNotEmpty(userApiResult.getError())){
//			modelAndView.addObject("message",userApiResult.getError());
//			return modelAndView;
//		}
//		List<AclUserEntity> userEntityList=(List<AclUserEntity>) userApiResult.getData();
//		modelAndView.addObject("totalUsers",userEntityList);

        return modelAndView;
    }

    private void formatMissionType(List<MissionVO> missionVOList) {
        if (CollectionUtils.isNotEmpty(missionVOList)) {
            Iterator<MissionVO> iterator = missionVOList.iterator();
            while (iterator.hasNext()) {
                MissionVO vo = iterator.next();
                vo.setMissionType(MissionTypeEnum.valueOf(vo.getMissionType()).name);
            }

        }
    }



    private List<Integer> string2IntegerList(String source) {
        List<Integer> integerList = null;
        if (StringUtils.isNotEmpty(source)) {
            String[] strs=source.split(",");
            integerList = new ArrayList<>(strs.length);
            for (String str : strs) {
                integerList.add(Integer.parseInt(str));
            }
        }
        return integerList;
    }

    private List<Long> string2LongList(String source) {
        List<Long> integerList = null;
        if (StringUtils.isNotEmpty(source)) {
            String[] strs=source.split(",");
            integerList = new ArrayList<>(strs.length);
            for (String str : strs) {
                integerList.add(Long.parseLong(str));
            }
        }
        return integerList;
    }

    private List<String> string2StringList(String source){
        List<String> stringList=null;
        if (StringUtils.isNotEmpty(source)){
            stringList=Arrays.asList(source.split(","));
        }
        return stringList;
    }

    private List<String> string2PercentStringList(String source){
        List<String> stringList=null;
        if (StringUtils.isNotEmpty(source)){
            String[] strs=source.split(",");
            stringList=new ArrayList<>(strs.length);
            for (String str:strs){
                stringList.add("%"+str+"%");
            }
        }
        return stringList;
    }


    @RequestMapping(value = "/allCreateUsers")
    @ResponseBody
    public ApiResult allCreateUsers(){
        com.meiren.common.result.ApiResult apiResult=aclClient.getAllUserByPrivilege("meiren.acl.mbc.backend.mission.list");
//		if(StringUtils.isNotEmpty(userApiResult.getError())){
//			modelAndView.addObject("message",userApiResult.getError());
//			return modelAndView;
//		}
////		List<AclUserEntity> userEntityList=(List<AclUserEntity>) userApiResult.getData();
        return apiResult;
    }


    @RequestMapping(value = "/toDetail")
    public ModelAndView toDetail(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("missionDetailUrl",RequestUtil.getString(request,"missionDetailUrl"));
        modelAndView.addObject("missionHtmlUrl",RequestUtil.getString(request,"missionHtmlUrl"));

        modelAndView.setViewName("/mission/missionDetail");
        return modelAndView;
    }
    /**
     * 根据id编号获取任务详情
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getDetail(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id == null) {
            apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
            return apiResult;
        }
        apiResult = missionDefineService.getMissionDefineDetail(id);
        return apiResult;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult missionDelete(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id == null) {
            apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
            return apiResult;
        }
        apiResult = missionDefineService.setMissionDefineState(id, MissionDefineStateEnum.OFFLINE.typeValue);
        return apiResult;
    }

    // @RequestMapping("/top")
    // @ResponseBody
    // public ApiResult missionTop(HttpServletRequest request,
    // HttpServletResponse response){
    // ApiResult apiResult=new ApiResult();
    // Long id = RequestUtil.getLong(request, "id");
    // if(id==null){
    // apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
    // return apiResult;
    // }
    // Map<String, Object> modifyParam = new HashMap<String, Object>();
    // modifyParam.put("isTop", 1);
    // apiResult = missionDefineService.updateMissionDefine(id, modifyParam);
    // return apiResult;
    // }
    //
    // @RequestMapping("/cancelTop")
    // @ResponseBody
    // public ApiResult cancelTop(HttpServletRequest request,
    // HttpServletResponse response){
    // ApiResult apiResult=new ApiResult();
    // Long id = RequestUtil.getLong(request, "id");
    // if(id==null){
    // apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
    // return apiResult;
    // }
    // Map<String, Object> modifyParam = new HashMap<String, Object>();
    // modifyParam.put("isTop", 0);
    // apiResult = missionDefineService.updateMissionDefine(id, modifyParam);
    // return apiResult;
    // }

    @RequestMapping("/loadContentEnum")
    @ResponseBody
    public ApiResult loadContentEnum(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        MissionDefineContentEnum[] values = MissionDefineContentEnum.values();
        List<MissionContentTypeVO> missionContentTypeVOList = new ArrayList<MissionContentTypeVO>();
        for (MissionDefineContentEnum missionDefineContentEnum : values) {
            MissionContentTypeVO missionContentTypeVO = new MissionContentTypeVO();
            missionContentTypeVO.setName(missionDefineContentEnum.name());
            missionContentTypeVO.setDesc(missionDefineContentEnum.name);
            missionContentTypeVOList.add(missionContentTypeVO);
        }
        apiResult.setData(missionContentTypeVOList);
        return apiResult;
    }


    @AuthorityToken(needToken = {"meiren.acl.boss.user.publish"})
	@RequestMapping("/modify")
	@ResponseBody
	public ApiResult missionModify(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        String idStr = RequestUtil.getString(request, "id");
        Long id = Long.parseLong(idStr);
        //获取当前登录的用户
        AclUserEntity operateUser = (AclUserEntity) request.getSession().getAttribute("user");
        if (operateUser == null) {
            apiResult.setError(ResultCode.USER_ID_NULL.getCode(),ResultCode.USER_ID_NULL.getMessage());
            return apiResult;
        }

        Integer toState = RequestUtil.getInteger(request, "state");
        ApiResult missionApiResult = missionDefineService.findMissionDefine(id);
        if (StringUtils.isNotEmpty(missionApiResult.getError())) {
            return missionApiResult;
        }
        MissionDefineEntity missionDefineEntity = (MissionDefineEntity) missionApiResult.getData();
        Integer fromState = missionDefineEntity.getState();
        MissionDefineStateEnum fromStateEnum=MissionDefineStateEnum.getByTypeValue(fromState);
        MissionDefineStateEnum toStateEnum=MissionDefineStateEnum.getByTypeValue(toState);

        if (MissionDefineStateEnum.PUBLISH.equals(toStateEnum)){//上架
            //如果是从下架状态改回上架状态 需判断是否满足再次上架的条件
            if (MissionDefineStateEnum.OFFLINE.equals(fromStateEnum)){
                if (DateUtils.getCurDate().getTime() > missionDefineEntity.getEndTime().getTime()) {//已经过了结束时间
                    apiResult.setError(ResultCode.MISSION_DEFINE_TIME_EXPIRE.getCode(),ResultCode.MISSION_DEFINE_TIME_EXPIRE.getMessage());
                    return apiResult;
                }
                //奖励是否发送完
                ApiResult awardSurplusResult=missionDefineService.awardSurplus(id);

//                ApiResult deliveryApiResult=missionInstanceTraceService.getTotalAwardAmountByMissionId(id);
//                if (StringUtils.isNotEmpty(deliveryApiResult.getError())){
//                    return deliveryApiResult;
//                }
//                Long delivery=(Long)deliveryApiResult.getData();
                if (Boolean.FALSE.equals(awardSurplusResult.getData())) {//奖励发送完了
                    apiResult.setError(ResultCode.MISSION_AWARD_SURPLUS.getCode(),ResultCode.MISSION_AWARD_SURPLUS.getMessage());
                    return apiResult;
                }
            }

        }else if (MissionDefineStateEnum.OFFLINE.equals(toStateEnum)){//下架
            if (MissionDefineStateEnum.PENDING.equals(fromStateEnum)){//限制直接从审核状态下架
                apiResult.setError(ResultCode.MISSION_DEFINE_PENDING.getCode(),ResultCode.MISSION_DEFINE_PENDING.getMessage());
                return apiResult;
            }
        }else {//其他状态 限制操作
            apiResult.setError(ResultCode.MISSION_DEFINE_STATE_ILLEGAL.getCode(),ResultCode.MISSION_DEFINE_STATE_ILLEGAL.getMessage());
            return apiResult;
        }
            Map<String, Object> modifyMap = new HashMap();
            modifyMap.put("state", toState);
            modifyMap.put("operateUserId", operateUser.getId());
            modifyMap.put("operateUserName", operateUser.getUserName());

            apiResult = missionDefineService.onLineOfflineMission(id, modifyMap);
            //任务定义审核（上下架）日志
            try {
                MissionLogEntity missionLogDO = new MissionLogEntity();
                missionLogDO.setOperationType(LogTypeEnum.MissionAudit.name());
                missionLogDO.setUserId(operateUser.getId());
                missionLogDO.setUserName(operateUser.getUserName());
                modifyMap.put("id", id);
                missionLogDO.setLogDesc(JSON.toJSONString(modifyMap));
                producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return apiResult;
    }

    @RequestMapping("/build")
    public ModelAndView build(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/mission/missionbuilder");
        return modelAndView;
    }

    // @RequestMapping("/missionType")
    // public ModelAndView missionType(HttpServletRequest request,
    // HttpServletResponse response) {
    // ModelAndView modelAndView = new ModelAndView();
    // String page = request.getParameter("page") == null ? "1" :
    // request.getParameter("page");
    // int pageNum = Integer.valueOf(page);
    // if (pageNum <= 0) {
    // pageNum = 1;
    // }
    // int pageSize = DEFAULT_ROWS;
    // Map<String, Object> searchParamMap=new HashedMap();
    // ApiResult apiResult =
    // missionTypeService.searchMissionType(searchParamMap, pageNum, pageSize);
    // if (!apiResult.isSuccess()) {
    // modelAndView.addObject("message", apiResult.getError());
    // return modelAndView;
    // }
    // Map<String, Object> resultMap = (Map<String, Object>)
    // apiResult.getData();
    //
    // if (resultMap.get("totalCount") != null) {
    // modelAndView.addObject("totalCount",
    // Integer.valueOf(resultMap.get("totalCount").toString()));
    // }
    // if (resultMap.get("data") != null) {
    // List<MissionTypeEntity> resultList = (List<MissionTypeEntity>)
    // resultMap.get("data");
    // modelAndView.addObject("basicVOList", resultList);
    // }
    // modelAndView.addObject("curPage", pageNum);
    // modelAndView.addObject("pageSize", DEFAULT_ROWS);
    // modelAndView.setViewName("/mission/missionType");
    // return modelAndView;
    // }
    //
    // /**
    // * 任务类型创建
    // * @param request
    // * @param response
    // * @return
    // */
    // @RequestMapping("/missionTypeCreate")
    // @ResponseBody
    // public ApiResult missionTypeCreate(HttpServletRequest request,
    // HttpServletResponse response){
    // ApiResult apiResult=new ApiResult();
    // MissionTypeEntity missionTypeEntity=new MissionTypeEntity();
    // String name = RequestUtil.getString(request, "name");
    // Integer isUseForm = RequestUtil.getInteger(request, "isUseForm");
    // Integer isSingleInvoke = RequestUtil.getInteger(request,
    // "isSingleInvoke");
    // missionTypeEntity.setName(name);
    // missionTypeEntity.setIsSingleInvoke(isSingleInvoke);
    // missionTypeEntity.setIsUseForm(isUseForm);
    // apiResult = missionTypeService.createMissionType(missionTypeEntity);
    // return apiResult;
    // }

    // /**
    // * 任务类型修改
    // * @param request
    // * @param response
    // * @return
    // */
    // @RequestMapping("/missionTypeModify")
    // @ResponseBody
    // public ApiResult missionTypeModify(HttpServletRequest request,
    // HttpServletResponse response){
    // ApiResult apiResult=new ApiResult();
    // Long id = RequestUtil.getLong(request, "id");
    // if(id==null){
    // apiResult.setError(ResultCode.MISSION_DEFINE_ID_EMPTY.getCode(),ResultCode.MISSION_DEFINE_ID_EMPTY.getMessage());
    // return apiResult;
    // }
    // Map<String, Object> modifyParam=new HashedMap();
    // Integer isUseForm = RequestUtil.getInteger(request, "isUseForm");
    // modifyParam.put("isUseForm",isUseForm);
    // Integer isSingleInvoke = RequestUtil.getInteger(request,
    // "isSingleInvoke");
    // modifyParam.put("isSingleInvoke",isSingleInvoke);
    // String name = RequestUtil.getString(request, "name");
    // modifyParam.put("name",name);
    // apiResult = missionTypeService.updateMissionType(id,modifyParam);
    // return apiResult;
    // }

    /**
     * 任务修改
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/missionDefineModify")
    @ResponseBody
    public ApiResult missionDefineModify(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        Long userId = RequestUtil.getLong(request, "userId");
        String userName = RequestUtil.getString(request, "userName");
        String missionType = RequestUtil.getString(request, "missionType");
        String missionName = RequestUtil.getString(request, "missionName");
        Integer state = RequestUtil.getInteger(request, "state");
        Long totalSnapshot = RequestUtil.getLong(request, "totalSnapshot");
        String beginTime = RequestUtil.getString(request, "beginTime");
        String endTime = RequestUtil.getString(request, "endTime");
        String channelIds = RequestUtil.getString(request, "channelIds");
        String content = RequestUtil.getString(request, "content");
        Long receiveLimit = RequestUtil.getLong(request, "receiveLimit");
        Long missionFilterId = RequestUtil.getLong(request, "missionFilterId");
        String html = RequestUtil.getString(request, "html");
        Integer isTop = RequestUtil.getInteger(request, "isTop");
        Integer isTimely = RequestUtil.getInteger(request, "isTimely");
        ApiResult detailResult = missionDefineService.getMissionDefineDetail(id);
        if (!detailResult.isSuccess()) {
            return detailResult;
        }
        // ===业务逻辑判断====任务定义与任务实例是复制的 因此需要进行同步更新
        // 如果有任务实例则不允许改动===== 过滤器采用外键关联，可以直接修改过滤器中内容
        // ApiResult exist =
        // missionInstanceService.existMissionInstanceByMissionId(id);
        // Boolean existData = (Boolean) exist.getData();
        // if(existData){
        // apiResult.setError(ResultCode.MISSION_INSTANCE_EXIST.getCode(),ResultCode.MISSION_INSTANCE_EXIST.getMessage());
        // return apiResult;
        // }
        // ===业务逻辑判断====结束

        Map<String, Object> modifyParam = new HashedMap();
        MissionVO missionVO = (MissionVO) detailResult.getData();
        if (!missionVO.getState().equals(state)) {
            modifyParam.put("state", state);
        }
        if (missionVO.getIsTop() != null && missionVO.getIsTop().equals(isTop)) {

        } else {
            modifyParam.put("isTop", isTop);
        }
        if (missionVO.getIsTimely() != null && missionVO.getIsTimely().equals(isTimely)) {

        } else {
            modifyParam.put("isTimely", isTimely);
        }

        if (!missionVO.getMissionType().equals(missionType)) {
            modifyParam.put("missionType", missionType);
        }
        if (!missionVO.getMissionName().equals(missionName)) {
            modifyParam.put("missionName", missionName);
        }
        if (!missionVO.getTotalSnapshot().equals(totalSnapshot)) {
            modifyParam.put("totalSnapshot", totalSnapshot);
        }

        if (!StringUtils.isBlank(beginTime)) {
            modifyParam.put("beginTime", beginTime);
        }
        if (!StringUtils.isBlank(endTime)) {
            modifyParam.put("endTime", endTime);
        }
        if (!StringUtils.isBlank(channelIds)) {
            // 比较channelId是否一样
            String oldChannelIdList = missionVO.getChannelIds();
            String[] oldSplit = oldChannelIdList.split(",");
            Set<String> oldSet = new HashSet<>();
            for (String temp : oldSplit) {
                if (!StringUtils.isBlank(temp)) {
                    oldSet.add(temp);
                }
            }
            String[] newSplit = channelIds.split(",");
            Set<String> newSet = new HashSet<>();
            for (String temp : newSplit) {
                if (!StringUtils.isBlank(temp)) {
                    newSet.add(temp);
                }
            }
            // TODO 是否允许：已经领取过任务的通道不在当前新的通道列表中
            modifyParam.put("channelIds", channelIds);
        }
        if (!StringUtils.isBlank(content)) {
            modifyParam.put("content", content);
        }
        if (receiveLimit != null) {
            modifyParam.put("receiveLimit", receiveLimit);
        }
        if (!StringUtils.isBlank(html)) {
            modifyParam.put("html", html);
        }
        if (missionFilterId != null) {
            // filter与missionDefine 是一一对应的
            ApiResult existMissionByFilterId = missionDefineService.existMissionByFilterId(id, missionFilterId);
            Boolean data = (Boolean) existMissionByFilterId.getData();
            if (data.equals(Boolean.TRUE)) {
                apiResult.setError(ResultCode.MISSION_FILTER_ALREADY_HAVE_MISSION.getCode(),ResultCode.MISSION_FILTER_ALREADY_HAVE_MISSION.getMessage());
                return apiResult;
            }
            modifyParam.put("missionFilterId", missionFilterId);
        }

        missionDefineService.updateMissionDefine(id, modifyParam);
        // 任务定义修改日志
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionModify.name());
            modifyParam.put("id", id);
            missionLogDO.setLogDesc(JSON.toJSONString(modifyParam));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;

    }

    /**
     * 任务通道获取
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/listMissionChannel")
    @ResponseBody
    public ApiResult listMissionChannel(HttpServletRequest request, HttpServletResponse response) {

        ApiResult apiResult = new ApiResult();
        apiResult = missionChannelDefineService.listMissionChannelDefine();
        return apiResult;
    }

 /*   @RequestMapping("/missionDefineCreate")
    @ResponseBody
    public ApiResult missionDefineCreate(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();

        MissionDefineEntity missionDefineEntity = new MissionDefineEntity();
        // TODO 应该是从上下文中去取得
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        String missionType = RequestUtil.getString(request, "missionType");
        String missionName = RequestUtil.getString(request, "missionName");
        Integer state = RequestUtil.getInteger(request, "state");
        Long totalSnapshot = RequestUtil.getLong(request, "totalSnapshot");
        String beginTime = RequestUtil.getString(request, "beginTime");
        String endTime = RequestUtil.getString(request, "endTime");
        String channelIds = RequestUtil.getString(request, "channelIds");
        String content = RequestUtil.getString(request, "content");
        Long missionFilterId = RequestUtil.getLong(request, "missionFilterId");
        Long receiveLimit = RequestUtil.getLong(request, "receiveLimit");
        String html = RequestUtil.getString(request, "html");
        missionDefineEntity.setCreateUserId(user.getId());
        missionDefineEntity.setCreateUserName(user.getUserName());
        missionDefineEntity.setMissionType(missionType);
        missionDefineEntity.setMissionName(missionName);
        missionDefineEntity.setState(state);
        missionDefineEntity.setTotalSnapshot(totalSnapshot);
        missionDefineEntity.setBeginTime(DateUtils.parseDate(beginTime, "yyyy-MM-dd HH:mm"));
        missionDefineEntity.setEndTime(DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm"));
        missionDefineEntity.setChannelIds(channelIds);
        missionDefineEntity.setContent(content);
        missionDefineEntity.setMissionFilterId(missionFilterId);
        missionDefineEntity.setReceiveLimit(receiveLimit);
        missionDefineEntity.setHtml(html);
        if (missionFilterId == null) {
            apiResult.setError(ResultCode.MISSION_FILTER_ALREADY_HAVE_MISSION.getCode(),ResultCode.MISSION_FILTER_ALREADY_HAVE_MISSION.getMessage());
            return apiResult;
        }
        apiResult = missionDefineService.createMissionDefine(missionDefineEntity);
        // 任务定义创建日志
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            // TODO 取出用户的id以及用户的姓名
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionCreate.name());
            missionLogDO.setLogDesc(JSON.toJSONString(missionDefineEntity));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }*/

    @RequestMapping("/missionAvailable")
    @ResponseBody
    public ApiResult missionAvailable(HttpServletRequest request, HttpServletResponse response) {

        ApiResult allAvailableMissionDefine = missionDefineService.getAllAvailableMissionDefine();
        return allAvailableMissionDefine;
    }

    @ResponseBody
    @RequestMapping(value = "importFromImg", method = RequestMethod.POST)
    public ApiResult importFromImg(@RequestParam("files") ArrayList<MultipartFile> files) {
        ApiResult apiResult = new ApiResult();
        StsInfo stsInfo = OssUtils.getStsInfo(Long.valueOf(ossConstant.getUserId()), ossConstant.getOssSecretKey(),
                ossConstant.getOssStsUrl());
        System.out.print(files);
        if (stsInfo == null) {
            apiResult.setError(ResultCode.MISSION_GET_OSS_STS_FAILED.getCode(),ResultCode.MISSION_GET_OSS_STS_FAILED.getMessage());
        }
        // 只有图片格式的才会有thumbnailUrl
        List<FileUploadVO> fileUploadVOList = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String path = OssUtils.uploadFile(stsInfo, multipartFile);
            path=OssUtils.enableCdn(stsInfo.getBucketName(),stsInfo.getEndPoint(),path,ossConstant.getCdnPath());
            FileUploadVO fileUploadVO = new FileUploadVO();
            fileUploadVO.setUrl(path);
            fileUploadVO.setThumbnailUrl(path);
            fileUploadVO.setName(multipartFile.getName());
            fileUploadVO.setSize(multipartFile.getSize());
            fileUploadVO.setImage(multipartFile.getContentType().contains("image"));
            fileUploadVOList.add(fileUploadVO);
        }
        apiResult.setData(fileUploadVOList);
        return apiResult;
    }

    @ResponseBody
    @RequestMapping(value = "uploadImage", method = RequestMethod.POST)
    public Map uploadImage(@RequestParam("upfile") MultipartFile upfile) {

        Map map = new HashedMap();
        StsInfo stsInfo = OssUtils.getStsInfo(Long.valueOf(ossConstant.getUserId()), ossConstant.getOssSecretKey(),
                ossConstant.getOssStsUrl());
        String path = OssUtils.uploadFile(stsInfo, upfile);
        path=OssUtils.enableCdn(stsInfo.getBucketName(),stsInfo.getEndPoint(),path,ossConstant.getCdnPath());
        System.out.print(path);


        map.put("success", true);
        map.put("code", 0);
        map.put("item_url", path);


        return map;
    }

    @RequestMapping("/upload")
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/mission/fileUpload");
        return modelAndView;
    }

    /**
     * 进入任务编辑页面第一步操作
     * @param missionType
     * @param id
     * @return
     */
    @RequestMapping("/step1")
    public ModelAndView step1( Long id,String missionType) {
        //任务新创建，id=null
        if (id == null) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("missionType", missionType);
            modelAndView.setViewName("/mission/missionbuilderstep1");
            return modelAndView;
        }
        //任务已存在
        ApiResult missionDefine = missionDefineService.getMissionDefineDetail(id);
        MissionVO missionVO = (MissionVO) missionDefine.getData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginTime = sdf.format(missionVO.getBeginTime());
        String endTime = sdf.format(missionVO.getEndTime());
        ApiResult missionFilterRules = missionFilterRulesService.getMissionFilterDetail(missionVO.getMissionFilterId());
        MissionFilterDetailResult missionFilterDetailResult = (MissionFilterDetailResult) missionFilterRules.getData();
        String ruleContent = missionFilterDetailResult.getContent();
        List<FilterRuleVO> filterRuleVOList = JSON.parseArray(ruleContent, FilterRuleVO.class);
        HashMap awardMap = MissionUtils.getAwardMap(filterRuleVOList,MissionTypeEnum.getByDesc(missionVO.getMissionType()));
        missionType = missionVO.getMissionType();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("missionType", missionType);
        modelAndView.addObject("missionVO", missionVO);
        modelAndView.addObject("beginTime", beginTime);
        modelAndView.addObject("endTime", endTime);
        modelAndView.addObject("awardMap", awardMap);
        modelAndView.addObject("id", id);
        modelAndView.addObject("missionFilterResult", missionFilterDetailResult);
        modelAndView.setViewName("/mission/missionbuilderstep1");
        return modelAndView;
    }
    /**
     * 进入任务编辑页面第二步操作
     * @param request
     * @param response
     * @param id
     * @return
     */
    @RequestMapping("/step2")
    public ModelAndView step2(HttpServletRequest request, HttpServletResponse response,  Long id) {
        ModelAndView modelAndView = new ModelAndView();
        ApiResult missionDefine = missionDefineService.getMissionDefineDetail(id);
        MissionVO missionVO = (MissionVO) missionDefine.getData();
        //通过任务类型获得任务规则
        ApiResult apiResult = missionTypeService.getConditionListByTypeName(missionVO.getMissionType());
        List<MissionConditionListResult> listResults = (List<MissionConditionListResult>) apiResult.getData();
        modelAndView.addObject("missionType",missionVO.getMissionType());
        //判断是否是新任务
        if(missionVO.getContent()==null&&missionVO.getChannelIds()==null&&missionVO.getShare()==null){
            modelAndView.addObject("id", id);
            modelAndView.addObject("list", listResults);
            modelAndView.setViewName("/mission/missionbuilderstep2");
            return modelAndView;
        }
        ApiResult missionFilterRules = missionFilterRulesService.getMissionFilterDetail(missionVO.getMissionFilterId());
        MissionFilterDetailResult missionFilterDetailResult = (MissionFilterDetailResult) missionFilterRules.getData();
        String ruleContent = missionFilterDetailResult.getContent();
        List<FilterRuleVO> filterRuleVOList = JSON.parseArray(ruleContent, FilterRuleVO.class);
        FilterRuleVO filterRuleVO = MissionUtils.getFilterRuleVOByMissionType(filterRuleVOList,MissionTypeEnum.getByDesc(missionVO.getMissionType()));
        String recommendChannelId = missionVO.getRecommendChannelId();
        String appType = missionVO.getAppType();
        String content = null;
        if(missionVO.getContent()!=null){
            content = missionVO.getContent().replaceAll("\"", "\\\\\"");
        }

        String[] channelList = null;
        if(missionVO.getChannelIds()!=null){
            String channels = missionVO.getChannelIds().substring(1, missionVO.getChannelIds().length()-1);
            channelList = channels.split(",");
        }


        String shareString = missionVO.getShare();
        MissionShareVO share =   JSON.parseObject(shareString,MissionShareVO.class);
        modelAndView.addObject("content",content);
        modelAndView.addObject("id", id);
        modelAndView.addObject("list", listResults);
        modelAndView.addObject("share", share);
        modelAndView.addObject("filterRuleVO",filterRuleVO);
        modelAndView.addObject("channelList",channelList);
        modelAndView.addObject("recommendChannelId", recommendChannelId);
        modelAndView.addObject("appType",appType);
        modelAndView.setViewName("/mission/missionbuilderstep2");
        return modelAndView;
    }
    /**
     * 进入任务编辑页面第三步操作
     * @param request
     * @param response
     * @param id
     * @return
     */
    @RequestMapping("/step3")
    public ModelAndView step3(HttpServletRequest request, HttpServletResponse response, Long id) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("id", id);
        ApiResult missionDefine = missionDefineService.getMissionDefineDetail(id);
        MissionVO missionVO = (MissionVO)missionDefine.getData();
        ApiResult apiResult = missionTemplateService.getAllValidTemplate();
        List<TemplateQueryResult> templateQueryResultList = (List<TemplateQueryResult>) apiResult.getData();

        modelAndView.addObject("templateList", templateQueryResultList);
        modelAndView.setViewName("/mission/missionbuilderstep3");
        //判断是否是新任务
        if(missionVO.getTemplateId()==null){
            return modelAndView;
        }
        String config = missionVO.getConfig();
        Map map = (Map) JSON.parse(config);
        String picString = (String)map.get("picList");

        String[] array = picString.split("&");

        modelAndView.addObject("config",map);
        modelAndView.addObject("picList",array);
        return modelAndView;
    }


    /**
     * 获取所有的任务条件
     *
     * @return
     */
    @RequestMapping("/missionConditionAvailable")
    @ResponseBody
    public ApiResult missionConditionAvailable() {
        ApiResult allValidCondition = missionConditionService.getAllValidCondition();
        return allValidCondition;
    }

    /**
     * 创建任务第一步
     *
     * @return
     */
    @RequestMapping("/missionDefineCreateStep1")
    @ResponseBody
    public ApiResult missionDefineCreateStep1(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        MissionDefineEntity missionDefineEntity = new MissionDefineEntity();
        MissionFilterRulesEntity missionFilterRulesEntity = new MissionFilterRulesEntity();
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");

        String missionType = RequestUtil.getString(request, "missionType");
        String missionName = RequestUtil.getString(request, "missionName");
        Long totalSnapshot = RequestUtil.getLong(request, "totalSnapshot");
        String beginTime = RequestUtil.getString(request, "beginTime");
        String endTime = RequestUtil.getString(request, "endTime");
        String pic = RequestUtil.getString(request, "pic");
        String award = RequestUtil.getString(request, "award");
        Map awardMap  = JSON.parseObject(award);

        if (RequestUtil.getString(request, "id") != null) {
            Long id = RequestUtil.getLong(request, "id");
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("missionType", missionType);
            objectMap.put("totalSnapshot", totalSnapshot);
            objectMap.put("beginTime", beginTime);
            objectMap.put("endTime", endTime);
            objectMap.put("pic", pic);
            missionDefineService.updateMissionDefine(id, objectMap);

            // 任务第一步更新操作日志
            try {
                MissionLogEntity missionLogDO = new MissionLogEntity();
                missionLogDO.setUserId(user.getId());
                missionLogDO.setUserName(user.getUserName());
                missionLogDO.setOperationType(LogTypeEnum.MissionStep1Modify.name());
                missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
                producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            } catch (Exception e) {
                e.printStackTrace();
            }

                apiResult = missionFilterRulesService.getMissionFilterDetailByMissionId(id);
                MissionFilterDetailResult missionFilterDetailResult = (MissionFilterDetailResult) apiResult.getData();
                String ruleContent = missionFilterDetailResult.getContent();
                List<FilterRuleVO> filterRuleVOList = JSON.parseArray(ruleContent, FilterRuleVO.class);
                filterRuleVOList = MissionUtils.setAwardAmount2(filterRuleVOList, MissionTypeEnum.getByDesc(missionType), awardMap);
                ruleContent = JSON.toJSONString(filterRuleVOList);
                Map<String, Object> paramMap = new HashedMap();
                paramMap.put("ruleContent", ruleContent);
                missionFilterRulesService.updateMissionFilterRules(missionFilterDetailResult.getFilterId(), paramMap);
                // 任务第一步更新FilterRule
                try {
                    MissionLogEntity missionLogDO = new MissionLogEntity();
                    missionLogDO.setUserId(user.getId());
                    missionLogDO.setUserName(user.getUserName());
                    missionLogDO.setOperationType(LogTypeEnum.FilterActionModify.name());
                    missionLogDO.setLogDesc(JSON.toJSONString(paramMap));
                    producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            } catch (Exception e) {
                e.printStackTrace();
            }
            apiResult.setData(id);
            return apiResult;

        } else {
            List<FilterRuleVO> filterRuleVOList = MissionUtils.setAwardAmount2(null,MissionTypeEnum.getByDesc(missionType),awardMap);
            String ruleContent = JSON.toJSONString(filterRuleVOList);
            missionFilterRulesEntity.setRuleName(missionName + "的规则");
            missionFilterRulesEntity.setRuleContent(ruleContent);
            apiResult = missionFilterRulesService.createMissionFilterRules(missionFilterRulesEntity);
            // 任务第一步创建FilterRule
            try {
                MissionLogEntity missionLogDO = new MissionLogEntity();
                missionLogDO.setUserId(user.getId());
                missionLogDO.setUserName(user.getUserName());
                missionLogDO.setOperationType(LogTypeEnum.FilterCreate.name());
                missionLogDO.setLogDesc(JSON.toJSONString(missionFilterRulesEntity));
                producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            } catch (Exception e) {
                e.printStackTrace();
            }
            missionDefineEntity.setMissionFilterId((Long) apiResult.getData());
            missionDefineEntity.setMissionType(missionType);
            missionDefineEntity.setMissionName(missionName);
            missionDefineEntity.setTotalSnapshot(totalSnapshot);
            missionDefineEntity.setBeginTime(DateUtils.parseDate(beginTime, "yyyy-MM-dd HH:mm:ss"));
            missionDefineEntity.setEndTime(DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss"));
            missionDefineEntity.setPic(pic);
            missionDefineEntity.setCreateUserId(user.getId());
            missionDefineEntity.setCreateUserName(user.getUserName());
            //任务默认状态为1（未发布）
            missionDefineEntity.setState(1);
            missionDefineEntity.setReceiveLimit(1l);
            apiResult = missionDefineService.createMissionDefine(missionDefineEntity);
            // 任务创建第一步日志
            try {
                MissionLogEntity missionLogDO = new MissionLogEntity();
                missionLogDO.setUserId(user.getId());
                missionLogDO.setUserName(user.getUserName());
                missionLogDO.setOperationType(LogTypeEnum.MissionCreate.name());
                missionLogDO.setLogDesc(JSON.toJSONString(missionDefineEntity));
                producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return apiResult;
        }


    }

    /**
     * 创建任务第二步
     *
     * @return
     */
    @RequestMapping("/missionDefineCreateStep2")
    @ResponseBody
    public ApiResult missionDefineCreateStep2(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        String content = RequestUtil.getString(request, "content");
        Long missionAwardAuto = RequestUtil.getLong(request, "missionAwardAuto");
        String missionChannel = RequestUtil.getString(request, "missionChannel");
        String missionChannelDesc = RequestUtil.getString(request, "missionChannelDesc");
        String recommendChannelId = RequestUtil.getString(request, "recommendChannelId");
        String condition = RequestUtil.getString(request, "condition");
        String missionType = RequestUtil.getString(request, "missionType");
        List<MissionConditionVO> missionConditionVOList = JSON.parseArray(condition, MissionConditionVO.class);
        String share = RequestUtil.getString(request, "share");
        String appType = RequestUtil.getString(request, "appType");
        content = content.replaceAll("style=\\\".*?\\\"","");//去除富文本编辑器中的style元素
        apiResult = missionFilterRulesService.getMissionFilterDetailByMissionId(id);
        MissionFilterDetailResult missionFilterDetailResult = (MissionFilterDetailResult) apiResult.getData();
        boolean isAuto = false;
        if (missionAwardAuto == 1l) {
            isAuto = true;
        }
        Map<String, Object> paramMap = new HashedMap();
        if(!missionType.equals("VIDEO")){

            paramMap.put("ruleContent",  MissionUtils.setFilterRule(missionFilterDetailResult.getContent(),missionConditionVOList,isAuto,MissionTypeEnum.getByDesc(missionType)));//任务类型将改为实时
            missionFilterRulesService.updateMissionFilterRules(missionFilterDetailResult.getFilterId(), paramMap);

        }
         AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        // 任务第二步更新FilterRule
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.FilterRuleModify.name());
            missionLogDO.setLogDesc(JSON.toJSONString(paramMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> objectMap = new HashedMap();
        objectMap.put("content", content);
        objectMap.put("channelIds", missionChannel);
        objectMap.put("recommendChannelId", recommendChannelId);
        objectMap.put("channelIdsDesc", missionChannelDesc);
        objectMap.put("share",share);
        objectMap.put("appType",appType);
        missionDefineService.updateMissionDefine(id, objectMap);
        // 任务第二步更新操作日志
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionStep2Modify.name());
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        apiResult.setData(id);

        return apiResult;
    }

    /**
     * 创建任务第三步
     *
     * @return
     */
    @RequestMapping("/missionDefineCreateStep3")
    @ResponseBody
    public ApiResult missionDefineCreateStep3(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long id = RequestUtil.getLong(request, "id");
        Long templateId = RequestUtil.getLong(request, "templateId");
        String config = RequestUtil.getString(request, "config");
        Map<String, Object> objectMap = new HashedMap();
        objectMap.put("templateId", templateId);
        objectMap.put("config", config);
        missionDefineService.updateMissionDefine(id, objectMap);
        // 任务第三步更新操作日志
        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.MissionStep3Modify.name());
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
            missionDefineService.insertHtml(id,config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }



    /**
     * 获取二维码
     *
     * @param id
     * @return
     */
    @RequestMapping("/missionQRCode")
    @ResponseBody
    public ApiResult missionQRCode(@RequestParam("id") Long id) {
        return missionDefineService.getQRCode(id);
    }

    /**
     * 任务列表
     *
     * @param
     * @return
     */
    @RequestMapping("/missionMainIndex")
    public ModelAndView mainIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        Long id = RequestUtil.getLong(request, "missionIdSearch");
        String name = RequestUtil.getStringTrans(request, "missionNameSearch");
        String startTime = RequestUtil.getString(request, "missionStartTimeSearch");
        String endTime = RequestUtil.getString(request, "missionEndTimeSearch");
        String createStartTime = RequestUtil.getString(request, "missionCreateStartTimeSearch");
        String createEndTime = RequestUtil.getString(request, "missionCreateEndTimeSearch");
        Integer sort = RequestUtil.getInteger(request, "sortSearch");
        String statesStr=RequestUtil.getString(request,"stateSearch");
        String typeStr= RequestUtil.getString(request,"typeSearch");
        String channelStr=RequestUtil.getString(request,"channelSearch");
        String createUsersStr=RequestUtil.getString(request,"createUserSearch");
        List<Integer> state = string2IntegerList(statesStr);
        List<String> type = string2StringList(typeStr);
        List<String> channel =string2PercentStringList(channelStr);
        List<Long> createUser = string2LongList(createUsersStr);//userId list
        modelAndView.addObject("missionIdSearch", id);
        modelAndView.addObject("missionNameSearch", name);
        modelAndView.addObject("missionStartTimeSearch", startTime);
        modelAndView.addObject("missionEndTimeSearch", endTime);
        modelAndView.addObject("missionCreateStartTimeSearch", createStartTime);
        modelAndView.addObject("missionCreateEndTimeSearch", createEndTime);
        modelAndView.addObject("stateSearch", statesStr);
        modelAndView.addObject("typeSearch", typeStr);
        modelAndView.addObject("channelSearch", channelStr);
        modelAndView.addObject("createUserSearch", createUsersStr);
        modelAndView.addObject("sortSearch", sort);
        modelAndView.setViewName("/mission/missionMainIndex");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        MissionDefineFilterVO missionDefineFilterVO = new MissionDefineFilterVO();
        missionDefineFilterVO.setPage(pageNum);
        missionDefineFilterVO.setSize(pageSize);
        if (id != null) {
            missionDefineFilterVO.setId(id);
        }
        if (StringUtils.isNotEmpty(name)) {
            missionDefineFilterVO.setMissionName("%"+name+"%");
        }

        if (StringUtils.isNotEmpty(startTime)) {
            missionDefineFilterVO.setStartTime(DateUtils.parseDate(startTime, timePatternWithSec));
        }

        if (StringUtils.isNotEmpty(endTime)) {
            missionDefineFilterVO.setEndTime(DateUtils.parse(endTime, timePatternWithSec));
        }

        if (StringUtils.isNotEmpty(createStartTime)) {
            missionDefineFilterVO.setCreateStartTime(DateUtils.parse(createStartTime, timePatternWithSec));
        }

        if (StringUtils.isNotEmpty(createEndTime)) {
            missionDefineFilterVO.setCreateEndTime(DateUtils.parse(createEndTime, timePatternWithSec));
        }
        missionDefineFilterVO.setSort(sort == null ? 1 : sort);

        if (state != null) {
            missionDefineFilterVO.setState(state);
        }
        if (type != null) {
            missionDefineFilterVO.setType(type);
        }

        if (channel != null) {
            missionDefineFilterVO.setChannel(channel);
        }


        if (createUser != null) {
            missionDefineFilterVO.setCreateUserId(createUser);
        }

        ApiResult apiResult = missionDefineService.getMissionDefinePage(missionDefineFilterVO);
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
            formatMissionType(resultList);
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);

        //查询所有的任务类型
        ApiResult typeApiResult = missionTypeService.getAllMissionType();
        if (!typeApiResult.isSuccess()) {
            modelAndView.addObject("message", typeApiResult.getError());
            return modelAndView;
        }

        List<MissionTypeListResult> totalTypes = (List<MissionTypeListResult>) typeApiResult.getData();
        modelAndView.addObject("totalTypes", totalTypes);

        //查询所有渠道
        ApiResult channelApiResult = missionChannelDefineService.listMissionChannelDefine();
        if (!channelApiResult.isSuccess()) {
            modelAndView.addObject("message", channelApiResult.getError());
            return modelAndView;
        }
        List<MissionChannelListResult> totalChannels = (List<MissionChannelListResult>) channelApiResult.getData();
        modelAndView.addObject("totalChannels", totalChannels);

        return modelAndView;
    }
    /**
     * 获取step1任务下拉框中的所有可选任务类型
     */
    @RequestMapping("/getAllMissionType")
    @ResponseBody
    public ApiResult getAllMissionType() {
        return missionTypeService.getAllMissionType();
    }
    /**
     * 获取任务属性用于判断
     */
    @RequestMapping("/checkMission")
    @ResponseBody
    public ApiResult checkMission(Long id) {
        ApiResult apiResult = missionDefineService.getMissionDefineDetail(id);
        MissionVO missionVO = (MissionVO) apiResult.getData();
        List<String>  promptList = new ArrayList<>();
        String shareStr = missionVO.getShare();
        Map shareMap = (Map)JSON.parse(shareStr);

        if(missionVO.getContent()==null){
            promptList.add("任务详情未进行编辑。");
        }
        if(missionVO.getChannelIds()==null){
            promptList.add("任务投放渠道未进行编辑。");
        }
        if(shareMap.get("title").equals("")||shareMap.get("desc").equals("")||shareMap.get("icon").equals("")){
            promptList.add("分享内容部分有遗漏。");

        }
        if(promptList.size()>0){
            promptList.add("如未填写内容不影响任务，可不进行最终提交操作。");
        }

        apiResult.setData(promptList);
        return apiResult;
    }

    @RequestMapping(value = "/getMissionQRCode")
    public ModelAndView getMissionQRCode(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView=new ModelAndView();
        Long id=RequestUtil.getLong(request,"id");
        String url = RequestUtil.getString(request,"url");
        try{
            ApiResult apiResult=missionDefineService.getQRCode(id);
            if (StringUtils.isEmpty(apiResult.getError())){
                QRCodeResult qrCodeResult=(QRCodeResult)apiResult.getData();
                modelAndView.addObject("missionDetailUrl",qrCodeResult.getMissionDetailUrl());
                modelAndView.addObject("missionHtmlUrl",qrCodeResult.getMissionHtmlUrl());
                modelAndView.setViewName("/mission/missionDetail");
                return modelAndView;
            }else {//调用接口出现异常后不跳转到详情页面
                modelAndView=new ModelAndView(new RedirectView(url));
                modelAndView.addObject("message",apiResult.getError());
            }
        }
        catch(Exception e){
            modelAndView=new ModelAndView(new RedirectView(url));

        }
        return modelAndView;

    }
    @RequestMapping(value = "/missionTypeSelect")
    public ModelAndView missionTypeSelect(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("/mission/missionTypeSelect");
        return modelAndView;

    }

    @RequestMapping(value = "/missionGlobalDetail")
    public ModelAndView missionGlobalDetail(Long id){
        ModelAndView modelAndView=new ModelAndView();
        ApiResult apiResult = new ApiResult();
        apiResult = missionDefineService.getQRCode(id);
        QRCodeResult qrCodeResult= (QRCodeResult) apiResult.getData();
        modelAndView.addObject("missionDetailUrl",qrCodeResult.getMissionDetailUrl());
        apiResult = missionDefineService.getMissionDefineDetail(id);
        MissionVO missionVO = (MissionVO) apiResult.getData();
        String ruleContent = missionVO.getRuleContent();
        String missionType = missionVO.getMissionType();
        List<FilterRuleVO> filterRuleVOList = JSON.parseArray(ruleContent, FilterRuleVO.class);
        MissionTypeEnum missionTypeEnum = MissionTypeEnum.getByDesc(missionType);
        HashMap awardMap = MissionUtils.getAwardMap(filterRuleVOList,missionTypeEnum);
        MissionShareVO share = JSON.parseObject(missionVO.getShare(),MissionShareVO.class);
        FilterRuleVO filterRuleVO = MissionUtils.getFilterRuleVOByMissionType(filterRuleVOList,missionTypeEnum);
        Integer score = (Integer)awardMap.get(AwardTypeEnum.SCORE);
        awardMap.put("SCORE",(Integer)awardMap.get(AwardTypeEnum.SCORE));
        awardMap.put("CASH",(Integer)awardMap.get(AwardTypeEnum.CASH));
        modelAndView.addObject("missionVO",missionVO);
        modelAndView.addObject("filterRuleVO",filterRuleVO);
        modelAndView.addObject("awardMap",awardMap);
        modelAndView.addObject("share",share);
        modelAndView.setViewName("/mission/missionGlobalDetail");
        return modelAndView;


    }
}
