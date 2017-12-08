package com.meiren.web.acl;

import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.acl.service.entity.AclBusinessHasPrivilegeEntity;
import com.meiren.acl.service.entity.AclPrivilegeEntity;
import com.meiren.acl.service.entity.AclRoleHasPrivilegeEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.exception.ApiResultException;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.tech.mbc.action.ActionControllerLog;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.BusinessVO;
import com.meiren.vo.SelectVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.all.superAdmin", "meiren.acl.mbc.crm.acl.business.index"})
@Controller
@RequestMapping("{uuid}/acl/business")
@ResponseBody
public class BusinessModule extends BaseController {

    @Autowired
    protected AclBusinessService aclBusinessService;
    @Autowired
    protected AclBusinessHasPrivilegeService aclBusinessHasPrivilegeService;
    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclRoleHasPrivilegeService aclRoleHasPrivilegeService;
    @Autowired
    protected AclRoleService aclRoleService;

    /**
     * 列表
     *
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
     * 添加编辑商家
     *
     * @param request
     * @return
     * @throws Exception
     */
    @ActionControllerLog(descriptions = "添加编辑商家")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, BusinessVO vo) throws Exception {
        VueResult result = new VueResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id != null) {
            aclBusinessService.updateAclBusiness(id, ObjectUtils.entityToMap(vo)).check();
        } else {
            aclBusinessService.createAclBusiness(this.voToEntity(vo)).check();
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
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public VueResult delete(HttpServletRequest request) throws ApiResultException {
        VueResult result = new VueResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id == null) {
            result.setError("请选择要删除的商家！");
            return result;
        }
        aclBusinessService.deleteById(id).check();
        return result;
    }

    /**
     * 批量导入权限
     * TODO　zhangw
     *
     * @param request
     * @param response
     * @param aclUserEntity
     * @return
     */
    @RequestMapping(value = "setBusinessHasRole", method = RequestMethod.POST)
    public ApiResult setBusinessHasRole(HttpServletRequest request) {
        ApiResult result = new ApiResult();
        List<Long> listOld = new ArrayList<>();
        List<Long> listNew = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();
        String roleIds = RequestUtil.getString(request, "roleId");

        String[] roleIdArr = roleIds.split(","); //获取角色id

        List<String> idsList = Arrays.asList(roleIdArr);//转换成list

        Long businessId = RequestUtil.getLong(request, "businessId"); //获取商家id
        map.put("businessId", businessId);
        //查询该商家拥有的权限
        List<AclBusinessHasPrivilegeEntity> aclBusinessHasPrivilegeEntityList = (List<AclBusinessHasPrivilegeEntity>) aclBusinessHasPrivilegeService.loadAclBusinessHasPrivilege(map).getData();
        Map<String, Object> roleMap = new HashMap<String, Object>();
        roleMap.put("inRoleIds", idsList);
        //查询角色拥有的权限
        List<AclRoleHasPrivilegeEntity> aclRoleHasPrivilegeEntity = (List<AclRoleHasPrivilegeEntity>) aclRoleHasPrivilegeService.loadAclRoleHasPrivilege(roleMap).getData();
        for (AclRoleHasPrivilegeEntity a : aclRoleHasPrivilegeEntity) {
            if (!listNew.contains(a.getPrivilegeId())) {
                listNew.add(a.getPrivilegeId());
            }
        }
        for (AclBusinessHasPrivilegeEntity b : aclBusinessHasPrivilegeEntityList) {
            listOld.add(b.getPrivilegeId());
        }
        listNew.removeAll(listOld);
        List<AclBusinessHasPrivilegeEntity> list = new ArrayList<>();
        for (int i = 0; i < listNew.size(); ++i) {
            AclBusinessHasPrivilegeEntity aclBusinessHasPrivilegeEntity = new AclBusinessHasPrivilegeEntity();
            aclBusinessHasPrivilegeEntity.setBusinessId(businessId);
            aclBusinessHasPrivilegeEntity.setPrivilegeId(listNew.get(i));
            list.add(aclBusinessHasPrivilegeEntity);
        }
        if (listNew.size() > 0) {
            result = aclBusinessHasPrivilegeService.createBatch(list);
        } else {
            result.setData("您选择导入的权限已经存在，无需重复添加！");
        }
        return result;
    }


    /**
     * 设置商家权限，可以设置的权限为可管理的权限。
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setBusinessHasPrivilege/{type}", method = RequestMethod.POST)
    public VueResult setPrivilege(HttpServletRequest request, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.hasPrivilegeAuthorized(user)) {
            result.setError("您没有权限授权权限！");
            return result;
        }
        Long initId = RequestUtil.getLong(request, "initId");
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (selectedIds != null) {
            selectedIds_arr = selectedIds.split(",");
        }
        switch (type) {
            case "init":
                Map<String, Object> data = this.setPrivilegeInit(initId);       //查询权限
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
            delMap.put("businessId", initId);
            aclBusinessHasPrivilegeService.deleteAclBusinessHasPrivilege(delMap);
        }
        return new VueResult("操作成功！");
    }

    /**
     * 为商家添加权限
     *
     * @param privilegeId
     * @param uid
     * @return
     */
    private VueResult setPrivilegeAdd(Long initId, String[] selectedIds_arr) {
        for (String id : selectedIds_arr) {
            AclBusinessHasPrivilegeEntity entity = new AclBusinessHasPrivilegeEntity();
            entity.setPrivilegeId(Long.parseLong(id));
            entity.setBusinessId(initId);
            aclBusinessHasPrivilegeService.createAclBusinessHasPrivilege(entity);
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
    private Map<String, Object> setPrivilegeInit(Long initId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("businessId", initId);
        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilegeJoinBusinessHas(searchParamMap).getData(); //查询已拥有的权限
        List<AclPrivilegeEntity> all = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilege(null).getData(); //查询全部权限

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

}
