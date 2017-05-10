package com.meiren.web.acl;

import com.meiren.acl.enums.UserPrivilegeStatusEnum;
import com.meiren.acl.enums.UserRoleStatusEnum;
import com.meiren.acl.enums.UserStatusEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.monitor.utils.ObjectUtils;
import com.meiren.vo.SelectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.index"})
@Controller
@RequestMapping("acl/user")
public class UserModule extends BaseController {

    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclGroupLeaderService aclGroupLeaderService;
    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclUserHasPrivilegeService aclUserHasPrivilegeService;
    @Autowired
    protected AclRoleService aclRoleService;
    @Autowired
    protected AclUserHasRoleService aclUserHasRoleService;
    @Autowired
    protected AclGroupHasUserService aclGroupHasUserService;
    @Autowired
    protected AclGroupService aclGrouprService;
    @Autowired
    protected AclBusinessService aclBusinessService;

    private String[] necessaryParam = {
            "userName",
            "mobile",
    };

    /**
     * 离职
     * TODO jijc
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/resign", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult resign(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long userId = RequestUtil.getLong(request, "id");
            if (userId == null) {
                result.setError("id not null");
                return result;
            }
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("userId", userId);
            aclUserHasPrivilegeService.deleteAclUserHasPrivilege(delMap);
            aclUserHasRoleService.deleteAclUserHasRole(delMap);
            aclGroupHasUserService.deleteAclGroupHasUser(delMap);
            Map<String, Object> delMap_leader = new HashMap<String, Object>();
            delMap_leader.put("leaderId", userId);
            aclGroupLeaderService.deleteAclGroupLeader(delMap_leader);
            delMap.put("status", UserStatusEnum.DISABLE.name());
            result = aclUserService.updateAclUser(userId, delMap);

        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 转岗
     * TODO jijc
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/group/changeGroup", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult changeGroup(HttpServletRequest request,
                                 HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long userId = RequestUtil.getLong(request, "userId");
            if (userId == null) {
                result.setError("id not null");
                return result;
            }
            Long fromGroupId = RequestUtil.getLong(request, "fromGroupId");
            Long toGroupId = RequestUtil.getLong(request, "toGroupId");
            result = this.changeGroup(userId, fromGroupId, toGroupId);     //转岗

        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private ApiResult changeGroup(Long userId, Long fromGroupId, Long toGroupId) throws Exception {
        ApiResult result = new ApiResult();
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        //1、查询fromGroupId拥有的权限id和角色id
        //2、根据权限id和角色id，删除用户拥有的原部门下关联的权限和角色
        searchParamMap.put("groupId", fromGroupId);
        List<AclPrivilegeEntity> allPrivilege = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilegeJoinGroupHas(searchParamMap).getData();
        for (AclPrivilegeEntity entity : allPrivilege) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("userId", userId);
            delMap.put("privilegeId", entity.getId());
            aclUserHasPrivilegeService.deleteAclUserHasPrivilege(delMap);
        }

        List<AclRoleEntity> allRole = (List<AclRoleEntity>) aclRoleService.loadAclRoleJoinGroupHas(searchParamMap).getData();
        for (AclRoleEntity entity : allRole) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("userId", userId);
            delMap.put("roleId", entity.getId());
            aclUserHasRoleService.deleteAclUserHasRole(delMap);
        }

        //3、将用户从原来的部门中删除，并添加到新的部门
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("userId", userId);
        delMap.put("groupId", fromGroupId);
        aclGroupHasUserService.deleteAclGroupHasUser(delMap);
        Map<String, Object> delMap_leader = new HashMap<String, Object>();
        delMap_leader.put("leaderId", userId);
        delMap_leader.put("leaderDeptId", fromGroupId);
        aclGroupLeaderService.deleteAclGroupLeader(delMap_leader);
        AclGroupHasUserEntity entity = new AclGroupHasUserEntity();
        entity.setUserId(userId);
        entity.setGroupId(toGroupId);
        result = aclGroupHasUserService.createAclGroupHasUser(entity);

        return result;
    }


    /**
     * 判断进行权限的哪种操作
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/privilege/control/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult privilegeControl(HttpServletRequest request,
                                      HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            Long initId = RequestUtil.getLong(request, "dataId");
            Long selectedId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.privilegeControlInit(initId);  //查询所有（当前拥有的权限、未拥有权限）
                    result.setData(data);
                    break;
                case "add":
                    result = this.privilegeControlAdd(selectedId, uid);     //禁用
                    break;
                case "del":
                    result = this.privilegeControlDel(selectedId, uid);     //取消禁用
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 删除权限控制
     *
     * @param selectedId
     * @param uid
     * @return
     * @throws Exception
     */
    private ApiResult privilegeControlDel(Long selectedId, Long uid) throws Exception {
        AclUserHasPrivilegeEntity entity = new AclUserHasPrivilegeEntity();
        entity.setStatus(UserPrivilegeStatusEnum.DELETE.name());
        entity.setUserId(uid);
        entity.setPrivilegeId(selectedId);
        return aclUserHasPrivilegeService.deleteAclUserHasPrivilege(ObjectUtils.reflexToMap(entity));
    }

