package com.meiren.web.acl;

import com.alibaba.fastjson.JSONArray;
import com.meiren.acl.enums.*;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.common.utils.StringUtils;
import com.meiren.tech.mbc.action.ActionControllerLog;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.privilege.index"})
@Controller
@RequestMapping("{uuid}/acl/privilege")
@ResponseBody
public class PrivilegeModule extends BaseController {

    @Autowired
    private AclPrivilegeService aclPrivilegeService;
    @Autowired
    private AclUserService aclUserService;
    @Autowired
    private AclPrivilegeProcessService aclPrivilegeProcessService;
    @Autowired
    private AclProcessModelService aclProcessModelService;
    @Autowired
    private AclProcessService aclProcessService;
    @Autowired
    private AclPrivilegeOwnerService aclPrivilegeOwnerService;
    @Autowired
    private AclUserHasPrivilegeService aclUserHasPrivilegeService;
    @Autowired
    private AclRoleHasPrivilegeService aclRoleHasPrivilegeService;
    @Autowired
    protected AclApplyService aclApplyService;
    private String[] necessaryParam = {"name", "token",};

    private PrivilegeVO entityToVo(AclPrivilegeEntity entity) {
        PrivilegeVO vo = new PrivilegeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclPrivilegeEntity voToEntity(PrivilegeVO vo) {
        AclPrivilegeEntity entity = new AclPrivilegeEntity();
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
        searchParamMap.put("nameOrToken", RequestUtil.getStringTrans(request, "name"));
        ApiResult apiResult = aclPrivilegeService.searchAclPrivilege(searchParamMap, pageNum, rowsNum);
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
        AclPrivilegeEntity entity = (AclPrivilegeEntity) aclPrivilegeService.findAclPrivilege(id).getData();
        PrivilegeVO vo = this.entityToVo(entity);
        return new VueResult(vo);
    }

    /**
     * 删除单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public VueResult delete(HttpServletRequest request) throws Exception {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("id");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.PRIVILEGE.typeName)) {
            result.setError("您没有权限操作权限！");
            return result;
        }
        delMap.put("id", id);
        aclPrivilegeService.deleteAclPrivilege(delMap).check();
        result.setData("操作成功！");
        return result;
    }


    /**
     * 添加编辑
     */
    @ActionControllerLog(descriptions = "添加编辑权限")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, PrivilegeVO vo) throws Exception {
        VueResult result = new VueResult();
        this.checkParamMiss(request, this.necessaryParam);
        String id = request.getParameter("id");
        SessionUserVO user = this.getUser(request);
        if (this.checkCanDo(user, id, CheckCanDoEnum.PRIVILEGE.typeName)) {
            AclPrivilegeEntity entity = this.voToEntity(vo);
            Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
            if (riskLevel == null) {
                result.setError("请选择正确的风险等级！");
                return result;
            } else {
                entity.setRiskLevel(RiskLevelEnum.getByTypeValue(riskLevel).typeValue);
            }
            Long privilegeId;
            Integer oldRiskLevel = RiskLevelEnum.NONE.typeValue;
            if (!StringUtils.isBlank(id)) {
                oldRiskLevel = aclPrivilegeService.findAclPrivilegeById(Long.valueOf(id)).getRiskLevel();
                Map<String, Object> paramMap = ObjectUtils.entityToMap(entity);
                aclPrivilegeService.updateAclPrivilege(Long.valueOf(id), paramMap);
                privilegeId = Long.valueOf(id);
            } else {
                entity.setCreateUserId(user.getId());
                entity.setStatus(PrivilegeStatusEnum.NORMAL.name());
                ApiResult apiResult = aclPrivilegeService.createAclPrivilege(entity);
                privilegeId = (Long) apiResult.getData();
            }
            List<String> userIds = RequestUtil.getArray(request, "ownerId");

            //添加owner
            this.addOwner(privilegeId,userIds);
            // 添加风险审核流程
            this.addPrivilegeProcess(privilegeId, RiskLevelEnum.getByTypeValue(riskLevel).typeValue, oldRiskLevel);
            result.setData(true);
        } else {
            result.setError("您无权添加编辑该权限");
            return result;
        }
        return result;
    }

