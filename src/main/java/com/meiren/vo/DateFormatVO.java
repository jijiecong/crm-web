package com.meiren.vo;

import java.io.Serializable;

/**
 * @author XiaoLuo
 * @ClassName: DateFormatVO
 * @Description: 时间格式多个值返回
 * @date 2018/1/11 13:47
 */
public class DateFormatVO implements Serializable{

    private Long timeEnd;
    private Long timeStart;
    private String dateFormat;

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
