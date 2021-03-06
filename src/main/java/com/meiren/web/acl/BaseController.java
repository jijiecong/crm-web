package com.meiren.web.acl;

import com.meiren.acl.enums.BusinessEnum;
import com.meiren.acl.enums.CheckCanDoEnum;
import com.meiren.acl.enums.PrivilegeTokenEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.StringUtils;
import com.meiren.sso.web.SsoHelper;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BaseController {
    @Autowired
    private SsoHelper ssoHelper;

    public static final int DEFAULT_ROWS = 10;

    @Autowired
    private AclBusinessService aclBusinessService;
    @Autowired
    protected AclRoleOwnerService aclRoleOwnerService;
    @Autowired
    protected AclPrivilegeOwnerService aclPrivilegeOwnerService;

    SessionUserVO getUser(HttpServletRequest request) {
        return RequestUtil.getSessionUser(request);
    }

    public Map<String, Object> converRequestMap(Map<String, String[]> paramMap) {
        return this.converRequestMap(paramMap, new String[]{});
    }

    public Map<String, Object> converRequestMap(Map<String, String[]> paramMap, String[] excludeName) {
        Map<String, Object> rMap = new HashMap<>();
        Set<String> set = new HashSet<>(Arrays.asList(excludeName));
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            if (set.contains(entry.getKey())) {
                continue;
            }
            rMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return rMap;
    }

    public boolean checkToken(SessionUserVO user, String token) {
        if (user == null || StringUtils.isBlank(token)) {
            return false;
        }
        Map<String, String> tokenMap = ssoHelper.getTokenMap(user.getId());
        return !StringUtils.isBlank(tokenMap.get(token));
    }

    /**
     * 验证返回结果
     *
     * @param apiResult
     * @return
     */
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

    /**
     * 组装查询条件参数 现有：根据名称模糊查询
     *
     * @param request
     * @param pramsMap
     * @param searchParamMap
     * @param modelAndView
     */
    public void mapPrams(HttpServletRequest request, Map<String, String> pramsMap, Map<String, Object> searchParamMap, ModelAndView modelAndView) {
        for (Map.Entry<String, String> entry : pramsMap.entrySet()) {
            String val = RequestUtil.getStringTrans(request, entry.getValue());
            if (!StringUtils.isBlank(val)) {
                searchParamMap.put(entry.getKey(), val);
                modelAndView.addObject(entry.getValue(), val);
            }
        }
    }

    public void forcedLogout(AclUserEntity userEntity) throws Exception {
        ssoHelper.forcedLogout(userEntity);
    }

    /**
     * id非空判断
     *
     * @param request
     * @return
     * @throws Exception
     */
    public Long checkId(HttpServletRequest request) throws Exception {
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            throw new Exception("id is empty");
        }
        return Long.valueOf(id);
    }

    /**
     * 参数非空判断
     *
     * @param request
     * @param necessaryParam
     * @return
     * @throws Exception
     */
    public String checkParamMiss(HttpServletRequest request, String[] necessaryParam) throws Exception {
        for (String param : necessaryParam) {
            if (StringUtils.isBlank(request.getParameter(param))) {
                throw new Exception(param + " is missing");
            }
        }
        return null;
    }

    /**
     * 判断用户是否拥有权限授权权限
     * TODO JIJC
     */
    public Boolean hasPrivilegeAuthorized(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.PRIVILEGE_AUTHORIZED.typeName);
    }

    /**
     * 判断用户是否拥有权限管理权限
     * TODO JIJC
     */
    public Boolean hasPrivilegeAll(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.PRIVILEGE_ALL.typeName);
    }

    /**
     * 判断用户是否拥有角色授权权限
     * TODO JIJC
     */
    public Boolean hasRoleAuthorized(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.ROLE_AUTHORIZED.typeName);
    }

    /**
     * 判断用户是否拥有角色管理权限
     * TODO JIJC
     */
    public Boolean hasRoleAll(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.ROLE_ALL.typeName);
    }

    /**
     * 判断用户是否拥有超级管理权限
     * TODO JIJC
     */
    public Boolean hasSuperAdmin(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.SUPERADMIN.typeName);
    }

    /**
     * 判断用户是否拥有用户管理权限
     * TODO JIJC
     */
    public Boolean hasUserAll(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.USER_ALL.typeName);
    }

    /**
     * 判断用户是否拥有部门管理权限
     * TODO JIJC
     */
    public Boolean hasGroupAll(SessionUserVO user) {
        return this.checkToken(user, PrivilegeTokenEnum.GROUP_ALL.typeName);
    }

    /**
     * 判断用户是否为内部
     * TODO JIJC
     */
    public Boolean isMeiren(SessionUserVO user) {
        AclBusinessEntity aclBusinessEntity = (AclBusinessEntity) aclBusinessService.findAclBusiness(user.getBusinessId()).getData();
        return aclBusinessEntity.getToken().equals(BusinessEnum.INSIDE.name);
    }

    /**
     * 判断是否有权限操作
     *
     * */
    public Boolean checkCanDo(SessionUserVO user, String id, String type){
        boolean canDo = false;
        HashMap<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("userId", user.getId());
        if(this.hasSuperAdmin(user)){// 超级管理员
            return true;
        }
        if(type.equals(CheckCanDoEnum.PRIVILEGE.typeName)){
            if (!StringUtils.isBlank(id)) {
                searchParamMap.put("privilegeId", Long.valueOf(id));
            }
            if (!StringUtils.isBlank(id) && (aclPrivilegeOwnerService.countAclPrivilegeOwner(searchParamMap) > 0)) {// 编辑情况，owner可以操作
                canDo = true;
            } else if (this.hasPrivilegeAll(user)) {// 有权限管理权限
                canDo = true;
            }
        }else if(type.equals(CheckCanDoEnum.ROLE.typeName)){
            if (!StringUtils.isBlank(id)) {
                searchParamMap.put("roleId", Long.valueOf(id));
            }
            if (!StringUtils.isBlank(id) && (aclRoleOwnerService.countAclRoleOwner(searchParamMap) > 0)) {// 编辑情况，owner可以操作
                canDo = true;
            } else if (this.hasRoleAll(user)) {// 有角色管理权限
                canDo = true;
            }
        }else if(type.equals(CheckCanDoEnum.USER.typeName)){
            if (this.hasUserAll(user)) {// 有用户管理权限
                canDo = true;
            }
        }else if(type.equals(CheckCanDoEnum.GROUP.typeName)){
            if (this.hasGroupAll(user)) {// 有部门管理权限
                canDo = true;
            }
        }
        return canDo;
    }
}
