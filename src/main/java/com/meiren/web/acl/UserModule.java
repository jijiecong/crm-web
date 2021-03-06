package com.meiren.web.acl;

import com.meiren.acl.enums.*;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.exception.ApiResultException;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.result.VueResultCode;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.common.utils.StringUtils;
import com.meiren.tech.mbc.action.ActionControllerLog;
import com.meiren.tech.mbc.service.MbcMenuLoadService;
import com.meiren.tech.mbc.service.MbcMenuService;
import com.meiren.tech.mbc.service.entity.MbcMenuEntity;
import com.meiren.utils.MBCStatusEnum;
import com.meiren.utils.MBCTypeEnum;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.SelectVO;
import com.meiren.vo.SessionUserVO;
import com.meiren.vo.UserVO;
import com.meiren.vo.ViewPrivilegeVO;
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

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.index"})
@Controller
@RequestMapping("{uuid}/acl/user")
@ResponseBody
public class UserModule extends BaseController {

    @Autowired
    private AclUserService aclUserService;
    @Autowired
    private AclGroupLeaderService aclGroupLeaderService;
    @Autowired
    private AclUserHasPrivilegeService aclUserHasPrivilegeService;
    @Autowired
    private AclUserHasRoleService aclUserHasRoleService;
    @Autowired
    private AclGroupHasUserService aclGroupHasUserService;
    @Autowired
    private AclRoleService aclRoleService;
    @Autowired
    private AclRoleOwnerService aclRoleOwnerService;
    @Autowired
    private AclPrivilegeOwnerService aclPrivilegeOwnerService;
    @Autowired
    private AclPrivilegeService aclPrivilegeService;
    @Autowired
    private MbcMenuLoadService mbcMenuLoadService;
    @Autowired
    private AclBusinessService aclBusinessService;

