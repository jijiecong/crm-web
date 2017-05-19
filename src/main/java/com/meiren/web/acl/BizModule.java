package com.meiren.web.acl;

import com.meiren.acl.service.AclBizHasPrivilegeService;
import com.meiren.acl.service.AclBizOwnerService;
import com.meiren.acl.service.AclBizService;
import com.meiren.acl.service.AclPrivilegeService;
import com.meiren.acl.service.entity.AclBizEntity;
import com.meiren.acl.service.entity.AclBizHasPrivilegeEntity;
import com.meiren.acl.service.entity.AclPrivilegeEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.common.utils.ObjectUtils;
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
 * BizModule
 *
 * @author wangzai
 * @date 2017-02-27 23:10
 * 应用相关
 */

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.biz.index"})
@Controller
@RequestMapping("acl/biz")
public class BizModule extends BaseController {

    @Autowired
    protected AclBizService aclBizService;
    @Autowired
    protected AclBizOwnerService aclBizOwnerService;
    @Autowired
    protected AclBizHasPrivilegeService aclBizHasPrivilegeService;
    @Autowired
    protected AclPrivilegeService aclPrivilegeService;

    private String[] necessaryParam = {"name",};
    /**
     * 列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView indexBiz(HttpServletRequest request, HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/biz/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        Map<String, String> userPrams = new HashMap<>();
        userPrams.put("nameLike", "bizName");
        this.mapPrams(request,userPrams,searchParamMap,modelAndView);
        ApiResult apiResult = aclBizService.searchAclBiz(searchParamMap, pageNum, pageSize);
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
            List<AclBizEntity> resultList = (List<AclBizEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);

        return modelAndView;

    }

    /**
     * 设置角色权限
     * TODO zhangw
     * @param request
     * @param response
     * @param type
     * @return
     */
    @RequestMapping(value = "/setBizPrivilege/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setBizPrivilege(HttpServletRequest request,
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
     *  删除角色权限
     *  TODO zhangw
     * @param userId
     * @param uid
     * @return
     */
    private ApiResult setPrivilegeDel(Long privilegeId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("privilegeId", privilegeId);
        delMap.put("bizId", uid);
        return aclBizHasPrivilegeService.deleteAclBizHasPrivilege(delMap);
    }

    /**
     * 为应用添加权限
     * TODO zhangw
     * @param userId
     * @param uid
     * @return
     */
    private ApiResult setPrivilegeAdd(Long privilegeId, Long uid) {
        AclBizHasPrivilegeEntity entity = new AclBizHasPrivilegeEntity();
        entity.setPrivilegeId(privilegeId);
        entity.setBizId(uid);
        return aclBizHasPrivilegeService.createAclBizHasPrivilege(entity);
    }
    /**
     * 查询已拥有的权限和全部权限
     * TODO zhangw
     * @param dataId
     * @return
     */
    private Map<String, Object> setPrivilegeInit(Long dataId) {
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("bizId", dataId);
        List<AclPrivilegeEntity> selected = (List<AclPrivilegeEntity>) aclPrivilegeService.loadAclPrivilegeJoinBizHas(searchParamMap).getData(); //查询已拥有的权限
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
            result = aclBizService.deleteAclBiz(delMap);
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
            result = aclBizService.deleteAclBiz(delMap);
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
            ApiResult apiResult = aclBizService.findAclBiz(id);
            AclBizEntity entity = (AclBizEntity) apiResult.getData();
            result.setData(entity);
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
    public ApiResult addOrUpdate(HttpServletRequest request, HttpServletResponse response, AclBizEntity aclBizEntity) {
        ApiResult result = new ApiResult();
        try {
            String id = request.getParameter("id");
            String privilegeIds = RequestUtil.getString(request, "privilegeIds");
            String userIds = RequestUtil.getString(request, "userIds");
            this.checkParamMiss(request, this.necessaryParam);  //验证参数是否为空
            if (!StringUtils.isBlank(id)) {
                Map<String, Object> paramMap = ObjectUtils.entityToMap(aclBizEntity);
                result = aclBizService.updateAclBiz(Long.valueOf(id), paramMap, privilegeIds, userIds);
            } else {
                result = aclBizService.createAclBiz(aclBizEntity, privilegeIds, userIds);
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
                modelAndView.addObject("title","添加应用");
                modelAndView.addObject("id", "");
                modelAndView.setViewName("acl/biz/edit");
                break;
            case "modify":
                modelAndView.addObject("title", "编辑应用");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.setViewName("acl/biz/edit");
                break;
        }
        return modelAndView;
    }
}