    private void addOwner(Long privilegeId, List<String> userIds) {
        //设置owner，另外添加
        HashMap<String,Object> delMap = new HashMap<>();
        delMap.put("privilegeId", privilegeId);
        aclPrivilegeOwnerService.deleteAclPrivilegeOwner(delMap);
        AclPrivilegeOwnerEntity aclPrivilegeOwnerEntity = new AclPrivilegeOwnerEntity();
        if (!userIds.isEmpty()) {
            for (String userId : userIds) {
                aclPrivilegeOwnerEntity.setUserId(Long.parseLong(userId));
                aclPrivilegeOwnerEntity.setPrivilegeId(privilegeId);
                aclPrivilegeOwnerService.createAclPrivilegeOwner(aclPrivilegeOwnerEntity);
            }
        }
    }

    /**
     * 根据风险等级添加默认审核流程
     *
     * @param privilegeId
     * @param riskLevel
     * @param oldRiskLevel
     * @return
     * @throws Exception
     */
    private void addPrivilegeProcess(Long privilegeId, Integer riskLevel, Integer oldRiskLevel) throws Exception {
        if (oldRiskLevel == null || riskLevel.intValue() != oldRiskLevel.intValue()) {
            Map<String, Object> delParamMap = new HashMap<>();
            delParamMap.put("privilegeId", privilegeId);
            aclPrivilegeProcessService.deleteAclPrivilegeProcess(delParamMap);

            Map<String, Object> searchParamMap = new HashMap<>();
            searchParamMap.put("riskLevel", riskLevel);
            List<AclProcessModelEntity> all = (List<AclProcessModelEntity>) aclProcessModelService
                .loadAclProcessModel(searchParamMap).getData();
            for (AclProcessModelEntity entity : all) {
                AclPrivilegeProcessEntity AclPrivilegeProcessEntity = new AclPrivilegeProcessEntity();
                AclPrivilegeProcessEntity.setPrivilegeId(privilegeId);
                AclPrivilegeProcessEntity.setProcessId(entity.getProcessId());
                AclPrivilegeProcessEntity.setHierarchyId(entity.getHierarchyId());
                AclPrivilegeProcessEntity.setApprovalCondition(entity.getApprovalCondition());
                AclPrivilegeProcessEntity.setApprovalLevel(entity.getApprovalLevel());
                aclPrivilegeProcessService.createAclPrivilegeProcess(AclPrivilegeProcessEntity);
            }
        }
    }

    /**
     * 设置权限审核流程
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/process/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult process(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("id");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.PRIVILEGE.typeName)) {
            result.setError("您没有权限操作权限！");
            return result;
        }
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("privilegeId", id);
        if (Objects.equals(type, "init")) {
            Map<String, Object> map = new HashMap<>();
            List<AclPrivilegeProcessEntity> have = (List<AclPrivilegeProcessEntity>) aclPrivilegeProcessService.loadAclPrivilegeProcess(searchParamMap).getData();
            List<AclProcessEntity> all = (List<AclProcessEntity>) aclProcessService.loadAclProcess(null).getData();
            List<ProcessVO> allVOs = new ArrayList<>();

            for (AclProcessEntity entity : all) {
                ProcessVO vo = new ProcessVO();
                BeanUtils.copyProperties(entity, vo);
                vo.setChecked(false);
                vo.setApprovalCondition(ApprovalConditionEnum.AND.name);
                for (AclPrivilegeProcessEntity privilegeProcessEntity : have) {
                    if (privilegeProcessEntity.getProcessId() == vo.getId()) {
                        vo.setChecked(true);
                        vo.setHierarchyId(privilegeProcessEntity.getHierarchyId());
                        vo.setApprovalCondition(privilegeProcessEntity.getApprovalCondition());
                    }
                }
                allVOs.add(vo);
            }
            map.put("all", allVOs); // 查询全部审核流程
            result.setData(map);
        } else {
            //
            String process = RequestUtil.getString(request, "process");
            List<AclPrivilegeProcessEntity> list = new ArrayList<>();
            list = JSONArray.parseArray(process, AclPrivilegeProcessEntity.class);
            aclPrivilegeProcessService.deleteAclPrivilegeProcess(searchParamMap); // 删除原来的再重新添加
            for (AclPrivilegeProcessEntity entity : list) {
                if (entity.getChecked()) {
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                        ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclPrivilegeProcessService.createAclPrivilegeProcess(entity);
                }
            }
            result.setData("操作成功！");
        }
        return result;
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
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("initId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.PRIVILEGE.typeName)) {
            result.setError("您没有权限操作权限！");
            return result;
        }
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (!StringUtils.isBlank(selectedIds)) {
            selectedIds_arr = selectedIds.split(",");
        }
        Long initId = Long.parseLong(id);
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
            delMap.put("userId", Long.parseLong(id));
            delMap.put("privilegeId", initId);
            aclPrivilegeOwnerService.deleteAclPrivilegeOwner(delMap);
        }
        return new VueResult("操作成功！");
    }

    private VueResult setOwnerAdd(Long initId, String[] selectedIds_arr) {
        for (String id : selectedIds_arr) {
            AclPrivilegeOwnerEntity entity = new AclPrivilegeOwnerEntity();
            entity.setUserId(Long.parseLong(id));
            entity.setPrivilegeId(initId);
            aclPrivilegeOwnerService.createAclPrivilegeOwner(entity);
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
        searchParamMap.put("privilegeId", dataId);
        List<AclUserEntity> selected = (List<AclUserEntity>)
            aclUserService.loadAclUserJoinPrivilegeOwner(searchParamMap).getData(); // 根据查询权限Id查询用户

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("businessId", businessId);
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

    @RequestMapping(value = "getOwner", method = RequestMethod.GET)
    @ResponseBody
    public VueResult getOwner(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        Long id = this.checkId(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("privilegeId", id);
        List<AclUserEntity> selected = (List<AclUserEntity>)
            aclUserService.loadAclUserJoinPrivilegeOwner(searchParamMap).getData();
        List<SelectVO> selectedVOs = new ArrayList<>();
        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId()); // 将信息转换为id
            selectedVOs.add(vo);
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        result.setData(dataMap);
        return result;
    }

    /**
     * 权限关联用户列表
     */
    @RequestMapping("/getJoinUserList")
    public VueResult getJoinUserList(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("privilegeId", RequestUtil.getLong(request, "privilegeId"));
        ApiResult apiResult = aclUserHasPrivilegeService.searchAclUserHasPrivilegeJoin(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);

    }

