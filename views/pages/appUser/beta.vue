<template >
  <div class="panel" >
    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple">
          <el-collapse v-model="activeNames" @change="handleChange">
            <el-collapse-item title="发送验证码" name="1">
              <el-form ref="sendSmsVerifyCodeForm" :model="MobileVerifyCodeParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="MobileVerifyCodeParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="MobileVerifyCodeParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item label="验证码类型">
                  <el-select v-model="MobileVerifyCodeParamsEO.type" placeholder="验证码类型">
                    <el-option label="注册" value="register"></el-option>
                    <el-option label="找回密码" value="find"></el-option>
                    <el-option label="绑定手机" value="bind_mobile"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="sendSmsVerifyCode">发送验证码</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="手机注册" name="2">
              <el-form ref="sendSmsVerifyCodeForm" :model="MobileRegisterAccessParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="MobileRegisterAccessParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="验证码">
                  <el-row>
                    <el-col :span="12">
                      <div class="grid-content bg-purple">
                        <el-input v-model="MobileRegisterAccessParamsEO.verifyCode" placeholder="验证码"></el-input>
                      </div>
                    </el-col>
                    <el-col :span="8" style="margin-left: 30px">
                      <div class="grid-content bg-purple-light">
                        <el-button type="info" @click="getSmsVerifyCode($event,'register')">获取验证码</el-button>
                      </div>
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item label="密码" v-if="showPwd">
                  <el-input v-model="MobileRegisterAccessParamsEO.pwd" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="MobileRegisterAccessParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="doRegisterByPhone">手机注册</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="填写注册信息" name="3">
              <el-form ref="sendSmsVerifyCodeForm" :model="WriteRegisterUserInfoEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="WriteRegisterUserInfoEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="token">
                  <el-input v-model="WriteRegisterUserInfoEO.accessToken" placeholder="token"></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="WriteRegisterUserInfoEO.nickName" placeholder="昵称"></el-input>
                </el-form-item>
                <el-form-item label="头像地址">
                  <el-input v-model="WriteRegisterUserInfoEO.userIcon" placeholder="头像地址"></el-input>
                </el-form-item>
                <el-form-item label="密码" v-if="!showPwd">
                  <el-input v-model="WriteRegisterUserInfoEO.pwd" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="生日年" v-if="showBirthday">
                  <el-input v-model="WriteRegisterUserInfoEO.birthdayYear" placeholder="生日年"></el-input>
                </el-form-item>
                <el-form-item label="生日月" v-if="showBirthday">
                  <el-input v-model="WriteRegisterUserInfoEO.birthdayMonth" placeholder="生日月"></el-input>
                </el-form-item>
                <el-form-item label="生日天" v-if="showBirthday">
                  <el-input v-model="WriteRegisterUserInfoEO.birthdayDay" placeholder="生日天"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="writeRegisterUserInfo">填写注册信息</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="手机登录" name="4">
              <el-form ref="sendSmsVerifyCodeForm" :model="LoginParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="LoginParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                  <el-input v-model="LoginParamsEO.password" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="LoginParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="doLogin">手机登录</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="获取用户信息" name="5">
              <el-form ref="sendSmsVerifyCodeForm" :model="GetUserInfoEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="GetUserInfoEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="token">
                  <el-input v-model="GetUserInfoEO.accessToken" placeholder="token"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="getUserInfo">获取用户信息</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="修改用户信息" name="6">
              <el-form ref="sendSmsVerifyCodeForm" :model="UpdateUserInfoEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="UpdateUserInfoEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="token">
                  <el-input v-model="UpdateUserInfoEO.accessToken" placeholder="token"></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="UpdateUserInfoEO.nickName" placeholder="昵称"></el-input>
                </el-form-item>
                <el-form-item label="头像地址">
                  <el-input v-model="UpdateUserInfoEO.userIcon" placeholder="头像地址"></el-input>
                </el-form-item>
                <el-form-item label="生日年">
                  <el-input v-model="UpdateUserInfoEO.birthdayYear" placeholder="生日年"></el-input>
                </el-form-item>
                <el-form-item label="生日月">
                  <el-input v-model="UpdateUserInfoEO.birthdayMonth" placeholder="生日月"></el-input>
                </el-form-item>
                <el-form-item label="生日天">
                  <el-input v-model="UpdateUserInfoEO.birthdayDay" placeholder="生日天"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateUserInfo">修改用户信息</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="修改密码" name="7">
              <el-form ref="sendSmsVerifyCodeForm" :model="ChangePasswordEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="ChangePasswordEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="token">
                  <el-input v-model="ChangePasswordEO.accessToken" placeholder="token"></el-input>
                </el-form-item>
                <el-form-item label="旧密码">
                  <el-input v-model="ChangePasswordEO.oldPwd" placeholder="旧密码"></el-input>
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="ChangePasswordEO.newPwd" placeholder="新密码"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="忘记密码" name="8">
              <el-form ref="sendSmsVerifyCodeForm" :model="ForgetPasswordEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="ForgetPasswordEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="验证码">
                  <el-row>
                    <el-col :span="12">
                      <div class="grid-content bg-purple">
                        <el-input v-model="ForgetPasswordEO.verifyCode" placeholder="验证码"></el-input>
                      </div>
                    </el-col>
                    <el-col :span="8" style="margin-left: 30px">
                      <div class="grid-content bg-purple-light">
                        <el-button type="info" @click="getSmsVerifyCode($event,'find')">获取验证码</el-button>
                      </div>
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item label="密码">
                  <el-input v-model="ForgetPasswordEO.password" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="ForgetPasswordEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="forgetPassWord">忘记密码</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="第三方登录" name="9">
              <el-form ref="sendSmsVerifyCodeForm" :model="PartnerLoginParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="PartnerLoginParamsEO.openid" placeholder="第三方平台的用户ID"></el-input>
                </el-form-item>
                <el-form-item label="授权Token">
                  <el-input v-model="PartnerLoginParamsEO.accessToken" placeholder="第三方平台的授权Token"></el-input>
                </el-form-item>
                <el-form-item label="刷新Token">
                  <el-input v-model="PartnerLoginParamsEO.refreshToken" placeholder="第三方平台的刷新Token"></el-input>
                </el-form-item>
                <el-form-item label="第三方标识">
                  <el-input v-model="PartnerLoginParamsEO.partner" placeholder="第三方标识"></el-input>
                </el-form-item>
                <el-form-item label="联合ID">
                  <el-input v-model="PartnerLoginParamsEO.unionid" placeholder="第三方平台的联合ID（微信open独有）"></el-input>
                </el-form-item>
                <el-form-item label="过期时间">
                  <el-input v-model="PartnerLoginParamsEO.expiresIn" placeholder="过期时间"></el-input>
                </el-form-item>
                <el-form-item label="签名请求" v-show="false">
                  <el-input v-model="PartnerLoginParamsEO.signedRequest" placeholder="签名请求(Facebook独有)"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="partnerDoLogin">第三方登录</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="绑定手机" name="10">
              <el-form ref="sendSmsVerifyCodeForm" :model="BindPhoneEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="BindPhoneEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="验证码">
                  <el-row>
                    <el-col :span="12">
                      <div class="grid-content bg-purple">
                        <el-input v-model="BindPhoneEO.verifyCode" placeholder="验证码"></el-input>
                      </div>
                    </el-col>
                    <el-col :span="8" style="margin-left: 30px">
                      <div class="grid-content bg-purple-light">
                        <el-button type="info" @click="getSmsVerifyCode($event,'bind_mobile')">获取验证码</el-button>
                      </div>
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item label="用户id">
                  <el-input v-model="BindPhoneEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                  <el-input v-model="BindPhoneEO.password" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="BindPhoneEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="bindPhone">绑定手机</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="刷新token" name="11">
              <el-form ref="sendSmsVerifyCodeForm" :model="RefreshTokenEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="RefreshTokenEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="refreshToken">
                  <el-input v-model="RefreshTokenEO.refreshToken" placeholder="refreshToken"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="refreshToken">刷新token</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="校验用户是否存在" name="12">
              <el-form ref="sendSmsVerifyCodeForm" :model="CheckLoginNameEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="CheckLoginNameEO.username" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="CheckLoginNameEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                  <el-input v-model="CheckLoginNameEO.type" :disabled="true" placeholder="类型"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="checkLoginNameExists">校验用户是否存在</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="校验手机验证码" name="13">
              <el-form ref="sendSmsVerifyCodeForm" :model="MobileVerifyCodeParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="MobileVerifyCodeParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="验证码">
                  <el-input v-model="MobileVerifyCodeParamsEO.verifyCode" placeholder="验证码"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="MobileVerifyCodeParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item label="验证码类型">
                  <el-select v-model="MobileVerifyCodeParamsEO.type" placeholder="验证码类型">
                    <el-option label="注册" value="register"></el-option>
                    <el-option label="找回密码" value="find"></el-option>
                    <el-option label="绑定手机" value="bind_mobile"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="checkSmsVerifyCode">校验手机验证码</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item title="密保注册" name="14">
              <el-form ref="sendSmsVerifyCodeForm" :model="MobileRegisterAccessParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="MobileRegisterAccessParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="家庭数量">
                  <el-input v-model="MobileRegisterAccessParamsEO.familiCount" placeholder="家庭数量"></el-input>
                </el-form-item>
                <el-form-item label="注册来源">
                  <el-input v-model="MobileRegisterAccessParamsEO.comeFrom" placeholder="注册来源"></el-input>
                </el-form-item>
                <el-form-item label="亲子注册类型">
                  <el-radio v-model="MobileRegisterAccessParamsEO.kidsRegisterType" label="register">注册</el-radio>
                  <el-radio v-model="MobileRegisterAccessParamsEO.kidsRegisterType" label="familyInviteRegister">邀请注册</el-radio>
                </el-form-item>
                <!--<el-form-item label="验证码">
                  <el-input v-model="MobileRegisterAccessParamsEO.verifyCode" placeholder="验证码"></el-input>
                  <el-button type="primary" @click="getSmsVerifyCode($event,'register')">获取验证码</el-button>
                </el-form-item>-->
                <el-form-item label="密码">
                  <el-input v-model="MobileRegisterAccessParamsEO.pwd" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="MobileRegisterAccessParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="doRegisterByQuestion">密保注册</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
