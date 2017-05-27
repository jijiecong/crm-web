package com.meiren.web.monitor;

import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.common.utils.StringUtils;
import com.meiren.monitor.service.PavepawsMonitorConfigHasUserService;
import com.meiren.monitor.service.PavepawsMonitorConfigService;
import com.meiren.monitor.service.entity.PavepawsMonitorConfigEntity;
import com.meiren.monitor.service.entity.PavepawsMonitorConfigHasUserEntity;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.PavepawsMonitorConfigVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("{uuid}/monitor/config")
@ResponseBody
public class MonitorConfigModule extends BaseController {

    @Autowired
    protected PavepawsMonitorConfigService configService;
    @Autowired
    protected PavepawsMonitorConfigHasUserService configHasUserService;

    /**
     * 列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/list")
    public VueResult index(HttpServletRequest request) {
        int rowsNum = com.meiren.utils.RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = com.meiren.utils.RequestUtil.getInteger(request, "page", 1);
        //搜索
        Map<String, Object> searchParamMap = new HashMap<>();
        ApiResult apiResult = configService.searchPavepawsMonitorConfig(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 删除单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public ApiResult delete(HttpServletRequest request) {
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
     * 查找单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "find", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult find(HttpServletRequest request) {
        Long id = RequestUtil.getLong(request, "id");
        ApiResult apiResult = configService.findPavepawsMonitorConfig(id);
        PavepawsMonitorConfigEntity pavepawsMonitorConfigEntity = (PavepawsMonitorConfigEntity) apiResult.getData();
        PavepawsMonitorConfigVO vo = this.entityToVo(pavepawsMonitorConfigEntity);
        return new VueResult(vo);
    }

    private PavepawsMonitorConfigVO entityToVo(PavepawsMonitorConfigEntity entity) {
        PavepawsMonitorConfigVO vo = new PavepawsMonitorConfigVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 添加/修改
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrUpdate(HttpServletRequest request,PavepawsMonitorConfigVO entity) {
        ApiResult result = new ApiResult();
        try {
            Long configId;
            String id = request.getParameter("id");
            entity.setParamType(request.getParameter("paramName1")+"|"+request.getParameter("paramName2")+"|"+request.getParameter("paramName3")+"|");
            entity.setParamValue(request.getParameter("paramValue1")+"|"+request.getParameter("paramValue2")+"|"+request.getParameter("paramValue3")+"|");
            if (!StringUtils.isBlank(id)) {
                result = configService.updatePavepawsMonitorConfig(Long.valueOf(id), ObjectUtils.entityToMap(entity));
                configId = Long.valueOf(id);
            } else {
                result = configService.createPavepawsMonitorConfig(this.voToEntity(entity));
                configId = (Long) result.getData();
            }
            List<String> userIds = RequestUtil.getArray(request, "userIds");
            this.updateConfigHasUser(userIds, configId);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    private PavepawsMonitorConfigEntity voToEntity(PavepawsMonitorConfigVO vo) {
        PavepawsMonitorConfigEntity entity = new PavepawsMonitorConfigEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    private void updateConfigHasUser(List<String> userIds, Long configId) {
        Map<String, Object> delmap = new HashMap<>();
        delmap.put("configId", configId);
        configHasUserService.deletePavepawsMonitorConfigHasUser(delmap);

        if (!userIds.isEmpty()) {
            for (String id : userIds) {
                PavepawsMonitorConfigHasUserEntity entity = new PavepawsMonitorConfigHasUserEntity();
                entity.setUserId(Long.valueOf(id));
                entity.setConfigId(configId);
                configHasUserService.createPavepawsMonitorConfigHasUser(entity);
            }
        }
    }


}
