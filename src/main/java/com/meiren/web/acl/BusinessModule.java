package com.meiren.web.acl;

import com.meiren.acl.enums.UserRoleStatusEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.monitor.utils.ObjectUtils;
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
import java.util.*;


/**
 * BusinessModule
 *
 * @author wangzai
 * @date 2017-05-02 23:10
 * 商家相关
 */

//@AuthorityToken(needToken = {"meiren.acl.mbc.backend.biz.index"})
@Controller
@RequestMapping("acl/business")
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

    public String roleAll = "meiren.acl.role.all";  //角色管理权限
    public String roleBiz = "meiren.acl.role.biz";  //应用管理权限

    private String[] necessaryParam = {"name",};
    /**
     * 列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

        ApiResult apiResult = new ApiResult();
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/business/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        Map<String, String> userPrams = new HashMap<>();
        userPrams.put("nameLike", "businessName");
        this.mapPrams(request,userPrams,searchParamMap,modelAndView);
        AclUserEntity user = this.getUser(request);
        AclBusinessEntity aclBusinessEntity = (AclBusinessEntity) aclBusinessService.findAclBusiness(user.getBusinessId()).getData();
        modelAndView.addObject(aclBusinessEntity);
        if(!this.isMeiren(user)) {
            searchParamMap.put("businessId", user.getBusinessId());
            apiResult = aclBusinessService.searchAclBusiness(searchParamMap, pageNum, pageSize);

        } else {
//            searchParamMap.put("businessId",id);
            apiResult = aclBusinessService.searchAclBusiness(searchParamMap, pageNum, pageSize);
            modelAndView.addObject("businessName", "INSIDE");
        }
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
            List<AclBusinessEntity> resultList = (List<AclBusinessEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);


        return modelAndView;

    }


    /**
     * 删除单个
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
            result = aclBusinessService.deleteAclBusiness(delMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 批量删除
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteBatch", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteBatch(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        Map<String, Object> delMap = new HashMap<>();
        String[] ids = request.getParameterValues("ids[]");
        List<String> idsList = Arrays.asList(ids);
        try {
            delMap.put("inIds", idsList);
            result = aclBusinessService.deleteAclBusiness(delMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 查找单个
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "find", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult findById(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            ApiResult apiResult = aclBusinessService.findAclBusiness(id);
            AclBusinessEntity entity = (AclBusinessEntity) apiResult.getData();
            result.setData(entity);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 查找单个
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/findByName/{type}")
    @ResponseBody
    public ApiResult findByName(HttpServletRequest request, HttpServletResponse response, @PathVariable String  type) {
        ApiResult result = new ApiResult();
        try {
            switch (type){
                case "query":
                    result = aclBusinessService.loadAclBusinessLikeName(RequestUtil.getStringTrans(request, "q"));
                    break;
                case "init":
                    Long id = this.checkId(request);
                    result = aclBusinessService.findAclBusiness(id);
                    break;
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 添加/修改
     * @param request
     * @param response
     * @param aclUserEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrUpdate(HttpServletRequest request, HttpServletResponse response, AclBusinessEntity aclBusinessEntity) {
        ApiResult result = new ApiResult();
        try {
            String id = request.getParameter("id");
            this.checkParamMiss(request, this.necessaryParam);  //验证参数是否为空
            if (!StringUtils.isBlank(id)) {
                Map<String, Object> paramMap = ObjectUtils.reflexToMap(aclBusinessEntity);
                result = aclBusinessService.updateAclBusiness(Long.valueOf(id), paramMap);
            } else {
                result = aclBusinessService.createAclBusiness(aclBusinessEntity);
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
        switch (type) {
            case "add":
                modelAndView.addObject("title","添加商家");
                modelAndView.addObject("id", "");
                modelAndView.setViewName("acl/business/edit");
                break;
            case "modify":
                modelAndView.addObject("title", "编辑应用");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.setViewName("acl/business/edit");
                break;
        }
        return modelAndView;
    }


//    @AuthorityToken(needToken = {"meiren.acl.role.authorized"})
    @RequestMapping("/searchRole/{type}")
    @ResponseBody
    public ApiResult select2(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            AclUserEntity user = this.getUser(request);
            List<Long> roleIds = new ArrayList<Long>();
            if (this.checkToken(user, roleAll)) {
                roleIds = null;              //如果当前用户拥有角色管理权限  则返回所有角色
            } else if (this.checkToken(user, roleBiz)) {
                //如果用户为应用owner且为正常角色owner 则返回正常角色+应用下角色
                roleIds = this.getAllRoleIdsByRoleOwner(user);  //查询正常角色+应用下角色id
            } else {
                //如果用户既不是角色管理权限 也不是应用owner 则返回用户为owner的所有角色id
                roleIds = this.getRoleIdsByRoleOwner(user);     //查询当前用户为owner的所有角色id
            }
            result = this.initAndQuery(request, roleIds, type);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

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
    private ApiResult initAndQuery(HttpServletRequest request, List<Long> roleIds, String type) throws Exception {
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
    }



    /**
     * 添加/修改
     * @param request
     * @param response
     * @param aclUserEntity
     * @return
     */
    @RequestMapping(value = {"addPrivilege"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addPrivilege(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            List<Long> list = new ArrayList<>();
            Map<String, Object> map = new HashMap<String, Object>();
            String[] roleIds = request.getParameterValues("roleIds[]");
            List<String> idsList = Arrays.asList(roleIds);
            Long businessId = RequestUtil.getLong(request, "businessId");
            map.put("businessId", businessId);
            List<AclBusinessHasPrivilegeEntity> aclBusinessHasPrivilegeEntityList = (List<AclBusinessHasPrivilegeEntity>) aclBusinessHasPrivilegeService.loadAclBusinessHasPrivilege(map).getData();
            Map<String, Object> roleMap = new HashMap<String, Object>();
            roleMap.put("inRoleIds", idsList);
            List<AclRoleHasPrivilegeEntity> aclRoleHasPrivilegeEntity = (List<AclRoleHasPrivilegeEntity>) aclRoleHasPrivilegeService.loadAclRoleHasPrivilege(roleMap).getData();
            for (AclRoleHasPrivilegeEntity a : aclRoleHasPrivilegeEntity) {
                list.add(a.getPrivilegeId());
            }
            for (AclBusinessHasPrivilegeEntity b : aclBusinessHasPrivilegeEntityList) {
                list.add(b.getPrivilegeId());
            }
            list = new ArrayList<Long>(new HashSet<Long>(list));
            for (int i = 0; i < list.size(); ++i) {
                AclBusinessHasPrivilegeEntity aclBusinessHasPrivilegeEntity = new AclBusinessHasPrivilegeEntity();
                aclBusinessHasPrivilegeEntity.setBusinessId(businessId);
                aclBusinessHasPrivilegeEntity.setPrivilegeId(list.get(i));
                aclBusinessHasPrivilegeService.createAclBusinessHasPrivilege(aclBusinessHasPrivilegeEntity);
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 设置商家权限
     * TODO zhangw
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setPrivilege/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setBusinessPrivilege(HttpServletRequest request,
                                  HttpServletResponse response, @PathVariable String type) {
        ApiResult apiResult = new ApiResult();
        try {
            Long initId = RequestUtil.getLong(request, "dataId");
            Long selectedId = RequestUtil.getLong(request, "selectedId");
            Long uid = RequestUtil.getLong(request, "uid");

            switch (type) {
                case "init":
                    Map<String, Object> data = this.setPrivilegeInit(initId);       //查询权限
                    apiResult.setData(data);
                    break;
                case "add":
                    apiResult = this.setPrivilegeAdd(selectedId, uid);         //添加权限
                    break;
                case "del":
                    apiResult = this.setPrivilegeDel(selectedId, uid);        //删除权限
                    break;
                default:
                    throw new Exception("type not find");
            }
        } catch (Exception e) {
            apiResult.setError(e.getMessage());
            return apiResult;
        }
        return apiResult;
    }

    /**
     *  删除商家权限
     *  TODO zhangw
     * @param privilegeId
     * @param uid
     * @return
     */
    private ApiResult setPrivilegeDel(Long privilegeId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("privilegeId", privilegeId);
        delMap.put("businessId", uid);
        return aclBusinessHasPrivilegeService.deleteAclBusinessHasPrivilege(delMap);
    }

    /**
     * 为商家添加权限
     * TODO zhangw
     * @param privilegeId
     * @param uid
     * @return
     */
    private ApiResult setPrivilegeAdd(Long privilegeId, Long uid) {
        AclBusinessHasPrivilegeEntity entity = new AclBusinessHasPrivilegeEntity();
        entity.setPrivilegeId(privilegeId);
        entity.setBusinessId(uid);
        return aclBusinessHasPrivilegeService.createAclBusinessHasPrivilege(entity);
    }
    /**
     * 查询已拥有的权限和全部权限
     * TODO zhangw
     * @param dataId
     * @return
     */
    private Map<String, Object> setPrivilegeInit(Long dataId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("businessId", dataId);
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
//    @AuthorityToken (needToken = {"meiren.acl.privilege.authorized"})
    @RequestMapping(value = "goTo/setPrivilege")
    public ModelAndView setPrivilege(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "设置拥有权限");
        modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
        modelAndView.setViewName("acl/business/edit_privilege");
        return modelAndView;
    }

    /**
     * 跳转添加/修改页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "setRole/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView addOrModify(HttpServletRequest request, HttpServletResponse response,@PathVariable String type) {
        ModelAndView modelAndView = new ModelAndView();
        Long businessId = RequestUtil.getLong(request, "businessId");
        String businessName = RequestUtil.getStringTrans(request,"name");
        switch (type) {
            case "add":
                modelAndView.addObject("title","角色导入");
                modelAndView.addObject("businessId",businessId);
                modelAndView.addObject("businessName",businessName);
                modelAndView.setViewName("acl/business/edit_role");
                break;
            case "modify":
                modelAndView.addObject("title", "用户角色编辑");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.setViewName("acl/business/edit_role");
                break;
        }
        return modelAndView;
    }

}
