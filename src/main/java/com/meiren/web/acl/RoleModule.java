package com.meiren.web.acl;

import com.meiren.acl.enums.ApprovalConditionEnum;
import com.meiren.acl.enums.RiskLevelEnum;
import com.meiren.acl.enums.RoleStatusEnum;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.role.index"})
@Controller
@RequestMapping("/acl/role")
public class RoleModule extends BaseController {

    @Autowired
    protected AclRoleService aclRoleService;
    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclRoleHasPrivilegeService aclRoleHasPrivilegeService;
    @Autowired
    protected AclUserHasPrivilegeService aclUserHasPrivilegeService;
    @Autowired
    protected AclGroupHasRoleService aclGroupHasRoleService;
    @Autowired
    protected AclBizService aclBizService;
    @Autowired
    protected AclRoleOwnerService aclRoleOwnerService;
    @Autowired
    protected AclBizOwnerService aclBizOwnerService;
    @Autowired
    protected AclProcessService aclProcessService;
    @Autowired
    protected AclRoleProcessService aclRoleProcessService;
    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclProcessModelService aclProcessModelService;
    private String[] necessaryParam = {
            "name",
    };

    /**
     * 查询角色
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping("/select2/{type}")
    @ResponseBody
    public ApiResult select2(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            if (Objects.equals(type, "init")) {
                Long id = this.checkId(request);
                if (id == 0) {
                    AclRoleEntity aclRoleEntity = new AclRoleEntity();
                    aclRoleEntity.setPid(0L);
                    aclRoleEntity.setName("顶级角色");
                    result.setData(aclRoleEntity);
                    return result;
                }
                result = aclRoleService.findAclRole(id);
            } else {
                String q = RequestUtil.getStringTrans(request, "q");
                result = aclRoleService.loadAclRoleLikeName(q);
            }
            result.setData(result.getData());
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/role/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        AclUserEntity user = this.getUser(request);

        //搜索名称和对应值
        Map<String, String> mapPrams = new HashMap<>();
        mapPrams.put("roleNameLike", "roleName");
        this.mapPrams(request, mapPrams, searchParamMap, modelAndView);

        Long businessId = RequestUtil.getLong(request, "businessId");
        if (businessId == null) {
            businessId = user.getBusinessId();
        }
        searchParamMap.put("businessId", businessId);
        modelAndView.addObject("businessId", businessId);
        ApiResult apiResult = aclRoleService.searchAclManageableRole(user.getId(), searchParamMap, pageNum, pageSize);

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
            List<AclRoleEntity> resultList = (List<AclRoleEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }

        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("inSide", this.isMeiren(user));

        return modelAndView;
    }

    /**
     * 删除单个
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
            result = aclRoleService.deleteAclRole(delMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 批量删除
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
            result = aclRoleService.deleteAclRole(delMap);
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
            result = aclRoleService.findAclRole(id);
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
     * @param aclRoleEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrUpdate(HttpServletRequest request, HttpServletResponse response, AclRoleEntity aclRoleEntity) {
        ApiResult result = new ApiResult();
        try {
            this.checkParamMiss(request, this.necessaryParam);
            AclUserEntity user = this.getUser(request);
            String id = request.getParameter("id");
            HashMap<String, Object> searchParamMap = new HashMap<String, Object>();
            searchParamMap.put("ownerId", user.getId());
            if (!StringUtils.isBlank(id)) {
                searchParamMap.put("roleId", Long.valueOf(id));
            }
            boolean canDo = false;
            if (!StringUtils.isBlank(id) && (aclRoleOwnerService.countAclRoleOwner(searchParamMap) > 0)) {//编辑情况
                canDo = true;
            } else if (this.hasRoleAll(user)) {// 有角色管理权限
                canDo = true;
            }
            if (canDo) {
                Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
                switch (RiskLevelEnum.getByTypeValue(riskLevel)) {
                    case LOW:
                        aclRoleEntity.setRiskLevel(RiskLevelEnum.LOW.typeValue);
                        break;
                    case MIDDLE:
                        aclRoleEntity.setRiskLevel(RiskLevelEnum.MIDDLE.typeValue);
                        break;
                    case HIGH:
                        aclRoleEntity.setRiskLevel(RiskLevelEnum.HIGH.typeValue);
                        break;
                    default:
                        throw new Exception("not find riskLevel");
                }
                Long roleId;
                Integer oldRiskLevel = RiskLevelEnum.NONE.typeValue;
                if (!StringUtils.isBlank(id)) {
                    oldRiskLevel = aclRoleService.findAclRoleById(Long.valueOf(id)).getRiskLevel();
                    Map<String, Object> paramMap = ObjectUtils.reflexToMap(aclRoleEntity);
                    result = aclRoleService.updateAclRole(Long.valueOf(id), paramMap);
                    roleId = Long.valueOf(id);
                } else {
                    aclRoleEntity.setStatus(RoleStatusEnum.NORMAL.name());
                    if (aclRoleEntity.getPid() == null) {
                        aclRoleEntity.setPid(0L);
                    }
                    result = aclRoleService.createAclRole(aclRoleEntity);
                    roleId = (Long) result.getData();
                }
                //添加风险审核流程
                result = this.addRoleProcess(roleId, riskLevel, oldRiskLevel);
            } else {
                result.setError("您无权操作角色");
                return result;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private ApiResult addRoleProcess(Long roleId, Integer riskLevel, Integer oldRiskLevel) throws Exception {
        ApiResult result = new ApiResult();
        if (oldRiskLevel == null || riskLevel.intValue() != oldRiskLevel.intValue()) {
            Map<String, Object> delParamMap = new HashMap<>();
            delParamMap.put("roleId", roleId);
            aclRoleProcessService.deleteAclRoleProcess(delParamMap);

            Map<String, Object> searchParamMap = new HashMap<>();
            searchParamMap.put("riskLevel", riskLevel);
            List<AclProcessModelEntity> all = (List<AclProcessModelEntity>) aclProcessModelService.loadAclProcessModel(searchParamMap).getData();
            for (AclProcessModelEntity entity : all) {
                AclRoleProcessEntity aclRoleProcessEntity = new AclRoleProcessEntity();
                aclRoleProcessEntity.setRoleId(roleId);
                aclRoleProcessEntity.setProcessId(entity.getProcessId());
                aclRoleProcessEntity.setHierarchyId(entity.getHierarchyId());
                aclRoleProcessEntity.setApprovalCondition(entity.getApprovalCondition());
                aclRoleProcessEntity.setApprovalLevel(entity.getApprovalLevel());
                result = aclRoleProcessService.createAclRoleProcess(aclRoleProcessEntity);
            }
        }
        return result;

    }

    @RequestMapping(value = "/process/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult process(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type, @RequestBody List<AclRoleProcessEntity> list) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> searchParamMap = new HashMap<>();
            Long id = RequestUtil.getLong(request, "id");
            searchParamMap.put("roleId", id);
            if (Objects.equals(type, "init")) {
                Map<String, Object> map = new HashMap<>();
                map.put("have", aclRoleProcessService.loadAclRoleProcess(searchParamMap).getData());  //已拥有的流程
                map.put("all", aclProcessService.loadAclProcess(null).getData());  //全部流程
                result.setData(map);
            } else {
                aclRoleProcessService.deleteAclRoleProcess(searchParamMap);
                for (AclRoleProcessEntity entity : list) {
                    entity.setRoleId(id);
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                            ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclRoleProcessService.createAclRoleProcess(entity);
                }
                result.setData(1);
            }
            result.setData(result.getData());
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 设置角色权限，可以设置的权限为可管理的权限。
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setPrivilege/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setPrivilege(HttpServletRequest request,
                                  HttpServletResponse response, @PathVariable String type) {
        ApiResult apiResult = new ApiResult();
        try {
            AclUserEntity user = this.getUser(request);
            if (!this.hasPrivilegeAuthorized(user)) {
                apiResult.setError("您没有权限授权权限！");
                return apiResult;
            }
            Long initId = RequestUtil.getLong(request, "dataId");
            Long selectedId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");

            switch (type) {
                case "init":
                    Map<String, Object> data = this.setPrivilegeInit(initId, user.getId());       //查询权限
                    apiResult.setData(data);
                    break;
                case "add":
                    apiResult = this.setPrivilegeAdd(selectedId, uid);         //添加权限
                    break;
                case "del":
                    apiResult = this.setPrivilegeDel(selectedId, uid);        //删除权限
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            apiResult.setError(e.getMessage());
            return apiResult;
        }
        return apiResult;
    }

    /**
     * 删除角色权限
     *
     * @param privilegeId
     * @param uid
     * @return
     */
    private ApiResult setPrivilegeDel(Long privilegeId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("privilegeId", privilegeId);
        delMap.put("roleId", uid);
        return aclRoleHasPrivilegeService.deleteAclRoleHasPrivilege(delMap);
    }

