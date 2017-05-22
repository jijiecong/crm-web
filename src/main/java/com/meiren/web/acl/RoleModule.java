package com.meiren.web.acl;

import com.meiren.acl.enums.ApprovalConditionEnum;
import com.meiren.acl.enums.RiskLevelEnum;
import com.meiren.acl.enums.RoleStatusEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.common.utils.StringUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.RoleVO;
import com.meiren.vo.SelectVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.role.index"})
@Controller
@RequestMapping("{uuid}/acl/role")
@ResponseBody
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
    private RoleVO entityToVo(AclRoleEntity entity) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclRoleEntity voToEntity(RoleVO vo) {
        AclRoleEntity entity = new AclRoleEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleNameLike", com.meiren.utils.RequestUtil.getStringTrans(request, "name"));
        searchParamMap.put("businessId", RequestUtil.getLong(request, "businessId"));
        ApiResult apiResult = aclRoleService.searchAclRole(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 查询
     */
    @RequestMapping("/find")
    public VueResult find(HttpServletRequest request) {
        Long id = com.meiren.utils.RequestUtil.getLong(request, "id");
        AclRoleEntity entity = (AclRoleEntity) aclRoleService.findAclRole(id).getData();
        RoleVO vo = this.entityToVo(entity);
        return new VueResult(vo);
    }

    /**
     * 添加编辑
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, RoleVO vo) throws Exception {
        VueResult result = new VueResult();
        this.checkParamMiss(request, this.necessaryParam);
        String id = request.getParameter("id");
        SessionUserVO user = this.getUser(request);
        HashMap<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("userId", user.getId());
        if (!StringUtils.isBlank(id)) {
            searchParamMap.put("roleId", Long.valueOf(id));
        }
        boolean canDo = false;
        if (!StringUtils.isBlank(id) && (aclRoleOwnerService.countAclRoleOwner(searchParamMap) > 0)) {// 编辑情况
            canDo = true;
        } else if (this.hasRoleAll(user)) {// 有权限管理权限
            canDo = true;
        }
        if (canDo) {
            AclRoleEntity entity = this.voToEntity(vo);
            Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
            if (riskLevel == null) {
                result.setError("请选择正确的风险等级！");
                return result;
            }
            switch (RiskLevelEnum.getByTypeValue(riskLevel)) {
                case LOW:
                    entity.setRiskLevel(RiskLevelEnum.LOW.typeValue);
                    break;
                case MIDDLE:
                    entity.setRiskLevel(RiskLevelEnum.MIDDLE.typeValue);
                    break;
                case HIGH:
                    entity.setRiskLevel(RiskLevelEnum.HIGH.typeValue);
                    break;
                default:
                    throw new Exception("请选择正确的风险等级！");
            }
            Long roleId;
            Integer oldRiskLevel = RiskLevelEnum.NONE.typeValue;
            if (!StringUtils.isBlank(id)) {
                oldRiskLevel = aclRoleService.findAclRoleById(Long.valueOf(id)).getRiskLevel();
                Map<String, Object> paramMap = ObjectUtils.entityToMap(entity);
                aclRoleService.updateAclRole(Long.valueOf(id), paramMap);
                roleId = Long.valueOf(id);
            } else {
                entity.setStatus(RoleStatusEnum.NORMAL.name());
                entity.setPid(0l);
                aclRoleService.createAclRole(entity);
                roleId = (Long) result.getData();
            }
            // 添加风险审核流程
            this.addRoleProcess(roleId, riskLevel, oldRiskLevel);
            result.setData(true);
        } else {
            result.setError("您无权添加编辑该权限");
            return result;
        }
        return result;
    }

    /**
     * 根据风险等级添加默认审核流程
     *
     * @param privilegeId
     * @param riskLevel
     * @param oldRiskLevel
     * @return
     */
    private void addRoleProcess(Long roleId, Integer riskLevel, Integer oldRiskLevel) {
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
                aclRoleProcessService.createAclRoleProcess(aclRoleProcessEntity);
            }
        }

    }

    /**
     * 设置owner
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setOwner/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult setOwner(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        VueResult result = new VueResult();
        try {
            SessionUserVO user = this.getUser(request);
            if(!this.hasPrivilegeAll(user)){
                result.setError("您没有权限操作角色！");
                return result;
            }
            Long initId = RequestUtil.getLong(request, "initId");
            String selectedIds = RequestUtil.getString(request,"selectedIds");
            String [] selectedIds_arr = null;
            if(selectedIds != null){
                selectedIds_arr = selectedIds.split(",");
            }
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setOwnerInit(initId,user.getBusinessId());
                    result.setData(data);
                    break;
                case "right":
                    result = this.setOwnerAdd(initId, selectedIds_arr);
                    break;
                case "left":
                    result = this.setOwnerDel(initId, selectedIds_arr);
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

    private VueResult setOwnerDel(Long initId, String[] selectedIds_arr) {
        for(String id : selectedIds_arr) {
            Map<String, Object> delMap = new HashMap<>();
            delMap.put("ownerId", Long.parseLong(id));
            delMap.put("roleId", initId);
            aclRoleOwnerService.deleteAclRoleOwner(delMap);
        }
        return new VueResult("操作成功！");
    }

    private VueResult setOwnerAdd(Long initId, String[] selectedIds_arr) {
        for(String id : selectedIds_arr) {
            AclRoleOwnerEntity entity = new AclRoleOwnerEntity();
            entity.setOwnerId(Long.parseLong(id));
            entity.setRoleId(initId);
            aclRoleOwnerService.createAclRoleOwner(entity);
        }
        return new VueResult("操作成功！");
    }

    /**
     * 查询权限属于某个用户及全部用户
     *
     * @param dataId
     * @return
     */
    private Map<String, Object> setOwnerInit(Long dataId, Long businessId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleId", dataId);
        List<AclUserEntity> selected = (List<AclUserEntity>)
            aclUserService.loadAclUserJoinRoleOwner(searchParamMap).getData(); // 根据查询权限Id查询用户

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("businessId", businessId);
        List<AclUserEntity> all = (List<AclUserEntity>)
            aclUserService.loadAclUser(paramMap).getData(); // 查询商家下所有用户

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId()); // 将信息转换为id name 类型
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

 /*   *//**
     * 查询角色
     *
     * @param request
     * @param response
     * @param type
     * @return
     *//*
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
    }*/

    /**
     * 删除单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    @ResponseBody
    public VueResult delete(HttpServletRequest request, HttpServletResponse response) {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        try {
            Long id = this.checkId(request);
            delMap.put("id", id);
            aclRoleService.deleteAclRole(delMap);
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
    public VueResult deleteBatch(HttpServletRequest request, HttpServletResponse response) {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        String[] ids = request.getParameterValues("ids[]");
        List<String> idsList = Arrays.asList(ids);
        try {
            delMap.put("inIds", idsList);
            aclRoleService.deleteAclRole(delMap);
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
    @RequestMapping(value = "/setRoleHasPrivilege/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult setPrivilege(HttpServletRequest request,
                                  HttpServletResponse response, @PathVariable String type) {
        VueResult result = new VueResult();
        try {
            SessionUserVO user = this.getUser(request);
            if (!this.hasPrivilegeAuthorized(user)) {
                result.setError("您没有权限授权权限！");
                return result;
            }
            Long initId = RequestUtil.getLong(request, "initId");
            String selectedIds = RequestUtil.getString(request,"selectedIds");
            String [] selectedIds_arr = null;
            if(selectedIds != null){
                selectedIds_arr = selectedIds.split(",");
            }
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setPrivilegeInit(initId, user.getId());       //查询权限
                    result.setData(data);
                    break;
                case "right":
                    result = this.setPrivilegeAdd(initId, selectedIds_arr);         //添加权限
                    break;
                case "left":
                    result = this.setPrivilegeDel(initId, selectedIds_arr);        //删除权限
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
     * 删除角色权限
     *
     * @param privilegeId
     * @param uid
     * @return
     */
    private VueResult setPrivilegeDel(Long initId, String[] selectedIds_arr) {
        for(String id : selectedIds_arr) {
            Map<String, Object> delMap = new HashMap<>();
            delMap.put("privilegeId", Long.parseLong(id));
            delMap.put("roleId", initId);
            aclRoleHasPrivilegeService.deleteAclRoleHasPrivilege(delMap);
        }
        return new VueResult("操作成功！");
    }

    /**
     * 为角色添加权限
     *
     * @param privilegeId
     * @param uid
     * @return
     */
    private VueResult setPrivilegeAdd(Long initId, String[] selectedIds_arr) {
        for(String id : selectedIds_arr) {
            AclRoleHasPrivilegeEntity entity = new AclRoleHasPrivilegeEntity();
            entity.setPrivilegeId(Long.parseLong(id));
            entity.setRoleId(initId);
            aclRoleHasPrivilegeService.createAclRoleHasPrivilege(entity);
        }
        return new VueResult("操作成功！");
    }

    /**
     * 查询已拥有的权限和全部可管理的权限
     * 只能将角色所属商家下的权限赋予这个角色
     *
     * @param dataId
     * @return
     */
    private Map<String, Object> setPrivilegeInit(Long initId, Long uid) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleId", initId);
        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>)
            aclPrivilegeService.loadAclPrivilegeJoinRoleHas(searchParamMap).getData(); //查询已拥有的权限

        AclRoleEntity roleEntity = (AclRoleEntity) aclRoleService.findAclRole(initId).getData();
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
 /*   @RequestMapping(value = "/process/{type}", method = RequestMethod.POST)
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
    }*/





}