<!--
            <el-collapse-item title="密保登录" name="14">
              <el-form ref="sendSmsVerifyCodeForm" :model="QuestionLoginParamsEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="QuestionLoginParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="QuestionLoginParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                  <el-form :inline="true"  class="demo-form-inline">
                    <el-input  placeholder="类型"></el-input>
                    <el-input  placeholder="类型"></el-input>
                  </el-form>
                  <el-form :inline="true"  class="demo-form-inline">
                    <el-input  placeholder="类型"></el-input>
                    <el-input  placeholder="类型"></el-input>
                  </el-form>


                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="doLoginByQuestion">密保登录</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
-->
            <el-collapse-item title="更新用户用于登录的手机号-简客" name="16">
              <el-form ref="sendSmsVerifyCodeForm" :model="ChangeBindPhoneEO" label-width="120px">
                <el-form-item label="用户id">
                  <el-input v-model="ChangeBindPhoneEO.userId" placeholder="用户id"></el-input>
                </el-form-item>
                <el-form-item label="token">
                  <el-input v-model="ChangeBindPhoneEO.accessToken" placeholder="token"></el-input>
                </el-form-item>
                <el-form-item label="新手机号">
                  <el-input v-model="ChangeBindPhoneEO.newPhone" placeholder="新手机号"></el-input>
                </el-form-item>
                <el-form-item label="新区号">
                  <el-input v-model="ChangeBindPhoneEO.newZoneNum" placeholder="新区号"></el-input>
                </el-form-item>
                <el-form-item label="旧手机号">
                  <el-input v-model="ChangeBindPhoneEO.oldPhone" placeholder="旧手机号"></el-input>
                </el-form-item>
                <el-form-item label="旧区号">
                  <el-input v-model="ChangeBindPhoneEO.oldZoneNum" placeholder="旧区号"></el-input>
                </el-form-item>
                <el-form-item label="旧手机是否存在">
                  <el-radio v-model="ChangeBindPhoneEO.bCheckMobileOld" label="true">校验</el-radio>
                  <el-radio v-model="ChangeBindPhoneEO.bCheckMobileOld" label="false">不校验</el-radio>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changeBindPhone">更新用户用于登录的手机号-简客</el-button>
                </el-form-item>
              </el-form>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="grid-content bg-purple-light">
          <el-row>
            <el-col :span="2">&nbsp;</el-col>
            <el-col :span="10">
              <div style="margin-left: 200px;margin-bottom: 20px">基本协议入参</div>
              <el-form ref="form" :model="ProtocolAccessEO" label-width="120px">
                <el-form-item label="app名称">
                  <el-select v-model="ProtocolAccessEO.appName" placeholder="app名称" @change="changeSelect">
                    <el-option
                      v-for="item in options"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="类型">
                  <el-radio v-model="ProtocolAccessEO.osType" label="android">Android</el-radio>
                  <el-radio v-model="ProtocolAccessEO.osType" label="ios">IOS</el-radio>
                </el-form-item>
                <el-form-item label="设备号">
                  <el-input v-model="ProtocolAccessEO.deviceMark" placeholder="设备号"></el-input>
                </el-form-item>
                <el-form-item label="版本号">
                  <el-input v-model="ProtocolAccessEO.version" placeholder="版本号"></el-input>
                </el-form-item>
                <el-form-item label="requestVersion">
                  <el-input v-model="ProtocolAccessEO.requestVersion" placeholder="requestVersion"></el-input>
                </el-form-item>
                <el-form-item label="当前时间">
                  <el-input v-model="ProtocolAccessEO.ctime" :disabled="true" placeholder="当前时间"></el-input>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="2">&nbsp;</el-col>
            <el-col :span="10">
              <div style="margin-left: 200px;margin-bottom: 20px">手机注册流程</div>
              <el-form ref="form" :model="RegisterProcessEO" label-width="120px">
                <el-form-item label="手机号">
                  <el-input v-model="MobileRegisterAccessParamsEO.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="区号">
                  <el-input v-model="MobileRegisterAccessParamsEO.zoneNum" placeholder="区号"></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="WriteRegisterUserInfoEO.nickName" placeholder="昵称"></el-input>
                </el-form-item>
                <el-form-item label="头像地址">
                  <el-input v-model="WriteRegisterUserInfoEO.userIcon" placeholder="头像地址"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                  <el-input v-model="MobileRegisterAccessParamsEO.pwd" placeholder="密码"></el-input>
                </el-form-item>
                <!--<el-form-item label="密码">
                  <el-input v-model="WriteRegisterUserInfoEO.pwd" placeholder="密码"></el-input>
                </el-form-item>-->
                <el-form-item label="生日年" v-if="showBirthday">
                  <el-input v-model="WriteRegisterUserInfoEO.birthdayYear" placeholder="生日年"></el-input>
                </el-form-item>
                <el-form-item label="生日月" v-if="showBirthday">
                  <el-input v-model="WriteRegisterUserInfoEO.birthdayMonth" placeholder="生日月"></el-input>
                </el-form-item>
                <el-form-item label="生日天" v-if="showBirthday">
                  <el-input v-model="WriteRegisterUserInfoEO.birthdayDay" placeholder="生日天"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="registerProcess">注册流程测试</el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>

  </div >
