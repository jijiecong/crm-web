package com.meiren.common.constant;

/**
 * Created by admin on 2017/2/28.
 * 基本的图片参数配置
 */
public class BasePicConstant {
    //质量系数
    private Integer qualityRatio;
    //锐化系数
    private Integer sharpen;
    //图片最大的大小
    private Double maxSize;
    //图片最大的高度
    private Integer maxHeight;
    //图片最大的宽度
    private Integer maxWidth;
    //图片高度
    private Integer height;
    //图片宽度
    private Integer width;

    public Integer getQualityRatio() {
        return qualityRatio;
    }

    public void setQualityRatio(Integer qualityRatio) {
        this.qualityRatio = qualityRatio;
    }

    public Integer getSharpen() {
        return sharpen;
    }

    public void setSharpen(Integer sharpen) {
        this.sharpen = sharpen;
    }

    public Double getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Double maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}
