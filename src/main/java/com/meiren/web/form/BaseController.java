package com.meiren.web.form;

import org.springframework.beans.factory.annotation.Autowired;

import com.meiren.common.constant.BossConstant;
import com.meiren.mission.service.LoginWhiteListService;

public class BaseController {

	@Autowired
	protected BossConstant bossConstant;

	@Autowired
	protected LoginWhiteListService loginWhiteListService;

	public static final int DEFAULT_ROWS = 10;

}