    /**
     * 删除权限和用户关联
     */
    @RequestMapping(value = "delUserHasPrivilege", method = RequestMethod.POST)
    @ResponseBody
    private VueResult delUserHasPrivilege(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("privilegeId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.PRIVILEGE.typeName)) {
            result.setError("您没有权限操作权限！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "userId");
        Long privilegeId = Long.parseLong(id);
        AclUserHasPrivilegeEntity entity = new AclUserHasPrivilegeEntity();
        entity.setUserId(userId);
        entity.setPrivilegeId(privilegeId);
        aclUserHasPrivilegeService.deleteAclUserHasPrivilege(ObjectUtils.entityToMap(entity));
        return result;
    }

    /**
     * 权限关联角色列表
     */
    @RequestMapping("/getJoinRoleList")
    public VueResult getJoinRoleList(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("privilegeId", RequestUtil.getLong(request, "privilegeId"));
        ApiResult apiResult = aclRoleHasPrivilegeService.searchAclRoleHasPrivilegeJoin(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);

    }

    /**
     * 删除权限和角色关联
     */
    @RequestMapping(value = "delRoleHasPrivilege", method = RequestMethod.POST)
    @ResponseBody
    private VueResult delRoleHasPrivilege(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String id = request.getParameter("privilegeId");
        if (!this.checkCanDo(user, id, CheckCanDoEnum.PRIVILEGE.typeName)) {
            result.setError("您没有权限操作权限！");
            return result;
        }
        Long roleId = RequestUtil.getLong(request, "roleId");
        Long privilegeId = Long.parseLong(id);
        AclRoleHasPrivilegeEntity entity = new AclRoleHasPrivilegeEntity();
        entity.setRoleId(roleId);
        entity.setPrivilegeId(privilegeId);
        aclRoleHasPrivilegeService.deleteAclRoleHasPrivilege(ObjectUtils.entityToMap(entity));
        return result;
    }

    /**
     * 权限关联申请中的列表
     */
    @RequestMapping("/getJoinApplyList")
    public VueResult getJoinApplyList(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        searchParamMap.put("wantId", RequestUtil.getLong(request, "privilegeId"));
        searchParamMap.put("applyType", ApplyTypeEnum.APPLY_PRIVILEGE.name);
        ApiResult apiResult = aclApplyService.searchAclApply(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);

    }
}
