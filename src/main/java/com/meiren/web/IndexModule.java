package com.meiren.web;

import com.meiren.common.result.VueResult;
import com.meiren.utils.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexModule {

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    @ResponseBody
    public VueResult getUserInfo(HttpServletRequest request) {
        return new VueResult(RequestUtil.getSessionUser(request));
    }

}

