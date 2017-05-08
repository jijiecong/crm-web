package com.meiren.web.router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.meiren.api.entity.ApiRouterEntity;
import com.meiren.api.result.ApiResult;
import com.meiren.common.utils.StringUtils;

/**
 * 
 * @ClassName: RouterModule
 * @Description: 路由管理功能
 * @author William.jiang
 * @date 2017年1月17日 上午11:42:22
 * 
 */

//@RestController
@RequestMapping("/route")
public class RouterModule extends BaseController {

	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {

		String page = request.getParameter("page") == null ? "1" : request
				.getParameter("page");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/route/index");

		int pageNum = Integer.valueOf(page);
		if (pageNum <= 0) {
			pageNum = 1;
		}

		int pageSize = DEFAULT_ROWS;

		Map<String, Object> searchParamMap = new HashMap<String, Object>();
		if (!StringUtils.isBlank(request.getParameter("searchPath"))) {
			searchParamMap.put("path", request.getParameter("searchPath"));
			modelAndView.addObject("searchPath",
					request.getParameter("searchPath"));
		}
		if (!StringUtils.isBlank(request.getParameter("searchMethod"))) {
			searchParamMap.put("method", request.getParameter("searchMethod"));
			modelAndView.addObject("searchMethod",
					request.getParameter("searchMethod"));
		}

		ApiResult apiResult = apiRouterService.searchApiRouter(searchParamMap,
				pageNum, pageSize);

		if (!apiResult.isSuccess()) {
			modelAndView.addObject("message", apiResult.getError());
			return modelAndView;
		}
		if (apiResult == null || !apiResult.isSuccess()) {
			modelAndView.addObject("message", "remote apiResult is empty"
					+ JSON.toJSONString(apiResult));
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
			List<ApiRouterEntity> resultList = (List<ApiRouterEntity>) resultMap
					.get("data");

			modelAndView.addObject("routerVOList", resultList);

		}

		modelAndView.addObject("curPage", pageNum);
		modelAndView.addObject("pageSize", DEFAULT_ROWS);

		return modelAndView;

	}

	// 创建路由映射
	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult add(HttpServletRequest request,
			HttpServletResponse response) {
		ApiResult result = new ApiResult();
		ApiRouterEntity apiRouterEntity = new ApiRouterEntity();
		if (StringUtils.isBlank(request.getParameter("name"))) {

			result.setError("name is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("path"))) {

			result.setError("path is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("method"))) {
			result.setError("method is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("interfaze"))) {
			result.setError("interfaze is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("paramTypeArray"))) {
			result.setError("paramTypeArray is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("responseDemo"))) {
			result.setError("responseDemo is missing");
			return result;
		}
		// User user = (User) request.getSession().getAttribute("user");
		// if (user == null) {
		// result.setError("userInfo is not stored in session");
		// return result;
		// }
//		apiRouterEntity.setInterfaze(request.getParameter("interfaze"));
//		apiRouterEntity.setInputParamDesc(request
//				.getParameter("paramTypeArray"));
//		apiRouterEntity.setName(request.getParameter("name"));
//		apiRouterEntity.setResponseDemo(request.getParameter("responseDemo"));
		apiRouterEntity.setMethod(request.getParameter("method"));
		apiRouterEntity.setPath(request.getParameter("path"));
		// apiRouterEntity.setCreator(user.getUserName());
		// apiRouterEntity.setUserId(user.getId());
		result = apiRouterService.createApiRouter(apiRouterEntity);
		return result;
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult delete(HttpServletRequest request,
			HttpServletResponse response) {
		ApiResult result = new ApiResult();
		String id = request.getParameter("id");
		if (StringUtils.isBlank(id)) {
			result.setError("routerId is empty");
			return result;
		}

		Map<String, Object> delMap = new HashMap<String, Object>();
		try {
			// User user = (User) request.getSession().getAttribute("user");
			// if (user == null) {
			// result.setError("userinfo is not stored in session please login again！");
			// return result;
			// }
			// delMap.put("creator", user.getUserName());
			// delMap.put("userId", user.getId());
			delMap.put("id", Long.valueOf(id));
		} catch (Exception e) {
			result.setError(e.getMessage());
			return result;
		}

		result = apiRouterService.deleteApiRouter(delMap);

		return result;

	}

	@RequestMapping(value = "edit", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult find(HttpServletRequest request,
			HttpServletResponse response) {
		ApiResult result = new ApiResult();
		String id = request.getParameter("id");
		if (StringUtils.isBlank(id)) {
			result.setError("routerId is empty");
			return result;
		}

		try {
			result = apiRouterService.findApiRouter(Integer.valueOf(id));
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
			result.setError("routerId is empty");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("name"))) {

			result.setError("name is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("path"))) {

			result.setError("path is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("method"))) {
			result.setError("method is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("interfaze"))) {
			result.setError("interfaze is missing");
			return result;
		}

		if (StringUtils.isBlank(request.getParameter("paramTypeArray"))) {
			result.setError("paramTypeArray is missing");
			return result;
		}
		if (StringUtils.isBlank(request.getParameter("responseDemo"))) {
			result.setError("responseDemo is missing");
			return result;
		}

		// User user = (User) request.getSession().getAttribute("user");
		// if (user == null) {
		// result.setError("userInfo is not stored in session");
		// return result;
		// }

		try {
			Map<String, Object> modifyParam = new HashMap<String, Object>();
			// modifyParam.put("userId", user.getId());
			// modifyParam.put("creator", user.getUserName());

			modifyParam.put("interfaze", request.getParameter("interfaze"));
			modifyParam.put("inputParamDesc",
					request.getParameter("paramTypeArray"));
			modifyParam.put("name", request.getParameter("name"));
			modifyParam.put("responseDemo",
					request.getParameter("responseDemo"));
			modifyParam.put("method", request.getParameter("method"));
			modifyParam.put("path", request.getParameter("path"));
			result = apiRouterService.updateApiRouter(Integer.valueOf(id),
					modifyParam);

		} catch (Exception e) {
			result.setError(e.getMessage());
			return result;
		}
		return result;
	}

}