    /**
     * 为角色添加权限
     *
     * @param privilegeId
     * @param uid
     * @return
     */
    private ApiResult setPrivilegeAdd(Long privilegeId, Long uid) {
        AclRoleHasPrivilegeEntity entity = new AclRoleHasPrivilegeEntity();
        entity.setPrivilegeId(privilegeId);
        entity.setRoleId(uid);
        return aclRoleHasPrivilegeService.createAclRoleHasPrivilege(entity);
    }

    /**
     * 查询已拥有的权限和全部可管理的权限
     * 只能将角色所属商家下的权限赋予这个角色
     *
     * @param dataId
     * @return
     */
    private Map<String, Object> setPrivilegeInit(Long dataId, Long uid) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleId", dataId);
        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>)
                aclPrivilegeService.loadAclPrivilegeJoinRoleHas(searchParamMap).getData(); //查询已拥有的权限

        AclRoleEntity roleEntity = (AclRoleEntity) aclRoleService.findAclRole(dataId).getData();
        List<AclPrivilegeEntity> all = (List<AclPrivilegeEntity>)
                aclPrivilegeService.getManageablePrivilege(uid, roleEntity.getBusinessId()).getData(); //查询用户在该商家下的全部权限

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
     * 设置角色owner
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setOwner/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setOwner(HttpServletRequest request,
                              HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            Long initId = RequestUtil.getLong(request, "dataId");
            Long selectedId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setOwnerInit(initId);
                    result.setData(data);
                    break;
                case "add":
                    result = this.setOwnerAdd(selectedId, uid);
                    break;
                case "del":
                    result = this.setOwnerDel(selectedId, uid);
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
     * 删除角色owner
     *
     * @param userId
     * @param uid
     * @return
     */
    private ApiResult setOwnerDel(Long userId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("ownerId", userId);
        delMap.put("roleId", uid);
        return aclRoleOwnerService.deleteAclRoleOwner(delMap);
    }

    /**
     * 添加角色owner
     *
     * @param userId
     * @param uid
     * @return
     */
    private ApiResult setOwnerAdd(Long userId, Long uid) {
        AclRoleOwnerEntity entity = new AclRoleOwnerEntity();
        entity.setOwnerId(userId);
        entity.setRoleId(uid);
        return aclRoleOwnerService.createAclRoleOwner(entity);
    }

    /**
     * 查询角色owner，角色所属商家的用户才能成为owner
     * TODO bugfix
     * @param dataId
     * @return
     */
    private Map<String, Object> setOwnerInit(Long dataId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleId", dataId);
        List<AclUserEntity> selected = (List<AclUserEntity>)
                aclUserService.loadAclUserJoinRoleOwner(searchParamMap).getData();

        AclRoleEntity roleEntity = aclRoleService.findAclRoleById(dataId);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("businessId", roleEntity.getBusinessId());
        List<AclUserEntity> all = (List<AclUserEntity>)
                aclUserService.loadAclUser(paramMap).getData();

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectedVOs.add(vo);
        }
        for (AclUserEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectDataVOs.add(vo);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        dataMap.put("selectData", selectDataVOs);
        return dataMap;
    }

    /**
     * 跳转添加/修改页面
     *
     * @param request
     * @param response
     * @return
     */
    @AuthorityToken(needToken = {"meiren.acl.role.all"})
    @RequestMapping(value = "goTo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ModelAndView modelAndView = new ModelAndView();
        AclUserEntity user = this.getUser(request);
        switch (type) {
            case "add":
                modelAndView.addObject("inSide", this.isMeiren(user));
                modelAndView.addObject("title", "添加角色");
                modelAndView.addObject("id", "");
                modelAndView.addObject("businessId", user.getBusinessId());
                modelAndView.setViewName("acl/role/edit");
                break;
            case "modify":
                //编辑的时候不需要修改businessId
                modelAndView.addObject("title", "编辑角色");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.addObject("businessId", "");
                modelAndView.setViewName("acl/role/edit");
                break;
        }
        return modelAndView;
    }

    /**
     * 跳转设置拥有权限页面
     *
     * @param request
     * @param response
     * @return
     */

    @AuthorityToken(needToken = {"meiren.acl.privilege.authorized"})
    @RequestMapping(value = "goTo/setPrivilege")
    public ModelAndView setPrivilege(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "设置拥有权限");
        modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
        modelAndView.setViewName("acl/role/edit_privilege");
        return modelAndView;
    }
}
