package com.meiren.web.pic;

import com.meiren.common.constant.*;
import com.meiren.common.utils.StringUtils;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.result.ResultCode;
import com.meiren.oss.StsInfo;
import com.meiren.oss.sts.OssUtils;
import com.meiren.vo.FileUploadVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/2/28.
 * 图片上传接口
 * 增加规则 不超过512k的照片不需要进行压缩处理
 */
@Controller
@RequestMapping("/pic")
public class PicModule {

    @Autowired
    TemplatePicConstant templatePicConstant;

    @Autowired
    TemplateBannerPicConstant templateBannerPicConstant;

    @Autowired
    MissionPicConstant missionPicConstant;

    @Autowired
    MissionDetailPicConstant missionDetailPicConstant;

    @Autowired
    SharePicConstant sharePicConstant;

    @Autowired
    private OssConstant ossConstant;

    private static Double Mb = 1024.0 * 1024.0;

    private static String picFormat = "?x-oss-process=image/resize,m_mfit,h_%d,w_%d/format,jpg/sharpen,%d/quality,Q_%d";

    private static String noConvertFormat = "?x-oss-process=image/resize,m_mfit,h_%d,w_%d/sharpen,%d/quality,Q_%d";

    /**
     * H5模板正文图片
     *
     * @param files
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "templatePic", method = RequestMethod.POST)
    public ApiResult templatePic(@RequestParam("files") ArrayList<MultipartFile> files) {

        ApiResult checkPic = checkPic(files.get(0), templatePicConstant);
        if (!checkPic.isSuccess()) {
            return checkPic;
        }
        InputStream inputStream = (InputStream) checkPic.getData();
        ApiResult apiResult = uploadFile(files.get(0), inputStream, templatePicConstant);

        List<FileUploadVO> fileUploadVOList = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String path = (String) apiResult.getData();
            FileUploadVO fileUploadVO = new FileUploadVO();
            fileUploadVO.setUrl(path);
            fileUploadVO.setThumbnailUrl(path);
            fileUploadVO.setName(multipartFile.getName());
            fileUploadVO.setSize(multipartFile.getSize());
            fileUploadVO.setImage(multipartFile.getContentType().contains("image"));
            fileUploadVOList.add(fileUploadVO);
        }
        apiResult.setData(fileUploadVOList);
        return apiResult;
    }


    /**
     * H5模板底部Banner图片
     *
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "templateBannerPic", method = RequestMethod.POST)
    public ApiResult templateBannerPic(@RequestParam("file") MultipartFile file) {
        ApiResult checkPic = checkPic(file, templateBannerPicConstant);
        if (!checkPic.isSuccess()) {
            return checkPic;
        }
        InputStream inputStream = (InputStream) checkPic.getData();
        ApiResult apiResult = uploadFile(file, inputStream, templateBannerPicConstant);


        return apiResult;
    }


    /**
     * 任务大厅图片
     *
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "missionPic", method = RequestMethod.POST)
    public ApiResult missionPic(@RequestParam("file") MultipartFile file) {

        ApiResult checkPic = checkPic(file, missionPicConstant);
        if (!checkPic.isSuccess()) {
            return checkPic;
        }

        InputStream inputStream = (InputStream) checkPic.getData();
        ApiResult apiResult = uploadFile(file, inputStream, missionPicConstant);

        return apiResult;
    }

    /**
     * 任务详情图片
     *
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "missionDetailPic", method = RequestMethod.POST)
    public ApiResult missionDetailPic(@RequestParam("file") MultipartFile file) {
        ApiResult checkPic = checkPic(file, missionDetailPicConstant);
        if (!checkPic.isSuccess()) {
            return checkPic;
        }
        InputStream inputStream = (InputStream) checkPic.getData();
        ApiResult apiResult = uploadFile(file, inputStream, missionDetailPicConstant);
        return apiResult;
    }

    /**
     * 任务分享Icon
     *
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "missionSharePic", method = RequestMethod.POST)
    public ApiResult missionSharePic(@RequestParam("file") MultipartFile file) {
        ApiResult checkPic = checkPic(file, sharePicConstant);
        if (!checkPic.isSuccess()) {
            return checkPic;
        }
        InputStream inputStream = (InputStream) checkPic.getData();
        ApiResult apiResult = uploadFile(file, inputStream, sharePicConstant);
        return apiResult;
    }


    /**
     * 图片格式判断 大小 宽高 是否满足要求
     *
     * @param file
     * @param basePicConstant
     * @return
     */
    public ApiResult checkPic(MultipartFile file, BasePicConstant basePicConstant) {
        ApiResult apiResult = new ApiResult();
        if (!file.getContentType().contains("image")) {
            apiResult.setError(ResultCode.CONTENT_TYPE_NOT_PIC.getCode(),ResultCode.CONTENT_TYPE_NOT_PIC.getMessage());
            return apiResult;
        }
        //校验大小
        double ratio = file.getSize() / Mb;
        if (ratio >= basePicConstant.getMaxSize()) {
            apiResult.setError(ResultCode.PIC_SIZE_EXCEED.getCode(),ResultCode.PIC_SIZE_EXCEED.getMessage());
            return apiResult;
        }
        //比较图片的像素是否在规定的范围内
        try {
            InputStream inputStream = file.getInputStream();
            byte[] buffer = IOUtils.toByteArray(inputStream);
            Image src = ImageIO.read(new ByteArrayInputStream(buffer));
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            if (width > basePicConstant.getMaxWidth() || height > basePicConstant.getMaxHeight()) {
                apiResult.setError(ResultCode.PIC_HEIGHT_WIDTH_EXCEED.getCode(),ResultCode.PIC_HEIGHT_WIDTH_EXCEED.getMessage());
                return apiResult;
            }
            apiResult.setData(new ByteArrayInputStream(buffer));
        } catch (IOException e) {
            e.printStackTrace();
            apiResult.setError(ResultCode.SYSTEM_BUSY.getCode(),ResultCode.SYSTEM_BUSY.getMessage());
        }
        return apiResult;
    }

