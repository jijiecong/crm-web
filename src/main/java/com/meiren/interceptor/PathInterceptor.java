package com.meiren.interceptor;

import com.alibaba.fastjson.JSON;
import com.meiren.common.result.VueResult;
import com.meiren.common.result.VueResultCode;
import com.meiren.utils.RequestUtil;
import com.meiren.utils.StringUtils;
import com.meiren.vo.SessionUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PathInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PathInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        SessionUserVO user = RequestUtil.getSessionUser(request);
        String paths[] = request.getServletPath().split("/");
        logger.info("session中的uuid"+user.getUuid());
        logger.info("paths长度："+paths.length);
        logger.info("paths中的uuid："+paths[1]);
        return true;
//        VueResult result = new VueResult();
//        result.setResultCode(VueResultCode.API_NOT_FIND);
//        this.returnJson(response, result);
//        return false;
    }


    private void returnJson(HttpServletResponse response, VueResult result) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSONString(result));
        } catch (IOException e) {
            logger.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

}
