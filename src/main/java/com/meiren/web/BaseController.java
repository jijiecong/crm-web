package com.meiren.web;

import com.meiren.utils.RequestUtil;
import com.meiren.vo.SessionUserVO;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    static final Integer DEFAULT_ROWS = 10;

    SessionUserVO getUser(HttpServletRequest request) {
        return RequestUtil.getSessionUser(request);
    }

}
