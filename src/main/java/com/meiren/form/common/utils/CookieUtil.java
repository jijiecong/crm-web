package com.meiren.form.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.common.utils.Base64;
import com.meiren.common.utils.StringUtils;
import com.meiren.exception.OpenApiException;
import com.meiren.http.NetWork;
import org.apache.commons.httpclient.NameValuePair;

import com.alibaba.fastjson.JSON;
import com.meiren.common.constant.BossConstant;

public class CookieUtil {
	// 保存cookie时的cookieName
	private final static String cookieName = "meiren_account";
	// 加密cookie时的网站自定码
	private final static String webKey = "meiren20161220";
	// 设置cookie有效期是两个星期，根据需要自定义
	private final static long cookieMaxAge = 60 * 60 * 24 * 7 * 2;

	public static void saveCookie(AclUserEntity user, HttpServletResponse response) {

		// cookie的有效期
		long validTime = System.currentTimeMillis() + (cookieMaxAge * 5000);

		// MD5加密用户详细信息

		String cookieValueWithMd5 = StringUtils.md5(user.getMobile() + ":"
				+ user.getId()

				+ ":" + validTime + ":" + webKey);

		// 将要被保存的完整的Cookie值

		String cookieValue = user.getMobile() + ":" + validTime + ":"
				+ user.getId() + ":" + user.getUserName() +":" + cookieValueWithMd5;

		// 再一次对Cookie的值进行BASE64编码
		String cookieValueBase64 = Base64.encode(cookieValue
				.getBytes());

		// 开始保存Cookie

		Cookie cookie = new Cookie(cookieName, cookieValueBase64);

		// 存两年(这个值应该大于或等于validTime)

		cookie.setMaxAge(60 * 60 * 24 * 365 * 2);

		// cookie有效路径是网站根目录

		cookie.setPath("/");

		// 向客户端写入

		response.addCookie(cookie);

	}

	// 读取Cookie,自动完成登陆操作----------------------------------------------------------------
	// 在Filter程序中调用该方法,见AutoLogonFilter.java
	public static Map<String, Object> readCookieAndLogin(
			HttpServletRequest request, HttpServletResponse response,
			AclUserEntity user, BossConstant bossConstant) throws IOException,
			ServletException, UnsupportedEncodingException {
		String contentType = "application/x-www-form-urlencoded";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 根据cookieName取cookieValue

		Cookie cookies[] = request.getCookies();

		String cookieValue = null;

		if (cookies != null) {

			for (int i = 0; i < cookies.length; i++) {

				if (cookieName.equals(cookies[i].getName())) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		}

		// 如果cookieValue为空,进行登录验证，否则直接进入cookie验证
		HttpSession session = request.getSession(true);
		if (cookieValue == null) {
			String mobile = request.getParameter("mobile");
			String password = request.getParameter("password");
			String md5Str = StringUtils.md5(bossConstant.getSignKey() + mobile);
			String result = null;

			try {
				NameValuePair[] data = { new NameValuePair("mobile", mobile),
						new NameValuePair("password", password),
						new NameValuePair("sign", md5Str) };
				result = NetWork.postRequest(bossConstant.getPhpApiRootUrl()
								.concat("member_check_account_pwd_api.php"), data,
						contentType);
				resultMap = JSON.parseObject(result, Map.class);
			} catch (OpenApiException e) {
				resultMap.put("message", e.getMessage());
				resultMap.put("code", -80);
			}

			if (resultMap.get("code").toString().equals("0")) {
				Map<String, Object> userMap = (Map<String, Object>) resultMap
						.get("data");
				user.setId(Long.valueOf(userMap.get("user_id").toString()));
				NameValuePair[] queryData = {
						new NameValuePair("user_id", String.valueOf(user
								.getId())),
						new NameValuePair("sign", StringUtils.md5(bossConstant
								.getSignKey()
								+ userMap.get("user_id").toString())) };

				try {
					result = NetWork.postRequest(
							bossConstant.getPhpApiRootUrl().concat(
									"member_get_user_info_by_user_id_api.php"),
							queryData, contentType);
					Map<String, Object> queryResultMap = JSON.parseObject(
							result, Map.class);
					Map<String, Object> userInfoMap = (Map<String, Object>) queryResultMap
							.get("data");
					user.setUserName(userInfoMap.get("nickname").toString());
				} catch (OpenApiException e) {
					resultMap.put("message", e.getMessage());
					resultMap.put("code", -80);
				}
				resultMap = JSON.parseObject(result, Map.class);
				saveCookie(user, response);
				session.setAttribute("user", user);
			}
			return resultMap;
		}

		// 如果cookieValue不为空,才执行下面的代码

		// 先得到的CookieValue进行Base64解码

		String cookieValueAfterDecode = new String(Base64.decode(cookieValue),
				"utf-8");

		// 对解码后的值进行分拆,得到一个数组,如果数组长度不为3,就是非法登陆

		String cookieValues[] = cookieValueAfterDecode.split(":");

		if (cookieValues.length != 4) {
			resultMap.put("message", "illegal login");
			resultMap.put("code", -80);
			return resultMap;

		}

		// 判断是否在有效期内,过期就删除Cookie

		long validTimeInCookie = new Long(cookieValues[1]);

		if (validTimeInCookie < System.currentTimeMillis()) {

			// 删除Cookie

			clearCookie(response);

			resultMap.put("message", "cookie is expire ,please login again");
			resultMap.put("code", -80);
			return resultMap;
		}

		// 如果user返回不为空,就取出密码,使用用户名+密码+有效时间+ webSiteKey进行MD5加密

		String md5ValueInCookie = cookieValues[3];

		String md5ValueFromUser = StringUtils.md5(user.getMobile() + ":"
				+ user.getPassword()

				+ ":" + validTimeInCookie + ":" + webKey);

		// 将结果与Cookie中的MD5码相比较,如果相同,写入Session,自动登陆成功,并继续用户请求

		if (md5ValueFromUser.equals(md5ValueInCookie)) {
			// 去除userID注入
			String userId = cookieValues[2];

			NameValuePair[] queryData = {
					new NameValuePair("user_id", userId),
					new NameValuePair("sign", StringUtils.md5(bossConstant
							.getSignKey() + userId)) };

			try {
				String result = NetWork.postRequest(
						bossConstant.getPhpApiRootUrl().concat(
								"member_get_user_info_by_user_id_api.php"),
						queryData, contentType);
				Map<String, Object> queryResultMap = JSON.parseObject(result,
						Map.class);
				Map<String, Object> userInfoMap = (Map<String, Object>) queryResultMap
						.get("data");
				user.setUserName(userInfoMap.get("nickname").toString());
			} catch (OpenApiException e) {
				resultMap.put("message", e.getMessage());
				resultMap.put("code", -80);
			}

			user.setId(Long.valueOf(userId));
			session.setAttribute("user", user);
			resultMap.put("code", 0);
			resultMap.put("data", Long.valueOf(userId));
			// 将结果与Cookie中的MD5码相比较,如果相同,写入Session,自动登陆成功,并继续用户请求
		}
		if (resultMap.size() == 0) {
			clearCookie(response);
		}
		return resultMap;

	}

	// 用户注销时,清除Cookie,在需要时可随时调用-----------------------------------------------------

	public static void clearCookie(HttpServletResponse response) {

		Cookie cookie = new Cookie(cookieName, null);

		cookie.setMaxAge(0);

		cookie.setPath("/");

		response.addCookie(cookie);

	}

	public static String getCookieName() {
		return cookieName;
	}

	public static String getWebKey() {
		return webKey;
	}
}
