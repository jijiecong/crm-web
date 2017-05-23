package com.meiren.web.acl;

import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.acl.service.entity.AclGroupEntity;
import com.meiren.acl.service.entity.AclHierarchyEntity;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.vo.GroupVO;
import com.meiren.vo.SelectVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("{uuid}/acl/search")
@ResponseBody
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


    /**
     * 查询商家信息列表
     */
    @RequestMapping("/business")
    public VueResult business(HttpServletRequest request) {
        Map<String, Object> searchParamMap = new HashMap<>();
        List<AclBusinessEntity> businessList = (List<AclBusinessEntity>)
            aclBusinessService.loadAclBusiness(searchParamMap).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclBusinessEntity entity : businessList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(entity.getId());
            selectVO.setName(entity.getName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 查询部门列表
     */
    @RequestMapping("/group")
    public VueResult group(HttpServletRequest request) {
        SessionUserVO user = this.getUser(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("businessId", user.getBusinessId());
        List<AclGroupEntity> groupList = (List<AclGroupEntity>)
            aclGroupService.loadAclGroup(searchParamMap).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclGroupEntity entity : groupList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(entity.getId());
            selectVO.setName(entity.getName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 查询层级列表
     */
    @RequestMapping("/hierarchy")
    public VueResult hierarchy(HttpServletRequest request) {
        SessionUserVO user = this.getUser(request);
        List<AclHierarchyEntity> groupList = (List<AclHierarchyEntity>)
            aclHierarchyService.loadAclHierarchy(null).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclHierarchyEntity entity : groupList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(entity.getId());
            selectVO.setName(entity.getHierarchyName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 查询用户列表
     */
    @RequestMapping("/user")
    public VueResult user(HttpServletRequest request) {
        SessionUserVO user = this.getUser(request);
        String query = RequestUtil.getStringTrans(request, "query");
        String initId = RequestUtil.getStringTrans(request, "initId");
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(initId)) {
            map.put("nicknameLike", query);
            map.put("businessId", user.getBusinessId());
        } else {
            map.put("id", initId);
        }
        List<AclUserEntity> userList = (List<AclUserEntity>) aclUserService.loadAclUser(map).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclUserEntity userEntity : userList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(userEntity.getId());
            selectVO.setName(userEntity.getUserName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 根据用户查询所在部门信息
     */
    @RequestMapping("/findByUserId")
    public VueResult findByUserId(HttpServletRequest request) {
        AclGroupEntity groupEntity = (AclGroupEntity)
            aclGroupService.findAclGroupJoinHasUser(com.meiren.utils.RequestUtil.getLong(request, "userId")).getData();
        GroupVO vo = new GroupVO();
        if (groupEntity == null) {
            return new VueResult();
        }
        BeanUtils.copyProperties(groupEntity, vo);
        return new VueResult(vo);
    }

    @RequestMapping("/hierarchy/{type}")
    @ResponseBody
    public ApiResult hierarchy(HttpServletRequest request,
                               HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            if (Objects.equals(type, "init")) {
                Long id = this.checkId(request);
                result = aclHierarchyService.findAclHierarchy(id);
            } else {
                String q = RequestUtil.getStringTrans(request, "q");
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("hierarchyNameLike", q);
                result = aclHierarchyService.loadAclHierarchy(paramMap);
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
            SessionUserVO userEntity = this.getUser(request);
            switch (type) {
                case "notUsed":
                    map.put("nicknameLike", RequestUtil.getStringTrans(request, "q"));
                    result = aclUserService.loadAclUserNotUsedWithRoleHas(map);
                    break;
                case "query":
                    String q = RequestUtil.getStringTrans(request, "q");
                    map.put("nicknameLike", q);
                    map.put("businessId", userEntity.getBusinessId());
                    result = aclUserService.loadAclUser(map);
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
            SessionUserVO userEntity = this.getUser(request);
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
                result = aclGroupService.loadAclGroup(paramMap);
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
            SessionUserVO userEntity = this.getUser(request);
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
                    map.put("businessId", userEntity.getBusinessId());
                    map.put("privilegeNameLike", q);
                    result = aclPrivilegeService.loadAclPrivilegeJoinBusinessHas(map);
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
            SessionUserVO userEntity = this.getUser(request);
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

    /**
     * 查找单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/business/findByName/{type}")
    @ResponseBody
    public ApiResult findByName(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            switch (type) {
                case "query":
                    result = aclBusinessService.loadAclBusinessLikeName(RequestUtil.getStringTrans(request, "q"));
                    break;
                case "init":
                    Long id = this.checkId(request);
                    result = aclBusinessService.findAclBusiness(id);
                    break;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }
}
