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
    public VueResult setOwner(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        VueResult result = new VueResult();
        try {
            SessionUserVO user = this.getUser(request);
            if(!this.hasRoleAuthorized(user)){
                result.setError("您没有权限操作授予角色！");
                return result;
            }
            Long initId = RequestUtil.getLong(request, "initId");
            String selectedIds = RequestUtil.getString(request,"selectedIds");
            String [] selectedIds_arr = null;
            if(selectedIds != null){
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
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
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
            aclRoleService.getManageableRole(userId, businessId).getData(); // 查询商家下所有角色

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

    /**
     * list转换成String
     *
     * @param stringList
     * @return
     */

    /*public String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }*/

    /**
     * 用户角色授权
     * 如果当前用户拥有角色管理权限 可授权所有角色
     * 如果用户为应用owner且为角色owner 则可授权角色+应用下角色
     * 如果用户既没有角色管理权限 也不是应用owner 则可授权用户为owner的所有角色
     * TODO zhangw
     *
     * @param request
     * @param response
     * @param type
     * @return
     * /

 /*   @AuthorityToken(needToken = {"meiren.acl.role.authorized"})
    @RequestMapping("/searchRole/{type}")
    @ResponseBody
    public ApiResult select2(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            AclUserEntity user = this.getUser(request);
            List<Long> roleIds = new ArrayList<Long>();
            Long userId = RequestUtil.getLong(request,"id");
            AclUserEntity userEntity = (AclUserEntity) aclUserService.findAclUser(userId).getData();
            List<AclRoleEntity> all = (List<AclRoleEntity>)
                    aclRoleService.getManageableRole(user.getId(), userEntity.getBusinessId()).getData();
            for (AclRoleEntity role : all) {
                roleIds.add(role.getId());
            }
            result = this.initAndQuery(request, roleIds, type);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }*/


    /**
     * init  and Query
     *
     * @param request
     * @param userId
     * @param roleIds
     * @param type
     * @return
     * @throws Exception
    */

 /*   private ApiResult initAndQuery(HttpServletRequest request, List<Long> roleIds, String type) throws Exception {
        ApiResult result = new ApiResult();
        if (Objects.equals(type, "init")) {
            Long userId = this.checkId(request);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("inRoleIds", roleIds);
            map.put("hasStatus", UserRoleStatusEnum.NORMAL.name());
            result = aclRoleService.loadAclRoleJoinUserHas(map);     //查询用户拥有的未被禁用的并且当前登录用户为owner的角色
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("inIds", roleIds);
            map.put("roleNameLike", RequestUtil.getStringTrans(request, "q"));
            result = aclRoleService.loadAclRole(map);               //查询角色
        }
        return result;
    }*/


    /**
     * 列表
     *
     * @param request
     * @param response
     * @return
    */

/*    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/userRole/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        AclUserEntity user = this.getUser(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        //搜索名称和对应值
        Map<String, String> paramNames = new HashMap<>();
        paramNames.put("nicknameLike", "nickname");
        this.mapPrams(request, paramNames, searchParamMap, modelAndView);

        Long businessId = RequestUtil.getLong(request, "businessId");
        if (businessId == null) {
            businessId = user.getBusinessId();
        }
        modelAndView.addObject("businessId", businessId);
        searchParamMap.put("businessId", businessId);
        ApiResult apiResult = aclUserService.searchAclUserAndRole(searchParamMap, pageNum, pageSize);

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
            List<UserRoleVO> reList = new ArrayList<>();
            List<AclUserEntity> resultList = (List<AclUserEntity>) resultMap.get("data");
            for (AclUserEntity entity : resultList) {
                UserRoleVO VO = new UserRoleVO();
                BeanUtils.copyProperties(entity, VO);
                List<String> roleNames = new ArrayList<>();
                List<String> roleDes = new ArrayList<>();
                List<AclUserHasRoleEntity> list = entity.getUserHasRoleEntityList();
                if (list != null) {
                    for (AclUserHasRoleEntity aclUserHasRoleEntity : list) {
                        roleNames.add(aclUserHasRoleEntity.getRoleName());
                        roleDes.add(aclUserHasRoleEntity.getRoleDescription());
                    }
                    VO.setRoleNames(this.listToString(roleNames));
                    VO.setRoleDescriptions(this.listToString(roleDes));
                }
                reList.add(VO);
            }
            modelAndView.addObject("basicVOList", reList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("inSide", this.isMeiren(user));

        return modelAndView;

    }*/


    /**
     * 添加/修改
     * 按照用户能授权的角色进行控制,只能改变可以授权的角色
     *TODO zhangw
     * @param request
     * @param response
     * @param aclUserHasRoleEntity
     * @return
    */

 /*   @RequestMapping(value = {"authorize"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult authorize(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            AclUserEntity user = this.getUser(request);
            String[] userIds = request.getParameterValues("userIds[]");
            String[] roleIds = request.getParameterValues("roleIds[]");
            if (userIds == null || userIds.length <= 0) {
                return result;
            }

            List<Long> ownerRoleIds = new ArrayList<Long>();
            AclUserEntity userEntity = (AclUserEntity) aclUserService.findAclUser(Long.parseLong(userIds[0])).getData();
            List<AclRoleEntity> all = (List<AclRoleEntity>)
                    aclRoleService.getManageableRole(user.getId(), userEntity.getBusinessId()).getData();
            for (AclRoleEntity role : all) {
                ownerRoleIds.add(role.getId());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("inUserIds", Arrays.asList(userIds));
            map.put("inRoleIds", ownerRoleIds);
            aclUserHasRoleService.deleteAclUserHasRole(map);   //删除权限
            if (roleIds == null || roleIds.length <= 0) {
                return result;
            }
            List<AclUserHasRoleEntity> list = new ArrayList<AclUserHasRoleEntity>();
            for (String userId : userIds) {
                for (String roleId : roleIds) {
                    if (!StringUtils.isBlank(userId) && !StringUtils.isBlank(roleId)) {
                        //ownerRoleIds不为空 但是 ownerRoleIds 不包含要添加权限  则跳过本次添加
                        if (ownerRoleIds != null && !ownerRoleIds.contains(Long.valueOf(roleId))) {
                            continue;
                        }
                        AclUserHasRoleEntity entity = new AclUserHasRoleEntity();
                        entity.setUserId(Long.valueOf(userId));
                        entity.setRoleId(Long.valueOf(roleId));
                        entity.setStatus(UserRoleStatusEnum.NORMAL.name());
                        list.add(entity);
                    }
                }
            }
            result = aclUserHasRoleService.createBatch(list);    //批量添加权限
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }*/





    /**
     * 跳转添加/修改页面
     *
     * @param request
     * @param response
     * @return
     */

    /*@AuthorityToken(needToken = {"meiren.acl.role.authorized"})
    @RequestMapping(value = "goTo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response,@PathVariable String type) {
    	ModelAndView modelAndView = new ModelAndView();
    	switch (type) {
    	case "add":
    		modelAndView.addObject("title","用户角色授权");
    		modelAndView.addObject("id", "");
    		modelAndView.setViewName("acl/userRole/edit");
            break;
        case "modify":
        	modelAndView.addObject("title", "用户角色编辑");
        	modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
        	modelAndView.setViewName("acl/userRole/edit");
            break;
    	}
    	return modelAndView;
    }*/
}
