package com.meiren.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.meiren.common.utils.StringUtils;
import com.meiren.exception.OpenApiException;
import com.meiren.http.NetWork;
import com.meiren.oss.StsInfo;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 该Oss工具类为商城改版，仅为解决图片异步上传时中文名乱码，
 * 一般上传请使用 com.meiren.oss.sts.OssUtils
 * @author wangdl
 */
public class OssMallUtils {
	// 请求获取访问STS相关信息
	public static String cdnPath = "oss.adnonstop.com";

	private static Double Mb = 1024.0 * 1024.0;

	private static Double maxSize = 3.0;

	public static StsInfo getStsInfo(Long userId, String secretKey, String url) {
		try {
			String contentType = "application/x-www-form-urlencoded";
			NameValuePair[] data1 = {
					new NameValuePair("user_id", userId.toString()),
					new NameValuePair("sign", StringUtils.md5(secretKey
							+ userId.toString())) };
			//
			String result = NetWork.postRequest(url, data1, contentType);
			JSONObject jsonObject = JSON.parseObject(result);
			Integer code = (Integer) jsonObject.get("code");
			if (code.intValue() != 0) {
				return null;
			} else {
				String message = (String) jsonObject.get("message");
				JSONObject data = (JSONObject) jsonObject.get("data");
				String accessKeyId = (String) data.get("access_key_id");
				String accessKeySecret = (String) data.get("access_key_secret");
				String securityToken = (String) data.get("security_token");
				String bucketName = (String) data.get("bucket_name");
				String endpoint = (String) data.get("endpoint");
				String imgEndpoint = (String) data.get("img_endpoint");
				String expireIn = (String) data.get("expire_in");
				StsInfo stsInfo = new StsInfo();
				stsInfo.setAccessKeyId(accessKeyId);
				stsInfo.setAccessKeySecret(accessKeySecret);
				stsInfo.setSecurityToken(securityToken);
				stsInfo.setBucketName(bucketName);
				stsInfo.setEndPoint(endpoint);
				stsInfo.setImgEndPoint(imgEndpoint);
				stsInfo.setExpireIn(expireIn);
				return stsInfo;
			}
		} catch (OpenApiException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验大小，上传图片不能大于3M
	 * @param file
	 * @return
	 */
	public static boolean checkSize(MultipartFile file){
		//校验大小
        double ratio = file.getSize() / Mb;
        if(ratio > maxSize){
        	return false;
        }else{
        	return true;
        }
	}

	/**
	 * 图片上传
	 *
	 * @param stsInfo
	 * @param file
	 * @param inputStream
	 * @return
	 */
	public static String uploadPicByInputStream(StsInfo stsInfo,
			MultipartFile file, InputStream inputStream) {
		try {
			OSSClient client = new OSSClient(stsInfo.getEndPoint(),
					stsInfo.getAccessKeyId(), stsInfo.getAccessKeySecret(),
					stsInfo.getSecurityToken());
			String uuid = UUID.randomUUID().toString();
			String originalFilename = new String(file.getOriginalFilename()
					.getBytes(), "UTF-8").replaceAll(" ", "-");
			ObjectMetadata objectMeta = new ObjectMetadata();
			objectMeta.setContentLength(file.getSize());
			String fullName = uuid.concat(originalFilename);
			PutObjectResult putObjectResult = client.putObject(
					stsInfo.getBucketName(), fullName, inputStream);
			String completeUrl = getCompleteUrl(stsInfo, fullName);
			return completeUrl;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 图片上传2(自定义文件名)
	 *
	 * @param stsInfo
	 * @param file
	 * @param inputStream
	 * @return
	 */
	public static String uploadPicByInputStream(StsInfo stsInfo,
			MultipartFile file, InputStream inputStream,String fileName) {
		try {
			OSSClient client = new OSSClient(stsInfo.getEndPoint(),
					stsInfo.getAccessKeyId(), stsInfo.getAccessKeySecret(),
					stsInfo.getSecurityToken());
			String uuid = UUID.randomUUID().toString();
			String originalFilename = new String(file.getOriginalFilename()
					.getBytes(), "UTF-8").replaceAll(" ", "-");
			String ext=originalFilename.substring(originalFilename.lastIndexOf("."));
			ObjectMetadata objectMeta = new ObjectMetadata();
			objectMeta.setContentLength(file.getSize());
			String fullName = uuid.concat(fileName + ext);
			PutObjectResult putObjectResult = client.putObject(
					stsInfo.getBucketName(), fullName, inputStream);
			String completeUrl = getCompleteUrl(stsInfo, fullName);
			return completeUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getCompleteUrl(StsInfo stsInfo, String fullName) {
		String endPoint = stsInfo.getEndPoint();
		String https = "http://";
		String completeUrl = https.concat(stsInfo.getBucketName()).concat(".")
				.concat(endPoint).concat("/").concat(fullName);
		return completeUrl;
	}

	public static String enableCdn(String bucket, String endPoint, String url,
			String cdnPath) {
		String replaceString = bucket.concat(".").concat(endPoint);
		String cdnString = bucket.concat("-").concat(cdnPath);
		return url.replaceFirst(replaceString, cdnString);
	}

}