    /**
     * 添加权限控制
     *
     * @param selectedId
     * @param uid
     * @return
     */
    private ApiResult privilegeControlAdd(Long selectedId, Long uid) {
        AclUserHasPrivilegeEntity entity = new AclUserHasPrivilegeEntity();
        entity.setStatus(UserPrivilegeStatusEnum.DELETE.name());
        entity.setUserId(uid);
        entity.setPrivilegeId(selectedId);
        return aclUserHasPrivilegeService.createAclUserHasPrivilege(entity);
    }

    /**
     * 初始化用户拥有的权限和被禁用的权限
     *
     * @param userId
     * @return
     */
    private Map<String, Object> privilegeControlInit(Long userId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("hasStatus", UserPrivilegeStatusEnum.DELETE.name());
        searchParamMap.put("userId", userId);
        List<AclPrivilegeEntity> all = (List<AclPrivilegeEntity>)
                aclPrivilegeService.getAllPrivilegeByUser(userId).getData();
        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>)
                aclPrivilegeService.loadAclPrivilegeJoinUserHas(searchParamMap).getData();

        all.addAll(selected);
        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclPrivilegeEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            selectedVOs.add(vo);
        }
        for (AclPrivilegeEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            selectDataVOs.add(vo);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        dataMap.put("selectData", selectDataVOs);
        return dataMap;
    }

    /**
     * 判断进行角色的哪种操作
     *
     * @param request
     * @param response
     * @param type     init:初始化；add:禁用；del:取消禁用
     * @return
     */
    @RequestMapping(value = "/role/control/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult roleControl(HttpServletRequest request,
                                 HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            Long initId = RequestUtil.getLong(request, "dataId");
            Long selectedId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.roleControlInit(initId);  //查询所有（当前拥有的角色、被禁用角色）
                    result.setData(data);
                    break;
                case "add":
                    result = this.roleControlAdd(selectedId, uid);     //禁用
                    break;
                case "del":
                    result = this.roleControlDel(selectedId, uid);     //取消禁用
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 取消禁用角色
     *
     * @param selectedId
     * @param uid
     * @return ApiResult
     * @throws Exception
     */
    private ApiResult roleControlDel(Long selectedId, Long uid) throws Exception {
        AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
        entity.setStatus(UserRoleStatusEnum.DELETE.name());
        entity.setUserId(uid);
        entity.setRoleId(selectedId);
        return aclUserHasRoleService.deleteAclUserHasRole(ObjectUtils.reflexToMap(entity));
    }

    /**
     * 禁用角色
     *
     * @param selectedId
     * @param uid
     * @return ApiResult
     */
    private ApiResult roleControlAdd(Long selectedId, Long uid) {
        AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
        entity.setStatus(UserRoleStatusEnum.DELETE.name());
        entity.setUserId(uid);
        entity.setRoleId(selectedId);
        return aclUserHasRoleService.createAclUserHasRole(entity);
    }

    /**
     * 初始化用户拥有角色和禁用角色
     *
     * @param initId
     * @return ApiResult
     */
    private Map<String, Object> roleControlInit(Long initId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("hasStatus", UserRoleStatusEnum.DELETE.name());
        searchParamMap.put("userId", initId);
        List<AclRoleEntity> all = (List<AclRoleEntity>)
                aclRoleService.getAllRoleByUser(initId).getData();
        List<AclRoleEntity> selected = (List<AclRoleEntity>)
                aclRoleService.loadAclRoleJoinUserHas(searchParamMap).getData();

        all.addAll(selected);
        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclRoleEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            selectedVOs.add(vo);
        }
        for (AclRoleEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            selectDataVOs.add(vo);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        dataMap.put("selectData", selectDataVOs);
        return dataMap;
    }

    /**
     * 用户层级修改
     *
     * @param request
     * @param response
     * @param aclUserEntity
     * @return
     */
    @RequestMapping(value = {"hierarchy/set"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult hierarchySet(HttpServletRequest request, HttpServletResponse response, AclUserEntity aclUserEntity) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("hierarchyId", aclUserEntity.getHierarchyId());
            result = aclUserService.updateAclUser(id, paramMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }


    /**
     * 用户列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ApiResult apiResult = new ApiResult();
        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/user/index");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        AclUserEntity user = this.getUser(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        //搜索名称和对应值
        Map<String, String> userPrams = new HashMap<>();
        userPrams.put("nicknameLike", "nickname");

        this.mapPrams(request, userPrams, searchParamMap, modelAndView);

        boolean isInside = this.isMeiren(user);

        Long businessId = RequestUtil.getLong(request, "businessId");
        if (businessId == null) {
            businessId = user.getBusinessId();
        }
        modelAndView.addObject("businessId", businessId);
        searchParamMap.put("businessId", businessId);
        apiResult = aclUserService.searchAclUser(searchParamMap, pageNum, pageSize);  //如果是内部用户则返回内部所有用户 且可查询任何商家用户

        String message = this.checkApiResult(apiResult);
        if (message != null) {
            modelAndView.addObject("message", message);
            return modelAndView;
        }

        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();
        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }

        if (resultMap.get("data") != null) {
            List<AclUserEntity> resultList = (List<AclUserEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }

        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("inSide", isInside);

        return modelAndView;

    }

    /**
     * 删除单个       --禁用
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult delete(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        Map<String, Object> delMap = new HashMap<>();
        try {
            Long id = this.checkId(request);
            delMap.put("id", id);
            result = aclUserService.deleteAclUser(delMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 禁用单个用户
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "disable", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult disable(HttpServletRequest request, HttpServletResponse response, String status) {
        ApiResult result = new ApiResult();
        Map<String, Object> delMap = new HashMap<>();
        try {
            UserStatusEnum userStatusEnum = !status.toLowerCase().equals("disable") ? UserStatusEnum.NORMAL : UserStatusEnum.DISABLE;
            Long id = this.checkId(request);
            delMap.put("id", id);
            delMap.put("status", userStatusEnum.name());
            result = aclUserService.updateAclUser(id, delMap);
            AclUserEntity user = (AclUserEntity) aclUserService.findAclUser(id).getData();
            this.forcedLogout(user);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 批量删除       --禁用
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteBatch", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteBatch(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        Map<String, Object> delMap = new HashMap<>();
        String[] ids = request.getParameterValues("ids[]");
        List<String> idsList = Arrays.asList(ids);
        try {
            delMap.put("inIds", idsList);
            result = aclUserService.deleteAclUser(delMap);
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
    @RequestMapping(value = "find", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult find(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            ApiResult apiResult = aclUserService.findAclUser(id);
            AclUserEntity entity = (AclUserEntity) apiResult.getData();
            entity.setPassword(null);
            result.setData(entity);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 添加/修改
     *
     * @param request
     * @param response
     * @param aclUserEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response, AclUserEntity aclUserEntity) {
        ApiResult result = new ApiResult();
        try {
            String id = request.getParameter("id");
            this.checkParamMiss(request, this.necessaryParam);
            if (!StringUtils.isBlank(id)) {
                Map<String, Object> paramMap = this.converRequestMap(request.getParameterMap());
                result = aclUserService.updateAclUserByPassword(Long.valueOf(id), paramMap);
            } else {
                if (aclUserEntity.getPassword() == null || StringUtils.isBlank(aclUserEntity.getPassword())) {
                    result.setError("password is missing");
                    return result;
                }
                result = aclUserService.createAclUser(aclUserEntity);
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 跳转添加/修改页面，test
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "goTo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ModelAndView modelAndView = new ModelAndView();
        AclUserEntity userEntity = this.getUser(request);
        boolean isInside = isMeiren(userEntity);
        modelAndView.addObject("isInside", isInside);
        switch (type) {
            case "add":
                modelAndView.addObject("title", "添加用户");
                modelAndView.addObject("id", "");
                modelAndView.addObject("add", "add");
                modelAndView.addObject("businessId", userEntity.getBusinessId());
                break;
            case "modify":
                modelAndView.addObject("title", "编辑用户");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                break;
        }
        modelAndView.setViewName("acl/user/edit");
        return modelAndView;
    }
}

