package com.meiren.web.acl;

import com.meiren.acl.enums.PrivilegeStatusEnum;
import com.meiren.acl.service.*;
import com.meiren.acl.service.entity.AclPrivilegeEntity;
import com.meiren.acl.service.entity.AclPrivilegeOwnerEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.vo.SessionUserVO;
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
@RequestMapping("/acl/privilegeOut")
public class PrivilegeOutModule extends BaseController {

    @Autowired
    protected AclPrivilegeService aclPrivilegeService;
    @Autowired
    protected AclPrivilegeOwnerService aclPrivilegeOwnerService;
    private String[] necessaryParam = {
            "name",
            "token",
    };

    /**
     * 列表
     * TODO jijc
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

        modelAndView.setViewName("acl/privilegeOut/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        Map<String, String> mapPrams = new HashMap<>();
        mapPrams.put("nameOrToken", "nameOrToken");
        this.mapPrams(request, mapPrams, searchParamMap, modelAndView);
        ApiResult apiResult = aclPrivilegeService.searchAclPrivilege(searchParamMap, pageNum, pageSize);

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
        modelAndView.addObject("userId", this.getUser(request).getId());
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);

        return modelAndView;

    }

    /**
     * 添加
     * TODO jijc
     *
     * @param request
     * @param response
     * @param aclPrivilegeEntity
     * @return
     */
    @RequestMapping(value = {"modify"}, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response, AclPrivilegeEntity aclPrivilegeEntity) {
        ApiResult result = new ApiResult();
        try {
            this.checkParamMiss(request, this.necessaryParam);
            SessionUserVO user = this.getUser(request);
            if (user != null) {
                aclPrivilegeEntity.setCreateUserId(user.getId());
            }
            aclPrivilegeEntity.setStatus(PrivilegeStatusEnum.NORMAL.name());
            Long privilegeId = (Long) aclPrivilegeService.createAclPrivilege(aclPrivilegeEntity).getData();

            //设置owner，另外添加
            AclPrivilegeOwnerEntity aclPrivilegeOwnerEntity = new AclPrivilegeOwnerEntity();
            String userIds = RequestUtil.getString(request, "userIds");
            if (!StringUtils.isBlank(userIds)) {
                String[] useridArr = userIds.split(",");
                for (int i = 0; i < useridArr.length; i++) {
                    aclPrivilegeOwnerEntity.setUserId(Long.parseLong(useridArr[i]));
                    aclPrivilegeOwnerEntity.setPrivilegeId(privilegeId);
                    result = aclPrivilegeOwnerService.createAclPrivilegeOwner(aclPrivilegeOwnerEntity);
                }
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
    @RequestMapping(value = "/add/privilege", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView goTo(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        SessionUserVO user = this.getUser(request);
        modelAndView.addObject("userId", user.getId());
        modelAndView.setViewName("acl/privilegeOut/edit");
        return modelAndView;
    }

}
