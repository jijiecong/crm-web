package com.meiren.web.acl;

import com.meiren.acl.enums.ApprovalConditionEnum;
import com.meiren.acl.service.AclProcessModelService;
import com.meiren.acl.service.AclProcessService;
import com.meiren.acl.service.entity.AclProcessEntity;
import com.meiren.acl.service.entity.AclProcessModelEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.process.index","meiren.acl.all.superAdmin"})
@Controller
@RequestMapping("/acl/process")
public class ProcessModule extends BaseController {

    @Autowired
    protected AclProcessService aclProcessService;
    @Autowired
    protected AclProcessModelService aclProcessModelService;

    private String[] necessaryParam = {
            "name",
            "hierarchyId",
            "approvalLevel",
    };

    /**
     * 列表
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
        modelAndView.setViewName("acl/process/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        //搜索名称和对应值
        Map<String, String> mapPrams = new HashMap<>();
        mapPrams.put("processNameLike", "processName");
        this.mapPrams(request,mapPrams,searchParamMap,modelAndView);
        ApiResult apiResult = aclProcessService.searchAclProcess(searchParamMap, pageNum, pageSize);
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
            List<AclProcessEntity> resultList = (List<AclProcessEntity>) resultMap.get("data");
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
            result = aclProcessService.deleteAclProcess(delMap);
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
            result = aclProcessService.findAclProcess(id);
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
     * @param aclProcessEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response, AclProcessEntity aclProcessEntity) {
        ApiResult result = new ApiResult();
        try {
            String id = request.getParameter("id");
            this.checkParamMiss(request, this.necessaryParam);
            if (!StringUtils.isBlank(id)) {
                Map<String, Object> paramMap = this.converRequestMap(request.getParameterMap());
                result = aclProcessService.updateAclProcess(Long.valueOf(id), paramMap);
            } else {
                result = aclProcessService.createAclProcess(aclProcessEntity);
            }
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }
    
    /**
     * 设置权限审核流程模板
     * TODO jijc
     * @param request
     * @param response
     * @param type
     * @param list
     * @return
     */
    @RequestMapping(value = "/setModel/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult process(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type, @RequestBody List<AclProcessModelEntity> list) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> searchParamMap = new HashMap<>();
            int riskLevel = RequestUtil.getInteger(request, "riskLevel");
            searchParamMap.put("riskLevel", riskLevel);
            if (Objects.equals(type, "init")) {
                Map<String, Object> map = new HashMap<>();
                map.put("have", aclProcessModelService.loadAclProcessModel(searchParamMap).getData());  //当前审核流程状态
                map.put("all", aclProcessService.loadAclProcess(null).getData());      //查询全部审核流程
                result.setData(map);
            } else {
            	aclProcessModelService.deleteAclProcessModel(searchParamMap);   //删除原来的再重新添加
                for (AclProcessModelEntity entity : list) {
                    entity.setRiskLevel(riskLevel);
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                            ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclProcessModelService.createAclProcessModel(entity);
                }
                result.setData(1);
            }
            result.setData(result.getData());
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
    		modelAndView.addObject("title","添加");
    		modelAndView.addObject("id", "");
    		modelAndView.setViewName("acl/process/edit");     
            break;
        case "modify":
        	modelAndView.addObject("title", "编辑");
        	modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
        	modelAndView.setViewName("acl/process/edit");     
            break;
        case "modify-low":
        	modelAndView.addObject("title", "编辑低风险模板");
        	modelAndView.addObject("riskLevel", RequestUtil.getInteger(request, "riskLevel"));
        	modelAndView.setViewName("acl/process/editModel");     
            break;
        case "modify-middle":
        	modelAndView.addObject("title", "编辑中风险模板");
        	modelAndView.addObject("riskLevel", RequestUtil.getInteger(request, "riskLevel"));
        	modelAndView.setViewName("acl/process/editModel");     
            break;
        case "modify-high":
        	modelAndView.addObject("title", "编辑高风险模板");
        	modelAndView.addObject("riskLevel", RequestUtil.getInteger(request, "riskLevel"));
        	modelAndView.setViewName("acl/process/editModel");     
            break;
    	}
    	return modelAndView;
    }
}
