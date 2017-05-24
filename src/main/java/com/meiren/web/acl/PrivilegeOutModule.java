package com.meiren.web.acl;

import com.meiren.acl.enums.PrivilegeStatusEnum;
import com.meiren.acl.enums.RiskLevelEnum;
import com.meiren.acl.service.AclPrivilegeOwnerService;
import com.meiren.acl.service.AclPrivilegeProcessService;
import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.acl.service.AclProcessModelService;
import com.meiren.acl.service.entity.AclPrivilegeEntity;
import com.meiren.acl.service.entity.AclPrivilegeOwnerEntity;
import com.meiren.acl.service.entity.AclPrivilegeProcessEntity;
import com.meiren.acl.service.entity.AclProcessModelEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.utils.RequestUtil;
import com.meiren.utils.StringUtils;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("{uuid}/acl/searchPrivilege")
@ResponseBody
public class PrivilegeOutModule extends BaseController {

    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclPrivilegeOwnerService aclPrivilegeOwnerService;
    @Autowired
    private AclPrivilegeProcessService aclPrivilegeProcessService;
    @Autowired
    private AclProcessModelService aclProcessModelService;
    private String[] necessaryParam = {
        "name",
        "token",
    };

    /**
     * 列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {

        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("nameOrToken", com.meiren.utils.RequestUtil.getStringTrans(request, "name"));
        ApiResult apiResult = aclPrivilegeService.searchAclPrivilege(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 添加
     *
     * @param request
     * @param response
     * @param aclPrivilegeEntity
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public VueResult add(HttpServletRequest request, AclPrivilegeEntity aclPrivilegeEntity) throws Exception {
        ApiResult result = new ApiResult();
        SessionUserVO user = this.getUser(request);
        if (user != null) {
            aclPrivilegeEntity.setCreateUserId(user.getId());
        }
        aclPrivilegeEntity.setStatus(PrivilegeStatusEnum.NORMAL.name());
        Long privilegeId = (Long) aclPrivilegeService.createAclPrivilege(aclPrivilegeEntity).getData();
        Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
        Integer oldRiskLevel = RiskLevelEnum.NONE.typeValue;
        this.addPrivilegeProcess(privilegeId, riskLevel, oldRiskLevel);
        //设置owner，另外添加
        AclPrivilegeOwnerEntity aclPrivilegeOwnerEntity = new AclPrivilegeOwnerEntity();
        String userIds = RequestUtil.getString(request, "ownerId");
        if (!StringUtils.isBlank(userIds)) {
            String[] useridArr = userIds.split(",");
            for (int i = 0; i < useridArr.length; i++) {
                aclPrivilegeOwnerEntity.setUserId(Long.parseLong(useridArr[i]));
                aclPrivilegeOwnerEntity.setPrivilegeId(privilegeId);
                result = aclPrivilegeOwnerService.createAclPrivilegeOwner(aclPrivilegeOwnerEntity);
            }
            }
            return new VueResult(result.getData());
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

}
