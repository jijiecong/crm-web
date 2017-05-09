package com.meiren.web.acl;

import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.vo.SelectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.group.index"})
@Controller
@RequestMapping("/acl/group")
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
    public String roleAll = "meiren.acl.role.all";

    @RequestMapping(value = "user/set", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult userset(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long userId = RequestUtil.getLong(request, "userId");
            Map<String, Object> delMap = new HashMap<>();
            delMap.put("userId", userId);
            aclGroupHasUserService.deleteAclGroupHasUser(delMap);

            Long groupId = RequestUtil.getLong(request, "groupId");
            AclGroupHasUserEntity entity = new AclGroupHasUserEntity();
            entity.setGroupId(groupId);
            entity.setUserId(userId);
            result = aclGroupHasUserService.createAclGroupHasUser(entity);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }


    /**
     * 列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/group/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;
        AclUserEntity userEntity = this.getUser(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        //搜索名称和对应值
        Map<String, String> mapPrams = new HashMap<>();
        mapPrams.put("nameLike", "deptName");
        this.mapPrams(request, mapPrams, searchParamMap, modelAndView);
        boolean isInside = this.isMeiren(userEntity);
        if (isInside) {
            Long businessId = RequestUtil.getLong(request, "businessId");
            if (businessId == null) {
                businessId = userEntity.getBusinessId();
            }
            searchParamMap.put("businessId", businessId);
            modelAndView.addObject("businessId", businessId);
        } else {
            searchParamMap.put("businessId", userEntity.getBusinessId());
        }
        ApiResult apiResult = aclGroupService.searchAclGroup(searchParamMap, pageNum, pageSize);
        String message = this.checkApiResult(apiResult);
        if (message != null) {
            modelAndView.addObject("message", message);
            return modelAndView;
        }

        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();

        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
        }

        if (resultMap.get("data") != null) {
            List<AclGroupEntity> resultList = (List<AclGroupEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }

        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("inSide",isInside);

        return modelAndView;

    }

    /**
     * 删除单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult delete(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        Map<String, Object> delMap = new HashMap<>();
        try {
            Long id = this.checkId(request);
            delMap.put("id", id);
            result = aclGroupService.deleteAclGroup(delMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    @RequestMapping(value = "loadByUserId", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult loadByUserId(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", request.getParameter("userId"));
            result = aclGroupService.loadAclGroupJoinHasUser(map);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 查找单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "find", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult find(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            result = aclGroupService.findAclGroup(id);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 添加/修改
     *
     * @param request
     * @param response
     * @param aclGroupEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response, AclGroupEntity aclGroupEntity) {
        ApiResult result = new ApiResult();
        try {
            String id = request.getParameter("id");
            this.checkParamMiss(request, this.necessaryParam);
            if (!StringUtils.isBlank(id)) {
                Map<String, Object> paramMap = this.converRequestMap(request.getParameterMap());
                result = aclGroupService.updateAclGroup(Long.valueOf(id), paramMap);
            } else {
                aclGroupEntity.setStatus("NORMAL");
                if (aclGroupEntity.getPid() == null) {
                    aclGroupEntity.setPid(0L);
                }
                result = aclGroupService.createAclGroup(aclGroupEntity);
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }


    /**
     * 跳转添加/修改页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "goTo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response,@PathVariable String type) {
        ModelAndView modelAndView = new ModelAndView();
        AclUserEntity userEntity = this.getUser(request);
        AclBusinessEntity aclBusinessEntity = (AclBusinessEntity) aclBusinessService.findAclBusiness(userEntity.getBusinessId()).getData();
        modelAndView.addObject(aclBusinessEntity);
        switch (type) {
            case "add":
                modelAndView.addObject("title","添加部门");
                modelAndView.addObject("id", "");
                modelAndView.setViewName("acl/group/edit");
                break;
            case "modify":
                modelAndView.addObject("title", "编辑部门");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.setViewName("acl/group/edit");
                break;
        }
        return modelAndView;
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
            Long initId = RequestUtil.getLong(request, "dataId");
            Long userId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");
            switch (type) {
                case "init":
                    Map<String, Object> data = this.setUserInit(initId,request);
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

    private Map<String, Object> setUserInit(Long dataId, HttpServletRequest request) {
        Map<String, Object> searchParamMap = new HashMap<>();
        List<AclUserEntity> all = new ArrayList<>();
        searchParamMap.put("groupId", dataId);
        AclUserEntity userEntity = this.getUser(request);

        List<AclUserEntity> selected = (List<AclUserEntity>)
                aclUserService.loadAclUserJoinGroupHas(searchParamMap).getData();
        searchParamMap.put("businessId", userEntity.getBusinessId());
        all = (List<AclUserEntity>) aclUserService.loadAclUser(searchParamMap).getData();

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
        AclUserEntity user = this.getUser(request);
        List<AclRoleEntity> selected = (List<AclRoleEntity>)
                aclRoleService.loadAclRoleJoinGroupHas(searchParamMap).getData();

        AclGroupEntity group = (AclGroupEntity) aclGroupService.findAclGroup(dataId).getData();
        List<AclRoleEntity> all = (List<AclRoleEntity>)
                aclRoleService.getManageableRole(user.getId(), group.getBusinessId()).getData(); //查询用户在该商家下的全部权限

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
