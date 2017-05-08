package com.meiren.web.validator;

import com.meiren.common.constant.BossConstant;
import com.meiren.mission.service.LoginWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

	@Autowired
	protected BossConstant bossConstant;

	@Autowired
	protected LoginWhiteListService loginWhiteListService;



}
