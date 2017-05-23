package com.meiren.web.acl;

import com.meiren.acl.enums.PrivilegeStatusEnum;
import com.meiren.acl.service.AclPrivilegeOwnerService;
import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.acl.service.entity.AclPrivilegeEntity;
import com.meiren.acl.service.entity.AclPrivilegeOwnerEntity;
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
import java.util.Map;

@Controller
@RequestMapping("{uuid}/acl/searchPrivilege")
@ResponseBody
public class PrivilegeOutModule extends BaseController {

    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclPrivilegeOwnerService aclPrivilegeOwnerService;
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
    public VueResult add(HttpServletRequest request, AclPrivilegeEntity aclPrivilegeEntity) {
        ApiResult result = new ApiResult();
        SessionUserVO user = this.getUser(request);
        if (user != null) {
            aclPrivilegeEntity.setCreateUserId(user.getId());
        }
        aclPrivilegeEntity.setStatus(PrivilegeStatusEnum.NORMAL.name());
        Long privilegeId = (Long) aclPrivilegeService.createAclPrivilege(aclPrivilegeEntity).getData();

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


}
