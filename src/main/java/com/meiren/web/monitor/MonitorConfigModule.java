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
import java.util.ArrayList;
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
        Map<String, Object> map = new HashMap<>();
        map.put("configId", id);
        List<PavepawsMonitorConfigHasUserEntity> pavepawsMonitorConfigHasUserEntities =
            (List<PavepawsMonitorConfigHasUserEntity>) configHasUserService.loadPavepawsMonitorConfigHasUser(map).getData();
        List<Long> userIds = new ArrayList<>();
        for(PavepawsMonitorConfigHasUserEntity pavepawsMonitorConfigHasUserEntity : pavepawsMonitorConfigHasUserEntities){
            userIds.add(pavepawsMonitorConfigHasUserEntity.getUserId());
        }
        vo.setUserIds(userIds);
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
    public ApiResult addOrUpdate(HttpServletRequest request,PavepawsMonitorConfigVO vo) {
        ApiResult result = new ApiResult();
        try {
            Long configId;
            PavepawsMonitorConfigEntity pavepawsMonitorConfigEntity = this.voToEntity(vo);
            String id = request.getParameter("id");
            String paramTypes = RequestUtil.getString(request,"paramTypes");
            String paramValues = RequestUtil.getString(request,"paramValues");
            pavepawsMonitorConfigEntity.setParamType(paramTypes);
            pavepawsMonitorConfigEntity.setParamValue(paramValues);
            if (!StringUtils.isBlank(id)) {
                result = configService.updatePavepawsMonitorConfig(Long.valueOf(id), ObjectUtils.entityToMap(pavepawsMonitorConfigEntity));
                configId = Long.valueOf(id);
            } else {
                result = configService.createPavepawsMonitorConfig(pavepawsMonitorConfigEntity);
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

    /**
     * vo转换为Entity
     * @param vo
     * @return
     */
    private PavepawsMonitorConfigEntity voToEntity(PavepawsMonitorConfigVO vo) {
        PavepawsMonitorConfigEntity entity = new PavepawsMonitorConfigEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 添加监控通知人
     * @param userIds
     * @param configId
     */
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
