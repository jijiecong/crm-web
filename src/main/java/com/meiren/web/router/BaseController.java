package com.meiren.web.router;

import org.springframework.beans.factory.annotation.Autowired;

import com.meiren.api.service.ApiRouterService;
import com.meiren.common.constant.BossConstant;
import com.meiren.mission.service.LoginWhiteListService;

public class BaseController {
	@Autowired
	protected BossConstant bossConstant;

	@Autowired
	protected LoginWhiteListService loginWhiteListService;

	@Autowired
	protected ApiRouterService apiRouterService;

	public static final int DEFAULT_ROWS = 10;
}
