package com.meiren.web.acl;

import com.meiren.acl.enums.ApplyTypeEnum;
import com.meiren.acl.service.AclApplyService;
import com.meiren.acl.service.entity.AclApplyEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.tech.mbc.action.ActionControllerLog;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.ApplyVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("{uuid}/acl/apply")
@ResponseBody
public class ApplyModule extends BaseController {

    @Autowired
    protected AclApplyService aclApplyService;

    /**
     * 查询当前登录用户申请列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/list")
    public VueResult index(HttpServletRequest request) {

        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索
        Map<String, Object> searchParamMap = new HashMap<>();
        SessionUserVO user = this.getUser(request);
        if (user.getId() != null) {
            searchParamMap.put("userId", user.getId());
        } else {
            return new VueResult("error");
        }
        searchParamMap.put("nameLike", RequestUtil.getStringTrans(request, "name"));
        ApiResult apiResult = aclApplyService.searchAclApply(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }
    /**
     * 查找单个申请
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "find", method = RequestMethod.GET)
    public VueResult find(HttpServletRequest request) {
        Long id = RequestUtil.getLong(request, "id");
        //搜索名称和对应值
        ApiResult apiResult = aclApplyService.findAclApply(id);
        AclApplyEntity aclApplyEntity = (AclApplyEntity) apiResult.getData();
        ApplyVO vo = this.entityToVo(aclApplyEntity);
        return new VueResult(vo);
    }
    private ApplyVO entityToVo(AclApplyEntity entity) {
        ApplyVO vo = new ApplyVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 创建角色申请、权限申请
     * @param request
     * @param response
     * @param aclApplyEntity
     * @return
     */
    private AclApplyEntity voToEntity(ApplyVO vo) {
        AclApplyEntity entity = new AclApplyEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    @ActionControllerLog(descriptions = "申请权限")
    @RequestMapping(value = "savePrivilege", method = RequestMethod.POST)
    public ApiResult createApplyPrivilege(HttpServletRequest request,ApplyVO vo) {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        vo.setUserId(user.getId());
        vo.setApplyType(ApplyTypeEnum.APPLY_PRIVILEGE.name());//权限申请
        aclApplyService.createAclApply(this.voToEntity(vo));

        return result;
    }

    @ActionControllerLog(descriptions = "申请角色")
    @RequestMapping(value = "saveRole", method = RequestMethod.POST)
    public ApiResult createApplyRole (HttpServletRequest request, ApplyVO vo) {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        vo.setUserId(user.getId());
        vo.setApplyType(ApplyTypeEnum.APPLY_ROLE.name());//角色申请
        aclApplyService.createAclApply(this.voToEntity(vo));
        return result;
    }

}
