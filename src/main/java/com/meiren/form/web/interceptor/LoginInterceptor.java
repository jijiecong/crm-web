package com.meiren.form.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.meiren.redis.client.RedisClient;
import com.meiren.sso.web.SsoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class LoginInterceptor implements HandlerInterceptor {

    private String loginUrl;
    @Autowired
    private SsoHelper ssoHelper;
//    @Autowired
//    private RedisClient redisClient;

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest arg0,
                           HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        if (ssoHelper.validateCookies(request,response)) {
//            String token = request.getParameter("token");
//            if (redisClient.exists(token)) {

                return true;
//            }
        }
        // 不符合条件的，跳转到登录界面
        response.sendRedirect(loginUrl);
        return false;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}

