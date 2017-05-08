package com.meiren.web.param;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.mission.enums.LogTypeEnum;
import com.meiren.mission.enums.ParamTypeEnum;
import com.meiren.mission.service.ProducerLogService;
import com.meiren.mission.service.entity.MissionLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.ParamResult;
import com.meiren.mission.service.MissionChannelDefineService;
import com.meiren.mission.service.MissionParamService;
import com.meiren.mission.service.vo.ParamVO;

@Controller
@RequestMapping("/param")
public class ParamModule {

	@Autowired
	private MissionChannelDefineService missionChannelDefineService;

	@Autowired
	private MissionParamService missionParamService;

	@Autowired
	private ProducerLogService producerLogService;

	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
		int pageNum = Integer.valueOf(page);
		if (pageNum <= 0) {
			pageNum = 1;
		}
		int pageSize = DEFAULT_ROWS;
		String paramTypeSearch = RequestUtil.getStringTrans(request, "paramTypeSearch");
		Map<String, Object> searchParamMap = new HashMap<String, Object>();
		if (!Strings.isNullOrEmpty(paramTypeSearch)) {
			searchParamMap.put("paramType", paramTypeSearch);
		}
		ApiResult apiResult = missionParamService.searchMissionParamAndValueExcludeRule(searchParamMap, pageNum, pageSize);
		if (!apiResult.isSuccess()) {
			modelAndView.addObject("message", apiResult.getError());
			return modelAndView;
		}
		Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

		if (resultMap.get("totalCount") != null) {
			modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
		}
		if (resultMap.get("data") != null) {
			List<ParamResult> resultList = (List<ParamResult>) resultMap.get("data");
			modelAndView.addObject("basicVOList", resultList);
		}
		modelAndView.addObject("curPage", pageNum);
		modelAndView.addObject("pageSize", DEFAULT_ROWS);
		modelAndView.addObject("paramTypeSearch", paramTypeSearch);

		modelAndView.addObject("paramTypes",getParamTypes());
		modelAndView.setViewName("/param/index");
		return modelAndView;
	}

	private List<String> getParamTypes(){//过滤WITHDRAW
		ParamTypeEnum[] typeEnums=ParamTypeEnum.values();
		List<String> typeNames=new ArrayList<>(typeEnums.length);
		for (ParamTypeEnum typeEnum:typeEnums){
			if (!ParamTypeEnum.WITHDRAW.equals(typeEnum)){
				typeNames.add(typeEnum.name());
			}
		}
		return typeNames;
	}
	/**
	 * 添加
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/create")
	@ResponseBody
	public ApiResult create(HttpServletRequest request, HttpServletResponse response) {
		ApiResult apiResult = new ApiResult();
		String paramType = RequestUtil.getString(request, "paramType");
		String paramKey = RequestUtil.getString(request, "paramKey");
		String paramValueType = RequestUtil.getString(request, "paramValueType");
		String desc = RequestUtil.getString(request, "desc");
		String missionParamValue = RequestUtil.getString(request, "missionParamValue");
		ParamVO paramVO = new ParamVO();
		paramVO.setDesc(desc);
		paramVO.setMissionParamValue(missionParamValue);
		paramVO.setParamKey(paramKey);
		paramVO.setParamType(paramType);
		paramVO.setParamValueType(paramValueType);		
		apiResult = missionParamService.createParamAndValue(paramVO);
		//参数配置创建操作日志
		try {
			MissionLogEntity missionLogDO = new MissionLogEntity();
			//取出用户的id以及用户的姓名记录日志中
			AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
			missionLogDO.setUserId(user.getId());
			missionLogDO.setUserName(user.getUserName());
			missionLogDO.setOperationType(LogTypeEnum.ParamOperate.name());
			Map<String,Object> objectMap=new HashMap<>();
			objectMap.put("operate","create");
			objectMap.put("param",paramVO);
			missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
			producerLogService.addQueen(JSON.toJSONString(missionLogDO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResult;
	}

	/**
	 * 修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/modify")
	@ResponseBody
	public ApiResult modify(HttpServletRequest request, HttpServletResponse response) {
		ApiResult apiResult = new ApiResult();
		Long id = RequestUtil.getLong(request, "id");

		String paramType = RequestUtil.getString(request, "paramType");
		String paramKey = RequestUtil.getString(request, "paramKey");
		String paramValueType = RequestUtil.getString(request, "paramValueType");
		String desc = RequestUtil.getString(request, "desc");
		String missionParamValue = RequestUtil.getString(request, "missionParamValue");
		Map<String, Object> objectMap = new HashMap<String, Object>();
		objectMap.put("paramType", paramType);
		objectMap.put("paramKey", paramKey);
		objectMap.put("paramValueType", paramValueType);
		objectMap.put("desc", desc);
		objectMap.put("missionParamValue", missionParamValue);
		apiResult = missionParamService.updateMissionParamAndValue(id, objectMap);

		//参数配置修改操作日志
		try {
			MissionLogEntity missionLogDO = new MissionLogEntity();
			//取出用户的id以及用户的姓名记录日志中
			AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
			missionLogDO.setUserId(user.getId());
			missionLogDO.setUserName(user.getUserName());
			objectMap.put("id",id);
			objectMap.put("operate","modify");
			missionLogDO.setOperationType(LogTypeEnum.ParamOperate.name());
			missionLogDO.setLogDesc(JSON.toJSONString(objectMap));
			producerLogService.addQueen(JSON.toJSONString(missionLogDO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResult;
	}

	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public ApiResult delete(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtil.getLong(request, "id");
		Map<String, Object> missionParam = new HashMap<String, Object>();
		missionParam.put("id", id);
		ApiResult apiResult = new ApiResult();
		apiResult = missionParamService.deleteMissionParam(missionParam);

		//参数配置删除操作日志
		try {
			MissionLogEntity missionLogDO = new MissionLogEntity();
			//取出用户的id以及用户的姓名记录日志中
			AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
			missionLogDO.setUserId(user.getId());
			missionLogDO.setUserName(user.getUserName());
			missionLogDO.setOperationType(LogTypeEnum.ParamOperate.name());
			missionParam.put("operate","delete");
			missionLogDO.setLogDesc(JSON.toJSONString(missionParam));
			producerLogService.addQueen(JSON.toJSONString(missionLogDO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResult;
	}


}
