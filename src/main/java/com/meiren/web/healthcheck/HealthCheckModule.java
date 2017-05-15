package com.meiren.web.healthcheck;

import com.meiren.common.result.ApiResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/health")
public class HealthCheckModule {
	@RequestMapping(value = "check", method = RequestMethod.GET)
	@ResponseBody
	public ApiResult check() {
		return  new ApiResult();
	}

}
