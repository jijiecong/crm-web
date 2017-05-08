package com.meiren.web.mission;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.utils.DateUtils;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.mission.result.TemplateDetailResult;
import com.meiren.mission.service.MissionTemplateService;
import com.meiren.mission.service.ProducerLogService;
import com.meiren.mission.service.entity.MissionLogEntity;
import com.meiren.mission.service.entity.MissionTemplateEntity;
import com.meiren.mission.service.param.TemplateParam;
import com.meiren.param.TemplateCreateParam;
import com.meiren.param.TemplateModifyParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/3/1.
 */
@AuthorityToken(needToken = {"meiren.acl.mbc.backend.template.template.index"})
@Controller
@RequestMapping("/template")
public class TemplateModule {

    @Autowired
    private MissionTemplateService missionTemplateService;
    @Autowired
    private ProducerLogService producerLogService;

    private String timePatternWithSec ="yyyy-MM-dd HH:mm:ss";


    /**
     * 获取H5模板
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/templateIndex")
    public ModelAndView channelIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        String templateNameSearch = RequestUtil.getStringTrans(request, "templateNameSearch");
        Long templateIdSearch = RequestUtil.getLong(request, "templateIdSearch");

        String createTimeBeginSearch = RequestUtil.getStringTrans(request, "createTimeBeginSearch");
        String createTimeEndSearch = RequestUtil.getStringTrans(request, "createTimeEndSearch");
        TemplateParam templateParam=new TemplateParam();
        templateParam.setPage(pageNum);
        templateParam.setSize(pageSize);
        if(StringUtils.isNotBlank(createTimeBeginSearch)) {
            templateParam.setCreateTimeBegin(DateUtils.parseDate(createTimeBeginSearch, timePatternWithSec));
        }
        if(StringUtils.isNotBlank(createTimeEndSearch)) {
            templateParam.setCreateTimeEnd(DateUtils.parseDate(createTimeEndSearch, timePatternWithSec));
        }
        templateParam.setTemplateId(templateIdSearch);
        templateParam.setTemplateName(templateNameSearch);

        ApiResult apiResult = missionTemplateService.getMissionTemplatePage(templateParam);
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
        modelAndView.addObject("templateNameSearch", templateNameSearch);
        modelAndView.addObject("templateIdSearch", templateIdSearch);
        modelAndView.addObject("createTimeBeginSearch", createTimeBeginSearch);
        modelAndView.addObject("createTimeEndSearch", createTimeEndSearch);
        modelAndView.setViewName("/mission/templateIndex");
        return modelAndView;
    }

    /**
     * 根据模板id获取任务模板详情
     */
    @RequestMapping("/templateDetail")
    @ResponseBody
    public ApiResult templateDetail(@RequestParam("id")Long id){
        ApiResult detailTemplate = missionTemplateService.getDetailTemplate(id);
        if(detailTemplate.isSuccess()){
            return detailTemplate;
        } else {
            detailTemplate.setError(ResultCode.TEMPLATE_NOT_FOUND.getCode(),ResultCode.TEMPLATE_NOT_FOUND.getMessage());
            return detailTemplate;
        }
    }


    /**
     * 启用或者禁用一个任务模板
     * @param id
     * @param isValid
     * @return
     */
    @RequestMapping("/missionTemplateValid")
    @ResponseBody
    public ApiResult missionTemplateValid(HttpServletRequest request,@RequestParam("id") Long id,@RequestParam("isValid") Boolean isValid){
        ApiResult apiResult=new ApiResult();
        //更新
        missionTemplateService.validMissionTemplate(id,isValid);

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.TemplateOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("isValid", isValid);
            objectMap.put("id",id);
            objectMap.put("operate","任务模板启用/禁用");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;
    }

    /**
     * 删除一个任务条件
     * @param id
     * @return
     */
    @RequestMapping("/missionTemplateDelete")
    @ResponseBody
    public ApiResult missionTemplateDelete(HttpServletRequest request,@RequestParam("id") Long id){
        ApiResult apiResult=new ApiResult();
        //删除
        //首先查看有没有被引用
        Integer integer = missionTemplateService.countMissionDefineByTemplateId(id);
        if(integer>0){
            apiResult.setError(ResultCode.MISSION_TEMPLATE_USED_ALREADY.getCode(),ResultCode.MISSION_TEMPLATE_USED_ALREADY.getMessage());
            return apiResult;
        }
        missionTemplateService.deleteMissionTemplate(id);

        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.TemplateOperate.name());
            Map<String, Object> objectMap = new HashedMap();
            objectMap.put("id",id);
            objectMap.put("operate","任务模板删除");
            missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    /**
     * 获取所有的任务模板
     * @return
     */
    @RequestMapping("/missionTemplateAvailable")
    @ResponseBody
    public ApiResult missionTemplateAvailable(){
        ApiResult allValidTemplate = missionTemplateService.getAllValidTemplate();
        return allValidTemplate;
    }

