package com.meiren.web.monitor;

import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.monitor.service.PavepawsMonitorResultService;
import com.meiren.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("{uuid}/monitor/result")
@ResponseBody
public class MonitorResultModule extends BaseController {

    @Autowired
    protected PavepawsMonitorResultService pavepawsMonitorResultService;

    /**
     * findAll
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {

        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        Map<String, Object> searchParamMap = new HashMap<>();
        ApiResult apiResult = pavepawsMonitorResultService.searchPavepawsMonitorResult(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

}
