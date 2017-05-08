package com.meiren.form.web.interceptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.meiren.common.utils.StringUtils;
import com.meiren.common.utils.UriBroker;

public class WebInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object arg2) throws Exception {
		ServletContext context = request.getSession().getServletContext();
		String rootLinkHolder = context.getInitParameter("Uri-PlaceHolder");
		if (StringUtils.isBlank(rootLinkHolder)) {
			rootLinkHolder = "webBasePath";
		}
		request.setAttribute(rootLinkHolder, new UriBroker(request, response));
		return true;
	}
}
