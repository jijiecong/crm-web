package com.meiren.web.acl;

import com.alibaba.fastjson.JSONArray;
import com.meiren.acl.enums.ApprovalConditionEnum;
import com.meiren.acl.enums.RiskLevelEnum;
import com.meiren.acl.enums.RoleStatusEnum;
import com.meiren.acl.service.AclProcessModelService;
import com.meiren.acl.service.AclProcessService;
import com.meiren.acl.service.entity.AclProcessEntity;
import com.meiren.acl.service.entity.AclProcessModelEntity;
import com.meiren.acl.service.entity.AclRoleEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.result.VueResult;
import com.meiren.common.utils.ObjectUtils;
import com.meiren.common.utils.StringUtils;
import com.meiren.tech.mbc.action.ActionControllerLog;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.ProcessVO;
import com.meiren.vo.RoleVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.process.index", "meiren.acl.all.superAdmin"})
@Controller
@RequestMapping("{uuid}/acl/process")
@ResponseBody
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

    private ProcessVO entityToVo(AclProcessEntity entity) {
        ProcessVO vo = new ProcessVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private AclProcessEntity voToEntity(ProcessVO vo) {
        AclProcessEntity entity = new AclProcessEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public VueResult list(HttpServletRequest request) {
        int rowsNum = RequestUtil.getInteger(request, "rows", DEFAULT_ROWS);
        int pageNum = RequestUtil.getInteger(request, "page", 1);
        //搜索名称和对应值
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("processNameLike", RequestUtil.getStringTrans(request, "name"));
        ApiResult apiResult = aclProcessService.searchAclProcess(searchParamMap, pageNum, rowsNum);
        Map<String, Object> rMap = new HashMap<>();
        if (apiResult.getData() != null) {
            rMap = (Map<String, Object>) apiResult.getData();
        }
        return new VueResult(rMap);
    }

    /**
     * 查询
     */
    @RequestMapping("/find")
    public VueResult find(HttpServletRequest request) throws Exception {
        Long id = this.checkId(request);
        AclProcessEntity entity = (AclProcessEntity) aclProcessService.findAclProcess(id).getData();
        ProcessVO vo = this.entityToVo(entity);
        return new VueResult(vo);
    }

    /**
     * 添加编辑
     */
    @ActionControllerLog(descriptions = "添加编辑流程")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VueResult save(HttpServletRequest request, ProcessVO vo) throws Exception {
        VueResult result = new VueResult();
        this.checkParamMiss(request, this.necessaryParam);
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            Map<String, Object> paramMap = this.converRequestMap(request.getParameterMap());
            aclProcessService.updateAclProcess(Long.valueOf(id), paramMap);
        } else {
            aclProcessService.createAclProcess(this.voToEntity(vo));
        }
        result.setData("操作成功！");
        return result;
    }

    /**
     * 删除单个
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    @ResponseBody
    public VueResult delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VueResult result = new VueResult();
        Map<String, Object> delMap = new HashMap<>();
        Long id = this.checkId(request);
        delMap.put("id", id);
        aclProcessService.deleteAclProcess(delMap).check();
        result.setData("操作成功！");
        return result;
    }

    /**
     * 设置权限审核流程模板
     * TODO jijc
     *
     * @param request
     * @param response
     * @param type
     * @param list
     * @return
     */
    @RequestMapping(value = "/setModel/{type}", method = RequestMethod.POST)
    @ResponseBody
    public VueResult process(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String type) throws Exception {
        VueResult result = new VueResult();

        Map<String, Object> searchParamMap = new HashMap<>();
        Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
        if (riskLevel == null) {
            throw new Exception("请选择要编辑的风险等级！");
        }
        searchParamMap.put("riskLevel", riskLevel);
        if (Objects.equals(type, "init")) {
            Map<String, Object> map = new HashMap<>();
            List<AclProcessModelEntity> have = (List<AclProcessModelEntity>) aclProcessModelService.loadAclProcessModel(searchParamMap).getData();
            List<AclProcessEntity> all = (List<AclProcessEntity>) aclProcessService.loadAclProcess(null).getData();
            List<ProcessVO> allVOs = new ArrayList<>();

            for (AclProcessEntity entity : all) {
                ProcessVO vo = new ProcessVO();
                BeanUtils.copyProperties(entity, vo);
                vo.setChecked(false);
                vo.setApprovalCondition(ApprovalConditionEnum.AND.name);
                for (AclProcessModelEntity processModelEntity : have) {
                    if (processModelEntity.getProcessId() == vo.getId()) {
                        vo.setChecked(true);
                        vo.setHierarchyId(processModelEntity.getHierarchyId());
                        vo.setApprovalCondition(processModelEntity.getApprovalCondition());
                    }
                }
                allVOs.add(vo);
            }
            map.put("all", allVOs); // 查询全部审核流程
            result.setData(map);
        } else {
            String process = RequestUtil.getString(request, "process");
            List<AclProcessModelEntity> list = new ArrayList<>();
            list = JSONArray.parseArray(process, AclProcessModelEntity.class);
            aclProcessModelService.deleteAclProcessModel(searchParamMap);   //删除原来的再重新添加
            for (AclProcessModelEntity entity : list) {
                if (entity.getChecked()) {
                    entity.setRiskLevel(riskLevel);
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                        ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclProcessModelService.createAclProcessModel(entity);
                }
            }
            result.setData("操作成功！");
        }
        return result;
    }
}
