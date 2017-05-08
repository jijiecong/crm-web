package com.meiren.web.monitor;

import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.monitor.service.PavepawsMonitorConfigHasUserService;
import com.meiren.monitor.service.PavepawsMonitorConfigService;
import com.meiren.monitor.service.PavepawsMonitorResultService;
import com.meiren.monitor.service.entity.PavepawsMonitorConfigEntity;
import com.meiren.monitor.service.entity.PavepawsMonitorConfigHasUserEntity;
import com.meiren.monitor.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/monitor/config")
public class MonitorConfigModule extends BaseController {

    @Autowired
    protected PavepawsMonitorConfigService configService;
    @Autowired
    protected PavepawsMonitorResultService resultService;
    @Autowired
    protected PavepawsMonitorConfigHasUserService configHasUserService;

    private String[] necessaryParam = {
            "name",
            "method",
            "router",
            "domain",
    };


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
        modelAndView.setViewName("monitor/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        if (!StringUtils.isBlank(request.getParameter("id"))) {
            searchParamMap.put("id", request.getParameter("id"));
            modelAndView.addObject("id", request.getParameter("id"));
        }
        if (!StringUtils.isBlank(RequestUtil.getString(request, "name"))) {
            searchParamMap.put("name", RequestUtil.getString(request, "name"));
            modelAndView.addObject("name", RequestUtil.getString(request, "name"));
        }
        ApiResult apiResult = configService.searchPavepawsMonitorConfig(searchParamMap, pageNum, pageSize);

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
            List<PavepawsMonitorConfigEntity> resultList =
                    (List<PavepawsMonitorConfigEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }

        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);

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
            result = configService.deletePavepawsMonitorConfig(delMap);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 批量删除
     *
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
            result = configService.deletePavepawsMonitorConfig(delMap);
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
    public ApiResult findById(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            PavepawsMonitorConfigEntity entity = (PavepawsMonitorConfigEntity)
                    configService.findPavepawsMonitorConfig(id).getData();
            result.setData(entity);
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
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrUpdate(HttpServletRequest request, HttpServletResponse response, PavepawsMonitorConfigEntity entity) {
        ApiResult result = new ApiResult();
        try {
            Long configId;
            String id = request.getParameter("id");
            this.checkParamMiss(request, this.necessaryParam);
            Map<String, String[]> paramMap = request.getParameterMap();
            entity.setParamType(paramMap.get("paramtype[]"));
            entity.setParamValue(paramMap.get("paramvalue[]"));
            if (!StringUtils.isBlank(id)) {
                result = configService.updatePavepawsMonitorConfig(Long.valueOf(id),
                        ObjectUtils.reflexToMap(entity));
                configId = Long.valueOf(id);
            } else {
                result = configService.createPavepawsMonitorConfig(entity);
                configId = (Long) result.getData();
            }
            this.updateConfigHasUser(request.getParameter("userIds"), configId);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private void updateConfigHasUser(String idsString, Long configId) {
        Map<String, Object> delmap = new HashMap<>();
        delmap.put("configId", configId);
        configHasUserService.deletePavepawsMonitorConfigHasUser(delmap);
        if (!StringUtils.isBlank(idsString)) {
            String[] Ids = idsString.split(",");
            for (String id : Ids) {
                PavepawsMonitorConfigHasUserEntity entity =
                        new PavepawsMonitorConfigHasUserEntity();
                entity.setUserId(Long.valueOf(id));
                entity.setConfigId(configId);
                configHasUserService.createPavepawsMonitorConfigHasUser(entity);
            }
        }
    }


}
