package com.meiren.vo;

import com.meiren.member.entity.*;

import java.io.Serializable;
import java.util.Date;

public class UserInfoVO implements Serializable {


    //index
    //用户ID
    private Long userId;
    //密码    示例：7fef6171469e80d32c0559f88b377245
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

    private String birthday;

    //地区（省份编码+城市编码+地区编码）
    private String locationInfo;
    //个人标签 暂时未使用
    private String privacySettings;
    //用头像
    private String userIcon;

    //details
    //邮箱
    private String email;
    //手机号码区号
    private Integer zoneNum;
    //手机号码
    private String mobile;
    //qq
    private Long qq;
    //msn
    private String msn;
    //签名（也就是发表的状态）
    private String signature;

    //补充
    //注册来源
    private String registerSource;//RegisterSourceTypeEnum
    //修改时间
    private Date updateTime;
    //是否是黑名单用户
    private String projectNameBlacklist;//工程名称，用逗号隔开
    //注册的APPNAME
    private String registerAppName;
    //注册的projectName
    private String registerProjectName;
    //登录过的projectName
    private String loginedProjectName;//工程名称，用逗号隔开
    //标签
    private String tags;//标签，数组形式

    //二次补充
    //用户分类id
    private String categoryId;//多个用逗号隔开
    //作品数
    private Integer productCount;
    //上次发秀时间
    private Date lastProductTime;
    //社区登录过的projrctName
    private String  communityLoginProjectName;//用逗号隔开
    //上传头像时间
    private Date uploadUserIconTime;

    private ArtCameraStatisticsEO artCameraStatisticsEO;
    private BeautyCameraStatisticsEO beautyCameraStatisticsEO;
    private CamhommeStatisticsEO camhommeStatisticsEO;
    private FacechatStatisticsEO facechatStatisticsEO;
    private InterphotoStatisticsEO interphotoStatisticsEO;
    private JanePlusStatisticsEO janePlusStatisticsEO;
    private JianpinStatisticsEO jianpinStatisticsEO;
    private KidsCameraStatisticsEO kidsCameraStatisticsEO;
    private PocoCameraStatisticsEO pocoCameraStatisticsEO;
    private XmenStatisticsEO xmenStatisticsEO;

    public Date getUploadUserIconTime() {
        return uploadUserIconTime;
    }

    public void setUploadUserIconTime(Date uploadUserIconTime) {
        this.uploadUserIconTime = uploadUserIconTime;
    }

    public ArtCameraStatisticsEO getArtCameraStatisticsEO() {
        return artCameraStatisticsEO;
    }

    public void setArtCameraStatisticsEO(ArtCameraStatisticsEO artCameraStatisticsEO) {
        this.artCameraStatisticsEO = artCameraStatisticsEO;
    }

    public BeautyCameraStatisticsEO getBeautyCameraStatisticsEO() {
        return beautyCameraStatisticsEO;
    }

    public void setBeautyCameraStatisticsEO(BeautyCameraStatisticsEO beautyCameraStatisticsEO) {
        this.beautyCameraStatisticsEO = beautyCameraStatisticsEO;
    }

    public CamhommeStatisticsEO getCamhommeStatisticsEO() {
        return camhommeStatisticsEO;
    }

    public void setCamhommeStatisticsEO(CamhommeStatisticsEO camhommeStatisticsEO) {
        this.camhommeStatisticsEO = camhommeStatisticsEO;
    }

    public FacechatStatisticsEO getFacechatStatisticsEO() {
        return facechatStatisticsEO;
    }

    public void setFacechatStatisticsEO(FacechatStatisticsEO facechatStatisticsEO) {
        this.facechatStatisticsEO = facechatStatisticsEO;
    }

    public InterphotoStatisticsEO getInterphotoStatisticsEO() {
        return interphotoStatisticsEO;
    }

    public void setInterphotoStatisticsEO(InterphotoStatisticsEO interphotoStatisticsEO) {
        this.interphotoStatisticsEO = interphotoStatisticsEO;
    }

    public JanePlusStatisticsEO getJanePlusStatisticsEO() {
        return janePlusStatisticsEO;
    }

    public void setJanePlusStatisticsEO(JanePlusStatisticsEO janePlusStatisticsEO) {
        this.janePlusStatisticsEO = janePlusStatisticsEO;
    }

    public JianpinStatisticsEO getJianpinStatisticsEO() {
        return jianpinStatisticsEO;
    }

    public void setJianpinStatisticsEO(JianpinStatisticsEO jianpinStatisticsEO) {
        this.jianpinStatisticsEO = jianpinStatisticsEO;
    }

    public KidsCameraStatisticsEO getKidsCameraStatisticsEO() {
        return kidsCameraStatisticsEO;
    }

    public void setKidsCameraStatisticsEO(KidsCameraStatisticsEO kidsCameraStatisticsEO) {
        this.kidsCameraStatisticsEO = kidsCameraStatisticsEO;
    }

    public PocoCameraStatisticsEO getPocoCameraStatisticsEO() {
        return pocoCameraStatisticsEO;
    }

    public void setPocoCameraStatisticsEO(PocoCameraStatisticsEO pocoCameraStatisticsEO) {
        this.pocoCameraStatisticsEO = pocoCameraStatisticsEO;
    }

    public XmenStatisticsEO getXmenStatisticsEO() {
        return xmenStatisticsEO;
    }

    public void setXmenStatisticsEO(XmenStatisticsEO xmenStatisticsEO) {
        this.xmenStatisticsEO = xmenStatisticsEO;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLoginedProjectName() {
        return loginedProjectName;
    }

    public void setLoginedProjectName(String loginedProjectName) {
        this.loginedProjectName = loginedProjectName;
    }

    public String getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource;
    }


    public String getProjectNameBlacklist() {
        return projectNameBlacklist;
    }

    public void setProjectNameBlacklist(String projectNameBlacklist) {
        this.projectNameBlacklist = projectNameBlacklist;
    }

    public String getRegisterAppName() {
        return registerAppName;
    }

    public void setRegisterAppName(String registerAppName) {
        this.registerAppName = registerAppName;
    }

    public String getRegisterProjectName() {
        return registerProjectName;
    }

    public void setRegisterProjectName(String registerProjectName) {
        this.registerProjectName = registerProjectName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }



    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getRegTime() {
        return regTime;
    }

    public Date getUpdateTime() {
        return updateTime;
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

    public String getPrivacySettings() {
        return privacySettings;
    }

    public void setPrivacySettings(String privacySettings) {
        this.privacySettings = privacySettings == null ? null : privacySettings.trim();
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon == null ? null : userIcon.trim();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public Date getLastProductTime() {
        return lastProductTime;
    }

    public void setLastProductTime(Date lastProductTime) {
        this.lastProductTime = lastProductTime;
    }

    public String getCommunityLoginProjectName() {
        return communityLoginProjectName;
    }

    public void setCommunityLoginProjectName(String communityLoginProjectName) {
        this.communityLoginProjectName = communityLoginProjectName;
    }

}
