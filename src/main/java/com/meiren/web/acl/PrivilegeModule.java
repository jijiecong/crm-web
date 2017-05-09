package com.meiren.web.acl;

import com.meiren.acl.enums.ApprovalConditionEnum;
import com.meiren.acl.enums.PrivilegeStatusEnum;
import com.meiren.acl.enums.RiskLevelEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.*;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.monitor.utils.ObjectUtils;
import com.meiren.vo.SelectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.privilege.index"})
@Controller
@RequestMapping("/acl/privilege")
public class PrivilegeModule extends BaseController {

    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclProcessService aclProcessService;
    @Autowired
    protected AclPrivilegeProcessService aclPrivilegeProcessService;
    @Autowired
    protected AclUserService aclUserService;
    @Autowired
    protected AclPrivilegeOwnerService aclPrivilegeOwnerService;
    @Autowired
    protected AclProcessModelService aclProcessModelService;

    public String privilegeAll = "meiren.acl.privilege.all";
    private String[] necessaryParam = {"name", "token",};


	/**
	 * 设置owner
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/setOwner/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult setOwner(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
		ApiResult result = new ApiResult();
		try {
			AclUserEntity user = this.getUser(request);
			Long initId = RequestUtil.getLong(request, "dataId");
			Long userId = RequestUtil.getLong(request, "selectedId");
			Long uid = RequestUtil.getLong(request, "uid");
			switch (type) {
			case "init":
				Map<String, Object> data = this.setOwnerInit(initId,user.getBusinessId());
				result.setData(data);
				break;
			case "add":
				result = this.setOwnerAdd(userId, uid);
				break;
			case "del":
				result = this.setOwnerDel(userId, uid);
				break;
			default:
				throw new Exception("type not find");
			}
		} catch (Exception e) {
			result.setError(e.getMessage());
			return result;
		}
		return result;
	}

    private ApiResult setOwnerDel(Long userId, Long uid) {
        Map<String, Object> delMap = new HashMap<>();
        delMap.put("userId", userId);
        delMap.put("privilegeId", uid);
        return aclPrivilegeOwnerService.deleteAclPrivilegeOwner(delMap);
    }

    private ApiResult setOwnerAdd(Long userId, Long uid) {
        AclPrivilegeOwnerEntity entity = new AclPrivilegeOwnerEntity();
        entity.setUserId(userId);
        entity.setPrivilegeId(uid);
        return aclPrivilegeOwnerService.createAclPrivilegeOwner(entity);
    }

	/**
	 * 查询权限属于某个用户及全部用户
	 * 
	 * @param dataId
	 * @return
	 */
	private Map<String, Object> setOwnerInit(Long dataId, Long businessId) {
		Map<String, Object> searchParamMap = new HashMap<>();
		searchParamMap.put("privilegeId", dataId);
		List<AclUserEntity> selected = (List<AclUserEntity>)
				aclUserService.loadAclUserJoinPrivilegeOwner(searchParamMap).getData(); // 根据查询权限Id查询用户

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("businessId", businessId);
		List<AclUserEntity> all = (List<AclUserEntity>)
				aclUserService.loadAclUser(paramMap).getData(); // 查询商家下所有用户

        List<SelectVO> selectedVOs = new ArrayList<>();
        List<SelectVO> selectDataVOs = new ArrayList<>();

        for (AclUserEntity entity : selected) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId()); // 将信息转换为id name 类型
            vo.setName(entity.getUserName());
            selectedVOs.add(vo);
        }
        for (AclUserEntity entity : all) {
            SelectVO vo = new SelectVO();
            vo.setId(entity.getId());
            vo.setName(entity.getUserName());
            selectDataVOs.add(vo);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("selected", selectedVOs);
        dataMap.put("selectData", selectDataVOs);
        return dataMap;
    }

    /**
     * 设置权限审核流程
     *
     * @param request
     * @param response
     * @param type
     * @param list
     * @return
     */
    @RequestMapping(value = "/process/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult process(HttpServletRequest request, HttpServletResponse response, @PathVariable String type,
                             @RequestBody List<AclPrivilegeProcessEntity> list) {
        ApiResult result = new ApiResult();
        try {
            Map<String, Object> searchParamMap = new HashMap<>();
            Long id = RequestUtil.getLong(request, "id");
            searchParamMap.put("privilegeId", id);
            if (Objects.equals(type, "init")) {
                Map<String, Object> map = new HashMap<>();
                map.put("have", aclPrivilegeProcessService.loadAclPrivilegeProcess(searchParamMap).getData()); // 当前审核流程状态
                map.put("all", aclProcessService.loadAclProcess(null).getData()); // 查询全部审核流程
                result.setData(map);
            } else {
                aclPrivilegeProcessService.deleteAclPrivilegeProcess(searchParamMap); // 删除原来的再重新添加
                for (AclPrivilegeProcessEntity entity : list) {
                    entity.setPrivilegeId(id);
                    entity.setApprovalCondition(entity.getApprovalCondition().toUpperCase().equals("AND")
                            ? ApprovalConditionEnum.AND.name() : ApprovalConditionEnum.OR.name());
                    aclPrivilegeProcessService.createAclPrivilegeProcess(entity);
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
     * 列表
     *
     * @param request
     * @param response
     * @return
     */
    @AuthorityToken(needToken = {"meiren.acl.mbc.backend.user.privilege.index"})
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

        ApiResult apiResult;
        String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("acl/privilege/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        Map<String, String> mapPrams = new HashMap<>();
        mapPrams.put("nameOrToken", "nameOrToken");
        this.mapPrams(request, mapPrams, searchParamMap, modelAndView);
        AclUserEntity user = this.getUser(request);

        if (this.checkToken(user, privilegeAll)) {
            // 如果用户拥有所有权限管理权限 返回所有权限
            apiResult = aclPrivilegeService.searchAclPrivilege(searchParamMap, pageNum, pageSize);
        } else {
            // 查询当前用户为owner的所有权限
            searchParamMap.put("userId", user.getId());
            apiResult = aclPrivilegeService.searchAclPrivilegeJoinPrivilegeOwner(searchParamMap, pageNum, pageSize);
        }
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
            List<AclPrivilegeEntity> resultList = (List<AclPrivilegeEntity>) resultMap.get("data");
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
            result = aclPrivilegeService.deleteAclPrivilege(delMap);
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
    public ApiResult find(HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        try {
            Long id = this.checkId(request);
            result = aclPrivilegeService.findAclPrivilege(id);
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
     * @param aclPrivilegeEntity
     * @return
     */
    @RequestMapping(value = {"add", "modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response,
                         AclPrivilegeEntity aclPrivilegeEntity) {
        ApiResult result = new ApiResult();
        try {
            this.checkParamMiss(request, this.necessaryParam);
            String id = request.getParameter("id");
            AclUserEntity user = this.getUser(request);
            HashMap<String, Object> searchParamMap = new HashMap<String, Object>();
            searchParamMap.put("userId", user.getId());
            if (!StringUtils.isBlank(id)) {
                searchParamMap.put("privilegeId", Long.valueOf(id));
            }
            boolean canDo = false;
            if (!StringUtils.isBlank(id) && (aclPrivilegeOwnerService.countAclPrivilegeOwner(searchParamMap) > 0)) {// 编辑情况
                canDo = true;
            } else if (this.checkToken(user, privilegeAll)) {// 有权限管理权限
                canDo = true;
            }
            if (canDo) {
                Integer riskLevel = RequestUtil.getInteger(request, "riskLevel");
                switch (RiskLevelEnum.getByTypeValue(riskLevel)) {
                    case LOW:
                        aclPrivilegeEntity.setRiskLevel(RiskLevelEnum.LOW.typeValue);
                        break;
                    case MIDDLE:
                        aclPrivilegeEntity.setRiskLevel(RiskLevelEnum.MIDDLE.typeValue);
                        break;
                    case HIGH:
                        aclPrivilegeEntity.setRiskLevel(RiskLevelEnum.HIGH.typeValue);
                        break;
                    default:
                        throw new Exception("not find riskLevel");
                }
                Long privilegeId;
                Integer oldRiskLevel = RiskLevelEnum.NONE.typeValue;
                if (!StringUtils.isBlank(id)) {
                    oldRiskLevel = aclPrivilegeService.findAclPrivilegeById(Long.valueOf(id)).getRiskLevel();
                    Map<String, Object> paramMap = ObjectUtils.reflexToMap(aclPrivilegeEntity);
                    result = aclPrivilegeService.updateAclPrivilege(Long.valueOf(id), paramMap);
                    privilegeId = Long.valueOf(id);
                } else {
                    aclPrivilegeEntity.setCreateUserId(user.getId());
                    aclPrivilegeEntity.setStatus(PrivilegeStatusEnum.NORMAL.name());
                    result = aclPrivilegeService.createAclPrivilege(aclPrivilegeEntity);
                    privilegeId = (Long) result.getData();
                }
                // 添加风险审核流程
                result = this.addPrivilegeProcess(privilegeId, riskLevel, oldRiskLevel);
            } else {
                result.setError("您无权添加编辑该权限");
                return result;
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
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) {
        ModelAndView modelAndView = new ModelAndView();
        switch (type) {
            case "add":
                modelAndView.addObject("title", "添加权限");
                modelAndView.addObject("id", "");
                modelAndView.setViewName("acl/privilege/edit");
                break;
            case "modify":
                modelAndView.addObject("title", "编辑权限");
                modelAndView.addObject("id", RequestUtil.getInteger(request, "id"));
                modelAndView.setViewName("acl/privilege/edit");
                break;
        }
        return modelAndView;
    }

    /**
     * 添加权限审核流程
     *
     * @param privilegeId
     * @param riskLevel
     * @param oldRiskLevel
     * @return
     * @throws Exception
     */
    private ApiResult addPrivilegeProcess(Long privilegeId, Integer riskLevel, Integer oldRiskLevel) throws Exception {
        ApiResult result = new ApiResult();
        if (riskLevel.intValue() != oldRiskLevel.intValue()) {
            Map<String, Object> delParamMap = new HashMap<>();
            delParamMap.put("privilegeId", privilegeId);
            aclPrivilegeProcessService.deleteAclPrivilegeProcess(delParamMap);

            Map<String, Object> searchParamMap = new HashMap<>();
            searchParamMap.put("riskLevel", riskLevel);
            List<AclProcessModelEntity> all = (List<AclProcessModelEntity>) aclProcessModelService
                    .loadAclProcessModel(searchParamMap).getData();
            for (AclProcessModelEntity entity : all) {
                AclPrivilegeProcessEntity AclPrivilegeProcessEntity = new AclPrivilegeProcessEntity();
                AclPrivilegeProcessEntity.setPrivilegeId(privilegeId);
                AclPrivilegeProcessEntity.setProcessId(entity.getProcessId());
                AclPrivilegeProcessEntity.setHierarchyId(entity.getHierarchyId());
                AclPrivilegeProcessEntity.setApprovalCondition(entity.getApprovalCondition());
                AclPrivilegeProcessEntity.setApprovalLevel(entity.getApprovalLevel());
                result = aclPrivilegeProcessService.createAclPrivilegeProcess(AclPrivilegeProcessEntity);
            }
        }
        return result;

    }
}
