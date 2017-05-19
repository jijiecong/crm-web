package com.meiren.web.acl;

import com.meiren.acl.enums.ApplyTypeEnum;
import com.meiren.acl.service.AclApplyService;
import com.meiren.acl.service.entity.AclApplyEntity;
import com.meiren.common.annotation.AuthorityToken;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.StringUtils;
import com.meiren.vo.SessionUserVO;
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

@AuthorityToken(needToken = {"meiren.acl.mbc.backend.acl.apply.index"})
@Controller
@RequestMapping("/acl/apply")
public class ApplyModule extends BaseController {

    @Autowired
    protected AclApplyService aclApplyService;

    /**
     * 查询当前登录用户申请列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("acl/apply/index");
        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int pageSize = DEFAULT_ROWS;

        Map<String, Object> searchParamMap = new HashMap<>();
        if (!StringUtils.isBlank(request.getParameter("searchTitle"))) {
            searchParamMap.put("title", request.getParameter("searchTitle"));
            modelAndView.addObject("searchTitle", request.getParameter("searchTitle"));
        }

        SessionUserVO user = this.getUser(request);
        if (user.getId() != null) {
            searchParamMap.put("userId", user.getId());
        } else {
            modelAndView.addObject("message", "没有登录不能使用此功能");
            return modelAndView;
        }

        ApiResult apiResult = aclApplyService.searchAclApply(searchParamMap, pageNum, pageSize);

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
            List<AclApplyEntity> resultList = (List<AclApplyEntity>) resultMap.get("data");
            modelAndView.addObject("basicVOList", resultList);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("pageSize", pageSize);

        return modelAndView;

    }

    /**
     * 查找单个申请
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
            result = aclApplyService.findAclApply(id);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 创建角色申请、权限申请
     * @param request
     * @param response
     * @param aclApplyEntity
     * @return
     */
    @RequestMapping(value = "apply/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult add(HttpServletRequest request, HttpServletResponse response, AclApplyEntity aclApplyEntity, @PathVariable String type) {
        ApiResult result = new ApiResult();
        try {
            SessionUserVO user = this.getUser(request);
            aclApplyEntity.setUserId(user.getId());
            switch (type) {
                case "add":
                    aclApplyEntity.setApplyType(ApplyTypeEnum.APPLY_PRIVILEGE.name());  //权限申请111
                    break;
                case "addRole":
                    aclApplyEntity.setApplyType(ApplyTypeEnum.APPLY_ROLE.name());       //角色申请
                    break;
                default:
                    throw new Exception("type not find");
            }
            result = aclApplyService.createAclApply(aclApplyEntity);
        } catch (Exception e) {
            result.setError(e.getMessage());
            return result;
        }
        return result;
    }

}
