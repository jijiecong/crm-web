package com.meiren.web.acl;

import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.GroupVO;
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
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.group.index"})
@Controller
@RequestMapping("{uuid}/acl/group")
@ResponseBody
public class GroupModule extends BaseController {

    @Autowired
    protected AclGroupService aclGroupService;
    @Autowired
    protected AclGroupHasUserService aclGroupHasUserService;
    @Autowired
    protected AclGroupLeaderService aclGroupLeaderService;
    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclRoleService aclRoleService;
    @Autowired
    protected AclGroupHasRoleService aclGroupHasRoleService;
    @Autowired
    protected AclBusinessService aclBusinessService;
    private String[] necessaryParam = {
        "name",
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
        //搜索
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("nicknameLike", RequestUtil.getStringTrans(request, "name"));
        searchParamMap.put("businessId", RequestUtil.getLong(request, "businessId"));
        ApiResult apiResult = aclGroupService.searchAclGroup(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 删除单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public ApiResult delete(HttpServletRequest request) {
        ApiResult result = new ApiResult();
        Map<String, Object> delMap = new HashMap<>();
        SessionUserVO user = this.getUser(request);
        if (!this.hasGroupAll(user)) {
            result.setError("您没有权限操作部门！");
            return result;
        }
        Long id = RequestUtil.getLong(request, "id");
        delMap.put("id", id);
        result = aclGroupService.deleteAclGroup(delMap);
        return result;
    }

    /**
     * 查找单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "find", method = RequestMethod.GET)
    public VueResult find(HttpServletRequest request) {
        Long id = RequestUtil.getLong(request, "id");
        //搜索名称和对应值
        ApiResult apiResult = aclGroupService.findAclGroup(id);
        AclGroupEntity aclGroupEntity = (AclGroupEntity) apiResult.getData();
        GroupVO vo = this.entityToVo(aclGroupEntity);
        return new VueResult(vo);
    }

    private GroupVO entityToVo(AclGroupEntity entity) {
        GroupVO vo = new GroupVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 添加/修改
     *
     * @param request
     * @param response
     * @param aclGroupEntity
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public VueResult add(HttpServletRequest request, GroupVO vo) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.hasUserAll(user)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long id = RequestUtil.getLong(request, "id");
        ApiResult apiResult;
        if (id != null) {
            apiResult = aclGroupService.updateAclGroup(id, ObjectUtils.entityToMap(vo));
        } else {
            apiResult = aclGroupService.createAclGroup(this.voToEntity(vo));
        }
        return new VueResult(apiResult.getData());
    }
    private AclGroupEntity voToEntity(GroupVO vo) {
        AclGroupEntity entity = new AclGroupEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 设置user
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setUser/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setUser(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            SessionUserVO user = this.getUser(request);
            if(!this.hasGroupAll(user)){
                result.setError("您没有权限操作部门！");
                return result;
            }
            Long initId = RequestUtil.getLong(request, "dataId");
            Long userId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setUserInit(initId);
                    result.setData(data);
                    break;
                case "add":
                    result = this.setUserAdd(userId, uid);
                    break;
                case "del":
                    result = this.setUserDel(userId, uid);
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private ApiResult setUserDel(Long userId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("userId", userId);
        delMap.put("groupId", uid);
        return aclGroupHasUserService.deleteAclGroupHasUser(delMap);
    }

    private ApiResult setUserAdd(Long userId, Long uid) {
        AclGroupHasUserEntity entity = new AclGroupHasUserEntity();
        entity.setUserId(userId);
        entity.setGroupId(uid);
        return aclGroupHasUserService.createAclGroupHasUser(entity);
    }

    private Map<String, Object> setUserInit(Long dataId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("groupId", dataId);
        List<AclUserEntity> selected = (List<AclUserEntity>)
                aclUserService.loadAclUserJoinGroupHas(searchParamMap).getData();
        AclGroupEntity group = (AclGroupEntity) aclGroupService.findAclGroup(dataId).getData();
        List<AclUserEntity> all = (List<AclUserEntity>) aclUserService.loadAclUserNotUsedWithGroupHas(group.getBusinessId()).getData();

        all.addAll(selected);
        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectedVOs.add(vo);
        }
        for (AclUserEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectDataVOs.add(vo);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        dataMap.put("selectData", selectDataVOs);
        return dataMap;
    }

    /**
     * 设置leader
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setLeader/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setLeader(HttpServletRequest request,
                               HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            SessionUserVO user = this.getUser(request);
            if(!this.hasGroupAll(user)){
                result.setError("您没有权限操作部门！");
                return result;
            }
            Long initId = RequestUtil.getLong(request, "dataId");
            Long userId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setLeaderInit(initId);
                    result.setData(data);
                    break;
                case "add":
                    result = this.setLeaderAdd(userId, uid);
                    break;
                case "del":
                    result = this.setLeaderDel(userId, uid);
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private ApiResult setLeaderDel(Long userId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("leaderId", userId);
        delMap.put("leaderDeptId", uid);
        return aclGroupLeaderService.deleteAclGroupLeader(delMap);
    }

    private ApiResult setLeaderAdd(Long userId, Long uid) {
        AclGroupLeaderEntity entity = new AclGroupLeaderEntity();
        entity.setLeaderId(userId);
        entity.setLeaderDeptId(uid);
        return aclGroupLeaderService.createAclGroupLeader(entity);
    }

    private Map<String, Object> setLeaderInit(Long dataId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("groupId", dataId);
        List<AclUserEntity> selected = (List<AclUserEntity>)
                aclUserService.loadAclUserJoinGroupLeader(searchParamMap).getData();
        List<AclUserEntity> all = (List<AclUserEntity>)
                aclUserService.loadAclUserJoinGroupHas(searchParamMap).getData();

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectedVOs.add(vo);
        }
        for (AclUserEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectDataVOs.add(vo);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        dataMap.put("selectData", selectDataVOs);
        return dataMap;
    }

    /**
     * 设置role
     *
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setRole/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setRole(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            SessionUserVO user = this.getUser(request);
            if(!this.hasGroupAll(user)){
                result.setError("您没有权限操作部门！");
                return result;
            }
            if(!this.hasRoleAuthorized(user)){
                result.setError("您没有权限授予角色！");
                return result;
            }
            Long initId = RequestUtil.getLong(request, "dataId");
            Long roleId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setRoleInit(initId,request);
                    result.setData(data);
                    break;
                case "add":
                    result = this.setRoleAdd(roleId, uid);
                    break;
                case "del":
                    result = this.setRoleDel(roleId, uid);
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private ApiResult setRoleDel(Long roleId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("roleId", roleId);
        delMap.put("groupId", uid);
        return aclGroupHasRoleService.deleteAclGroupHasRole(delMap);
    }

    private ApiResult setRoleAdd(Long roleId, Long uid) {
        AclGroupHasRoleEntity entity = new AclGroupHasRoleEntity();
        entity.setRoleId(roleId);
        entity.setGroupId(uid);
        return aclGroupHasRoleService.createAclGroupHasRole(entity);
    }

    private Map<String, Object> setRoleInit(Long dataId ,HttpServletRequest request) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("groupId", dataId);
        SessionUserVO user = this.getUser(request);
        List<AclRoleEntity> selected = (List<AclRoleEntity>)
                aclRoleService.loadAclRoleJoinGroupHas(searchParamMap).getData();

        AclGroupEntity group = (AclGroupEntity) aclGroupService.findAclGroup(dataId).getData();
        List<AclRoleEntity> all = (List<AclRoleEntity>)
                aclRoleService.getManageableRole(user.getId(), group.getBusinessId()).getData(); //查询用户在该商家下可管理的所有角色

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclRoleEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
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
