package com.meiren.web;

import com.meiren.acl.enums.PrivilegeStatusEnum;
import com.meiren.acl.enums.RiskLevelEnum;
import com.meiren.acl.service.AclPrivilegeProcessService;
import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.acl.service.AclProcessModelService;
import com.meiren.acl.service.AclProcessService;
import com.meiren.acl.service.entity.AclPrivilegeEntity;
import com.meiren.acl.service.entity.AclPrivilegeProcessEntity;
import com.meiren.acl.service.entity.AclProcessEntity;
import com.meiren.acl.service.entity.AclProcessModelEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.utils.StringUtils;
import com.meiren.vo.PrivilegeProcessVO;
import com.meiren.vo.PrivilegeVO;
import com.meiren.vo.ProcessVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

//@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.privilege.index"})
//@Controller
//@RequestMapping("{uuid}/acl/privilege")
//@ResponseBody
public class PrivilegeModule extends BaseController {

    @Autowired
    private AclPrivilegeService aclPrivilegeService;
    @Autowired
    private AclPrivilegeProcessService aclPrivilegeProcessService;
    @Autowired
    private AclProcessModelService aclProcessModelService;
    @Autowired
    private AclProcessService aclProcessService;

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
    public VueResult find(HttpServletRequest request) {
        Long id = RequestUtil.getLong(request, "id");
        AclPrivilegeEntity entity = (AclPrivilegeEntity) aclPrivilegeService.findAclPrivilege(id).getData();
        PrivilegeVO vo = this.entityToVo(entity);
        return new VueResult(vo);
    }

    /**
     * 添加编辑
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, PrivilegeVO vo) throws Exception {
        VueResult result = new VueResult();
        try {
            String id = request.getParameter("id");
            SessionUserVO user = this.getUser(request);
            HashMap<String, Object> searchParamMap = new HashMap<String, Object>();
            searchParamMap.put("userId", user.getId());
            if (!StringUtils.isBlank(id)) {
                searchParamMap.put("privilegeId", Long.valueOf(id));
            }
            /*boolean canDo = false;
            if (!StringUtils.isBlank(id) && (aclPrivilegeOwnerService.countAclPrivilegeOwner(searchParamMap) > 0)) {// 编辑情况
                canDo = true;
            } else if (this.checkToken(user, privilegeAll)) {// 有权限管理权限
                canDo = true;
            }*/
            if (true) {
                AclPrivilegeEntity entity = this.voToEntity(vo);
                Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
                if(riskLevel == null){
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
                    aclPrivilegeService.createAclPrivilege(entity);
                    privilegeId = (Long) result.getData();
                }
                // 添加风险审核流程
                this.addPrivilegeProcess(privilegeId, riskLevel, oldRiskLevel);
                result.setData(true);
            } else {
                result.setError("您无权添加编辑该权限");
                return result;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
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
    public ApiResult process(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> searchParamMap = new HashMap<>();
            Long id = RequestUtil.getLong(request, "id");
            searchParamMap.put("privilegeId", id);
            if (Objects.equals(type, "init")) {
                Map<String, Object> map = new HashMap<>();
                List<AclPrivilegeProcessEntity> have = (List<AclPrivilegeProcessEntity>) aclPrivilegeProcessService.loadAclPrivilegeProcess(searchParamMap).getData();
                List<AclProcessEntity> all = (List<AclProcessEntity>) aclProcessService.loadAclProcess(null).getData();
                List<PrivilegeProcessVO> haveVOs = new ArrayList<>();
                List<ProcessVO> allVOs = new ArrayList<>();
                for(AclPrivilegeProcessEntity entity: have){
                    PrivilegeProcessVO vo = new PrivilegeProcessVO();
                    BeanUtils.copyProperties(entity, vo);
                    haveVOs.add(vo);
                }
                for(AclProcessEntity entity: all){
                    ProcessVO vo = new ProcessVO();
                    BeanUtils.copyProperties(entity, vo);
                    allVOs.add(vo);
                }
                map.put("have", haveVOs); // 当前审核流程状态
                map.put("all", allVOs); // 查询全部审核流程
                result.setData(map);
            } else {
                /*aclPrivilegeProcessService.deleteAclPrivilegeProcess(searchParamMap); // 删除原来的再重新添加
                for (AclPrivilegeProcessEntity entity : list) {
                    entity.setPrivilegeId(id);
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                        ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclPrivilegeProcessService.createAclPrivilegeProcess(entity);
                }
                result.setData(1);*/
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }
}
