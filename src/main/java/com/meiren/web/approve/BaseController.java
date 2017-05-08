package com.meiren.web.approve;

import com.alibaba.fastjson.JSONObject;
import com.meiren.api.service.ApiRouterService;
import com.meiren.common.utils.StringUtils;
import com.meiren.common.constant.BossConstant;
import com.meiren.mission.service.LoginWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class BaseController {
//	@Autowired
//	protected BossConstant bossConstant;
//
//	@Autowired
//	protected LoginWhiteListService loginWhiteListService;
//
//	@Autowired
//	protected ApiRouterService apiRouterService;

	public static final int DEFAULT_ROWS = 10;

	//test json
	public static void main(String[] args) {
		JSONObject jsonObject=new JSONObject();
//		jsonObject.put("name","麻子");
//		jsonObject.put("IDnum","123456789");
//		List<String> image=new ArrayList<>(2);
//		image.add("http://www.baidu.com/img/bdlogo.png");
//		image.add("http://www.baidu.com/img/bdlogo.png");
//		jsonObject.put("image",image);
		String IDInfo=jsonObject.toJSONString();
		System.out.print("{}".equals(IDInfo));
	}
}
