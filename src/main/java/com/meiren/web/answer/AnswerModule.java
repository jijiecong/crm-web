package com.meiren.web.answer;


import com.alibaba.fastjson.JSON;
import com.meiren.common.utils.StringUtils;
import com.meiren.common.result.ApiResult;
import com.meiren.tech.form.service.QuestionaireAnswerDetailsService;
import com.meiren.tech.form.service.QuestionaireAnswerService;
import com.meiren.tech.form.service.entity.QuestionaireAnswerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/answer")
public class AnswerModule extends BaseController{

    @Autowired
    protected QuestionaireAnswerService questionaireAnswerService;
    @Autowired
    protected QuestionaireAnswerDetailsService questionaireAnswerDetailsService;


    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/answer/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }

        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<String, Object>();

        ApiResult apiResult = questionaireAnswerService.searchQuestionaireAnswer(
                searchParamMap, pageNum, pageSize);

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
            List<QuestionaireAnswerEntity> resultList = (List<QuestionaireAnswerEntity>) resultMap.get("data");

            modelAndView.addObject("basicVOList", resultList);

        }

        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_ROWS);

        return modelAndView;

    }

    @RequestMapping(value = "detail")
    @ResponseBody
    public ApiResult detail(HttpServletRequest request,
                            HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long answerId = Long.parseLong(request.getParameter("answerId"));
        apiResult=questionaireAnswerService.findQuestionaireAnswerVO(answerId);
        return apiResult;
    }

    @RequestMapping(value = "test")
    @ResponseBody
    public ApiResult test(HttpServletRequest request,
                            HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long missionInstanceId = Long.parseLong(request.getParameter("missionInstanceId"));
        apiResult=questionaireAnswerService.findQuestionaireAnswerVOByMissionInstanceId(missionInstanceId);
        return apiResult;
    }

    @RequestMapping(value = "search")
    @ResponseBody
    public ApiResult search(HttpServletRequest request,
                          HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }

        int pageSize = DEFAULT_ROWS;

        String answerId = request.getParameter("answerId");
        String missionInstanceId = request.getParameter("missionInstanceId");
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(answerId)) {
            searchParamMap.put("answerId", Long.parseLong(answerId));
        } if (!StringUtils.isEmpty(missionInstanceId)) {
            searchParamMap.put("missionInstanceId", Long.parseLong(missionInstanceId));
        }
        apiResult=questionaireAnswerService.searchQuestionaireAnswerVO(searchParamMap,pageNum,pageSize);
        return apiResult;
    }

}
