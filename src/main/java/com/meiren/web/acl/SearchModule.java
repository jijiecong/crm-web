package com.meiren.web.acl;

import com.meiren.acl.enums.BusinessEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.acl.service.entity.AclGroupEntity;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/acl/search")
public class SearchModule extends BaseController {

    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclGroupService aclGroupService;
    @Autowired
    protected AclBizService aclBizService;
    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclHierarchyService aclHierarchyService;
    @Autowired
    protected AclRoleService aclRoleService;
    @Autowired
    protected AclBusinessService aclBusinessService;

    @RequestMapping("/hierarchy/{type}")
    @ResponseBody
    public ApiResult hierarchy(HttpServletRequest request,
                               HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            Long businessId = this.getUser(request).getBusinessId();
            if (Objects.equals(type, "init")) {
                Long id = this.checkId(request);
                result = aclHierarchyService.findAclHierarchy(id);
            } else {
                String q = RequestUtil.getStringTrans(request, "q");
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("hierarchyNameLike", q);
                paramMap.put("businessId", businessId);
                result = aclHierarchyService.loadAclHierarchyLikeName(paramMap);
            }
            result.setData(result.getData());
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    @RequestMapping("/biz/{type}")
    @ResponseBody
    public ApiResult select2(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            if (Objects.equals(type, "init")) {
                Long id = this.checkId(request);
                result = aclBizService.findAclBiz(id);
            } else {
                String q = RequestUtil.getStringTrans(request, "q");
                result = aclBizService.loadAclBizLikeName(q);
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }


    @RequestMapping("/user/{type}")
    @ResponseBody
    public ApiResult user(HttpServletRequest request,
                          HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            switch (type) {
                case "notUsed":
                    map.put("nicknameLike", RequestUtil.getStringTrans(request, "q"));
                    result = aclUserService.loadAclUserNotUsedWithRoleHas(map);
                    break;
                case "query":
                    result = aclUserService.loadAclUserLikeName(RequestUtil.getStringTrans(request, "q"));
                    break;

                case "initMoniorConfig":
                    map.put("configId", RequestUtil.getLong(request, "id"));
                    result = aclUserService.loadAclUserJoinMonitorConfig(map);
                    break;
                case "initBizOwner":
                    map.put("bizId", RequestUtil.getLong(request, "id"));
                    result = aclUserService.loadAclUserJoinBizOwner(map);
                    break;
                case "init":
                    Long id = this.checkId(request);
                    result = aclUserService.findAclUser(id);
                    break;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }


    @RequestMapping("/group/{type}")
    @ResponseBody
    public ApiResult group(HttpServletRequest request,
                           HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            AclUserEntity userEntity = this.getUser(request);
            if (Objects.equals(type, "init")) {
                Long id = this.checkId(request);
                if (id == 0) {
                    AclGroupEntity aclGroupEntity = new AclGroupEntity();
                    aclGroupEntity.setPid(0L);
                    aclGroupEntity.setName("顶级");
                    result.setData(aclGroupEntity);
                    return result;
                }
                result = aclGroupService.findAclGroup(id);
            } else {
                String q = RequestUtil.getStringTrans(request, "q");
                paramMap.put("nameLike", q);
                paramMap.put("businessId", userEntity.getBusinessId());
                result = aclGroupService.loadAclGroupLikeName(paramMap);
            }
            result.setData(result.getData());
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 查询权限
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping("/privilege/{type}")
    @ResponseBody
    public ApiResult select2biz(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> map = new HashMap<>();
            switch (type) {
                case "init":
                    Long id = this.checkId(request);
                    result = aclPrivilegeService.findAclPrivilege(id);
                    break;
                case "initBiz":
                    Long initBizId = this.checkId(request);
                    map.put("bizId", initBizId);
                    result = aclPrivilegeService.loadAclPrivilegeJoinBizHas(map);
                    break;
                case "query":
                    String q = RequestUtil.getStringTrans(request, "q");
                    result = aclPrivilegeService.loadAclPrivilegeLikeName(q);
                    break;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    @RequestMapping("/role/{type}")
    @ResponseBody
    public ApiResult role(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            AclUserEntity userEntity = this.getUser(request);
            Map<String, Object> map = new HashMap<>();
            switch (type) {
                case "query":
                    String q = RequestUtil.getStringTrans(request, "q");
                    map.put("businessId", userEntity.getBusinessId());
                    map.put("roleNameLike", q);
                    result = aclRoleService.loadAclRole(map);
                    break;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * TODO jijc
     */
    @RequestMapping(value = "/group/loadByUserId", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult loadByUserId(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", request.getParameter("userId"));
            result = aclGroupService.loadAclGroupJoinHasUser(map);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }
}
