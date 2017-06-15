package com.meiren.interceptor;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.enums.BusinessEnum;
import com.meiren.acl.service.AclBusinessService;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.result.VueResultCode;
import com.meiren.sso.web.SsoHelper;
import com.meiren.utils.RequestUtil;
import com.meiren.utils.StringUtils;
import com.meiren.vo.SessionUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    private String loginUrl;
    private String cookieName = "meiren_account";
    @Autowired
    private SsoHelper ssoHelper;
    @Autowired
    private AclBusinessService aclBusinessService;

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        SessionUserVO oldUserVO = RequestUtil.getSessionUser(request);

        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.endsWith(cookie.getName())) {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        if (value != null) {
            ApiResult apiResult = ssoHelper.validateCookies(value);
            if (apiResult.isSuccess()) {
                AclUserEntity user = (AclUserEntity) apiResult.getData();

                SessionUserVO userVO = new SessionUserVO();
                BeanUtils.copyProperties(user, userVO);
                //当老的session没有失效且用户信息和sso校验得到的用户信息一致时，直接用老的session
                //避免uuid每次sso校验后都重置
                if(oldUserVO != null && (oldUserVO.getId() == userVO.getId())){
                    return true;
                }
                userVO.setInSide(this.isInSide(user.getId()));
                userVO.setUuid(StringUtils.shortUuid());

                RequestUtil.setSessionUser(request, userVO);
                return true;
            }
        }
        VueResult result = new VueResult();
        result.setData(loginUrl);
        result.setResultCode(VueResultCode.UNLOGIN);
        this.returnJson(response, result);
        return false;
    }

    private Boolean isInSide(Long userId) {
        AclBusinessEntity aclBusinessEntity = (AclBusinessEntity)
            aclBusinessService.getBusinessByUser(userId).getData();
        return aclBusinessEntity.getToken().equals(BusinessEnum.INSIDE.name);
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

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

}
