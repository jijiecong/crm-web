package com.meiren.web.acl;

import com.meiren.acl.service.AclApplyService;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.form.web.interceptor.ExceptionResult;
import com.meiren.param.ApplyTokenParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/14.
 */
@Controller
@RequestMapping("/acl/apply")
public class ApplyTokenModule  {

    @Autowired
    private AclApplyService aclApplyService;

    /**
     * 权限点申请
     * @param applyTokenParam
     * @return
     */
    @RequestMapping(value = "/applyToken", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult withdrawRuleUpdate(HttpServletRequest request, ApplyTokenParam applyTokenParam) {
        String applyText=applyTokenParam.getApplyText();
        String tokeName = applyTokenParam.getTokeName();
        String[] split = tokeName.split(",");
        List<String> tokenList=new ArrayList<>();
        for(String token:split){
            if(StringUtils.isNotBlank(token)) {
                tokenList.add(token);
            }
        }
        AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
        ApiResult apiResult = aclApplyService.createApplyByToken(tokenList, user.getId(), applyText);
        return apiResult;
    }

    @RequestMapping("/noPrivilege")
    public ModelAndView noPrivilege(HttpServletRequest request){
        ModelAndView modelAndView=new ModelAndView();
        String tokenName = RequestUtil.getStringTrans(request, "tokenName");
        modelAndView.setViewName("/account/noPrivilege");
        modelAndView.addObject("tokenName",tokenName);
        return modelAndView;
    }

    @RequestMapping(value = "/exception",method = RequestMethod.POST)
    public ModelAndView exception(HttpServletRequest request, ExceptionResult exceptionResult){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("/account/error");
//        modelAndView.addObject("exception", exceptionResult.getException());
//        modelAndView.addObject("url", exceptionResult.getUrl());
        return modelAndView;
    }
}
