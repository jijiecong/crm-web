package com.meiren.web.monitor;

import com.meiren.tech.mbc.monitor.Monitor;
import com.meiren.tech.mbc.service.entity.MbcMonitorEntity;
import com.meiren.vo.SessionUserVO;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jijc
 * @ClassName: MbcMonitor
 * @Description: mbc用户行为监控
 * @date 2017/10/12 10:23
 */
@Component("mbcMonitor")
public class MbcMonitor implements Monitor {
    @Override
    public MbcMonitorEntity getEntity(HttpServletRequest request) {
        MbcMonitorEntity mbcMonitorEntity = new MbcMonitorEntity();
        try {
            SessionUserVO user = (SessionUserVO) request.getSession().getAttribute("user");
            mbcMonitorEntity.setUserName(user.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mbcMonitorEntity;
    }
}

