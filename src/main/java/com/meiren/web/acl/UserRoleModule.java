package com.meiren.web.acl;

import com.meiren.acl.enums.UserRoleStatusEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.AclRoleEntity;
import com.meiren.acl.service.entity.AclRoleOwnerEntity;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.acl.service.entity.AclUserHasRoleEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.StringUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.SelectVO;
import com.meiren.vo.SessionUserVO;
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
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.userRole.index"})
@Controller
@RequestMapping("{uuid}/acl/userRole")
@ResponseBody
public class UserRoleModule extends BaseController {

    @Autowired
    protected AclUserHasRoleService aclUserHasRoleService;
    @Autowired
    protected AclRoleService aclRoleService;
    @Autowired
    protected AclUserService aclUserService;

    /**
     * 用户角色列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("nicknameLike", RequestUtil.getStringTrans(request, "name"));
        searchParamMap.put("businessId", RequestUtil.getLong(request, "businessId"));

        ApiResult apiResult = aclUserService.searchAclUserAndRole(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
/*        if (rMap.get("data") != null) {
            List<AclUserEntity> resultList = (List<AclUserEntity>) rMap.get("data");
            List<String> roleNames = new ArrayList<>();
            List<String> roleDes = new ArrayList<>();
            for (AclUserEntity entity : resultList) {
                List<AclUserHasRoleEntity> list = entity.getUserHasRoleEntityList();
                if (list != null) {
                    for (AclUserHasRoleEntity aclUserHasRoleEntity : list) {
                        roleNames.add(aclUserHasRoleEntity.getRoleName());
                        roleDes.add(aclUserHasRoleEntity.getRoleDescription());
                    }
                }
            }
            rMap.put("roleNames",this.listToString(roleNames));
            rMap.put("roleDes",this.listToString(roleDes));
        }*/
        return new VueResult(rMap);
    }

    /**
     * 给用户授予角色
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setRole/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult setOwner(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if(!this.hasRoleAuthorized(user)){
            result.setError("您没有权限操作授予角色！");
            return result;
        }
        Long initId = RequestUtil.getLong(request, "initId");
        if (initId == null) {
            throw new Exception("请选择要操作的用户！");
        }
        String selectedIds = RequestUtil.getString(request,"selectedIds");
        String [] selectedIds_arr = null;
        if (!StringUtils.isBlank(selectedIds)) {
            selectedIds_arr = selectedIds.split(",");
        }
        switch (type) {
            case "init":
                Map<String, Object> data = this.setRoleInit(initId,user.getBusinessId(),user.getId());
                result.setData(data);
                break;
            case "right":
                result = this.setRoleAdd(initId, selectedIds_arr);
                break;
            case "left":
                result = this.setRoleDel(initId, selectedIds_arr);
                break;
            default:
                throw new Exception("type not find");
        }
            result.setData("操作成功！");
        return result;
    }

    private VueResult setRoleDel(Long initId, String[] selectedIds_arr) {
        for(String id : selectedIds_arr) {
            Map<String, Object> delMap = new HashMap<>();
            delMap.put("userId", initId);
            delMap.put("roleId", Long.parseLong(id));
            aclUserHasRoleService.deleteAclUserHasRole(delMap);
        }
        return new VueResult("操作成功！");
    }

    private VueResult setRoleAdd(Long initId, String[] selectedIds_arr) {
        for(String id : selectedIds_arr) {
            AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
            entity.setUserId(initId);
            entity.setRoleId(Long.parseLong(id));
            entity.setStatus(UserRoleStatusEnum.NORMAL.name());
            aclUserHasRoleService.createAclUserHasRole(entity);
        }
        return new VueResult("操作成功！");
    }

    /**
     * 查询用户拥有的角色
     *
     * @param dataId
     * @return
     */
    private Map<String, Object> setRoleInit(Long initId, Long businessId, Long userId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("userId", initId);
        searchParamMap.put("hasStatus", UserRoleStatusEnum.NORMAL.name());
        List<AclRoleEntity> selected = (List<AclRoleEntity>)
            aclRoleService.loadAclRoleJoinUserHas(searchParamMap).getData(); // 根据查询用户查询拥有的角色

        List<AclRoleEntity> all = (List<AclRoleEntity>)
            aclRoleService.getManageableRole(userId, businessId).getData(); // 查询登陆用户可授权的所有角色

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclRoleEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId()); // 将信息转换为id name 类型
            vo.setName(entity.getName());
            selectedVOs.add(vo);
        }
        for (AclRoleEntity entity : all) {
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