    /**
     * 用户列表
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

        ApiResult apiResult = aclUserService.searchAclUser(searchParamMap, pageNum, rowsNum);
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
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public VueResult delete(HttpServletRequest request) throws ApiResultException {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long id = RequestUtil.getLong(request, "id");
        delMap.put("id", id);
        aclUserService.deleteAclUser(delMap).check();
        return result;
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
        ApiResult apiResult = aclUserService.findAclUser(id);
        AclUserEntity userEntity = (AclUserEntity) apiResult.getData();
        UserVO vo = this.entityToVo(userEntity);
        vo.setPassword(null);
        return new VueResult(vo);
    }

    /**
     * 添加编辑用户
     *
     * @param request
     * @return
     * @throws Exception
     */
    @ActionControllerLog(descriptions = "添加编辑用户")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, UserVO vo) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long id = RequestUtil.getLong(request, "id");
        ApiResult apiResult;
        if (id != null) {
            apiResult = aclUserService.updateAclUserByPassword(id, ObjectUtils.entityToMap(vo));
        } else {
            if (vo.getPassword() == null) {
                return result.setResultCode(VueResultCode.NO_PASSWORD);
            }
            apiResult = aclUserService.createAclUser(this.voToEntity(vo));
        }
        return new VueResult(apiResult.getData());
    }

    private UserVO entityToVo(AclUserEntity entity) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclUserEntity voToEntity(UserVO vo) {
        AclUserEntity entity = new AclUserEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 离职
     * TODO jijc
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/resign", method = RequestMethod.POST)
    @ResponseBody
    public VueResult resign(HttpServletRequest request, HttpServletResponse response) {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "userId");
        if (userId == null) {
            result.setError("请选择要离职的用户！");
            return result;
        }
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("userId", userId);
        aclUserHasPrivilegeService.deleteAclUserHasPrivilege(delMap);
        aclUserHasRoleService.deleteAclUserHasRole(delMap);
        aclGroupHasUserService.deleteAclGroupHasUser(delMap);
        aclPrivilegeOwnerService.deleteAclPrivilegeOwner(delMap);

        Map<String, Object> delMap_owner = new HashMap<String, Object>();
        delMap_owner.put("ownerId", userId);
        aclRoleOwnerService.deleteAclRoleOwner(delMap_owner);

        Map<String, Object> delMap_leader = new HashMap<String, Object>();
        delMap_leader.put("leaderId", userId);
        aclGroupLeaderService.deleteAclGroupLeader(delMap_leader);

        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("status", UserStatusEnum.DISABLE.name());
        aclUserService.updateAclUser(userId, updateMap);
        result.setData("操作成功！");
        return result;
    }

    /**
     * 转岗
     * TODO jijc
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/changeGroup", method = RequestMethod.POST)
    @ResponseBody
    public VueResult changeGroup(HttpServletRequest request,
                                 HttpServletResponse response) {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "userId");
        if (userId == null) {
            result.setError("请选择要转岗的用户！");
            return result;
        }
        Long fromGroupId = RequestUtil.getLong(request, "fromGroupId");
        Long toGroupId = RequestUtil.getLong(request, "toGroupId");
        result = this.changeGroup(userId, fromGroupId, toGroupId);     //转岗
        return result;
    }

    private VueResult changeGroup(Long userId, Long fromGroupId, Long toGroupId) {
        VueResult result = new VueResult();
        Map<String, Object> searchParamMap = new HashMap<String, Object>();
        //1、查询fromGroupId拥有的权限id和角色id
        //2、根据权限id和角色id，删除用户拥有的原部门下关联的权限和角色
        searchParamMap.put("groupId", fromGroupId);
        List<AclPrivilegeEntity> allPrivilege = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilegeJoinGroupHas(searchParamMap).getData();
        for (AclPrivilegeEntity entity : allPrivilege) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("userId", userId);
            delMap.put("privilegeId", entity.getId());
            aclUserHasPrivilegeService.deleteAclUserHasPrivilege(delMap);
        }

        List<AclRoleEntity> allRole = (List<AclRoleEntity>) aclRoleService.loadAclRoleJoinGroupHas(searchParamMap).getData();
        for (AclRoleEntity entity : allRole) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("userId", userId);
            delMap.put("roleId", entity.getId());
            aclUserHasRoleService.deleteAclUserHasRole(delMap);
        }

        //3、将用户从原来的部门中删除，并添加到新的部门
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("userId", userId);
        delMap.put("groupId", fromGroupId);
        aclGroupHasUserService.deleteAclGroupHasUser(delMap);
        Map<String, Object> delMap_leader = new HashMap<String, Object>();
        delMap_leader.put("leaderId", userId);
        delMap_leader.put("leaderDeptId", fromGroupId);
        aclGroupLeaderService.deleteAclGroupLeader(delMap_leader);
        AclGroupHasUserEntity entity = new AclGroupHasUserEntity();
        entity.setUserId(userId);
        entity.setGroupId(toGroupId);
        aclGroupHasUserService.createAclGroupHasUser(entity);
        result.setData("操作成功！");
        return result;
    }

    /**
     * 禁用单个用户
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    @ResponseBody
    public VueResult disable(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        Map<String, Object> updateMap = new HashMap<>();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "userId");
        String status = RequestUtil.getString(request, "status");
        UserStatusEnum userStatusEnum = status.toLowerCase().equals("disable")
            ? UserStatusEnum.NORMAL : UserStatusEnum.DISABLE;
        updateMap.put("status", userStatusEnum.name());
        aclUserService.updateAclUser(userId, updateMap);

        AclUserEntity userEntity = (AclUserEntity) aclUserService.findAclUser(userId).getData();
        this.forcedLogout(userEntity);
        return result;
    }

    /**
     * 用户层级修改
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/setHierarchy"}, method = RequestMethod.POST)
    @ResponseBody
    public VueResult hierarchySet(HttpServletRequest request, HttpServletResponse response) {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "userId");
        Long hierarchyId = RequestUtil.getLong(request, "hierarchyId");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hierarchyId", hierarchyId);
        aclUserService.updateAclUser(userId, paramMap);
        result.setData("操作成功！");
        return result;
    }

    /**
     * 判断进行角色的哪种操作
     *
     * @param request
     * @param response
     * @param type     init:初始化；right:禁用；left:取消禁用
     * @return
     */
    @RequestMapping(value = "/role/control/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult roleControl(HttpServletRequest request,
                                 HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "initId");
        if (userId == null) {
            result.setError("用户为空");
            return result;
        }
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (selectedIds != null) {
            selectedIds_arr = selectedIds.split(",");
        }
        switch (type) {
            case "init":
                Map<String, Object> data = this.roleControlInit(userId);  //查询所有（当前可用的角色、被禁用角色）
                result.setData(data);
                break;
            case "right":
                this.roleControlAdd(selectedIds_arr, userId);     //禁用
                break;
            case "left":
                this.roleControlDel(selectedIds_arr, userId);     //取消禁用
                break;
            default:
                throw new Exception("type not find");
        }
        return result;
    }

    /**
     * 取消禁用角色
     *
     * @param selectedIds_arr
     * @param uid
     * @return ApiResult
     * @throws Exception
     */
    private VueResult roleControlDel(String[] selectedIds_arr, Long uid) throws Exception {
        VueResult result = new VueResult();
        for (String id : selectedIds_arr) {
            AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
            entity.setStatus(UserRoleStatusEnum.DELETE.name());
            entity.setUserId(uid);
            entity.setRoleId(Long.parseLong(id));
            aclUserHasRoleService.deleteAclUserHasRole(ObjectUtils.entityToMap(entity));
        }
        return result;
    }

    /**
     * 禁用角色
     *
     * @param selectedIds_arr
     * @param uid
     * @return ApiResult
     */
    private VueResult roleControlAdd(String[] selectedIds_arr, Long uid) {
        VueResult result = new VueResult();
        for (String id : selectedIds_arr) {
            AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
            entity.setStatus(UserRoleStatusEnum.DELETE.name());
            entity.setUserId(uid);
            entity.setRoleId(Long.parseLong(id));
            aclUserHasRoleService.createAclUserHasRole(entity);
        }
        return result;
    }

    /**
     * 初始化用户拥有角色和禁用角色
     *
     * @param initId
     * @return ApiResult
     */
    private Map<String, Object> roleControlInit(Long initId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("hasStatus", UserRoleStatusEnum.DELETE.name());
        searchParamMap.put("userId", initId);
        List<AclRoleEntity> all = (List<AclRoleEntity>)
            aclRoleService.getAllRoleByUser(initId).getData();
        List<AclRoleEntity> selected = (List<AclRoleEntity>)
            aclRoleService.loadAclRoleJoinUserHas(searchParamMap).getData();

        all.addAll(selected);
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

    /**
     * 判断进行权限的哪种操作
     *
     * @param request
     * @param response
     * @param type     init:初始化；right:禁用；left:取消禁用
     * @return
     */
    @RequestMapping(value = "/privilege/control/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult privilegeControl(HttpServletRequest request,
                                      HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.checkCanDo(user, null, CheckCanDoEnum.USER.typeName)) {
            result.setError("您没有权限操作用户！");
            return result;
        }
        Long userId = RequestUtil.getLong(request, "initId");
        if (userId == null) {
            result.setError("用户为空");
            return result;
        }
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (selectedIds != null) {
            selectedIds_arr = selectedIds.split(",");
        }
        switch (type) {
            case "init":
                Map<String, Object> data = this.privilegeControlInit(userId);  //查询所有（当前可用的权限、被禁用拥有权限）
                result.setData(data);
                break;
            case "right":
                this.privilegeControlAdd(selectedIds_arr, userId);     //禁用
                break;
            case "left":
                this.privilegeControlDel(selectedIds_arr, userId);     //取消禁用
                break;
            default:
                throw new Exception("type not find");
        }
        return result;
    }

    /**
     * 删除权限控制
     *
     * @param selectedIds_arr
     * @param uid
     * @return
     * @throws Exception
     */
    private VueResult privilegeControlDel(String[] selectedIds_arr, Long uid) throws Exception {
        VueResult result = new VueResult();
        for (String id : selectedIds_arr) {
            AclUserHasPrivilegeEntity entity = new AclUserHasPrivilegeEntity();
            entity.setStatus(UserPrivilegeStatusEnum.DELETE.name());
            entity.setUserId(uid);
            entity.setPrivilegeId(Long.parseLong(id));
            aclUserHasPrivilegeService.deleteAclUserHasPrivilege(ObjectUtils.entityToMap(entity));
        }
        return result;
    }

    /**
     * 添加权限控制
     *
     * @param selectedIds_arr
     * @param uid
     * @return
     */
    private VueResult privilegeControlAdd(String[] selectedIds_arr, Long uid) {
        VueResult result = new VueResult();
        for (String id : selectedIds_arr) {
            AclUserHasPrivilegeEntity entity = new AclUserHasPrivilegeEntity();
            entity.setStatus(UserPrivilegeStatusEnum.DELETE.name());
            entity.setUserId(uid);
            entity.setPrivilegeId(Long.parseLong(id));
            aclUserHasPrivilegeService.createAclUserHasPrivilege(entity);
        }
        return result;
    }

    /**
     * 初始化用户拥有的权限和被禁用的权限
     *
     * @param userId
     * @return
     */
    private Map<String, Object> privilegeControlInit(Long userId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("hasStatus", UserPrivilegeStatusEnum.DELETE.name());
        searchParamMap.put("userId", userId);
        List<AclPrivilegeEntity> all = (List<AclPrivilegeEntity>)
            aclPrivilegeService.getAllPrivilegeByUser(userId).getData();
        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>)
            aclPrivilegeService.loadAclPrivilegeJoinUserHas(searchParamMap).getData();

        all.addAll(selected);
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

    /**
     * 获取用户视图权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getViewPrivilege", method = RequestMethod.GET)
    public VueResult getViewPrivilege(HttpServletRequest request) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.hasUserAll(user)) {
            result.setError("您没有权限查看用户视图权限！");
            return result;
        }
        Long businessId = RequestUtil.getLong(request, "businessId");
        if (businessId == null) {
            throw new Exception("操作失败！");
        }
        AclBusinessEntity aclBusinessEntity =
            (AclBusinessEntity) aclBusinessService.findAclBusiness(businessId).check().getData();
        Integer type = MBCTypeEnum.MenuScopeBackend.getType();
        if(aclBusinessEntity.getToken().equals(BusinessEnum.ELSE.typeName)){
            type = MBCTypeEnum.MenuScopeMerchant.getType();
        }
        Long userId = RequestUtil.getLong(request, "initId");
        ApiResult apiResult = mbcMenuLoadService.loadMbcMenus(userId, type).check();
        List<MbcMenuEntity> menuEntityList = (List<MbcMenuEntity>) apiResult.getData();
        List<ViewPrivilegeVO> viewPrivilegeVOList = formatList(menuEntityList);
        Map<String, Object> viewMap = new HashMap();
        viewMap.put("treeData", viewPrivilegeVOList);
        result.setData(viewMap);
        return result;
    }

    private List<ViewPrivilegeVO> formatList(List<MbcMenuEntity> menuEntityList) {
        List<ViewPrivilegeVO> viewPrivilegeVOList = new ArrayList<ViewPrivilegeVO>();
        for(MbcMenuEntity mbcMenuEntity : menuEntityList){
            if(!mbcMenuEntity.getHasPrivilege()){
                continue;
            }
            ViewPrivilegeVO viewPrivilegeVO = new ViewPrivilegeVO();
            if(mbcMenuEntity.getSubMenus().size() > 0) {
                viewPrivilegeVO.setId(mbcMenuEntity.getToken());
                viewPrivilegeVO.setName(mbcMenuEntity.getName());
                viewPrivilegeVO.setLabel(mbcMenuEntity.getName());
                viewPrivilegeVO.setChildren(formatList(mbcMenuEntity.getSubMenus()));
            }else {
                viewPrivilegeVO.setId(mbcMenuEntity.getToken());
                viewPrivilegeVO.setName(mbcMenuEntity.getName());
                viewPrivilegeVO.setLabel(mbcMenuEntity.getName());
            }
            viewPrivilegeVOList.add(viewPrivilegeVO);
        }
        return viewPrivilegeVOList;
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
    public VueResult setRole(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();
        SessionUserVO user = this.getUser(request);
        if (!this.hasRoleAuthorized(user)) {
            result.setError("您没有权限操作授予角色！");
            return result;
        }
        Long initId = RequestUtil.getLong(request, "initId");
        if (initId == null) {
            throw new Exception("请选择要操作的用户！");
        }
        String selectedIds = RequestUtil.getString(request, "selectedIds");
        String[] selectedIds_arr = null;
        if (!StringUtils.isBlank(selectedIds)) {
            selectedIds_arr = selectedIds.split(",");
        }
        switch (type) {
        case "init":
            Map<String, Object> data = this.setRoleInit(initId, user.getId());
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
        return result;
    }

    private VueResult setRoleDel(Long initId, String[] selectedIds_arr) {
        for (String id : selectedIds_arr) {
            Map<String, Object> delMap = new HashMap<>();
            delMap.put("userId", initId);
            delMap.put("roleId", Long.parseLong(id));
            aclUserHasRoleService.deleteAclUserHasRole(delMap);
        }
        return new VueResult("操作成功！");
    }

    private VueResult setRoleAdd(Long initId, String[] selectedIds_arr) {
        for (String id : selectedIds_arr) {
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
    private Map<String, Object> setRoleInit(Long initId, Long userId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("userId", initId);
        searchParamMap.put("hasStatus", UserRoleStatusEnum.NORMAL.name());
        List<AclRoleEntity> selected = (List<AclRoleEntity>)
            aclRoleService.loadAclRoleJoinUserHas(searchParamMap).getData(); // 根据查询用户查询拥有的角色

        Long businessId = aclUserService.findAclUserById(initId).getBusinessId();// 查询被授权用户所属的商家
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

