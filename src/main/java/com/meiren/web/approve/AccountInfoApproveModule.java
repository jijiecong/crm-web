package com.meiren.web.approve;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiren.common.utils.RequestUtil;
import com.meiren.common.utils.StringUtils;
import com.meiren.mission.enums.AccountApproveStatusEnum;
import com.meiren.common.result.ApiResult;
import com.meiren.mission.service.AccountInfoService;
import com.meiren.mission.service.WalletAccountService;
import com.meiren.mission.service.entity.AccountInfoEntity;
import com.meiren.vo.AccountInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xgang on 2017/2/8.
 */
@Controller
@RequestMapping("/approve")
public class AccountInfoApproveModule extends BaseController {

    @Autowired
    protected AccountInfoService accountInfoService;

    @Autowired
    protected WalletAccountService walletAccountService;


    /**
     * index 默认显示审核中的账号
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) {

        String page = request.getParameter("page") == null ? "1" : request
                .getParameter("page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/approve/index");

        int pageNum = Integer.valueOf(page);
        if (pageNum <= 0) {
            pageNum = 1;
        }

        int pageSize = DEFAULT_ROWS;


        Map<String, Object> searchParamMap = new HashMap<String, Object>();

        String status=request.getParameter("status");
        if (StringUtils.isEmpty(status)){
            status= AccountApproveStatusEnum.APPROVING.name();
        }
        searchParamMap.put("status",status);

        //先查出accountInfo
        ApiResult accountApiResult = accountInfoService.searchAccountInfo(
                searchParamMap, pageNum, pageSize);

        if (accountApiResult == null) {
            modelAndView.addObject("message", "remote apiResult is empty"
                    + JSON.toJSONString(accountApiResult));
            return modelAndView;
        }
        if (!accountApiResult.isSuccess()) {
            modelAndView.addObject("message", accountApiResult.getError());
            return modelAndView;
        }

        if (!StringUtils.isEmpty(accountApiResult.getError())) {
            modelAndView.addObject("message", accountApiResult.getError());
            return modelAndView;
        }
        if (accountApiResult.getData() == null) {
            modelAndView.addObject("message",
                    "data is null #" + accountApiResult.getError());
            return modelAndView;
        }

        Map<String, Object> resultMap = (Map<String, Object>) accountApiResult
                .getData();


        if (resultMap.get("totalCount") != null) {
            modelAndView.addObject("totalCount",
                    Integer.valueOf(resultMap.get("totalCount").toString()));
        }

        if (resultMap.get("data") != null) {
            List<AccountInfoEntity> resultList = (List<AccountInfoEntity>) resultMap
                    .get("data");

            //组装成VO
            List<AccountInfoVO> accountInfoVOS=new ArrayList<>();
            for (AccountInfoEntity accountInfoEntity:resultList){
                AccountInfoVO vo=new AccountInfoVO();
                vo.setId(accountInfoEntity.getId());
                String IDInfoStr=accountInfoEntity.getIdentityInfo();
                JSONObject jsonObject=JSONObject.parseObject(IDInfoStr);

                vo.setName(jsonObject.getString("name"));
                vo.setRemark(accountInfoEntity.getRemark());
                vo.setIDnum(jsonObject.getString("IDnum"));
                vo.setStatus(accountInfoEntity.getStatus());
                vo.setImages((List<String>) jsonObject.get("images"));
                vo.setGmtCreated(accountInfoEntity.getGmtCreated());
                vo.setGmtModified(accountInfoEntity.getGmtModified());
                accountInfoVOS.add(vo);
            }
            modelAndView.addObject("accountVOList", accountInfoVOS);
        }
        modelAndView.addObject("curPage", pageNum);
        modelAndView.addObject("status", status);//这里将状态返回给前段 供分页使用
        modelAndView.addObject("pageSize", DEFAULT_ROWS);
        return modelAndView;
    }



    /**
     * success
     * @param request
     * @param response
     * @return
     */

    @RequestMapping("/success")
    @ResponseBody
    public ApiResult success(HttpServletRequest request,
                                HttpServletResponse response){

        Long id= RequestUtil.getLong(request,"id");
        String remark=RequestUtil.getString(request,"remark");
        Map<String,Object> updateParam=new HashMap<>();
        updateParam.put("remark",remark);
        updateParam.put("status",AccountApproveStatusEnum.SUCCESS.name());

        ApiResult apiResult=accountInfoService.updateAccountInfo(id,updateParam);
        return apiResult;
    }


    /**
     * fail
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/fail" )
    @ResponseBody
    public ApiResult fail(HttpServletRequest request,
                                HttpServletResponse response){
        Long id= RequestUtil.getLong(request,"id");
        String remark=RequestUtil.getString(request,"remark");
        Map<String,Object> updateParam=new HashMap<>();
        updateParam.put("remark",remark);
        updateParam.put("status",AccountApproveStatusEnum.FAIL.name());

        ApiResult apiResult=accountInfoService.updateAccountInfo(id,updateParam);
        return apiResult;
    }


    /**
     * detail
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/detail")//这个接口取消
    public ModelAndView detail(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("/approve/detail");//TODO 参照之前 重新写一个页面还是index页面
        Long id= RequestUtil.getLong(request,"id");
        ApiResult apiResult=accountInfoService.findAccountInfo(id);
        modelAndView.addObject("data",apiResult);
        return modelAndView;
    }


}
