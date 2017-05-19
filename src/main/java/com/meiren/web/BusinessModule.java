package com.meiren.web;

import com.meiren.acl.service.AclBusinessService;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.BusinessVO;
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

//@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.business.index"})
@Controller
@RequestMapping("{uuid}/acl/business")
@ResponseBody
public class BusinessModule extends BaseController {

    @Autowired
    protected AclBusinessService aclBusinessService;

    /**
     * 列表
     * @param request
     * @return
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("nameLike", RequestUtil.getStringTrans(request, "name"));
        ApiResult apiResult = aclBusinessService.searchAclBusiness(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 查找用户
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public VueResult find(HttpServletRequest request) {
        Long id = RequestUtil.getLong(request, "id");
        //搜索名称和对应值
        ApiResult apiResult = aclBusinessService.findAclBusiness(id);
        AclBusinessEntity businessEntity = (AclBusinessEntity) apiResult.getData();
        BusinessVO vo = this.entityToVo(businessEntity);
        return new VueResult(vo);
    }

    /**
     * 添加编辑用户
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, BusinessVO vo) throws Exception {
        VueResult result = new VueResult();
        try {
            Long id = RequestUtil.getLong(request, "id");
            if (id != null) {
                aclBusinessService.updateAclBusiness(id, ObjectUtils.entityToMap(vo));
            } else {
                aclBusinessService.createAclBusiness(this.voToEntity(vo));
            }
            result.setData("操作成功！");
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private BusinessVO entityToVo(AclBusinessEntity entity) {
        BusinessVO vo = new BusinessVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclBusinessEntity voToEntity(BusinessVO vo) {
        AclBusinessEntity entity = new AclBusinessEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 删除单个
     * @param request
     * @return
     */
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public VueResult delete(HttpServletRequest request) {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        /*if(!this.hasSuperAdmin(user)){
            result.setError("您没有权限操作层级！");
            return result;
        }*/
        Map<String, Object> delMap = new HashMap<>();
        try {
            Long id = RequestUtil.getLong(request,"id");
            if(id == null){
                result.setError("请选择要删除的商家！");
                return result;
            }
            aclBusinessService.deleteById(id);
            result.setData("操作成功！");
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }
}
