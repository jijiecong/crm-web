package com.meiren.web.monitor;

import com.meiren.common.result.ApiResult;
import com.meiren.monitor.service.PavepawsMonitorResultService;
import com.meiren.monitor.service.entity.PavepawsMonitorResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/monitor/result")
public class MonitorResultModule extends BaseController {

    @Autowired
    protected PavepawsMonitorResultService pavepawsMonitorResultService;

    /**
     * findAll
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request,HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("monitor/result");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();

        ApiResult apiResult = pavepawsMonitorResultService.searchPavepawsMonitorResult(searchParamMap, pageNum, pageSize);

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
            List<PavepawsMonitorResultEntity> resultList = (List<PavepawsMonitorResultEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);
        return modelAndView;
    }
    
    /**
     * findById
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
            PavepawsMonitorResultEntity entity = (PavepawsMonitorResultEntity) pavepawsMonitorResultService.findPavepawsMonitorResult(id).getData();
            result.setData(entity);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

}
