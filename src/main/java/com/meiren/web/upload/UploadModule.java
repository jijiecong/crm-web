package com.meiren.web.upload;

import com.meiren.common.constants.OssConstant;
import com.meiren.common.result.VueResult;
import com.meiren.oss.StsInfo;
import com.meiren.utils.OssMallUtils;
import com.meiren.web.acl.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

@Controller
@RequestMapping("{uuid}/upload")
@ResponseBody
public class UploadModule extends BaseController {
    @Resource
    private OssConstant ossConstant;

    @RequestMapping(value = "imageUpload", method = RequestMethod.POST)
    public VueResult imageUpload(HttpServletRequest request,@RequestParam("file") MultipartFile[] files) throws IOException {
        VueResult result = new VueResult();
        Random rd = new Random();
        for(MultipartFile file : files){
            file.transferTo(new File("F://test/"+rd.nextInt(100)+".jpg"));
        }
        return  result;
    }

    /**
     * 上传头像
     *
     * @param
     * @return
     */
    @RequestMapping(value = "headUpload", method = RequestMethod.POST)
    public VueResult headUpload(@RequestParam("file") MultipartFile file) throws IOException {
        VueResult result = new VueResult();
        String upload = upload(file);
//        System.out.println(upload);
        result.setData(upload(file));
        return result;
    }

    /**
     * 上传图片代码
     **/
    private String upload(MultipartFile file) {
        if (!file.getContentType().contains("image")) {
            return "failed";
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            return "failed";
        }
        StsInfo stsInfo = OssMallUtils
            .getStsInfo(Long.valueOf(ossConstant.getUserId()), ossConstant.getOssSecretKey(), ossConstant.getOssStsUrl());
        String businessImage = OssMallUtils.uploadPicByInputStream(stsInfo, file, inputStream, "ocp");
        return businessImage;
    }
}
