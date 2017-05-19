package com.meiren.web;

import com.alibaba.fastjson.JSON;
import com.meiren.common.result.VueResult;
import com.meiren.common.result.VueResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 2017/3/15.
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;
        //===未捕获异常
        logger.error(JSON.toJSONString(e));
        VueResult result = new VueResult();
        result.setError(VueResultCode.ERROR.getCode(), e.getMessage());
        return result;
    }
}
