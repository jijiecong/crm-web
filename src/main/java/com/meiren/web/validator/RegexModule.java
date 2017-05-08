package com.meiren.web.validator;


import com.alibaba.fastjson.JSON;
import com.meiren.common.utils.StringUtils;
import com.meiren.common.result.ApiResult;
import com.meiren.tech.form.service.QuestionaireRegexService;
import com.meiren.tech.form.service.QuestionaireValidatorService;
import com.meiren.tech.form.service.entity.QuestionaireAnswerEntity;
import com.meiren.tech.form.service.entity.QuestionaireRegexEntity;
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
@RequestMapping("/regex")
public class RegexModule extends BaseController {

    @Autowired
    protected QuestionaireRegexService questionaireRegexService;
    @Autowired
    protected QuestionaireValidatorService questionaireValidatorService;


    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/regex/index");

        Map<String, Object> searchParamMap = new HashMap<String, Object>();

        ApiResult apiResult = questionaireRegexService.searchQuestionaireRegex(
                searchParamMap);

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

        if (resultMap.get("data") != null) {
            List<QuestionaireAnswerEntity> resultList = (List<QuestionaireAnswerEntity>) resultMap
                    .get("data");

            modelAndView.addObject("basicVOList", resultList);

        }

        return modelAndView;

    }
    @RequestMapping(value = "find")
    @ResponseBody
    public ApiResult find(HttpServletRequest request,
                            HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        Long regexId = Long.parseLong(request.getParameter("regexId"));
        apiResult=questionaireRegexService.findQuestionaireRegex(regexId);
        return apiResult;
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public ApiResult update(HttpServletRequest request,
                            HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        apiResult.setSuccess(false);
        String regexId= request.getParameter("id");
        if (StringUtils.isEmpty(regexId)) {
            return apiResult;
        }
        String name = request.getParameter("name");
        String rule = request.getParameter("rule");
        String info = request.getParameter("info");


        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, Object> validatorParamMap = new HashMap<String, Object>();
        boolean hasInfo = false;

        if (!StringUtils.isEmpty(name)) {
            paramMap.put("name", name);
            validatorParamMap.put("regexName", name);
            hasInfo = true;
        }
        if (!StringUtils.isEmpty(rule)) {
            paramMap.put("rule", rule);
            validatorParamMap.put("regexRule", rule);
            hasInfo = true;
        }
        if (!StringUtils.isEmpty(info)) {
            paramMap.put("info", info);
            validatorParamMap.put("info", info);
            hasInfo = true;
        }
        if (!hasInfo) {
            return apiResult;
        }
        apiResult=questionaireRegexService.updateQuestionaireRegex(Long.parseLong(regexId),paramMap);
        questionaireValidatorService.updateQuestionaireValidatorRegex(Long.parseLong(regexId),validatorParamMap);
        return apiResult;
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ApiResult delete(HttpServletRequest request,
                          HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        String regexId = request.getParameter("regexId");
        if (StringUtils.isEmpty(regexId)) {
            return apiResult;
        }
        apiResult = questionaireRegexService.deleteQuestionaireRegex(Long.parseLong(regexId));
        return apiResult;
    }

    @RequestMapping(value = "add")
    @ResponseBody
    public ApiResult add(HttpServletRequest request,
                            HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        String name = request.getParameter("name");
        String rule = request.getParameter("rule");
        String info = request.getParameter("info");
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(rule)) {
            return apiResult;
        }
        QuestionaireRegexEntity questionaireRegexEntity = new QuestionaireRegexEntity();
        questionaireRegexEntity.setName(name);
        questionaireRegexEntity.setRule(rule);
        questionaireRegexEntity.setInfo(info);
        apiResult=questionaireRegexService.createQuestionaireRegex(questionaireRegexEntity);
        return apiResult;
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ApiResult list(HttpServletRequest request,
                          HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        apiResult=questionaireRegexService.searchQuestionaireRegex(new HashMap<String, Object>());
        return apiResult;
    }

}
