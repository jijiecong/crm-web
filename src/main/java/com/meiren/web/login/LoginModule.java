package com.meiren.web.login;

import com.alibaba.fastjson.JSON;
import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.result.ApiResult;
import com.meiren.common.utils.StringUtils;
import com.meiren.exception.OpenApiException;
import com.meiren.http.NetWork;
import com.meiren.sso.web.SsoHelper;
import com.meiren.web.form.BaseController;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/account")
public class LoginModule extends BaseController {

	@Autowired
	private SsoHelper ssoHelper;
	@RequestMapping("/login")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		// 检验mobile是否存在，如果不存在登陆验证页
		if (StringUtils.isBlank(request.getParameter("mobile"))
				|| StringUtils.isBlank(request.getParameter("password"))) {
			modelAndView.setViewName("/account/login");
			return modelAndView;
		}
		// 验证用户名密码是否有效
		ApiResult result = ssoHelper.validateAccount(request, response);

		if (result.isSuccess()) {
			AclUserEntity user = (AclUserEntity) request.getSession().getAttribute("user");
			modelAndView.addObject("userModel", user);
			//登录成功后,重定向到首页
			modelAndView.setViewName("/index");
			return modelAndView;
		}
//		modelAndView.addObject("validateMap", resultMap);
		modelAndView.setViewName("/account/login");
		return modelAndView;

	}

//	@RequestMapping("/register")
//	public ModelAndView reg(HttpServletRequest request,
//			HttpServletResponse response) {
//		String md5Str = StringUtils.md5(bossConstant.getSignKey()
//				+ request.getParameter("mobile"));
//		Map<String, Object> resultMap = null;
//		ModelAndView modelAndView = new ModelAndView();
//
//		modelAndView.setViewName("/account/login");
//
//		if (StringUtils.isBlank(request.getParameter("userName"))) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", "userName is missing");
//			resultMap.put("code", -80);
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//		if (StringUtils.isBlank(request.getParameter("password"))) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", "password is missing");
//			resultMap.put("code", -80);
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//		if (StringUtils.isBlank(request.getParameter("sex"))) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", "sex is missing");
//			resultMap.put("code", -80);
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//		if (StringUtils.isBlank(request.getParameter("mobile"))) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", "mobile is missing");
//			resultMap.put("code", -80);
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//		if (StringUtils.isBlank(request.getParameter("rpassword"))) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", "repassword is missing");
//			resultMap.put("code", -80);
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//		if (!request.getParameter("rpassword").trim()
//				.equals(request.getParameter("password"))) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", "password and repassword is different!");
//			resultMap.put("code", -80);
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//
//		if (resultMap != null) {
//			modelAndView.addObject("validateMap", resultMap);
//			return modelAndView;
//		}
//
//		NameValuePair[] data = {
//				new NameValuePair("nickname", request.getParameter("userName")),
//				new NameValuePair("pwd", request.getParameter("password")),
//				new NameValuePair("sex", request.getParameter("sex")),
//				new NameValuePair("mobile", request.getParameter("mobile")),
//				new NameValuePair("sign", md5Str) };
//
//		String contentType = "application/x-www-form-urlencoded";
//		String result = null;
//		try {
//			result = NetWork
//					.postRequest(
//							bossConstant.getPhpApiRootUrl().concat(
//									"member_create_account_api.php"), data,
//							contentType);
//			resultMap = JSON.parseObject(result, Map.class);
//		} catch (OpenApiException e) {
//			resultMap = new HashMap<String, Object>();
//			resultMap.put("message", e.getMessage());
//			resultMap.put("code", -80);
//		}
//		if (resultMap.get("code").toString().equals("0")) {
//			modelAndView.addObject("mobile", request.getParameter("mobile"));
//			modelAndView
//					.addObject("password", request.getParameter("password"));
//			LoginWhiteListEntity loginWhiteListEntity = new LoginWhiteListEntity();
//			Map<String, Object> userMap = (Map<String, Object>) resultMap
//					.get("data");
//			loginWhiteListEntity.setUserId(Long.valueOf(userMap.get("user_id")
//					.toString()));
//			loginWhiteListEntity.setMobile(request.getParameter("mobile"));
//			ApiResult apiResult = loginWhiteListService
//					.createLoginWhiteList(loginWhiteListEntity);
//			if (!apiResult.isSuccess()) {
//				resultMap = new HashMap<String, Object>();
//				resultMap.put("message", apiResult.getError()
//						+ "请联系管理员手动录入白名单！");
//				resultMap.put("code", -80);
//			}
//		}
//		modelAndView.addObject("validateMap", resultMap);
//		return modelAndView;
//
//	}
//
	public static void main(String[] args) {
		String url = "http://tw.adnonstop.com/modules/apps/task_platform/api/v1/member_check_account_pwd_api.php";
		String url1 = "http://tw.adnonstop.com/modules/apps/task_platform/api/v1/__test_api.php";
		String url2 = "http://tw.adnonstop.com/modules/apps/task_platform/api/v1/member_create_account_api.php";
		String url3 = "http://tw.adnonstop.com/modules/apps/task_platform/api/v1/member_get_user_info_by_user_id_api.php";
		String url4="http://tw.adnonstop.com/modules/apps/task_platform/api/v1/credit_beauty_credit_income_task_platform_api.php";
		String srcretKey = "1ae4mnp5";
		String md5Str = StringUtils.md5(srcretKey + "17682310216");

		String result;
		try {
			NameValuePair[] data = {
					new NameValuePair("nickname", "姚嘉晨"),
					new NameValuePair("pwd", "123456"),
					new NameValuePair("sex", "男"),
					new NameValuePair("mobile", "17682310216"),
					new NameValuePair("sign", md5Str) };
			// 7000135
			String contentType = "application/x-www-form-urlencoded";
			result = NetWork.postRequest(url2, data, contentType);
			Map<String, Object> resultMap = JSON.parseObject(result, Map.class);
			Map<String, Object> userMap = (Map<String, Object>) resultMap
					.get("data");
			System.out.println(userMap.get("user_id"));
			System.out.println(result);

			NameValuePair[] data1 = {
					new NameValuePair("user_id", "7000135"),
					new NameValuePair("sign", StringUtils.md5(srcretKey
							+ "7000135")) };
			//
			result = NetWork.postRequest(url3, data1, contentType);
			System.out.println(result);
			System.out.println(result);

		} catch (OpenApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
