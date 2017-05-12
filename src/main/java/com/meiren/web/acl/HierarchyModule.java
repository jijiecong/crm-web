package com.meiren.web.acl;

import com.meiren.acl.service.AclHierarchyService;
import com.meiren.acl.service.entity.AclHierarchyEntity;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.hierarchy.index"})
@Controller
@RequestMapping("/acl/hierarchy")
public class HierarchyModule extends BaseController {

    @Autowired
    protected AclHierarchyService aclHierarchyService;

    private String[] necessaryParam = {
            "hierarchyName",
            "hierarchyValue",
            "sort",
    };

    /**
     * 列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/hierarchy/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        AclUserEntity user = this.getUser(request);
        //搜索名称和对应值
        Map<String, String> mapPrams = new HashMap<>();
        mapPrams.put("hierarchyNameLike", "hierarchyName"); //模糊查询
        this.mapPrams(request,mapPrams,searchParamMap,modelAndView);

        ApiResult apiResult = aclHierarchyService.searchAclHierarchy(searchParamMap, pageNum, pageSize);
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
            List<AclHierarchyEntity> resultList = (List<AclHierarchyEntity>) resultMap.get("data");
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
        AclUserEntity user = this.getUser(request);
        if(!this.hasSuperAdmin(user)){
            result.setError("您没有权限操作层级！");
            return result;
        }
        Map<String, Object> delMap = new HashMap<>();
        try {
            Long id = this.checkId(request);
            delMap.put("id", id);
            result = aclHierarchyService.deleteAclHierarchy(delMap);
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
    public ApiResult find(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            result = aclHierarchyService.findAclHierarchy(id);
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
     * @param aclHierarchyEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response, AclHierarchyEntity aclHierarchyEntity) {
        ApiResult result = new ApiResult();
        try {
            AclUserEntity user = this.getUser(request);
            if(!this.hasSuperAdmin(user)){
                result.setError("您没有权限操作层级！");
                return result;
            }
            String id = request.getParameter("id");
            this.checkParamMiss(request, this.necessaryParam);
            if (!StringUtils.isBlank(id)) {
                Map<String, Object> paramMap = this.converRequestMap(request.getParameterMap());
                result = aclHierarchyService.updateAclHierarchy(Long.valueOf(id), paramMap);
            } else {
                if(!this.isMeiren(user)){
                    aclHierarchyEntity.setBusinessId(user.getBusinessId());
                }
                result = aclHierarchyService.createAclHierarchy(aclHierarchyEntity);
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
    @AuthorityToken(needToken = {"meiren.acl.all.superAdmin"})
    @RequestMapping(value = "goTo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response,@PathVariable String type) {
        ModelAndView modelAndView = new ModelAndView();
        AclUserEntity user = this.getUser(request);
        switch (type) {
            case "add":
                modelAndView.addObject("title","添加层级");
                modelAndView.addObject("id", "");
                modelAndView.setViewName("acl/hierarchy/edit");
                break;
            case "modify":
                modelAndView.addObject("title", "编辑层级");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.setViewName("acl/hierarchy/edit");
                break;
        }
        return modelAndView;
    }
}
