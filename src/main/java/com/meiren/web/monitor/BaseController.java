package com.meiren.web.monitor;

import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.constant.BossConstant;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BaseController {

    @Autowired
    protected BossConstant bossConstant;

    public static final int DEFAULT_ROWS = 10;

    public AclUserEntity getUser(HttpServletRequest request) {
        return  (AclUserEntity) request.getSession().getAttribute("user");
    }
    public Map<String, Object> converRequestMap(Map<String, String[]> paramMap) {
        return this.converRequestMap(paramMap,new String[]{});
    }

    public Map<String, Object> converRequestMap(Map<String, String[]> paramMap,String[] excludeName) {
        Map<String, Object> rMap = new HashMap<>();
        Set<String> set = new HashSet<>(Arrays.asList(excludeName));
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            if(set.contains(entry.getKey())){
                continue;
            }
            rMap.put(entry.getKey(),entry.getValue()[0]);
        }
        return rMap;
    }

    public String checkApiResult(ApiResult apiResult) {
        if (apiResult == null) {
            return "remote apiResult is empty";
        }
        if (!apiResult.isSuccess()) {
            return apiResult.getError();
        }

        if (!StringUtils.isEmpty(apiResult.getError())) {
            return apiResult.getError();
        }
        if (apiResult.getData() == null) {
            return "data is null #" + apiResult.getError();
        }
        return null;
    }

    public Long checkId(HttpServletRequest request) throws Exception {
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            throw new Exception("id is empty");
        }
        return Long.valueOf(id);
    }

    public String checkParamMiss(HttpServletRequest request, String[] necessaryParam) throws Exception {
        for (String param : necessaryParam) {
            if (StringUtils.isBlank(request.getParameter(param))) {
                throw new Exception(param + " is missing");
            }
        }
        return null;
    }

}
