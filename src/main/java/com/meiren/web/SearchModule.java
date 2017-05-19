package com.meiren.web;

import com.meiren.acl.service.AclBusinessService;
import com.meiren.acl.service.AclGroupService;
import com.meiren.acl.service.AclHierarchyService;
import com.meiren.acl.service.entity.AclBusinessEntity;
import com.meiren.acl.service.entity.AclGroupEntity;
import com.meiren.acl.service.entity.AclHierarchyEntity;
import com.meiren.common.result.VueResult;
import com.meiren.utils.RequestUtil;
import com.meiren.vo.GroupVO;
import com.meiren.vo.SelectVO;
import com.meiren.vo.SessionUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("{uuid}/acl/search")
@ResponseBody
public class SearchModule extends BaseController {

    @Autowired
    private AclBusinessService aclBusinessService;
    @Autowired
    private AclGroupService aclGroupService;
    @Autowired
    private AclHierarchyService aclHierarchyService;

    /**
     * 列表
     */
    @RequestMapping("/business")
    public VueResult business(HttpServletRequest request) {
        Map<String, Object> searchParamMap = new HashMap<>();
        List<AclBusinessEntity> businessList = (List<AclBusinessEntity>)
            aclBusinessService.loadAclBusiness(searchParamMap).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclBusinessEntity entity : businessList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(entity.getId());
            selectVO.setName(entity.getName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 查询部门列表
     */
    @RequestMapping("/group")
    public VueResult group(HttpServletRequest request) {
        SessionUserVO user = this.getUser(request);
        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("businessId", user.getBusinessId());
        List<AclGroupEntity> groupList = (List<AclGroupEntity>)
            aclGroupService.loadAclGroup(searchParamMap).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclGroupEntity entity : groupList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(entity.getId());
            selectVO.setName(entity.getName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 查询层级列表
     */
    @RequestMapping("/hierarchy")
    public VueResult hierarchy(HttpServletRequest request) {
        SessionUserVO user = this.getUser(request);
        List<AclHierarchyEntity> groupList = (List< AclHierarchyEntity>)
            aclHierarchyService.loadAclHierarchy(null).getData();
        List<SelectVO> all = new ArrayList<>();
        for (AclHierarchyEntity entity : groupList) {
            SelectVO selectVO = new SelectVO();
            selectVO.setId(entity.getId());
            selectVO.setName(entity.getHierarchyName());
            all.add(selectVO);
        }
        return new VueResult(all);
    }

    /**
     * 根据用户查询所在部门信息
     */
    @RequestMapping("/findByUserId")
    public VueResult findByUserId(HttpServletRequest request) {
        AclGroupEntity  groupEntity = (AclGroupEntity)
            aclGroupService.findAclGroupJoinHasUser(RequestUtil.getLong(request,"userId")).getData();
        GroupVO vo = new GroupVO();
        if(groupEntity == null){
            return new VueResult();
        }
        BeanUtils.copyProperties(groupEntity,vo);
        return new VueResult(vo);
    }
}

