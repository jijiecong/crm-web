package com.meiren.common.constant;

public class BossConstant {
	// 访问php端的私秘
	private String signKey;
	// http://tw.adnonstop.com/modules/apps/task_platform/api/v1/
	private String phpApiRootUrl;
	// java服务端的秘钥
	private String secretKey;

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String getPhpApiRootUrl() {
		return phpApiRootUrl;
	}

	public void setPhpApiRootUrl(String phpApiRootUrl) {
		this.phpApiRootUrl = phpApiRootUrl;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
