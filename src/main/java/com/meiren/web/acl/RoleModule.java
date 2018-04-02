package com.meiren.web.acl;

import com.alibaba.fastjson.JSONArray;
import com.meiren.acl.enums.*;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.exception.ApiResultException;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.common.utils.StringUtils;
import com.meiren.tech.mbc.action.ActionControllerLog;
import com.meiren.tech.mbc.service.MbcMenuLoadService;
import com.meiren.tech.mbc.service.MbcMenuService;
import com.meiren.tech.mbc.service.entity.MbcMenuEntity;
import com.meiren.utils.MBCTypeEnum;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.*;
import org.apache.commons.collections.map.HashedMap;
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
    protected AclRoleOwnerService aclRoleOwnerService;
    @Autowired
    protected AclProcessService aclProcessService;
    @Autowired
    protected AclRoleProcessService aclRoleProcessService;
    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclProcessModelService aclProcessModelService;
    @Autowired
    private MbcMenuLoadService mbcMenuLoadService;
    @Autowired
    protected AclUserHasRoleService aclUserHasRoleService;
    @Autowired
    protected AclGroupHasRoleService aclGroupHasRoleService;
    @Autowired
    protected AclApplyService aclApplyService;
    @Autowired
    private AclBusinessService aclBusinessService;

    private String[] necessaryParam = {
        "name","businessId",
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
        searchParamMap.put("roleNameLike", RequestUtil.getStringTrans(request, "name"));
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
    public VueResult find(HttpServletRequest request) throws Exception {
        Long id = this.checkId(request);
        AclRoleEntity entity = (AclRoleEntity) aclRoleService.findAclRole(id).getData();
        RoleVO vo = this.entityToVo(entity);
        return new VueResult(vo);
    }

    /**
     * 添加编辑
     */
    @ActionControllerLog(descriptions = "添加编辑角色")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, RoleVO vo) throws Exception {
        VueResult result = new VueResult();
        this.checkParamMiss(request, this.necessaryParam);
        String id = request.getParameter("id");
        SessionUserVO user = this.getUser(request);
        if (this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            AclRoleEntity entity = this.voToEntity(vo);
            Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
            if (riskLevel == null) {
                result.setError("请选择正确的风险等级！");
                return result;
            }else{
                entity.setRiskLevel(RiskLevelEnum.getByTypeValue(riskLevel).typeValue);
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
                entity.setPid(0L);
                ApiResult apiResult = aclRoleService.createAclRole(entity);
                roleId = (Long) apiResult.getData();
            }
            // 添加风险审核流程
            this.addRoleProcess(roleId, RiskLevelEnum.getByTypeValue(riskLevel).typeValue, oldRiskLevel);
            result.setData(true);
        } else {
            result.setError("您无权添加编辑该角色");
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
    public VueResult setOwner(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        String id = request.getParameter("initId");
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        Long initId = Long.parseLong(id);
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (!StringUtils.isBlank(selectedIds)) {
            selectedIds_arr = selectedIds.split(",");
        }
        switch (type) {
            case "init":
                Map<String, Object> data = this.setOwnerInit(initId, user.getBusinessId());
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
        return result;
    }

    private VueResult setOwnerDel(Long initId, String[] selectedIds_arr) {
        for (String id : selectedIds_arr) {
            Map<String, Object> delMap = new HashMap<>();
            delMap.put("ownerId", Long.parseLong(id));
            delMap.put("roleId", initId);
            aclRoleOwnerService.deleteAclRoleOwner(delMap);
        }
        return new VueResult("操作成功！");
    }

    private VueResult setOwnerAdd(Long initId, String[] selectedIds_arr) {
        for (String id : selectedIds_arr) {
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
        paramMap.put("status", UserStatusEnum.NORMAL.typeName);
        List<AclUserEntity> all = (List<AclUserEntity>)
            aclUserService.loadAclUser(paramMap).getData(); // 查询商家下所有用户

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId()); // 将信息转换为id name 类型
            vo.setName(entity.getNickname());
            selectedVOs.add(vo);
        }
        for (AclUserEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getNickname());
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
    public VueResult delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        String id = request.getParameter("id");
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        delMap.put("id", id);
        aclRoleService.deleteAclRole(delMap).check();
        result.setData("操作成功！");
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
    public VueResult deleteBatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        String[] ids = request.getParameterValues("ids[]");
        if (ids.length == 0 || ids == null) {
            throw new Exception("请选择要删除的角色！");
        }
        List<String> idsList = Arrays.asList(ids);
        delMap.put("inIds", idsList);
        aclRoleService.deleteAclRole(delMap);
        result.setData("操作成功！");
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
                                  HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("initId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        if (!this.hasPrivilegeAuthorized(user)) {
            result.setError("您没有权限授权权限！");
            return result;
        }
        Long initId = Long.parseLong(id);
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (!StringUtils.isBlank(selectedIds)) {
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
        for (String id : selectedIds_arr) {
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
        List<AclRoleHasPrivilegeEntity> list = new ArrayList<>();
        for (String id : selectedIds_arr) {
            AclRoleHasPrivilegeEntity entity = new AclRoleHasPrivilegeEntity();
            entity.setPrivilegeId(Long.parseLong(id));
            entity.setRoleId(initId);
            list.add(entity);
        }
        aclRoleHasPrivilegeService.createBatch(list);
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

    @RequestMapping(value = "/process/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult process(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type) throws Exception {

        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("id");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleId", id);
        if (Objects.equals(type, "init")) {
            Map<String, Object> map = new HashMap<>();
            List<AclRoleProcessEntity> have = (List<AclRoleProcessEntity>) aclRoleProcessService.loadAclRoleProcess(searchParamMap).getData();
            List<AclProcessEntity> all = (List<AclProcessEntity>) aclProcessService.loadAclProcess(null).getData();
            List<ProcessVO> allVOs = new ArrayList<>();

            for (AclProcessEntity entity : all) {
                ProcessVO vo = new ProcessVO();
                BeanUtils.copyProperties(entity, vo);
                vo.setChecked(false);
                vo.setApprovalCondition(ApprovalConditionEnum.AND.name);
                for (AclRoleProcessEntity roleProcessEntity : have) {
                    if (roleProcessEntity.getProcessId() == vo.getId()) {
                        vo.setChecked(true);
                        vo.setHierarchyId(roleProcessEntity.getHierarchyId());
                        vo.setApprovalCondition(roleProcessEntity.getApprovalCondition());
                    }
                }
                allVOs.add(vo);
            }
            map.put("all", allVOs); // 查询全部审核流程
            result.setData(map);
        } else {
            String process = RequestUtil.getString(request, "process");
            List<AclRoleProcessEntity> list = new ArrayList<>();
            list = JSONArray.parseArray(process, AclRoleProcessEntity.class);
            aclRoleProcessService.deleteAclRoleProcess(searchParamMap);
            for (AclRoleProcessEntity entity : list) {
                if (entity.getChecked()) {
                    entity.setRoleId(Long.parseLong(id));
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                        ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclRoleProcessService.createAclRoleProcess(entity);
                }
            }
            result.setData("操作成功！");
        }
        return result;
    }

    /**
     * 获取角色视图权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getRoleViewPrivilege", method = RequestMethod.GET)
    public VueResult getRoleViewPrivilege(HttpServletRequest request) throws
        Exception {
        VueResult result = new VueResult();

        Long roleId = RequestUtil.getLong(request, "initId");
        if (roleId == null) {
            throw new Exception("请选择要操作的角色！");
        }
        Long businessId = RequestUtil.getLong(request, "businessId");
        if (businessId == null) {
            throw new Exception("操作失败！");
        }
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, String.valueOf(roleId), CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        List<AclPrivilegeEntity> manageablePrivilege = (List<AclPrivilegeEntity>)
            aclPrivilegeService.getManageablePrivilege(user.getId(), businessId).getData();
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("roleId", roleId);
        List<AclPrivilegeEntity> haveList = (List<AclPrivilegeEntity>)
            aclPrivilegeService.loadAclPrivilegeJoinRoleHas(searchParamMap).getData();
        List<String> defaultCheckedData = new ArrayList<>();
        for(AclPrivilegeEntity aclPrivilegeEntity: haveList){
            defaultCheckedData.add(aclPrivilegeEntity.getToken());
        }
        AclBusinessEntity aclBusinessEntity =
            (AclBusinessEntity) aclBusinessService.findAclBusiness(businessId).check().getData();
        Integer type = MBCTypeEnum.MenuScopeBackend.getType();
        if(aclBusinessEntity.getToken().equals(BusinessEnum.ELSE.typeName)){
            type = MBCTypeEnum.MenuScopeMerchant.getType();
        }
        ApiResult apiResult = mbcMenuLoadService.loadAllMbcMenus(type).check();
        List<MbcMenuEntity> menuEntityList = (List<MbcMenuEntity>) apiResult.getData();
        List<ViewPrivilegeVO> viewPrivilegeVOList = formatList(manageablePrivilege, menuEntityList);
        Map<String, Object> viewMap = new HashMap();
        viewMap.put("treeData", viewPrivilegeVOList);
        viewMap.put("defaultCheckedData", defaultCheckedData);
        result.setData(viewMap);
        return result;
    }

    private List<ViewPrivilegeVO> formatList(List<AclPrivilegeEntity> manageablePrivilege, List<MbcMenuEntity> menuEntityList) {
        List<ViewPrivilegeVO> viewPrivilegeVOList = new ArrayList<ViewPrivilegeVO>();
        Map<Long, ViewPrivilegeVO> ViewPrivilegeVOMap = new HashMap<>();
        Map<String, String> manageablePrivilegeVOMap = new HashMap<>();
        for(AclPrivilegeEntity aclPrivilegeEntity : manageablePrivilege){
            manageablePrivilegeVOMap.put(aclPrivilegeEntity.getToken(), aclPrivilegeEntity.getToken());
        }
        //先把所有菜单放进缓存map
        for (MbcMenuEntity mbcMenuEntity : menuEntityList) {
            if(!mbcMenuEntity.getPrivilege() || manageablePrivilegeVOMap.containsKey(mbcMenuEntity.getToken())){
                ViewPrivilegeVO viewPrivilegeVO = new ViewPrivilegeVO();
                viewPrivilegeVO.setId(mbcMenuEntity.getToken());
                if(!mbcMenuEntity.getPrivilege() ){
                    viewPrivilegeVO.setDisabled(Boolean.TRUE);
                    viewPrivilegeVO.setId("default" + mbcMenuEntity.getId());
                }
                viewPrivilegeVO.setName(mbcMenuEntity.getName());
                viewPrivilegeVO.setLabel(mbcMenuEntity.getName());
                viewPrivilegeVO.setChildren(new ArrayList<ViewPrivilegeVO>());
                ViewPrivilegeVOMap.put(mbcMenuEntity.getId(), viewPrivilegeVO);
                if (mbcMenuEntity.getParentId() == -1L) {
                    //根菜单
                    viewPrivilegeVOList.add(viewPrivilegeVO);
                }
            }
        }

        for (MbcMenuEntity subMenuEntity : menuEntityList) {
            //寻找父菜单并加入进子菜单列表
            if (subMenuEntity.getParentId() != -1L &&
                ViewPrivilegeVOMap.get(subMenuEntity.getParentId()) != null && ViewPrivilegeVOMap.get(subMenuEntity.getId()) !=null) {
                ViewPrivilegeVO viewPrivilegeVO = ViewPrivilegeVOMap.get(subMenuEntity.getId());
                ViewPrivilegeVOMap.get(subMenuEntity.getParentId()).getChildren().add(viewPrivilegeVO);
            }
        }

        return viewPrivilegeVOList;
    }

    /**
     * 添加角色视图权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getRoleViewPrivilege/add", method = RequestMethod.POST)
    @ResponseBody
    public VueResult addRoleViewPrivilege(HttpServletRequest request) throws ApiResultException {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("initId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        if (!this.hasPrivilegeAuthorized(user)) {
            result.setError("您没有权限授权权限！");
            return result;
        }
        Long initId = Long.parseLong(id);
        String token = request.getParameter("settedId");
        if(StringUtils.isEmpty(token)){
            result.setError("操作失败！");
            return result;
        }
        AclPrivilegeEntity aclPrivilegeEntity = (AclPrivilegeEntity) aclPrivilegeService.findAclPrivilegeByToken(token).check().getData();
        AclRoleHasPrivilegeEntity entity = new AclRoleHasPrivilegeEntity();
        entity.setPrivilegeId(aclPrivilegeEntity.getId());
        entity.setRoleId(initId);
        aclRoleHasPrivilegeService.createAclRoleHasPrivilege(entity);
        result.setData("操作成功！");
        return result;
    }

    /**
     * 删除角色视图权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getRoleViewPrivilege/del", method = RequestMethod.POST)
    @ResponseBody
    public VueResult delRoleViewPrivilege(HttpServletRequest request) throws ApiResultException {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("initId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        if (!this.hasPrivilegeAuthorized(user)) {
            result.setError("您没有权限授权权限！");
            return result;
        }
        Long initId = Long.parseLong(id);
        String token = request.getParameter("settedId");
        if(StringUtils.isEmpty(token)){
            result.setError("操作失败！");
            return result;
        }
        AclPrivilegeEntity aclPrivilegeEntity = (AclPrivilegeEntity) aclPrivilegeService.findAclPrivilegeByToken(token).check().getData();
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("privilegeId", aclPrivilegeEntity.getId());
        delMap.put("roleId", initId);
        aclRoleHasPrivilegeService.deleteAclRoleHasPrivilege(delMap);
        result.setData("操作成功！");
        return result;
    }

    /**
     * 角色关联用户列表
     */
    @RequestMapping("/getJoinUserList")
    public VueResult getJoinUserList(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("roleId", RequestUtil.getLong(request, "roleId"));
        ApiResult apiResult = aclUserHasRoleService.searchAclUserHasRoleJoin(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);

    }

    /**
     * 删除角色和用户关联
     */
    @RequestMapping(value = "delUserHasRole", method = RequestMethod.POST)
    @ResponseBody
    private VueResult delUserHasRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("roleId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "userId");
        Long roleId = Long.parseLong(id);
        AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        aclUserHasRoleService.deleteAclUserHasRole(ObjectUtils.entityToMap(entity));
        return result;
    }

    /**
     * 角色关联部门列表
     */
    @RequestMapping("/getJoinGroupList")
    public VueResult getJoinGroupList(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("roleId", RequestUtil.getLong(request, "roleId"));
        ApiResult apiResult = aclGroupHasRoleService.searchAclGroupHasRoleJoin(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);

    }

    /**
     * 删除角色和部门关联
     */
    @RequestMapping(value = "delGroupHasRole", method = RequestMethod.POST)
    @ResponseBody
    private VueResult delGroupHasRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("roleId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.ROLE.typeName)) {
            result.setError("您没有权限操作角色！");
            return result;
        }
        Long groupId = RequestUtil.getLong(request, "groupId");
        Long roleId = Long.parseLong(id);
        AclGroupHasRoleEntity entity = new AclGroupHasRoleEntity();
        entity.setGroupId(groupId);
        entity.setRoleId(roleId);
        aclGroupHasRoleService.deleteAclGroupHasRole(ObjectUtils.entityToMap(entity));
        return result;
    }

    /**
     * 角色关联申请中的列表
     */
    @RequestMapping("/getJoinApplyList")
    public VueResult getJoinApplyList(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("wantId", RequestUtil.getLong(request, "roleId"));
        searchParamMap.put("applyType", ApplyTypeEnum.APPLY_ROLE.name);
        ApiResult apiResult = aclApplyService.searchAclApply(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);

    }
}
