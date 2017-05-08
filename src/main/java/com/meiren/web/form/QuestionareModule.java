package com.meiren.web.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meiren.acl.enums.AclCommonEnum;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.AclTokenEnum;
import com.meiren.common.utils.RequestUtil;
import com.meiren.redis.client.RedisClient;
import com.meiren.tech.form.service.*;
import com.meiren.tech.form.service.entity.QuestionaireValidatorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.meiren.common.utils.StringUtils;
import com.meiren.common.result.ApiResult;
import com.meiren.tech.form.service.entity.QuestionaireBasicEntity;
import com.meiren.tech.form.service.entity.QuestionaireOptionEntity;
import com.meiren.tech.form.service.entity.QuestionaireTopicEntity;

@Controller
@RequestMapping("/form")
public class QuestionareModule extends BaseController {

	@Autowired
	protected QuestionaireBasicService questionaireBasicService;

	@Autowired
	protected QuestionaireOptionService questionaireOptionService;

	@Autowired
	protected QuestionaireTopicService questionaireTopicService;

	@Autowired
    protected QuestionaireValidatorService questionaireValidatorService;

	@Autowired
    protected QuestionaireRegexService questionaireRegexService;

	@Autowired
	private RedisClient redisClient;

	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request,
							  HttpServletResponse response) {

		ModelAndView modelAndView = new ModelAndView();
		if (!checkPrivilege(request)) {
			modelAndView.setViewName("/account/noPrivilege");
			return modelAndView;
		}

		String page = request.getParameter("page") == null ? "1" : request
				.getParameter("page");
		modelAndView.setViewName("/form/index");

		int pageNum = Integer.valueOf(page);
		if (pageNum <= 0) {
			pageNum = 1;
		}

		int pageSize = DEFAULT_ROWS;

		Map<String, Object> searchParamMap = new HashMap<String, Object>();
		String searchTitle = RequestUtil.getStringTrans(request, "searchTitle");
		searchParamMap.put("title", searchTitle);
		modelAndView.addObject("searchTitle",searchTitle);

		ApiResult apiResult = questionaireBasicService.searchQuestionaireBasic(
				searchParamMap, pageNum, pageSize);

		if (apiResult == null) {
			modelAndView.addObject("message", "remote apiResult is empty"
					+ JSON.toJSONString(apiResult));
			return modelAndView;
		}
		if (!apiResult.isSuccess()) {
			modelAndView.addObject("message", apiResult.getError());
			return modelAndView;
		}

		if (!StringUtils.isEmpty(apiResult.getError())) {
			modelAndView.addObject("message", apiResult.getError());
			return modelAndView;
		}
		if (apiResult.getData() == null) {
			modelAndView.addObject("message",
					"data is null #" + apiResult.getError());
			return modelAndView;
		}

		Map<String, Object> resultMap = (Map<String, Object>) apiResult
				.getData();

		if (resultMap.get("totalCount") != null) {
			modelAndView.addObject("totalCount",
					Integer.valueOf(resultMap.get("totalCount").toString()));
		}

		if (resultMap.get("data") != null) {
			List<QuestionaireBasicEntity> resultList = (List<QuestionaireBasicEntity>) resultMap
					.get("data");

			modelAndView.addObject("basicVOList", resultList);

		}

		modelAndView.addObject("curPage", pageNum);
		modelAndView.addObject("pageSize", DEFAULT_ROWS);
		return modelAndView;

	}

	// 创建表单基础信息
	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult add(HttpServletRequest request,
						 HttpServletResponse response) {
		ApiResult result = new ApiResult();
		if (!checkPrivilege(request)) {
			result.setError("no privilege");
			return result;
		}
		QuestionaireBasicEntity questionaireBasicEntity = new QuestionaireBasicEntity();
		if (StringUtils.isBlank(request.getParameter("uid"))) {

			result.setError("uid is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("title"))) {
			result.setError("title is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("userName"))) {
			result.setError("userName is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("summary"))) {
			result.setError("summary is missing");
			return result;
		}
		AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
		if (user == null) {
			result.setError("userInfo is not stored in session");
			return result;
		}
		questionaireBasicEntity.setUid(user.getId());
		questionaireBasicEntity.setUserName(user.getUserName());
		questionaireBasicEntity.setTitle(request.getParameter("title"));
		questionaireBasicEntity.setSummary(request.getParameter("summary"));
		result = questionaireBasicService
				.createQuestionaireBasic(questionaireBasicEntity);
		return result;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult delete(HttpServletRequest request,
							HttpServletResponse response) {
		ApiResult result = new ApiResult();
		String id = request.getParameter("id");
		if (StringUtils.isBlank(id)) {
			result.setError("formId is empty");
			return result;
		}

		Map<String, Object> delMap = new HashMap<String, Object>();
		try {
			AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
			if (user == null) {
				result.setError("userinfo is not stored in session please login again！");
				return result;
			}
			delMap.put("id", Long.valueOf(id));
			delMap.put("uid", user.getId());
			delMap.put("userName", user.getUserName());
		} catch (Exception e) {
			result.setError(e.getMessage());
			return result;
		}

		result = questionaireBasicService.deleteQuestionaireBasic(delMap);

		return result;

	}

	@RequestMapping("/build")
	public ModelAndView build(HttpServletRequest request,
							  HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/form/formbuilder");
		ApiResult apiResult=questionaireRegexService.searchQuestionaireRegex(new HashMap<String,Object>());
		if (apiResult.isSuccess()) {
			Map<String, Object> resultMap = (Map<String, Object>) apiResult
					.getData();
			modelAndView.addObject("regexList", resultMap.get("data"));
		}
		return modelAndView;
	}

	// 保存表单结构
	@RequestMapping(value = "formSubmit", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult formSubmit(HttpServletRequest request,
								HttpServletResponse response) {
		ApiResult result = new ApiResult();
		String basicId = request.getParameter("id");
		if (StringUtils.isBlank(basicId)) {
			result.setError("basicId is empty");
			return result;
		}

		String formInfo = request.getParameter("fieldList");

		if (StringUtils.isBlank(formInfo)) {
			result.setError("fieldList is empty");
			return result;
		}

		List<Map<String, Object>> fieldList = null;

		try {
			fieldList = JSON.parseObject(formInfo, List.class);
			batchSave(basicId, fieldList);
		} catch (Exception e) {
			result.setError(e.getMessage());
		}

		return result;

	}


	// 关联持久化
	private void batchSave(String basicId, List<Map<String, Object>> fieldList) {
		if (CollectionUtils.isEmpty(fieldList)) {
			return;
		}
		// check是否已经存在，如果存在相关topic和option就删除
		Map<String, Object> searchParamMap = new HashMap<String, Object>();
		searchParamMap.put("basicId", Long.valueOf(basicId));
		ApiResult topicApiResult = questionaireTopicService
				.searchQuestionaireTopic(searchParamMap, null, null);

		if (!topicApiResult.isSuccess()) {
			return;
		}

		Map<String, Object> topicSearchResultMap = (Map<String, Object>) topicApiResult
				.getData();

		List<QuestionaireTopicEntity> topicList = (List<QuestionaireTopicEntity>) topicSearchResultMap
				.get("data");
		if (!CollectionUtils.isEmpty(topicList)) {
			questionaireTopicService.deleteQuestionaireTopic(searchParamMap);
			for (QuestionaireTopicEntity topicEntity : topicList) {
				Map<String, Object> delOptMap = new HashMap<String, Object>();
				delOptMap.put("topicId", topicEntity.getId());
				questionaireOptionService.deleteQuestionaireOption(delOptMap);
				questionaireValidatorService.deleteQuestionaireValidator(topicEntity.getValidateId());
			}
		}

		for (Map<String, Object> fieldNode : fieldList) {
			//先存验证器
			QuestionaireValidatorEntity questionaireValidatorEntity = JSON.parseObject(
					fieldNode.get("validator").toString(), QuestionaireValidatorEntity.class);
			if (questionaireValidatorEntity.haveInfo()) {
				questionaireValidatorEntity = (QuestionaireValidatorEntity) questionaireValidatorService.createQuestionaireValidator(questionaireValidatorEntity).getData();
			}

			QuestionaireTopicEntity questionaireTopicEntity = new QuestionaireTopicEntity();
			questionaireTopicEntity.setValidateId(questionaireValidatorEntity.getId());
			questionaireTopicEntity.setBasicId(Long.valueOf(basicId));
			questionaireTopicEntity.setSort(Integer.valueOf(fieldNode
					.get("sort").toString().replace("Q", "").trim()));
			questionaireTopicEntity.setTitle(fieldNode.get("title").toString());
			questionaireTopicEntity.setType(Integer.valueOf(fieldNode.get(
					"type").toString()));
			questionaireTopicEntity.setIsRequired(questionaireValidatorEntity.getIsRequired());
			ApiResult result = questionaireTopicService
					.createQuestionaireTopic(questionaireTopicEntity);

			if (!result.isSuccess()) {
				continue;
			}
			questionaireTopicEntity = (QuestionaireTopicEntity) result
					.getData();
			Long topicId = questionaireTopicEntity.getId();

			if (topicId == null) {
				continue;
			}

			String subNodeListInfo = fieldNode.get("subNode").toString();

			List<Map<String, Object>> optionList = JSON.parseObject(
					subNodeListInfo, List.class);

			for (Map<String, Object> optionMap : optionList) {
				QuestionaireOptionEntity optionEntity = new QuestionaireOptionEntity();
				optionEntity.setTopicId(topicId);
				optionEntity.setSort(Integer.valueOf(optionMap.get("sort")
						.toString()));
				optionEntity
						.setLabelName(optionMap.get("labelName").toString());
				optionEntity
						.setLogicName(optionMap.get("logicName").toString());
				optionEntity.setValue(optionMap.get("value") == null ? ""
						: optionMap.get("value").toString());
				questionaireOptionService
						.createQuestionaireOption(optionEntity);
			}

		}

	}

	// 加载表单结构
	@RequestMapping(value = "loadFormStructure", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult getFormStructure(HttpServletRequest request,
									  HttpServletResponse response) {
		ApiResult result = new ApiResult();
		String basicId = request.getParameter("id");
		if (StringUtils.isBlank(basicId)) {
			result.setError("basicId is empty");
			return result;
		}

		return questionaireBasicService.loadFormStructure(basicId);

	}

	@RequestMapping(value = "edit", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult find(HttpServletRequest request,
						  HttpServletResponse response) {
		ApiResult result = new ApiResult();
		String id = request.getParameter("id");
		if (StringUtils.isBlank(id)) {
			result.setError("formId is empty");
			return result;
		}

		try {
			result = questionaireBasicService.findQuestionaireBasic(Long
					.valueOf(id));
		} catch (Exception e) {
			result.setError(e.getMessage());
			return result;
		}

		return result;

	}

	@RequestMapping(value = "modify", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult modify(HttpServletRequest request,
							HttpServletResponse response) {
		ApiResult result = new ApiResult();

		String id = request.getParameter("id");
		if (StringUtils.isBlank(id)) {
			result.setError("formId is empty");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("uid"))) {

			result.setError("uid is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("userName"))) {
			result.setError("userName is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("title"))) {
			result.setError("title is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("summary"))) {
			result.setError("summary is missing");
			return result;
		}
		AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
		if (user == null) {
			result.setError("userInfo is not stored in session");
			return result;
		}

		try {
			Map<String, Object> modifyParam = new HashMap<String, Object>();
			modifyParam.put("uid", user.getId());
			modifyParam.put("userName", user.getUserName());
			modifyParam.put("title", request.getParameter("title"));
			modifyParam.put("summary", request.getParameter("summary"));
			result = questionaireBasicService.updateQuestionaireBasic(
					Long.valueOf(id), modifyParam);

		} catch (Exception e) {
			result.setError(e.getMessage());
			return result;
		}
		return result;
	}







	private boolean checkPrivilege(HttpServletRequest request) {
		AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
		if (user == null) {
			return false;
		}
		Map<String, String> tokenMap = (Map<String, String>) redisClient.get(AclCommonEnum.REDISKEY.getName() + user.getId());
		String token = tokenMap.get(AclTokenEnum.FORM.getToken());
		if (StringUtils.isEmpty(token)) {
			return false;
		}
		return true;
	}
}
