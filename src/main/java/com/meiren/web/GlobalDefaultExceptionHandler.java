package com.meiren.web;

import com.alibaba.fastjson.JSON;
import com.meiren.common.context.CurrentContext;
import com.meiren.common.result.ApiResult;
import com.meiren.form.web.interceptor.ExceptionResult;
import com.meiren.mission.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by admin on 2017/3/15.
 */
@ControllerAdvice
class GlobalDefaultExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "/account/error";

    private static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;
        //===未捕获异常
        logger.error(JSON.toJSONString(e));
        Boolean modelAndView = CurrentContext.getModelAndView();
        if(modelAndView==null || modelAndView){
            ModelAndView mav = new ModelAndView();
//            mav.addObject("exception", e);
//            mav.addObject("url", req.getRequestURL());
            mav.setViewName(DEFAULT_ERROR_VIEW);
            return mav;
        } else {
            ApiResult apiResult=new ApiResult();
            ExceptionResult exceptionResult=new ExceptionResult();
//            exceptionResult.setException(e);
//            exceptionResult.setUrl(req.getRequestURL().toString());
            apiResult.setData(exceptionResult);
            apiResult.setError(ResultCode.EXCEPTION_STACK_TRACE.getCode(),ResultCode.EXCEPTION_STACK_TRACE.getMessage());
            return apiResult;
        }
    }
}
