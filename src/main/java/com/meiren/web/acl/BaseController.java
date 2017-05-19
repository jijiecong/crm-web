package com.meiren.web.acl;

import com.meiren.acl.enums.BusinessEnum;
import com.meiren.acl.enums.PrivilegeTokenEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.sso.web.SsoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BaseController {
    @Autowired
    private SsoHelper ssoHelper;
    public static final int DEFAULT_ROWS = 10;

    @Autowired
    protected AclBizHasRoleService aclBizHasRoleService;
    @Autowired
    protected AclBizOwnerService aclBizOwnerService;
    @Autowired
    protected AclRoleOwnerService aclRoleOwnerService;
    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclBusinessService aclBusinessService;

    /**
     * 查询当前是否是应用owner,并返回应用id
     */
    public List<Long> getBizIdsByBizOwner(AclUserEntity user) {
        List<Long> inBizIds = new ArrayList<>();
        Map<String, Object> whereBiz = new HashMap<>();
        whereBiz.put("ownerId", user.getId());
        List<AclBizOwnerEntity> list = (List<AclBizOwnerEntity>)
                aclBizOwnerService.loadAclBizOwner(whereBiz).getData();
        for (AclBizOwnerEntity entity : list) {
            inBizIds.add(entity.getBizId());
        }
        return inBizIds;
    }

    /**
     * 查询用户如果是应用owner,获取应用下所有的角色id
     *
     * @param user
     * @return
     */
    public List<Long> getBizHasRoleIdsByBizOwner(AclUserEntity user) {
        List<Long> roleIds = new ArrayList<>();
        List<Long> bizIds = this.getBizIdsByBizOwner(user);
        Map<String, Object> whereBiz = new HashMap<>();
        if (bizIds != null && bizIds.size() > 0) {
            whereBiz.put("inBizIds", bizIds);
            List<AclBizHasRoleEntity> list = (List<AclBizHasRoleEntity>)
                    aclBizHasRoleService.loadAclBizHasRole(whereBiz).getData();
            for (AclBizHasRoleEntity entity : list) {
                roleIds.add(entity.getRoleId());
            }
        }
        return roleIds;
    }

    /**
     * 根据角色owner查询角色id
     * TODO zhangw
     * @param user
     * @return
     */
    public List<Long> getRoleIdsByRoleOwner(AclUserEntity user) {
        List<Long> ids = new ArrayList<>();
        Map<String, Object> wherOwner = new HashMap<String, Object>();
        wherOwner.put("ownerId", user.getId());
        List<AclRoleOwnerEntity> list = (List<AclRoleOwnerEntity>)
                aclRoleOwnerService.loadAclRoleOwner(wherOwner).getData();
        for (AclRoleOwnerEntity aclRoleOwnerEntity : list) {
            ids.add(aclRoleOwnerEntity.getRoleId());
        }
        return ids;
    }

    /**
     * 查询用户为角色owner 和 应用owner 时所拥有的所有角色
     * TODO zhangw
     * @param user
     * @return
     */
    public List<Long> getAllRoleIdsByRoleOwner(AclUserEntity user) {
        //获取该用户是角色owner的角色id
        List<Long> roleIds = this.getRoleIdsByRoleOwner(user);  //角色owner
        //获取该用户如果是bizOwner的时候,biz下的所有角色id
        List<Long> roleIdsByBizOwner = this.getBizHasRoleIdsByBizOwner(user);  //应用owner
        roleIds.addAll(roleIdsByBizOwner);
        roleIds.add(0L);
        return roleIds;
    }

    public boolean checkToken(AclUserEntity user, String token) {
        if (user == null || StringUtils.isBlank(token)) {
            return false;
        }
        Map<String, String> tokenMap = ssoHelper.getTokenMap(user.getId());
        return !StringUtils.isBlank(tokenMap.get(token));
    }

    public AclUserEntity getUser(HttpServletRequest request) {
        return (AclUserEntity) request.getSession().getAttribute("user");
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

    /**
     * 验证返回结果
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
     * @param request
     * @param pramsMap
     * @param searchParamMap
     * @param modelAndView
     */
    public void mapPrams(HttpServletRequest request, Map<String,String> pramsMap, Map<String,Object> searchParamMap, ModelAndView modelAndView) {
     for(Map.Entry<String, String> entry :pramsMap.entrySet()){
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
     *
     * */
    public Boolean hasPrivilegeAuthorized(AclUserEntity user){
    	return this.checkToken(user, PrivilegeTokenEnum.PRIVILEGE_AUTHORIZED.typeName);
    }

    /**
     * 判断用户是否拥有权限管理权限
     * TODO JIJC
     *
     * */
	public Boolean hasPrivilegeAll(AclUserEntity user) {
		return this.checkToken(user, PrivilegeTokenEnum.PRIVILEGE_ALL.typeName);
	}

	/**
     * 判断用户是否拥有角色授权权限
     * TODO JIJC
     *
     * */
	public Boolean hasRoleAuthorized(AclUserEntity user) {
		return this.checkToken(user, PrivilegeTokenEnum.ROLE_AUTHORIZED.typeName);
	}

	/**
     * 判断用户是否拥有角色管理权限
     * TODO JIJC
     *
     * */
	public Boolean hasRoleAll(AclUserEntity user) {
		return this.checkToken(user, PrivilegeTokenEnum.ROLE_ALL.typeName);
	}

	/**
     * 判断用户是否拥有超级管理权限
     * TODO JIJC
     *
     * */
	public Boolean hasSuperAdmin(AclUserEntity user) {
		return this.checkToken(user, PrivilegeTokenEnum.SUPERADMIN.typeName);
	}

	/**
     * 判断用户是否拥有用户管理权限
     * TODO JIJC
     *
     * */
	public Boolean hasUserAll(AclUserEntity user) {
		return this.checkToken(user, PrivilegeTokenEnum.USER_ALL.typeName);
	}

	/**
     * 判断用户是否拥有部门管理权限
     * TODO JIJC
     *
     * */
	public Boolean hasGroupAll(AclUserEntity user) {
		return this.checkToken(user, PrivilegeTokenEnum.GROUP_ALL.typeName);
	}

    /**
     * 判断用户是否为内部
     * TODO JIJC
     *
     * */
    public Boolean isMeiren(AclUserEntity user) {
        AclBusinessEntity aclBusinessEntity = (AclBusinessEntity) aclBusinessService.findAclBusiness(user.getBusinessId()).getData();
        return aclBusinessEntity.getToken().equals(BusinessEnum.INSIDE.name);
    }
}
