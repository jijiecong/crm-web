package com.meiren.interceptor;

import com.alibaba.fastjson.JSON;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.annotation.Logical;
import com.meiren.common.result.VueResult;
import com.meiren.common.result.VueResultCode;
import com.meiren.sso.web.SsoHelper;
import com.meiren.utils.RequestUtil;
import com.meiren.utils.StringUtils;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/3/13.
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SsoHelper ssoHelper;

    private String applyUrl;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SessionUserVO user = RequestUtil.getSessionUser(request);
        if (user == null) {
            return false;
        }
        Map<String, String> tokenMap = ssoHelper.getTokenMap(user.getId());

        HandlerMethod method = (HandlerMethod) handler;

        Boolean hasToken = Boolean.TRUE;
        Set<String> missToken = new HashSet<>();
        //类上的注解
        AuthorityToken authorityTokenClass = method.getBean().getClass().getAnnotation(AuthorityToken.class);
        if (authorityTokenClass != null) {
            boolean checkToken = checkToken(authorityTokenClass, tokenMap, missToken);
            if (!checkToken) {
                hasToken = Boolean.FALSE;
            }
        }
        //方法上的注解
        AuthorityToken authorityToken = method.getMethodAnnotation(AuthorityToken.class);
        if (authorityToken != null) {
            boolean checkToken = checkToken(authorityToken, tokenMap, missToken);
            if (!checkToken) {
                hasToken = Boolean.FALSE;
            }
        }
        if (!hasToken) {
            //页面跳转
            StringBuilder stringBuilder = new StringBuilder();
            for (String missingToken : missToken) {
                stringBuilder.append(missingToken).append(",");
            }
            String tokenName = stringBuilder.toString();
            tokenName = tokenName.substring(0, tokenName.length() - 1);
            //ApiResult返回
            VueResult result = new VueResult();
            result.setResultCode(VueResultCode.PRIVILEGE_REQUIRED);
            result.setData(this.getApplyUrl() + "?token=" + tokenName);
            this.returnJson(response, result);
            return false;
        } else {
            return true;
        }
    }

    private void returnJson(HttpServletResponse response, VueResult result) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSONString(result));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    private boolean checkToken(AuthorityToken authorityTokenClass, Map<String, String> tokenMap, Set<String> missToken) {
        Boolean pass = Boolean.TRUE;
        String[] tokenArray = authorityTokenClass.needToken();
        Logical logical = authorityTokenClass.logical();

        //逻辑判断
        if (logical.equals(Logical.AND)) {
            //and判断
            for (String token : tokenArray) {
                if (StringUtils.isNotBlank(token) && StringUtils.isBlank(tokenMap.get(token))) {
                    pass = Boolean.FALSE;
                    missToken.add(token);
                }
            }
        } else {
            //or判断 只需要其中一个就行
            for (String token : tokenArray) {
                if (StringUtils.isNotBlank(token) && StringUtils.isBlank(tokenMap.get(token))) {
                    pass = Boolean.FALSE;
                    missToken.add(token);
                } else {
                    pass = Boolean.TRUE;
                    break;
                }
            }
        }
        return pass;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

}
