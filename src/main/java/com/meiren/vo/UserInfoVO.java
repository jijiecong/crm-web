package com.meiren.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author XiaoLuo
 * @ClassName: UserInfoVO
 * @Description:
 * @date 2017/12/22 11:55
 */
public class UserInfoVO implements Serializable{

    private Long userId;
    //用户id
    private String userName;
    //昵称    示例：zhzhyljp
    private String nickname;
    //真实姓名  示例：仇冰媛
    private String realname;
    //性别（男，女） 示例：男
    private String sex;
    //注册时间  注册时间：1455610890
    private Date regTime;
    //注册ip
    private Long regIp;
    //生日  示例：2017年12月22日
    private String birthday;
    //地区（省份+城市+地区）
    private String locationInfo;
    //用头像
    private String userIcon;
    //手机号码区号
    private Integer zoneNum;
    //手机号码
    private String mobile;
    //注册来源
    private String registerSource;
    //修改时间
    private Date updateTime;
    //注册的projectName
    private String registerProjectName;
    //登录过的projectName
    private String loginedProjectName;//工程名称，用逗号隔开

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public Long getRegIp() {
        return regIp;
    }

    public void setRegIp(Long regIp) {
        this.regIp = regIp;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public Integer getZoneNum() {
        return zoneNum;
    }

    public void setZoneNum(Integer zoneNum) {
        this.zoneNum = zoneNum;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRegisterProjectName() {
        return registerProjectName;
    }

    public void setRegisterProjectName(String registerProjectName) {
        this.registerProjectName = registerProjectName;
    }

    public String getLoginedProjectName() {
        return loginedProjectName;
    }

    public void setLoginedProjectName(String loginedProjectName) {
        this.loginedProjectName = loginedProjectName;
    }
}