    public ApiResult uploadFile(MultipartFile file, InputStream inputStream, BasePicConstant basePicConstant) {
        ApiResult apiResult = new ApiResult();
        StsInfo stsInfo = OssUtils.getStsInfo(Long.valueOf(ossConstant.getUserId()), ossConstant.getOssSecretKey(),
                ossConstant.getOssStsUrl());
        if (stsInfo == null) {
            apiResult.setError(ResultCode.MISSION_GET_OSS_STS_FAILED.getCode(),ResultCode.MISSION_GET_OSS_STS_FAILED.getMessage());
        }
        String key = OssUtils.uploadPicByInputStream(stsInfo, file, inputStream);
        if (StringUtils.isBlank(key)) {
            apiResult.setError(ResultCode.OSS_UPLOAD_FAILED.getCode(),ResultCode.OSS_UPLOAD_FAILED.getMessage());
            return apiResult;
        }
        key=OssUtils.enableCdn(stsInfo.getBucketName(),stsInfo.getEndPoint(),key,ossConstant.getCdnPath());
        //图片参数设置 按照短边 定必缩放 锐化系统90 绝对质量系数90
        //?x-oss-process=image/resize,m_mfit,h_1024,w_640/format,jpg/sharpen,90/quality,Q_90
        double ratio = file.getSize() / Mb;
        //大于512K才会设置压缩
        if (ratio > 0.5) {
            String format = null;
            if (basePicConstant instanceof TemplateBannerPicConstant) {
                format = String.format(noConvertFormat, basePicConstant.getHeight(), basePicConstant.getWidth(), basePicConstant.getSharpen(), basePicConstant.getQualityRatio());
            } else {
                format = String.format(picFormat, basePicConstant.getHeight(), basePicConstant.getWidth(), basePicConstant.getSharpen(), basePicConstant.getQualityRatio());
            }
            apiResult.setData(key.concat(format));
            return apiResult;
        } else {
            apiResult.setData(key);
            return apiResult;
        }
    }
}
