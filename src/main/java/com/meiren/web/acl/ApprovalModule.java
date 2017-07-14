package com.meiren.web.acl;

import com.meiren.acl.enums.ApprovalResultEnum;
import com.meiren.acl.enums.SignStatusEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.AclSignedEntity;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.acl.service.entity.ApprovalJoinApplyEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.StringUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.SessionUserVO;
import com.meiren.vo.SignedVO;
import com.meiren.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("{uuid}/acl/approval")
@ResponseBody
public class ApprovalModule extends BaseController {

    @Autowired
    protected AclApprovalService aclApprovalService;
    @Autowired
    protected AclApplyService aclApplyService;
    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclSignedService aclSignedService;
    @Autowired
    protected AclUserService aclUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        SessionUserVO user = this.getUser(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        if (user.getId() != null) {
            searchParamMap.put("approverId", user.getId());
        } else {
            return new VueResult("没有登录不能使用此功能");

        }

        ApiResult apiResult = aclApprovalService.searchAclApprovalJoinApply(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 查找单个
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value = "find", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult find(HttpServletRequest request, HttpServletResponse response) {
//        ApiResult result = new ApiResult();
//        try {
//            Long id = this.checkId(request);
//            result = aclApprovalService.findAclApproval(id);
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }

    /**
     * 转,加签
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "signed/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult signed(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        Long id = this.checkId(request);
        Long toUserId = RequestUtil.getLong(request, "toUserId");
        if(toUserId == null){
            throw new Exception("请选择要转签或加签的用户！");
        }
        Long userId = this.getUser(request).getId();
        if (Objects.equals(type, "add")) {
            aclApprovalService.updateAclApprovalDoAdd(id, toUserId,userId);  //加签
        } else {
            aclApprovalService.updateAclApprovalDoChange(id, toUserId,userId);  //转签
        }
        result.setData("操作成功！");
        return result;
    }

    private SignedVO entityToVo(AclSignedEntity entity) {
        SignedVO vo = new SignedVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclSignedEntity voToEntity(SignedVO vo) {
        AclSignedEntity entity = new AclSignedEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
    /**
     * 查找代签
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "agent/find", method = RequestMethod.POST)
    @ResponseBody
    public VueResult agentFind(HttpServletRequest request, HttpServletResponse response) {
        SessionUserVO user = this.getUser(request);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", user.getId());
        List<AclSignedEntity> list = (List <AclSignedEntity>)
                aclSignedService.loadAclSigned(map).getData();
        if(list.size() > 0){
            AclSignedEntity signedEntity = list.get(0);
            SignedVO vo = this.entityToVo(signedEntity);
            AclUserEntity userEntity = (AclUserEntity) aclUserService.findAclUser(vo.getToUserId()).getData();
            vo.setToUserName(userEntity.getUserName());
            return new VueResult(vo);
        }
        return new VueResult("没有代签记录！");
    }

    /**
     * 代签
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"agent/edit"}, method = RequestMethod.POST)
    @ResponseBody
    public VueResult signedSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        String toUserId = request.getParameter("toUserId");
        Integer isUsed = RequestUtil.getInteger(request, "isUsed");
        if (StringUtils.isBlank(toUserId)){
            throw new Exception("请选择要代签的用户！");
        }
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("userId", user.getId());
        aclSignedService.deleteAclSigned(delMap);

        AclSignedEntity entity = new AclSignedEntity();
        entity.setIsUsed(String.valueOf(SignStatusEnum.getByTypeValue(isUsed).typeValue));
        entity.setToUserId(Long.valueOf(toUserId));
        entity.setUserId(user.getId());
        aclSignedService.createAclSigned(entity);  //设置代签
        result.setData("操作成功！");
        return result;
    }

    /**
     * 设置审核结果
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "result/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult result(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        Long id = this.checkId(request);
        ApprovalResultEnum resultEnum = Objects.equals(type, "pass") ? ApprovalResultEnum.PASS : ApprovalResultEnum.NOT_PASS;
        //需要事物处理,放入一个service

        aclApplyService.updateAclApprovalResult(id, resultEnum,this.getUser(request).getId());
        result.setData("操作成功！");
        return result;
    }
}
