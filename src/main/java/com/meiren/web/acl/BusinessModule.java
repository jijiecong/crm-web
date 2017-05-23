package com.meiren.web.acl;
//
//import com.meiren.acl.enums.UserRoleStatusEnum;
//import com.meiren.acl.service.*;
//import com.meiren.acl.service.entity.AclBusinessEntity;
//import com.meiren.acl.service.entity.AclBusinessHasPrivilegeEntity;
//import com.meiren.acl.service.entity.AclPrivilegeEntity;
//import com.meiren.acl.service.entity.AclRoleHasPrivilegeEntity;
//import com.meiren.common.annotation.AuthorityToken;
//import com.meiren.common.result.ApiResult;
//import com.meiren.common.utils.RequestUtil;
//import com.meiren.common.utils.StringUtils;
//import com.meiren.monitor.utils.ObjectUtils;
//import com.meiren.vo.SelectVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.*;
//
///**
// * BusinessModule
// *
// * @author wangzai
// * @date 2017-05-02 23:10
// * 商家相关
// */
//
//@AuthorityToken(needToken = {"meiren.acl.all.superAdmin", "meiren.acl.mbc.crm.acl.business.index"})
//@Controller
//@RequestMapping("acl/business")
//public class BusinessModule extends BaseController {
//
//    @Autowired
//    protected AclBusinessService aclBusinessService;
//    @Autowired
//    protected AclBusinessHasPrivilegeService aclBusinessHasPrivilegeService;
//    @Autowired
//    protected AclPrivilegeService aclPrivilegeService;
//    @Autowired
//    protected AclRoleHasPrivilegeService aclRoleHasPrivilegeService;
//    @Autowired
//    protected AclRoleService aclRoleService;
//
//    private String[] necessaryParam = {"name",};
//
//    /**
//     * 列表
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping("/index")
//    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
//
//        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("acl/business/index");
//
//        int pageNum = Integer.valueOf(page);
//        if (pageNum <= 0) {
//            pageNum = 1;
//        }
//        int pageSize = DEFAULT_ROWS;
//
//        Map<String, Object> searchParamMap = new HashMap<>();
//        Map<String, String> userPrams = new HashMap<>();
//        userPrams.put("nameLike", "businessName");
//        this.mapPrams(request, userPrams, searchParamMap, modelAndView);
//        ApiResult apiResult = aclBusinessService.searchAclBusiness(searchParamMap, pageNum, pageSize);
//        String message = this.checkApiResult(apiResult);
//        if (message != null) {
//            modelAndView.addObject("message", message);
//            return modelAndView;
//        }
//        Map<String, Object> resultMap = (Map<String, Object>) apiResult.getData();
//
//        if (resultMap.get("totalCount") != null) {
//            modelAndView.addObject("totalCount", Integer.valueOf(resultMap.get("totalCount").toString()));
//        }
//        if (resultMap.get("data") != null) {
//            List<AclBusinessEntity> resultList = (List<AclBusinessEntity>) resultMap.get("data");
//            modelAndView.addObject("basicVOList", resultList);
//        }
//        modelAndView.addObject("curPage", pageNum);
//        modelAndView.addObject("pageSize", pageSize);
//
//        return modelAndView;
//
//    }
//
//    /**
//     * 删除单个 -- 暂未使用
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "deleteMap", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult deleteMap(HttpServletRequest request, HttpServletResponse response) {
//        ApiResult result = new ApiResult();
//        Map<String, Object> delMap = new HashMap<>();
//        try {
//            Long id = this.checkId(request);
//            delMap.put("id", id);
//            result = aclBusinessService.deleteAclBusiness(delMap);
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//    /**
//     * 删除单个
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "delete", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult delete(HttpServletRequest request, HttpServletResponse response) {
//        ApiResult result = new ApiResult();
//        try {
//            Long id = this.checkId(request);
//            result = aclBusinessService.deleteById(id);
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//    /**
//     * 批量删除
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "deleteBatch", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult deleteBatch(HttpServletRequest request, HttpServletResponse response) {
//        ApiResult result = new ApiResult();
//        Map<String, Object> delMap = new HashMap<>();
//        String[] ids = request.getParameterValues("ids[]");
//        List<String> idsList = Arrays.asList(ids);
//        try {
//            delMap.put("inIds", idsList);
//            result = aclBusinessService.deleteAclBusiness(delMap);
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//    /**
//     * 查找单个
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "find", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult findById(HttpServletRequest request, HttpServletResponse response) {
//        ApiResult result = new ApiResult();
//        try {
//            Long id = this.checkId(request);
//            ApiResult apiResult = aclBusinessService.findAclBusiness(id);
//            AclBusinessEntity entity = (AclBusinessEntity) apiResult.getData();
//            result.setData(entity);
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//    /**
//     * 添加/修改
//     *
//     * @param request
//     * @param response
//     * @param aclUserEntity
//     * @return
//     */
//    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult addOrUpdate(HttpServletRequest request, HttpServletResponse response, AclBusinessEntity aclBusinessEntity) {
//        ApiResult result = new ApiResult();
//        try {
//            String id = request.getParameter("id");
//            this.checkParamMiss(request, this.necessaryParam);  //验证参数是否为空
//            if (!StringUtils.isBlank(id)) {
//                Map<String, Object> paramMap = ObjectUtils.reflexToMap(aclBusinessEntity);
//                result = aclBusinessService.updateAclBusiness(Long.valueOf(id), paramMap);
//            } else {
//                result = aclBusinessService.createAclBusiness(aclBusinessEntity);
//            }
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//
//    /**
//     * 跳转添加/修改页面
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "goTo/{type}", method = RequestMethod.GET)
//    @ResponseBody
//    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
//        ModelAndView modelAndView = new ModelAndView();
//        switch (type) {
//            case "add":
//                modelAndView.addObject("title", "添加商家");
//                modelAndView.addObject("id", "");
//                break;
//            case "modify":
//                modelAndView.addObject("title", "编辑商家");
//                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
//                break;
//        }
//        modelAndView.setViewName("acl/business/edit");
//        return modelAndView;
//    }
//
//
//    /**
//     * 查询角色
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @param type
//     * @return
//     */
//    //    @AuthorityToken(needToken = {"meiren.acl.role.authorized"})
//    @RequestMapping("/searchRole/{type}")
//    @ResponseBody
//    public ApiResult select2(HttpServletRequest request,
//                             HttpServletResponse response, @PathVariable String type) {
//        ApiResult result = new ApiResult();
//        try {
//            result = this.initAndQuery(request, type);
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//    /**
//     * init  and Query
//     * TODO zhangw
//     *
//     * @param request
//     * @param userId
//     * @param roleIds
//     * @param type
//     * @return
//     * @throws Exception
//     */
//    private ApiResult initAndQuery(HttpServletRequest request, String type) throws Exception {
//        ApiResult result = new ApiResult();
//        if (Objects.equals(type, "init")) {
//            Long userId = this.checkId(request);
//            Map<String, Object> map = new HashMap<>();
//            map.put("userId", userId);
//            map.put("hasStatus", UserRoleStatusEnum.NORMAL.name());
//            result = aclRoleService.loadAclRoleJoinUserHas(map);     //查询用户拥有的未被禁用的并且当前登录用户为owner的角色
//        } else {
//            Map<String, Object> map = new HashMap<>();
//            map.put("roleNameLike", RequestUtil.getStringTrans(request, "q"));
//            result = aclRoleService.loadAclRole(map);               //查询角色
//        }
//        return result;
//    }
//
//
//    /**
//     * 批量导入权限
//     * TODO　zhangw
//     *
//     * @param request
//     * @param response
//     * @param aclUserEntity
//     * @return
//     */
//    @RequestMapping(value = {"addPrivilege"}, method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult addPrivilege(HttpServletRequest request, HttpServletResponse response) {
//        ApiResult result = new ApiResult();
//        try {
//            List<Long> listOld = new ArrayList<>();
//            List<Long> listNew = new ArrayList<>();
//            Map<String, Object> map = new HashMap<String, Object>();
//            String[] roleIds = request.getParameterValues("roleIds[]");
//            List<String> idsList = Arrays.asList(roleIds);
//            Long businessId = RequestUtil.getLong(request, "businessId");
//            map.put("businessId", businessId);
//            List<AclBusinessHasPrivilegeEntity> aclBusinessHasPrivilegeEntityList = (List<AclBusinessHasPrivilegeEntity>) aclBusinessHasPrivilegeService.loadAclBusinessHasPrivilege(map).getData();
//            Map<String, Object> roleMap = new HashMap<String, Object>();
//            roleMap.put("inRoleIds", idsList);
//            List<AclRoleHasPrivilegeEntity> aclRoleHasPrivilegeEntity = (List<AclRoleHasPrivilegeEntity>) aclRoleHasPrivilegeService.loadAclRoleHasPrivilege(roleMap).getData();
//            for (AclRoleHasPrivilegeEntity a : aclRoleHasPrivilegeEntity) {
//                if(!listNew.contains(a.getPrivilegeId())){
//                    listNew.add(a.getPrivilegeId());
//                }
//            }
//            for (AclBusinessHasPrivilegeEntity b : aclBusinessHasPrivilegeEntityList) {
//                listOld.add(b.getPrivilegeId());
//            }
//            listNew.removeAll(listOld);
//            List<AclBusinessHasPrivilegeEntity> list = new ArrayList<>();
//            for (int i = 0; i < listNew.size(); ++i) {
//                AclBusinessHasPrivilegeEntity aclBusinessHasPrivilegeEntity = new AclBusinessHasPrivilegeEntity();
//                aclBusinessHasPrivilegeEntity.setBusinessId(businessId);
//                aclBusinessHasPrivilegeEntity.setPrivilegeId(listNew.get(i));
//                list.add(aclBusinessHasPrivilegeEntity);
//            }
//            if(listNew.size() > 0 ){
//                result = aclBusinessHasPrivilegeService.createBatch(list);
//            }else{
//                result.setData("您选择导入的权限已经存在，无需重复添加！");
//            }
//
//        } catch (Exception e) {
//            result.setError(e.getMessage());
//            return result;
//        }
//        return result;
//    }
//
//    /**
//     * 设置商家权限
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @param type
//     * @return
//     */
//    @RequestMapping(value = "/setPrivilege/{type}", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult setBusinessPrivilege(HttpServletRequest request,
//                                          HttpServletResponse response, @PathVariable String type) {
//        ApiResult apiResult = new ApiResult();
//        try {
//            Long initId = RequestUtil.getLong(request, "dataId");
//            Long selectedId = RequestUtil.getLong(request, "selectedId");
//            Long uid = RequestUtil.getLong(request, "uid");
//            switch (type) {
//                case "init":
//                    Map<String, Object> data = this.setPrivilegeInit(initId);       //查询权限
//                    apiResult.setData(data);
//                    break;
//                case "add":
//                    apiResult = this.setPrivilegeAdd(selectedId, uid);         //添加权限
//                    break;
//                case "del":
//                    apiResult = this.setPrivilegeDel(selectedId, uid);        //删除权限
//                    break;
//                default:
//                    throw new Exception("type not find");
//            }
//        } catch (Exception e) {
//            apiResult.setError(e.getMessage());
//            return apiResult;
//        }
//        return apiResult;
//    }
//
//    /**
//     * 删除商家权限
//     * TODO zhangw
//     *
//     * @param privilegeId
//     * @param uid
//     * @return
//     */
//    private ApiResult setPrivilegeDel(Long privilegeId, Long uid) {
//        Map<String, Object> delMap = new HashMap<>();
//        delMap.put("privilegeId", privilegeId);
//        delMap.put("businessId", uid);
//        return aclBusinessHasPrivilegeService.deleteAclBusinessHasPrivilege(delMap);
//    }
//
//    /**
//     * 为商家添加权限
//     * TODO zhangw
//     *
//     * @param privilegeId
//     * @param uid
//     * @return
//     */
//    private ApiResult setPrivilegeAdd(Long privilegeId, Long uid) {
//        AclBusinessHasPrivilegeEntity entity = new AclBusinessHasPrivilegeEntity();
//        entity.setPrivilegeId(privilegeId);
//        entity.setBusinessId(uid);
//        return aclBusinessHasPrivilegeService.createAclBusinessHasPrivilege(entity);
//    }
//
//    /**
//     * 查询已拥有的权限和全部权限
//     * TODO zhangw
//     *
//     * @param dataId
//     * @return
//     */
//    private Map<String, Object> setPrivilegeInit(Long dataId) {
//        Map<String, Object> searchParamMap = new HashMap<>();
//        searchParamMap.put("businessId", dataId);
//        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilegeJoinBusinessHas(searchParamMap).getData(); //查询已拥有的权限
//        List<AclPrivilegeEntity> all = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilege(null).getData(); //查询全部权限
//
//        List<SelectVO> selectedVOs = new ArrayList<>();
//        List<SelectVO> selectDataVOs = new ArrayList<>();
//
//        for (AclPrivilegeEntity entity : selected) {
//            SelectVO vo = new SelectVO();
//            vo.setId(entity.getId());
//            vo.setName(entity.getName());
//            selectedVOs.add(vo);
//        }
//        for (AclPrivilegeEntity entity : all) {
//            SelectVO vo = new SelectVO();
//            vo.setId(entity.getId());
//            vo.setName(entity.getName());
//            selectDataVOs.add(vo);
//        }
//
//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("selected", selectedVOs);
//        dataMap.put("selectData", selectDataVOs);
//        return dataMap;
//    }
//
//    /**
//     * 设置权限
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    //    @AuthorityToken (needToken = {"meiren.acl.privilege.authorized"})
//    @RequestMapping(value = "goTo/setPrivilege")
//    public ModelAndView setPrivilege(HttpServletRequest request, HttpServletResponse response) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("title", "设置权限");
//        modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
//        modelAndView.setViewName("acl/business/edit_privilege");
//        return modelAndView;
//    }
//
//    /**
//     * 跳转添加/修改页面
//     * TODO zhangw
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "setRole/{type}", method = RequestMethod.GET)
//    @ResponseBody
//    public ModelAndView addOrModify(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
//        ModelAndView modelAndView = new ModelAndView();
//        Long businessId = RequestUtil.getLong(request, "businessId");
//        String businessName = RequestUtil.getStringTrans(request, "name");
//        switch (type) {
//            case "add":
//                modelAndView.addObject("title", "权限导入");
//                modelAndView.addObject("businessId", businessId);
//                modelAndView.addObject("businessName", businessName);
//                modelAndView.setViewName("acl/business/edit_role");
//                break;
//        }
//        return modelAndView;
//    }
//
//}


import com.meiren.acl.service.AclBusinessService;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.BusinessVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@AuthorityToken(needToken = {"meiren.acl.all.superAdmin", "meiren.acl.mbc.crm.acl.business.index"})
@Controller
@RequestMapping("acl/business")
public class BusinessModule extends BaseController {

    @Autowired
    protected AclBusinessService aclBusinessService;

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
     * 添加编辑用户
     *
     * @param request
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, BusinessVO vo) throws Exception {
        VueResult result = new VueResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id != null) {
            aclBusinessService.updateAclBusiness(id, ObjectUtils.entityToMap(vo));
        } else {
            aclBusinessService.createAclBusiness(this.voToEntity(vo));
        }
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
    @RequestMapping(value = {"addPrivilege"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addPrivilege(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            List<Long> listOld = new ArrayList<>();
            List<Long> listNew = new ArrayList<>();
            Map<String, Object> map = new HashMap<String, Object>();
            String[] roleIds = request.getParameterValues("roleIds[]");
            List<String> idsList = Arrays.asList(roleIds);
            Long businessId = RequestUtil.getLong(request, "businessId");
            map.put("businessId", businessId);
            List<AclBusinessHasPrivilegeEntity> aclBusinessHasPrivilegeEntityList = (List<AclBusinessHasPrivilegeEntity>) aclBusinessHasPrivilegeService.loadAclBusinessHasPrivilege(map).getData();
            Map<String, Object> roleMap = new HashMap<String, Object>();
            roleMap.put("inRoleIds", idsList);
            List<AclRoleHasPrivilegeEntity> aclRoleHasPrivilegeEntity = (List<AclRoleHasPrivilegeEntity>) aclRoleHasPrivilegeService.loadAclRoleHasPrivilege(roleMap).getData();
            //对不同角色拥有同一权限，去重处理
            for (AclRoleHasPrivilegeEntity a : aclRoleHasPrivilegeEntity) {
                if(!listNew.contains(a.getPrivilegeId())){
                    listNew.add(a.getPrivilegeId());
                }
            }
            for (AclBusinessHasPrivilegeEntity b : aclBusinessHasPrivilegeEntityList) {
                listOld.add(b.getPrivilegeId());
            }
            //对新加的权限商家之前已经拥有的，去重处理
            listNew.removeAll(listOld);

            List<AclBusinessHasPrivilegeEntity> list = new ArrayList<>();
            for (int i = 0; i < listNew.size(); ++i) {
                AclBusinessHasPrivilegeEntity aclBusinessHasPrivilegeEntity = new AclBusinessHasPrivilegeEntity();
                aclBusinessHasPrivilegeEntity.setBusinessId(businessId);
                aclBusinessHasPrivilegeEntity.setPrivilegeId(listNew.get(i));
                list.add(aclBusinessHasPrivilegeEntity);
            }
            if(listNew.size() > 0 ){
                result = aclBusinessHasPrivilegeService.createBatch(list);
            }else{
                result.setData("您选择导入的权限已经存在，无需重复添加！");
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
     *
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
     * 删除商家权限
     * TODO zhangw
     *
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
    public VueResult delete(HttpServletRequest request) {
        VueResult result = new VueResult();
        Long id = RequestUtil.getLong(request, "id");
        if (id == null) {
            result.setError("请选择要删除的商家！");
            return result;
        }
        aclBusinessService.deleteById(id);
        return result;
    }
}