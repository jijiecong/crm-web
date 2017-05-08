package com.meiren.web.mission;

import com.alibaba.common.lang.StringUtil;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.utils.DateUtils;
import com.meiren.common.utils.RequestUtil;
import com.meiren.mission.enums.MissionInstanceStatusEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.service.MissionDefineService;
import com.meiren.mission.service.MissionInstanceService;
import com.meiren.mission.service.MissionInstanceTraceService;
import com.meiren.mission.service.entity.MissionDefineEntity;
import com.meiren.mission.service.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

import static com.meiren.web.form.BaseController.DEFAULT_ROWS;

/**
 * Created by admin on 2017/2/17.
 */
@AuthorityToken(needToken = { "meiren.acl.mbc.backend.missionTrace.index" })
@Controller
@RequestMapping("/missionTrace")
public class MissionTraceModule {

	@Autowired
	private MissionInstanceTraceService missionInstanceTraceService;
	@Autowired
	private MissionDefineService missionDefineService;
	@Autowired
	private MissionInstanceService missionInstanceService;
	private String timePatternWithSec = "yyyy-MM-dd HH:mm:ss";

	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		String page = request.getParameter("page") == null ? "1" : request.getParameter("page");

		String status = RequestUtil.getString(request, "searchStatus");
		Long missionInstanceTraceIdSearch = RequestUtil.getLong(request, "missionInstanceTraceIdSearch");
		Long missionIdSearch = RequestUtil.getLong(request, "missionIdSearch");
		Long userIdSearch = RequestUtil.getLong(request, "userIdSearch");
		modelAndView.addObject("searchStatus", status);
		modelAndView.addObject("missionIdSearch", missionIdSearch);
		modelAndView.addObject("userIdSearch", userIdSearch);
		modelAndView.addObject("missionInstanceTraceIdSearch", missionInstanceTraceIdSearch);
		modelAndView.setViewName("/missionTrace/index");
		int pageNum = Integer.valueOf(page);
		if (pageNum <= 0) {
			pageNum = 1;
		}
		int pageSize = DEFAULT_ROWS;
		MissionTraceQueryVO missionDefineFilterVO = new MissionTraceQueryVO();
		missionDefineFilterVO.setPage(pageNum);
		missionDefineFilterVO.setSize(pageSize);
		missionDefineFilterVO.setStatus(status);
		missionDefineFilterVO.setMissionId(missionIdSearch);
		missionDefineFilterVO.setReceiverId(userIdSearch);
		missionDefineFilterVO.setMissionInstanceTraceId(missionInstanceTraceIdSearch);
		ApiResult apiResult = missionInstanceTraceService.getMissionInstanceTracePage(missionDefineFilterVO);
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
		modelAndView.addObject("curPage", pageNum);
		modelAndView.addObject("pageSize", DEFAULT_ROWS);
		return modelAndView;
	}

	@RequestMapping("/build")
	public ModelAndView build(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/missionTrace/missionTraceDetail");
		return modelAndView;
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult getDetail(HttpServletRequest request, HttpServletResponse response, Long id) {
		ApiResult apiResult = missionInstanceTraceService.getMissionInstanceTraceDetail(id);
		return apiResult;
	}

	@RequestMapping(value = "/compensate", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult compensate(HttpServletRequest request, HttpServletResponse response, Long id) {
		ApiResult apiResult = missionInstanceTraceService.compensateForTrace(id);
		return apiResult;
	}

	/**
	 * 点击任务进入任务审核，自动发放奖励的任务进入自动审核查看页面，手动发放奖励的任务进入手动审核页面
	 *
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/missionReview")
	public ModelAndView listByMissionId(HttpServletRequest request, HttpServletResponse response, Long id) {
		ModelAndView modelAndView = new ModelAndView();

		String page = request.getParameter("page") == null ? "1" : request.getParameter("page");

		Long missionInstanceIdSearch = RequestUtil.getLong(request, "missionInstanceIdSearch");
		Long missionIdSearch = RequestUtil.getLong(request, "missionIdSearch");
		String missionNameSearch = RequestUtil.getStringTrans(request, "missionNameSearch");

		String userNameSearch = RequestUtil.getStringTrans(request, "userNameSearch");
		Long userIdSearch = RequestUtil.getLong(request, "userIdSearch");
		String startTimeSearch = RequestUtil.getString(request, "startTimeSearch");
		String endTimeSearch = RequestUtil.getString(request, "endTimeSearch");
		if (id != null) {
			missionIdSearch = id;
		}

		modelAndView.addObject("startTimeSearch", startTimeSearch);
		modelAndView.addObject("endTimeSearch", endTimeSearch);
		modelAndView.addObject("missionInstanceIdSearch", missionInstanceIdSearch);
		modelAndView.addObject("missionIdSearch", missionIdSearch);
		modelAndView.addObject("userIdSearch", userIdSearch);
		modelAndView.addObject("userNameSearch", userNameSearch);
		modelAndView.addObject("missionNameSearch", missionNameSearch);
		modelAndView.setViewName("/missionTrace/missionTrace");
		int pageNum = Integer.valueOf(page);
		if (pageNum <= 0) {
			pageNum = 1;
		}

		int pageSize = DEFAULT_ROWS;
		ApiResult apiResult = missionDefineService.findMissionDefine(missionIdSearch);
		if (!apiResult.isSuccess()) {
			modelAndView = new ModelAndView(new RedirectView("/mission/missionMainIndex"));
			return modelAndView;
		}
		MissionDefineEntity missionDefineEntity = (MissionDefineEntity) apiResult.getData();
		if (missionDefineEntity.getState().equals(1)) {
			modelAndView = new ModelAndView(new RedirectView("/mission/missionMainIndex"));
			return modelAndView;
		}
		apiResult = missionDefineService.isAuto(missionIdSearch);
		boolean flag = (boolean) apiResult.getData();
		if (flag) {
			MissionTraceQueryVO missionTraceQueryVO = new MissionTraceQueryVO();
			missionTraceQueryVO.setPage(pageNum);
			missionTraceQueryVO.setSize(pageSize);
			missionTraceQueryVO.setMissionId(missionIdSearch);

			if (StringUtil.isNotEmpty(missionNameSearch)) {
				missionTraceQueryVO.setMissionName("%" + missionNameSearch + "%");
			}
			if (StringUtil.isNotEmpty(userNameSearch)) {
				missionTraceQueryVO.setUserName("%" + userNameSearch + "%");
			}
			missionTraceQueryVO.setFinishBeginTime(DateUtils.parseDate(startTimeSearch, timePatternWithSec));
			missionTraceQueryVO.setFinishEndTime(DateUtils.parseDate(endTimeSearch, timePatternWithSec));
			missionTraceQueryVO.setUserId(userIdSearch);

			missionTraceQueryVO.setMissionInstanceId(missionInstanceIdSearch);

			apiResult = missionInstanceTraceService.getMissionInstanceTracePage(missionTraceQueryVO);
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
		} else {
			MissionInstanceQueryVO missionInstanceQueryVO = new MissionInstanceQueryVO();
			missionInstanceQueryVO.setPage(pageNum);
			missionInstanceQueryVO.setPage(pageNum);
			missionInstanceQueryVO.setSize(pageSize);
			missionInstanceQueryVO.setMissionId(missionIdSearch);

			if (StringUtil.isNotEmpty(missionNameSearch)) {
				missionInstanceQueryVO.setMissionName("%" + missionNameSearch + "%");
			}
			if (StringUtil.isNotEmpty(userNameSearch)) {
				missionInstanceQueryVO.setUserName("%" + userNameSearch + "%");
			}
			missionInstanceQueryVO.setFinishBeginTime(DateUtils.parseDate(startTimeSearch, timePatternWithSec));
			missionInstanceQueryVO.setFinishEndTime(DateUtils.parseDate(endTimeSearch, timePatternWithSec));
			missionInstanceQueryVO.setUserId(userIdSearch);

			missionInstanceQueryVO.setMissionInstanceId(missionInstanceIdSearch);

			apiResult = missionInstanceService.getMissionInstanceReviewPage(missionInstanceQueryVO);
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

		}
		modelAndView.addObject("isAuto", flag);
		modelAndView.addObject("curPage", pageNum);
		modelAndView.addObject("pageSize", DEFAULT_ROWS);
		return modelAndView;
	}

	/**
	 * 审核通过
	 *
	 * @param request
	 * @param response
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/throughAudit", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult throughAudit(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtil.getLong(request, "id");

		ApiResult apiResult = missionInstanceTraceService.auditMissionInstance(id, MissionInstanceStatusEnum.SUCCESS);
		return apiResult;
	}

	/**
	 * 审核不通过
	 *
	 * @param request
	 * @param response
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/notApproveAudit", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult notApproveAudit(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtil.getLong(request, "id");

		ApiResult apiResult = missionInstanceTraceService.auditMissionInstance(id, MissionInstanceStatusEnum.FAIL);
		return apiResult;
	}

}
