package com.meiren.form.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.annotation.Logical;
import com.meiren.common.context.CurrentContext;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.ResultCode;
import com.meiren.common.utils.StringUtils;
import com.meiren.redis.client.RedisClient;
import com.meiren.sso.web.SsoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/3/13.
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {

    private RedisClient redisClient;

    private String noPrivilegeUrl;

    @Autowired
    private SsoHelper ssoHelper;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        if (user == null) {
            return false;
        }
//        Map<String, String> tokenMap = (Map<String, String>) redisClient.get(AclCommonEnum.REDISKEY.getName() + user.getId());
        Map<String, String> tokenMap = ssoHelper.getTokenMap(user.getId());
        HandlerMethod method = (HandlerMethod) handler;
        boolean equalsModelAndView = method.getMethod().getReturnType().getCanonicalName().equals(ModelAndView.class.getCanonicalName());
        CurrentContext.setModelAndView(equalsModelAndView);
        Boolean hasToken=Boolean.TRUE;
        Set<String> missToken=new HashSet<>();
        //类上的注解
        AuthorityToken authorityTokenClass = method.getBean().getClass().getAnnotation(AuthorityToken.class);
        if(authorityTokenClass!=null){
            boolean checkToken = checkToken(authorityTokenClass, tokenMap,missToken);
            if(!checkToken){
                hasToken=Boolean.FALSE;
            }
        }
        //方法上的注解
        AuthorityToken authorityToken = method.getMethodAnnotation(AuthorityToken.class);
        if(authorityToken!=null){
            boolean checkToken = checkToken(authorityToken, tokenMap,missToken);
            if(!checkToken){
                hasToken=Boolean.FALSE;
            }
        }
        if(!hasToken){
            //页面跳转
            StringBuilder stringBuilder=new StringBuilder();
            for(String missingToken:missToken){
                stringBuilder.append(missingToken).append(",");
            }
           String tokenName= stringBuilder.toString();
           tokenName=tokenName.substring(0,tokenName.length()-1);
           //跳转 "/account/noPrivilege"
            //ApiResult返回
            if(!equalsModelAndView){
                ApiResult apiResult=new ApiResult();
                apiResult.setError(ResultCode.PRIVILEGE_REQUIRED.getCode(),ResultCode.PRIVILEGE_REQUIRED.getMessage());
                apiResult.setData(tokenName);
                response.getWriter().write(JSON.toJSONString(apiResult));
            } else {
                response.sendRedirect(noPrivilegeUrl.concat("?token=").concat(tokenName));
            }
            return false;

        } else {
            return true;
        }
    }

    private boolean checkToken(AuthorityToken authorityTokenClass, Map<String, String> tokenMap,Set<String> missToken) {
        Boolean pass=Boolean.TRUE;
        String[] tokenArray = authorityTokenClass.needToken();
        Logical logical = authorityTokenClass.logical();

        //逻辑判断
        if(logical==null || logical.equals(Logical.AND)) {
            //and判断
            for (String token : tokenArray) {
                if (StringUtils.isNotBlank(token) && StringUtils.isBlank(tokenMap.get(token))) {
                    pass = Boolean.FALSE;
                    missToken.add(token);
                }
            }
        } else {
            //or判断 只需要其中一个就行
            for(String token :tokenArray){
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

    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void setNoPrivilegeUrl(String noPrivilegeUrl) {
        this.noPrivilegeUrl = noPrivilegeUrl;
    }

    /**
     *   需要申请的逻辑
     *   AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
     if (user == null) {
     return false;
     }
     Boolean hasToken=Boolean.TRUE;
     Set<String> missToken=new HashSet<>();
     Map<String, String> tokenMap = (Map<String, String>) redisClient.get(AclCommonEnum.REDISKEY.getName() + user.getId());
     HandlerMethod method = (HandlerMethod) handler;
     //类上的注解
     AuthorityToken authorityTokenClass = method.getClass().getAnnotation(AuthorityToken.class);
     if(authorityTokenClass!=null){
     String[] tokenArray = authorityTokenClass.needToken();
     for(String token:tokenArray){
     if(StringUtils.isBlank(tokenMap.get(token))){
     missToken.add(token);
     hasToken=Boolean.FALSE;
     }
     }
     }
     //方法上的注解
     AuthorityToken authorityToken = method.getMethodAnnotation(AuthorityToken.class);
     if(authorityToken!=null){
     String[] tokenArray = authorityToken.needToken();
     for(String token:tokenArray){
     if(StringUtils.isBlank(tokenMap.get(token))){
     missToken.add(token);
     hasToken=Boolean.FALSE;
     }
     }
     }
     if(!hasToken){
     //missToken 参数
     response.sendRedirect("/account/noPrivilege?parameter");
     return false;
     }
     return true;
     */
}