    @RequestMapping(value = "/missionTemplateCreate" ,method = RequestMethod.POST)
    @ResponseBody
    public ApiResult templateCreate(HttpServletRequest request,TemplateCreateParam templateCreateParam){
        ApiResult apiResult=new ApiResult();
        //校验参数
        if(StringUtils.isBlank(templateCreateParam.getTemplateName())){
            apiResult.setError(ResultCode.TEMPLATE_NAME_NOT_EMPTY.getCode(),ResultCode.TEMPLATE_NAME_NOT_EMPTY.getMessage());
            return apiResult;
        }
        if(StringUtils.isBlank(templateCreateParam.getTemplateContent())){
            apiResult.setError(ResultCode.TEMPLATE_CONTENT_NOT_EMPTY.getCode(),ResultCode.TEMPLATE_CONTENT_NOT_EMPTY.getMessage());
            return apiResult;
        }
        ApiResult templateNameResult = missionTemplateService.getByTemplateName(templateCreateParam.getTemplateName());
        if(templateNameResult.isSuccess() && templateNameResult.getData()!=null){
            apiResult.setError(ResultCode.TEMPLATE_NAME_EXISTS.getCode(),ResultCode.TEMPLATE_NAME_EXISTS.getMessage());
            return apiResult;
        }
        //是否需要校验配置部分

        MissionTemplateEntity missionTemplateEntity=new MissionTemplateEntity();
        missionTemplateEntity.setTemplateName(templateCreateParam.getTemplateName());
        if(templateCreateParam.getIsValid()!=null) {
            missionTemplateEntity.setIsValid(missionTemplateEntity.getIsValid());
        } else {
            missionTemplateEntity.setIsValid(Boolean.TRUE);
        }
        missionTemplateEntity.setDescription(templateCreateParam.getDescription());
        missionTemplateEntity.setTemplateContent(templateCreateParam.getTemplateContent());
        if(templateCreateParam.getTemplateConfigVO()!=null) {
            missionTemplateEntity.setConfig(templateCreateParam.getTemplateConfigVO());
        }
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        missionTemplateEntity.setUserId(user.getId());
        missionTemplateEntity.setUserName(user.getUserName());

        //记录任务条件操作日志
        try {
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.TemplateOperate.name());
            missionLogDO.setLogDesc(JSON.toJSONString(missionTemplateEntity));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return missionTemplateService.createMissionTemplate(missionTemplateEntity);
    }


    @RequestMapping("/missionTemplateModify")
    @ResponseBody
    public ApiResult missionTemplateModify(HttpServletRequest request,TemplateModifyParam templateModifyParam){
        ApiResult apiResult=new ApiResult();
        if(templateModifyParam.getId()==null){
            apiResult.setError(ResultCode.TEMPLATE_ID_NOT_NULL.getCode(),ResultCode.TEMPLATE_ID_NOT_NULL.getMessage());
            return apiResult;
        }
        if(StringUtils.isNotBlank(templateModifyParam.getTemplateName())){
            ApiResult byTemplateName = missionTemplateService.getByTemplateName(templateModifyParam.getTemplateName());
            if(byTemplateName.isSuccess() && byTemplateName.getData()!=null){
                TemplateDetailResult templateDetailResult= (TemplateDetailResult) byTemplateName.getData();
                if(templateDetailResult.getId()!=templateModifyParam.getId()){
                    apiResult.setError(ResultCode.TEMPLATE_NAME_EXISTS.getCode(),ResultCode.TEMPLATE_NAME_EXISTS.getMessage());
                    return apiResult;
                }
            }
        }
        //修改
        Map<String, Object> modifyMap=new HashedMap();
        if(StringUtils.isNotBlank(templateModifyParam.getTemplateName())){
            modifyMap.put("templateName",templateModifyParam.getTemplateName());
        }
        if(StringUtils.isNotBlank(templateModifyParam.getDescription())){
            modifyMap.put("description",templateModifyParam.getDescription());
        }
        if(StringUtils.isNotBlank(templateModifyParam.getTemplateContent())){
            modifyMap.put("templateContent",templateModifyParam.getTemplateContent());
        }
        if(templateModifyParam.getTemplateConfigVO()!=null){
            modifyMap.put("config",templateModifyParam.getTemplateConfigVO());
        }

         apiResult = missionTemplateService.updateMissionTemplate(templateModifyParam.getId(), modifyMap);

        //日志记录
        try {
            AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
            MissionLogEntity missionLogDO = new MissionLogEntity();
            //取出用户的id以及用户的姓名记录日志中
            missionLogDO.setUserId(user.getId());
            missionLogDO.setUserName(user.getUserName());
            missionLogDO.setOperationType(LogTypeEnum.TemplateOperate.name());
            modifyMap.put("id",templateModifyParam.getId());
            modifyMap.put("operate","任务模板修改");
            missionLogDO.setLogDesc(JSON.toJSONString(modifyMap));
            producerLogService.addQueen(JSON.toJSONString(missionLogDO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResult;

    }
}
