package com.meiren.web;

import com.meiren.acl.service.AclHierarchyService;
import com.meiren.acl.service.entity.AclHierarchyEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.HierarchyVO;
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

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.hierarchy.index"})
@Controller
@RequestMapping("{uuid}/acl/hierarchy")
@ResponseBody
public class HierarchyModule extends BaseController {

    @Autowired
    protected AclHierarchyService aclHierarchyService;

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
        searchParamMap.put("hierarchyNameLike", RequestUtil.getStringTrans(request, "name"));
        ApiResult apiResult = aclHierarchyService.searchAclHierarchy(searchParamMap, pageNum, rowsNum);
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
        VueResult result = new VueResult();
        try {
            Long id = RequestUtil.getLong(request, "id");
            //搜索名称和对应值
            ApiResult apiResult = aclHierarchyService.findAclHierarchy(id);
            AclHierarchyEntity hierarchyEntity = (AclHierarchyEntity) apiResult.getData();
            HierarchyVO vo = this.entityToVo(hierarchyEntity);
            result.setData(vo);
        }catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 添加编辑用户
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, HierarchyVO vo) throws Exception {
        VueResult result = new VueResult();
        try {
            Long id = RequestUtil.getLong(request, "id");
            if (id != null) {
                aclHierarchyService.updateAclHierarchy(id, ObjectUtils.entityToMap(vo));
            } else {
                aclHierarchyService.createAclHierarchy(this.voToEntity(vo));
            }
            result.setData("操作成功！");
        }catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private HierarchyVO entityToVo(AclHierarchyEntity entity) {
        HierarchyVO vo = new HierarchyVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclHierarchyEntity voToEntity(HierarchyVO vo) {
        AclHierarchyEntity entity = new AclHierarchyEntity();
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
    public ApiResult delete(HttpServletRequest request) {
        ApiResult result = new ApiResult();
        SessionUserVO user = this.getUser(request);
        /*if(!this.hasSuperAdmin(user)){
            result.setError("您没有权限操作层级！");
            return result;
        }*/
        Map<String, Object> delMap = new HashMap<>();
        try {
            Long id = RequestUtil.getLong(request,"id");
            delMap.put("id", id);
            aclHierarchyService.deleteAclHierarchy(delMap);
            result.setData("操作成功！");
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }
}