</template >
<script type="text/javascript" >
  import { simpleSelect, panelTitle, bottomToolBar } from 'components'
  import { request_appUser } from 'common/request_api'
  import { mapGetters, mapActions } from 'vuex'

  export default{
    data(){
      return {
        activeNames: [],
        showPwd:true,
        showBirthday:false,
        options: [{
          value: 'jianpin_app_android',
          label: '简拼 Android'
        }, {
          value: 'jianpin_app_iphone',
          label: '简拼 IOS'
        }, {
          value: 'beauty_camera_android',
          label: '美人相机 Android'
        }, {
          value: 'beauty_camera_iphone',
          label: '美人相机 IOS'
        }, {
          value: 'art_camera_android',
          label: '图片合成器 Android'
        }, {
          value: 'art_camera_iphone',
          label: '图片合成器 IOS'
        }, {
          value: 'camhomme_iphone',
          label: '型男相机 Android'
        }, {
          value: 'camhomme_android',
          label: '型男相机 IOS'
        }, {
          value: 'x_star_app_android',
          label: '在一起 Android'
        }, {
          value: 'x_star_app_iphone',
          label: '在一起 IOS'
        }, {
          value: 'interphoto_app_android',
          label: '印象 Android'
        }, {
          value: 'interphoto_app_iphone',
          label: '印象 IOS'
        }, {
          value: 'poco_camera_android',
          label: 'poco相机 Android'
        }, {
          value: 'poco_camera_iphone',
          label: 'poco相机 IOS'
        }, {
          value: 'kids_camera_android',
          label: '亲子相机 Android'
        }, {
          value: 'kids_camera_iphone',
          label: '亲子相机 IOS'
        }, {
          value: 'jianke_app_android',
          label: '简客 Android'
        }, {
          value: 'jianke_app_iphone',
          label: '简客 IOS'
        }, {
          value: 'facechat_app_android',
          label: '简讯 Android'
        }, {
          value: 'facechat_app_iphone',
          label: '简讯 IOS'
        }, {
          value: 'material_platform_android',
          label: '素材平台 Android'
        }, {
          value: 'material_platform_iphone',
          label: '素材平台 IOS'
        }, {
          value: 'task_platform_android',
          label: '任务大厅 Android'
        }, {
          value: 'task_platform_iphone',
          label: '任务大厅 IOS'
        }, {
          value: 'beauty_business',
          label: '美人商业 '
        }],
        //基本协议
        ProtocolAccessEO: {
          version: '1.7.4',
          osType: 'android',
          ctime: Date.now(),
          appName: 'jianpin_app_android',
          signCode: '',
          isEnc: '',
          deviceMark: 'abcd1234',
          ip: '',
          imei: '',
          requestVersion: '1.7.4'
        },
         MobileVerifyCodeParamsEO:{
          zoneNum: '86',
          phone: '',
          verifyCode: ''
        },
        //验证码
        RegisterProcessEO: {
          userId: '',
          zoneNum: '86',
          phone: '',
          type: 'register',
          verifyCode: '',
          pwd:'12345678',
          userId:'',
          accessToken:'',
          userIcon:'http://avatar.adnonstop.com/member/camhomme/20170908/09/700351520170908094828339.jpg',
          nickName:'',
          sex:'男',
          birthdayYear:'1993',
          birthdayMonth:'10',
          birthdayDay:'1'
        },
        //手机注册
        MobileRegisterAccessParamsEO:{
          zoneNum: '86',//国际区号
          phone:'13907793923',//手机号码
          verifyCode:'',//校验码
          pwd:'12345678',//密码
          familiCount:'3',//家庭数量  KIDS_CAMERA 专用
          comeFrom:'532',//注册来源  KIDS_CAMERA 专用
          kidsRegisterType:'familyInviteRegister'
        },
        //填写注册信息
        WriteRegisterUserInfoEO:{
          userId:'',
          accessToken:'',
          userIcon:'http://avatar.adnonstop.com/member/camhomme/20170908/09/700351520170908094828339.jpg',
          nickName:'',
          pwd:'12345678',
          sex:'男',
          birthdayYear:'',
          birthdayMonth:'',
          birthdayDay:''
        },
        LoginParamsEO:{
          phone:'',//手机号码
          password:'12345678',//当前登录用户密码
          zoneNum:'86'//国际区号
        },
        GetUserInfoEO:{
          userId:'',
          accessToken:''
        },
        UpdateUserInfoEO:{
          userId:'',
          accessToken:'',
          nickname:'',
          sex:'女',
          birthdayYear:'',
          birthdayMonth:'',
          birthdayDay:'',
          locationId:'101011001006',
          userIcon:'http://avatar.adnonstop.com/member/camhomme/20170908/09/700351520170908094828339.jpg'
        },
        ForgetPasswordEO:{
          zoneNum:'86',
          phone:'',
          verifyCode:'',
          password:''
        },
        ChangePasswordEO:{
          userId:'',
          accessToken:'',
          newPwd:'',
          oldPwd:''
        },
        BindPhoneEO:{
         userId:'',
         zoneNum:'86',
         phone:'',
         verifyCode:'',
         password:''
        },
        PartnerLoginParamsEO:{
         openid:'o5OKNwh3OZVJkC1jG6azuqKAKilw',//第三方平台的用户ID
         accessToken:'mFcPYk0zn3F7jI2x7aqtm421WXmsiME0OS5THpRMQRfMyXdilfEDd7etDkDr1BsdyZPQViJOJ1uDQNET6_MxeofHOJhA_n7FUvPIy48FKT8',//第三方平台的授权Token
         refreshToken:'5lIq74PX2B_yhmSid9mRC1470zqf1QMVDak',//第三方平台的刷新Token
         signedRequest:'',//签名请求(Facebook独有)
         expiresIn:'',//过期时间
         partner:'weixin_open',//第三方标识(facebook:Facebook、sina:新浪微博、qq:腾讯QQ、weixin_open:微信open平台)
         unionid:'oc02NtyyYHw4yZCTl_w4fzB31tck'//第三方平台的联合ID（微信open独有）
        },
        RefreshTokenEO:{
          userId:'',
          refreshToken:''
        },
        CheckLoginNameEO:{
          username:'',
          zoneNum:'86',
          type:'mobile'
        },
        QuestionLoginParamsEO:{
          phone:'',
          zoneNum:'86',
          questions:[{//集合
              id:1,
              answer: 'Apple',
            }, {
              id:2,
              answer: 'Peach',
            },
          ]
        },
        ChangeBindPhoneEO:{
         accessToken:'',
         userId:'',
         newZoneNum:'86',
         newPhone:'',
         oldZoneNum:'86',
         oldPhone:'',
         bCheckMobileOld:'true'
        },
        DetectionPartnerEO:{
          openId:'',
          partner:'',
          bCheckMobile:''
        },
      }
    },
    created(){
      this.isShowPwd()
    },
    methods: {
      //折叠面板
      handleChange(val) {
        console.log(val);
      },
      //基本协议app名称下拉框改变
      changeSelect(val){
        this.isShowPwd()
        this.isShowBirthday()
        debugger
        if(val.indexOf('android') > 1){
          this.ProtocolAccessEO.osType = 'android'
        }else{
          this.ProtocolAccessEO.osType = 'ios'
        }
      },
      isShowPwd(){
        if(this.ProtocolAccessEO.appName == 'jianpin_app_android' || this.ProtocolAccessEO.appName == 'jianpin_app_iphone'
          || this.ProtocolAccessEO.appName == 'camhomme_android' || this.ProtocolAccessEO.appName == 'camhomme_iphone'
          || this.ProtocolAccessEO.appName == 'beauty_camera_android' || this.ProtocolAccessEO.appName == 'beauty_camera_iphone'
          || this.ProtocolAccessEO.appName == 'jianke_app_android' || this.ProtocolAccessEO.appName == 'jianke_app_iphone'){
          this.showPwd = false
          this.WriteRegisterUserInfoEO.pwd = this.MobileRegisterAccessParamsEO.pwd
        }else{
          this.showPwd = true
          this.WriteRegisterUserInfoEO.pwd = this.MobileRegisterAccessParamsEO.pwd
        }
      },
      isShowBirthday(){
        if(this.ProtocolAccessEO.appName == 'x_star_app_android' || this.ProtocolAccessEO.appName == 'x_star_app_iphone'){
          this.showBirthday = true
        }else{
          this.showBirthday = false
        }
      },
      /*//查询全部app名
      getAppName(){
        this.$http.get(request_appUser.getAppName, {
          params: {
          }
        }).then(({ data }) => {
          let projects = [];
          for(var key in data ){
            projects.push({
              value:data[key],
              label:data[key]
            })
          }
          this.options = projects
        }).catch(() => {
        })
      },*/
      getNumberByStr(str){
        return str.replace(/[^0-9]/ig, "")
      },
      //返回结果处理
      doResult(data,title){
        let flag = null;
        let temp = null;
        if(data.success){
          flag = 'success'
          temp = title + '(请求成功)'
        }else{
          flag = 'error'
          temp = title + '('+data.error+')'
        }
        this.$notify({
          title: temp,
          message: JSON.stringify(data),
          duration: 30000,
          type: flag
        });
        this.load_data = false
      },
      //组装参数
      doParam(paramEO){
        let param = JSON.stringify({param:paramEO})
        let ProtocolAccessEO = this.ProtocolAccessEO
        let base =JSON.stringify(ProtocolAccessEO)
        let a1 = param.substring(0,param.length - 1)
        let a2 = base.substring(1)
        let json = param.substring(0,param.length - 1)+","+base.substring(1,base.length);
        return json
      },
      //发送验证码
      sendSmsVerifyCode(){
        this.load_data = true
        let MobileVerifyCodeParamsEO = this.MobileVerifyCodeParamsEO
        let json = this.doParam(MobileVerifyCodeParamsEO)
        this.$http.get(request_appUser.sendSmsVerifyCode,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'发送验证码')
        }).catch(() => {
          this.load_data = false
        })
      },
      //获取验证码
      getSmsVerifyCode(evnt,type){
        debugger
        this.load_data = true
        let phone = null
        let zoneNum = null
        let temp = null
        if(type == 'register'){
          phone = this.MobileRegisterAccessParamsEO.phone
          zoneNum = this.MobileRegisterAccessParamsEO.zoneNum
          temp = '注册'
        }else if(type == 'bind_mobile'){
          phone = this.BindPhoneEO.phone
          zoneNum = this.BindPhoneEO.zoneNum
          temp = '绑定'
        }else if(type == 'find'){
          phone = this.ForgetPasswordEO.phone
          zoneNum = this.ForgetPasswordEO.zoneNum
          temp = '找回'
        }
        //先调用发送验证码
        this.MobileVerifyCodeParamsEO.zoneNum = zoneNum
        this.MobileVerifyCodeParamsEO.phone = phone
        this.MobileVerifyCodeParamsEO.type = type
        this.sendSmsVerifyCode()
        this.$message('验证已发送，请稍等片刻')
        var context = this
        setTimeout(function () {
          //再调用获取验证码
          context.$http.get(request_appUser.getSmsVerifyCode,{
            params: {
              phone:phone,
              zoneNum:zoneNum,
              type:temp
            }
          }).then(({ data }) => {
            if(data == null || data ==''){
              return
            }
            console.log("获取验证码的data："+data)
            if(type == 'register'){
              context.MobileRegisterAccessParamsEO.verifyCode = data
            }else if(type == 'bind_mobile'){
              context.BindPhoneEO.verifyCode = data
            }else if(type == 'find'){
              context.ForgetPasswordEO.verifyCode = data
            }
            context.$message({
              message: '获取成功，验证码已填入',
              type: 'success'
            });
            context.load_data = false
          }).catch(() => {
            this.load_data = false
          })
        }, 2000);
      },
      //手机注册
      doRegisterByPhone(){
        this.load_data = true
        let MobileRegisterAccessParamsEO = this.MobileRegisterAccessParamsEO
        let json = this.doParam(MobileRegisterAccessParamsEO)
        this.$http.get(request_appUser.doRegisterByPhone,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'手机注册')
          this.WriteRegisterUserInfoEO.userId = data.data.accessInfo.userId
          this.WriteRegisterUserInfoEO.accessToken = data.data.accessInfo.accessToken
          this.LoginParamsEO.phone = this.MobileRegisterAccessParamsEO.phone
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //填写注册信息
      writeRegisterUserInfo(){
        this.load_data = true
        let WriteRegisterUserInfoEO = this.WriteRegisterUserInfoEO
        let json = this.doParam(WriteRegisterUserInfoEO)
        this.$http.get(request_appUser.writeRegisterUserInfo,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'填写注册信息')
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //注册流程 三个接口结合
      registerProcess(){
        let context = this
        this.getSmsVerifyCode(null,'register')
        setTimeout(function () {
          context.doRegisterByPhone()
        },3000)
        setTimeout(function () {
          context.writeRegisterUserInfo()
        },5000)
        //todo
        this.LoginParamsEO.password = this.MobileRegisterAccessParamsEO.pwd
      },
      //手机登录
      doLogin(){
        this.load_data = true
        let LoginParamsEO = this.LoginParamsEO
        let json = this.doParam(LoginParamsEO)
        this.$http.get(request_appUser.doLogin,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'手机登录')
          this.GetUserInfoEO.userId = data.data.userId
          this.GetUserInfoEO.accessToken = data.data.accessToken
          this.UpdateUserInfoEO.userId = data.data.userId
          this.UpdateUserInfoEO.accessToken = data.data.accessToken
          this.ChangePasswordEO.userId = data.data.userId
          this.ChangePasswordEO.accessToken = data.data.accessToken
          this.RefreshTokenEO.userId = data.data.userId
          this.ChangePasswordEO.oldPwd = this.LoginParamsEO.password
          this.RefreshTokenEO.refreshToken = data.data.refreshToken
          this.ChangeBindPhoneEO.userId = data.data.userId
          this.ChangeBindPhoneEO.accessToken = data.data.accessToken
          this.ChangeBindPhoneEO.oldPhone = this.LoginParamsEO.phone
        }).catch(() => {
          this.load_data = false
        })
      },
      //获取用户信息
      getUserInfo(){
        this.load_data = true
        let GetUserInfoEO = this.GetUserInfoEO
        let json = this.doParam(GetUserInfoEO)
        this.$http.get(request_appUser.getUserInfo,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'获取用户信息')
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //修改用户信息
      updateUserInfo(){
        this.load_data = true
        let UpdateUserInfoEO = this.UpdateUserInfoEO
        let json = this.doParam(UpdateUserInfoEO)
        this.$http.get(request_appUser.updateUserInfo,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'修改用户信息')
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //忘记密码
      forgetPassWord(){
        this.load_data = true
        let ForgetPasswordEO = this.ForgetPasswordEO
        let json = this.doParam(ForgetPasswordEO)
        this.$http.get(request_appUser.forgetPassWord,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'忘记密码')
        }).catch(() => {
          this.load_data = false
        })
      },
      //修改密码
      changePassword(){
        this.load_data = true
        let ChangePasswordEO = this.ChangePasswordEO
        let json = this.doParam(ChangePasswordEO)
        this.$http.get(request_appUser.changePassword,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'修改密码')
        }).catch(() => {
          this.load_data = false
        })
      },
      //绑定手机
      bindPhone(){
        this.load_data = true
        let BindPhoneEO = this.BindPhoneEO
        let json = this.doParam(BindPhoneEO)
        this.$http.get(request_appUser.bindPhone,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'绑定手机')
        }).catch(() => {
          this.load_data = false
        })
      },
      //第三方登录
      partnerDoLogin(){
        this.load_data = true
        let PartnerLoginParamsEO = this.PartnerLoginParamsEO
        let json = this.doParam(PartnerLoginParamsEO)
        this.$http.get(request_appUser.partnerDoLogin,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'第三方登录')
          this.BindPhoneEO.userId = data.data.userId
          this.RefreshTokenEO.userId = data.data.userId
          this.RefreshTokenEO.refreshToken = data.data.accessInfo.refreshToken
        }).catch(() => {
          this.load_data = false
        })
      },
      //刷新token
      refreshToken(){
        this.load_data = true
        let RefreshTokenEO = this.RefreshTokenEO
        let json = this.doParam(RefreshTokenEO)
        this.$http.get(request_appUser.refreshToken,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'刷新token')
        }).catch(() => {
          this.load_data = false
        })
      },
      //校验用户是否存在
      checkLoginNameExists(){
        this.load_data = true
        let CheckLoginNameEO = this.CheckLoginNameEO
        let json = this.doParam(CheckLoginNameEO)
        this.$http.get(request_appUser.checkLoginNameExists,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          if(data.success && data.data){
            this.$notify({
              title: '用户存在',
              message: JSON.stringify(data),
              duration: 30000,
              type: 'success'
            });
          }else{
            this.$notify({
              title: '用户不存在',
              message: JSON.stringify(data),
              duration: 30000,
              type: 'warning'
            });
          }
          this.load_data = false
        }).catch(() => {
          this.load_data = false
        })
      },
      //用户密保登录
      doLoginByQuestion(){
        this.load_data = true
        let QuestionLoginParamsEO = this.QuestionLoginParamsEO
        let json = this.doParam(QuestionLoginParamsEO)
        this.$http.get(request_appUser.doLoginByQuestion,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'密保登录')
        }).catch(() => {
          this.load_data = false
        })
      },
      //更新用户用于登录的手机号-简客
      changeBindPhone(){
        this.load_data = true
        let ChangeBindPhoneEO = this.ChangeBindPhoneEO
        let json = this.doParam(ChangeBindPhoneEO)
        this.$http.get(request_appUser.changeBindPhone,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'修改手机号')
        }).catch(() => {
          this.load_data = false
        })
      },
      //校验手机验证码
      checkSmsVerifyCode(){
        this.load_data = true
        let MobileVerifyCodeParamsEO = this.MobileVerifyCodeParamsEO
        let json = this.doParam(MobileVerifyCodeParamsEO)
        this.$http.get(request_appUser.checkSmsVerifyCode,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'校验验证码')
        }).catch(() => {
          this.load_data = false
        })
      },
      //密保注册
      doRegisterByQuestion(){
        this.load_data = true
        let MobileRegisterAccessParamsEO = this.MobileRegisterAccessParamsEO
        let registerType = this.MobileRegisterAccessParamsEO.kidsRegisterType
        let json = this.doParam(MobileRegisterAccessParamsEO)
        this.$http.get(request_appUser.doRegisterByQuestion,{
          params: {
            param:json,
            kidsRegisterType:registerType
          }
        }).then(({ data }) => {
          this.doResult(data,'密保注册')
        }).catch(() => {
          this.load_data = false
        })
      },
      //检测第三方账号
      detectionPartner(){
        this.load_data = true
        let DetectionPartnerEO = this.DetectionPartnerEO
        let json = this.doParam(DetectionPartnerEO)
        this.$http.get(request_appUser.detectionPartner,{
          params: {
            param:json
          }
        }).then(({ data }) => {
          this.doResult(data,'检测第三方账号')
        }).catch(() => {
          this.load_data = false
        })
      },


    },
    watch: {},
    components: {
      simpleSelect,
      panelTitle,
      bottomToolBar
    }
  }
</script >
